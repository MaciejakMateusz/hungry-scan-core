package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Service
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ExceptionHelper exceptionHelper;
    private final RestaurantMapper restaurantMapper;

    public RestaurantServiceImp(RestaurantRepository restaurantRepository,
                                ExceptionHelper exceptionHelper,
                                RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.exceptionHelper = exceptionHelper;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    @Cacheable(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    public TreeSet<RestaurantSimpleDTO> findAll(User currentUser) {
        Set<Restaurant> restaurants = currentUser.getRestaurants();
        return restaurants.stream()
                .map(restaurantMapper::toSimpleDTO)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    @Cacheable(value = USER_RESTAURANT, key = "#currentUser.getId()")
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
            @CacheEvict(value = RESTAURANT_ID, key = "#restaurantDTO.id()", condition = "#restaurantDTO.id() != null"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    })
    public Restaurant save(RestaurantDTO restaurantDTO, User currentUser) {
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO);
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = USER_RESTAURANT_ID, key = "#currentUser.getUsername()")
    })
    public ResponseEntity<?> persistInitialRestaurant(Map<String, Object> params, User currentUser) {
        UserService userService = (UserService) params.get("userService");
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
        persistInitialData(restaurantDTO, userService, currentUser);
        return ResponseEntity.ok(Map.of("redirectUrl", "/app"));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#restaurantDTO.id()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getId()")
    })
    public void update(RestaurantDTO restaurantDTO, User currentUser) throws LocalizedException {
        Restaurant restaurant = getById(restaurantDTO.id());
        restaurant.setName(restaurantDTO.name());
        restaurant.setAddress(restaurantDTO.address());
        restaurantRepository.save(restaurant);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#id"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getId()")
    })
    public void delete(Long id, User currentUser) throws LocalizedException {
        restaurantRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = RESTAURANT_TOKEN, key = "#token")
    public RestaurantDTO findByToken(String token) throws LocalizedException {
        Restaurant restaurant = restaurantRepository.findByToken(token)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFoundByToken"));
        return restaurantMapper.toDTO(restaurant);
    }

    private RestaurantDTO getDTOById(Long id) throws LocalizedException {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound"));
        return restaurantMapper.toDTO(restaurant);
    }

    private Restaurant getById(Long id) throws LocalizedException {
        return restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound"));
    }

    private void persistInitialData(RestaurantDTO restaurantDTO, UserService userService, User currentUser) {
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO);
        restaurant = restaurantRepository.save(restaurant);
        restaurant.setMenus(new TreeSet<>());
        createInitialMenu(restaurant);
        setupRestaurantSettings(restaurant);
        restaurant = restaurantRepository.save(restaurant);
        setupUser(restaurant, currentUser, userService);
    }

    private static void setupRestaurantSettings(Restaurant restaurant) {
        Settings s = new Settings();
        s.setRestaurantId(restaurant.getId());
        s.setCapacity((short) 100);
        s.setOpeningTime(LocalTime.of(10, 0));
        s.setClosingTime(LocalTime.of(22, 0));
        s.setBookingDuration(2L);
        s.setLanguage(Language.PL);
        s.setOrderCommentAllowed(false);
        s.setWaiterCommentAllowed(false);
        restaurant.setSettings(s);
    }

    private static void createInitialMenu(Restaurant restaurant) {
        Menu menu = new Menu();
        menu.setStandard(true);
        menu.setName("Menu");
        menu.setRestaurantId(restaurant.getId());
        restaurant.addMenu(menu);
    }

    private static void setupUser(Restaurant restaurant, User currentUser, UserService userService) {
        currentUser.addRestaurant(restaurant);
        currentUser.setActiveRestaurantId(restaurant.getId());
        Optional<Menu> menu = restaurant.getMenus().stream().findFirst();
        menu.ifPresent(m -> currentUser.setActiveMenuId(m.getId()));
        userService.save(currentUser);
    }
}
