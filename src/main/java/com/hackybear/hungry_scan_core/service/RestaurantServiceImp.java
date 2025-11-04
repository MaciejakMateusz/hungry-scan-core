package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.OrganizationRestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.enums.Theme;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuColorRepository;
import com.hackybear.hungry_scan_core.repository.PricePlanRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantService;
import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import com.hackybear.hungry_scan_core.utility.MenuPlanUpdater;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final PricePlanRepository pricePlanRepository;
    private final MenuColorRepository menuColorRepository;
    private final UserService userService;
    private final QRService qrService;
    private final ExceptionHelper exceptionHelper;
    private final RestaurantMapper restaurantMapper;
    private final UserRepository userRepository;
    private final MenuPlanUpdater menuPlanUpdater;
    private final S3Service s3Service;

    private static final String S3_PATH = "menuItems";

    @Override
    @Cacheable(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    public TreeSet<RestaurantSimpleDTO> findAll(User currentUser) {
        Set<Restaurant> restaurants = currentUser.getRestaurants();
        return restaurants.stream()
                .map(restaurantMapper::toSimpleDTO)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public TreeSet<OrganizationRestaurantDTO> findAllByOrganizationId(User currentUser) {
        Set<Restaurant> restaurants = restaurantRepository.findAllByOrganizationId(currentUser.getOrganizationId());
        return restaurants.stream()
                .map(restaurantMapper::toOrganizationDTO)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    @Cacheable(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()")
    public RestaurantDTO findCurrent(User currentUser) throws LocalizedException {
        Restaurant restaurant = restaurantRepository.findById(currentUser.getActiveRestaurantId())
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound"));
        return restaurantMapper.toDTO(restaurant);
    }

    @Override
    @Cacheable(value = RESTAURANT_ID, key = "#id")
    public RestaurantDTO findById(Long id) throws LocalizedException {
        return getDTOById(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = USER_RESTAURANT_ID, key = "#currentUser.getActiveRestaurantId()")
    })
    public void save(RestaurantDTO restaurantDTO, User currentUser) throws Exception {
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO);
        restaurant.setOrganizationId(currentUser.getOrganizationId());
        setupRestaurantSettings(restaurant);
        restaurant.getSettings().setOperatingHours(restaurantDTO.settings().operatingHours());
        restaurant.getSettings().setLanguage(restaurantDTO.settings().language());
        restaurant.getSettings().setSupportedLanguages(restaurantDTO.settings().supportedLanguages());
        restaurant = setupNew(restaurant);
        restaurant = restaurantRepository.save(restaurant);
        setupUser(restaurant, currentUser, userService);
    }

    @Override
    @Transactional
    public ResponseEntity<?> persistInitialRestaurant(Map<String, Object> params, User currentUser) throws Exception {
        BindingResult br = (BindingResult) params.get("bindingResult");
        ResponseHelper responseHelper = (ResponseHelper) params.get("responseHelper");
        RestaurantDTO restaurantDTO = (RestaurantDTO) params.get("restaurantDTO");
        if (userService.hasCreatedRestaurant()) {
            return ResponseEntity.badRequest().body(Map.of("error",
                    exceptionHelper.getLocalizedMsg("error.createRestaurant.alreadyCreated")));
        }
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        createAndPersistNew(restaurantDTO, currentUser);
        return ResponseEntity.ok(Map.of("redirectUrl", "/app"));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#restaurantDTO.id()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()")
    })
    public void update(RestaurantDTO restaurantDTO, User currentUser) throws LocalizedException {
        Restaurant restaurant = getById(restaurantDTO.id());
        menuPlanUpdater.updateMenusPlans(restaurant, restaurantDTO);
        restaurantMapper.updateFromDTO(restaurantDTO, restaurant);
        restaurantRepository.save(restaurant);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()")
    })
    @Transactional
    public void delete(User currentUser) throws LocalizedException {
        Long restaurantId = currentUser.getActiveRestaurantId();
        Long menuId = currentUser.getActiveMenuId();
        validateDeletion(restaurantId, currentUser);
        Restaurant otherRestaurant = findOtherUserRestaurant(currentUser, restaurantId);
        Long otherMenuId = findOtherUserMenuId(otherRestaurant, menuId);
        currentUser.setActiveRestaurantId(otherRestaurant.getId());
        currentUser.setActiveMenuId(otherMenuId);
        currentUser.removeRestaurantById(restaurantId);
        Restaurant restaurant = getById(restaurantId);
        restaurantRepository.delete(restaurant);
        userRepository.save(currentUser);
        removeMenuItemsFiles(restaurant);
    }

    @Override
    @Cacheable(value = RESTAURANT_TOKEN, key = "#token")
    public RestaurantDTO findByToken(String token) throws LocalizedException {
        Restaurant restaurant = restaurantRepository.findByToken(token)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFoundByToken"));
        return restaurantMapper.toDTO(restaurant);
    }

    private Restaurant getById(Long id) throws LocalizedException {
        return restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound"));
    }

    private RestaurantDTO getDTOById(Long id) throws LocalizedException {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound"));
        return restaurantMapper.toDTO(restaurant);
    }

    private void createAndPersistNew(RestaurantDTO restaurantDTO, User currentUser) throws Exception {
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO);
        restaurant.setOrganizationId(currentUser.getOrganizationId());
        restaurant.addUser(currentUser);
        setupRestaurantSettings(restaurant);
        restaurant.getSettings().setLanguage(restaurantDTO.settings().language());
        setupSupportedLanguages(restaurant, restaurantDTO);
        restaurant = setupInitial(restaurant);
        restaurant = restaurantRepository.save(restaurant);
        setupUser(restaurant, currentUser, userService);
    }

    private void setupSupportedLanguages(Restaurant restaurant, RestaurantDTO restaurantDTO) {
        Set<Language> allSupported = Arrays.stream(Language.values()).collect(Collectors.toSet());
        Set<Language> supported = allSupported.stream()
                .filter(language -> !language.equals(restaurantDTO.settings().language()))
                .collect(Collectors.toSet());
        restaurant.getSettings().setSupportedLanguages(supported);
    }

    @NotNull
    private Restaurant setupNew(Restaurant restaurant) throws Exception {
        restaurant.setPricePlan(pricePlanRepository.findById(1L).orElseThrow());
        restaurant.setToken(UUID.randomUUID().toString());
        restaurant.setQrVersion(1);
        restaurant = restaurantRepository.save(restaurant);
        restaurant.setMenus(new TreeSet<>());
        createNewMenu(restaurant);
        qrService.generate(restaurant.getId());
        return restaurant;
    }

    @NotNull
    private Restaurant setupInitial(Restaurant restaurant) throws Exception {
        restaurant.setPricePlan(pricePlanRepository.findById(1L).orElseThrow());
        restaurant.setToken(UUID.randomUUID().toString());
        restaurant.setQrVersion(1);
        restaurant = restaurantRepository.save(restaurant);
        restaurant.setMenus(new TreeSet<>());
        createInitialMenu(restaurant);
        qrService.generate(restaurant.getId());
        return restaurant;
    }

    private void setupRestaurantSettings(Restaurant restaurant) {
        Settings s = new Settings();
        s.setRestaurant(restaurant);
        s.setCapacity((short) 100);
        s.setOperatingHours(createDefaultOperatingHours());
        s.setBookingDuration(2L);
        s.setOrderCommentAllowed(false);
        s.setWaiterCommentAllowed(false);
        restaurant.setSettings(s);
    }

    private static Map<DayOfWeek, TimeRange> createDefaultOperatingHours() {
        Map<DayOfWeek, TimeRange> operatingHours = new HashMap<>();
        Arrays.asList(DayOfWeek.values()).forEach(day -> {
            TimeRange timeRange = createDefaultTimeRange();
            operatingHours.put(day, timeRange);
        });
        return operatingHours;
    }

    private void createNewMenu(Restaurant restaurant) throws LocalizedException {
        Menu menu = new Menu();
        menu.setStandard(true);
        menu.setName("Menu");
        menu.setRestaurant(restaurant);
        menu.setTheme(Theme.COLOR_318E41);
        menu.setMessage(getMenuMessage());
        menu.setPlan(createNewPlan(menu, restaurant));
        MenuColor menuColor = menuColorRepository.findById(9L)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuColorService.menuColorNotFound"));
        menu.setColor(menuColor);
        restaurant.addMenu(menu);
    }

    private void createInitialMenu(Restaurant restaurant) throws LocalizedException {
        Menu menu = new Menu();
        menu.setStandard(true);
        menu.setName("Menu");
        menu.setRestaurant(restaurant);
        menu.setTheme(Theme.COLOR_318E41);
        menu.setMessage(getMenuMessage());
        menu.setPlan(createDefaultPlan(menu));
        MenuColor menuColor = menuColorRepository.findById(9L)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuColorService.menuColorNotFound"));
        menu.setColor(menuColor);
        restaurant.addMenu(menu);
    }

    private static Set<MenuPlan> createNewPlan(Menu menu, Restaurant restaurant) {
        Set<MenuPlan> plans = new HashSet<>();
        Settings settings = restaurant.getSettings();
        Map<DayOfWeek, TimeRange> operatingHours = settings.getOperatingHours();
        for (Map.Entry<DayOfWeek, TimeRange> entry : operatingHours.entrySet()) {
            TimeRange timeRange = entry.getValue();
            if (timeRange.isAvailable()) {
                MenuPlan plan = new MenuPlan();
                plan.setDayOfWeek(entry.getKey());
                plan.setTimeRanges(Set.of(timeRange));
                plan.setMenu(menu);
                plans.add(plan);
            }
        }
        return plans;
    }

    private static Set<MenuPlan> createDefaultPlan(Menu menu) {
        Set<MenuPlan> plans = new HashSet<>();
        Arrays.asList(DayOfWeek.values()).forEach(dayOfWeek -> {
            MenuPlan plan = new MenuPlan();
            plan.setDayOfWeek(dayOfWeek);
            plan.setTimeRanges(Set.of(createDefaultTimeRange()));
            plan.setMenu(menu);
            plans.add(plan);
        });
        return plans;
    }

    private static TimeRange createDefaultTimeRange() {
        return new TimeRange(LocalTime.of(10, 0), LocalTime.of(22, 0));
    }

    private static void setupUser(Restaurant restaurant, User currentUser, UserService userService) {
        restaurant.addUser(currentUser);
        currentUser.setActiveRestaurantId(restaurant.getId());
        Optional<Menu> menu = restaurant.getMenus().stream().findFirst();
        menu.ifPresent(m -> currentUser.setActiveMenuId(m.getId()));
        userService.save(currentUser);
    }

    private void validateDeletion(Long id, User currentUser) throws LocalizedException {
        boolean hasPaidPricePlan = restaurantRepository.hasPaidPricePlan(id);

        if (hasPaidPricePlan) {
            exceptionHelper.throwLocalizedMessage("error.restaurantService.paidPlanRestaurantRemoval");
        } else if (currentUser.getRestaurants().size() == 1) {
            exceptionHelper.throwLocalizedMessage("error.restaurantService.lastRestaurantRemoval");
        }
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(
                exceptionHelper.supplyLocalizedMessage("error.restaurantService.restaurantNotFound"));
        Set<User> users = restaurant.getUsers();

        Set<User> usersWithOneRestaurant = users.stream()
                .filter(user -> user.getRestaurants().size() == 1)
                .collect(Collectors.toSet());
        if (!usersWithOneRestaurant.isEmpty()) {
            exceptionHelper.throwLocalizedMessage("error.restaurantService.oneUserOneRestaurant");
        }
    }

    private Restaurant findOtherUserRestaurant(User user, Long currentRestaurantId) throws LocalizedException {
        return user.getRestaurants().stream()
                .filter(r -> !r.getId().equals(currentRestaurantId))
                .findFirst()
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound"));
    }

    private Long findOtherUserMenuId(Restaurant restaurant, Long currentMenuId) throws LocalizedException {
        return restaurant.getMenus().stream()
                .map(Menu::getId).filter(m -> !m.equals(currentMenuId))
                .findFirst()
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuService.menuNotFound"));
    }

    private void removeMenuItemsFiles(Restaurant restaurant) {
        List<MenuItem> menuItems = new ArrayList<>();
        for (Menu menu : restaurant.getMenus()) {
            for (Category category : menu.getCategories()) {
                menuItems.addAll(category.getMenuItems());
            }
        }
        List<Long> menuItemIds = menuItems.stream().map(MenuItem::getId).toList();
        s3Service.deleteAllFiles(S3_PATH, menuItemIds);
    }

    private static Translatable getMenuMessage() {
        return new Translatable()
                .withPl("Witaj!")
                .withEn("Welcome!");
    }
}
