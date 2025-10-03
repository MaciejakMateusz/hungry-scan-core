package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.CategoryCustomerDTO;
import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
import com.hackybear.hungry_scan_core.dto.mapper.CategoryMapper;
import com.hackybear.hungry_scan_core.dto.mapper.TranslatableMapper;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImpTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private TranslatableMapper translatableMapper;
    @Mock
    private ExceptionHelper exceptionHelper;
    @Mock
    private SortingHelper sortingHelper;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private VariantRepository variantRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private S3Service s3Service;
    @Captor
    private ArgumentCaptor<Consumer<Set<Category>>> captor;

    @InjectMocks
    private CategoryServiceImp service;

    private final Long MENU_ID = 42L;
    private final Long CAT_ID = 7L;

    @Test
    void findAll_shouldReturnMappedSortedDTOs() throws LocalizedException {
        Category c1 = new Category();
        c1.setDisplayOrder(2);
        Category c2 = new Category();
        c2.setDisplayOrder(1);
        Set<Category> set = new HashSet<>(List.of(c1, c2));
        when(categoryRepository.findAllByMenuId(MENU_ID)).thenReturn(set);

        CategoryDTO dto1 = new CategoryDTO(98, null, new ArrayList<>(), false, null,
                null, null, null, null);
        CategoryDTO dto2 = new CategoryDTO(99, null, new ArrayList<>(), false, null,
                null, null, null, null);
        when(categoryMapper.toDTO(c1)).thenReturn(dto1);
        when(categoryMapper.toDTO(c2)).thenReturn(dto2);

        List<CategoryDTO> result = service.findAll(MENU_ID);

        assertEquals(2, result.size());
        assertSame(dto2, result.get(0));
        assertSame(dto1, result.get(1));
        verify(categoryRepository).findAllByMenuId(MENU_ID);
    }

    @Test
    void findAllDisplayOrders_shouldReturnList() {
        List<Integer> orders = List.of(1, 3, 5);
        when(categoryRepository.findAllDisplayOrdersByMenuId(MENU_ID)).thenReturn(orders);

        List<Integer> result = service.findAllDisplayOrders(MENU_ID);
        assertEquals(orders, result);
        verify(categoryRepository).findAllDisplayOrdersByMenuId(MENU_ID);
    }

    @Test
    void updateDisplayOrders_shouldCallRepositoryForEach() throws LocalizedException {
        Translatable translatable1 = new Translatable().withId(111L).withEn("A");
        Translatable translatable2 = new Translatable().withId(112L).withEn("B");
        TranslatableDTO translatable1DTO = translatableMapper.toDTO(translatable1);
        TranslatableDTO translatable2DTO = translatableMapper.toDTO(translatable2);
        CategoryFormDTO f1 = new CategoryFormDTO(1L, translatable1DTO, true, 5);
        CategoryFormDTO f2 = new CategoryFormDTO(2L, translatable2DTO, true, 6);
        Category c1 = new Category();
        c1.setId(1L);
        c1.setDisplayOrder(5);
        Category c2 = new Category();
        c2.setId(2L);
        c2.setDisplayOrder(6);
        when(categoryMapper.toCategory(f1)).thenReturn(c1);
        when(categoryMapper.toCategory(f2)).thenReturn(c2);

        service.updateDisplayOrders(List.of(f1, f2), MENU_ID);

        verify(categoryRepository).updateDisplayOrders(1L, 5);
        verify(categoryRepository).updateDisplayOrders(2L, 6);
    }

    @Test
    void countAll_shouldReturnCount() throws LocalizedException {
        when(categoryRepository.countByMenuId(MENU_ID)).thenReturn(99L);
        Long count = service.countAll(MENU_ID);
        assertEquals(99L, count);
    }

    @Test
    void findAllAvailableAndVisible_filtersInvisibleItems() {
        MenuItem visible = new MenuItem();
        visible.setVisible(true);
        MenuItem invisible = new MenuItem();
        invisible.setVisible(false);
        Category cat = new Category();
        cat.setMenuItems(new HashSet<>(List.of(visible, invisible)));
        when(categoryRepository.findAllAvailableByMenuId(MENU_ID))
                .thenReturn(List.of(cat));
        Translatable translatable = new Translatable().withId(111L).withEn("Category name");
        TranslatableDTO translatableDTO = translatableMapper.toDTO(translatable);
        CategoryCustomerDTO mapped = new CategoryCustomerDTO(1111L, translatableDTO, new ArrayList<>());
        when(categoryMapper.toCustomerDTO(cat)).thenReturn(mapped);

        List<CategoryCustomerDTO> result = service.findAllAvailableAndVisible(MENU_ID);

        assertEquals(1, result.size());
        assertSame(mapped, result.getFirst());
    }

    @Test
    void findById_existing_shouldReturnFormDTO() throws LocalizedException {
        Category cat = new Category();
        when(categoryRepository.findById(CAT_ID)).thenReturn(Optional.of(cat));
        Translatable translatable = new Translatable().withId(111L).withEn("Category name");
        TranslatableDTO translatableDTO = translatableMapper.toDTO(translatable);
        CategoryFormDTO form = new CategoryFormDTO(CAT_ID, translatableDTO, true, 1);
        when(categoryMapper.toFormDTO(cat)).thenReturn(form);

        CategoryFormDTO dto = service.findById(CAT_ID);
        assertSame(form, dto);
    }

    @Test
    void findById_missing_shouldThrow() {
        when(categoryRepository.findById(CAT_ID)).thenReturn(Optional.empty());
        when(exceptionHelper.supplyLocalizedMessage("error.categoryService.categoryNotFound", CAT_ID))
                .thenReturn(() -> new LocalizedException("not found"));
        LocalizedException ex = assertThrows(LocalizedException.class, () -> service.findById(CAT_ID));
        assertEquals("not found", ex.getMessage());
    }

    @Test
    void save_shouldMapAndAssignDisplayOrderAndSave() throws Exception {
        Translatable translatable = new Translatable().withId(111L).withEn("Category name");
        TranslatableDTO translatableDTO = translatableMapper.toDTO(translatable);
        CategoryFormDTO form = new CategoryFormDTO(null, translatableDTO, true, null);
        Category mapped = new Category();
        when(categoryMapper.toCategory(form)).thenReturn(mapped);
        Menu menu = new Menu();
        menu.setId(MENU_ID);
        when(menuRepository.findById(MENU_ID)).thenReturn(Optional.of(menu));
        when(categoryRepository.findMaxDisplayOrderByMenuId(MENU_ID))
                .thenReturn(Optional.of(10));

        service.save(form, MENU_ID);

        assertEquals(11, mapped.getDisplayOrder());
        assertSame(menu, mapped.getMenu());
        verify(categoryRepository).save(mapped);
    }

    @Test
    void update_shouldLoadModifyAndFlush() throws Exception {
        Translatable translatable = new Translatable().withId(111L).withEn("Category name");
        TranslatableDTO translatableDTO = translatableMapper.toDTO(translatable);
        CategoryFormDTO form = new CategoryFormDTO(CAT_ID, translatableDTO, false, null);
        Category existing = new Category();
        when(categoryRepository.findById(CAT_ID)).thenReturn(Optional.of(existing));
        when(translatableMapper.toTranslatable(form.name())).thenReturn(/* some translatable */ null);

        service.update(form, MENU_ID);

        verify(categoryRepository).saveAndFlush(existing);
        assertFalse(existing.isAvailable());
    }

    @Test
    void delete_noChildren_shouldDeleteAndReassign() throws LocalizedException {
        Category existing = new Category();
        existing.setId(CAT_ID);
        existing.setMenu(new Menu());
        existing.getMenu().setId(MENU_ID);
        existing.setMenuItems(new HashSet<>());

        when(categoryRepository.findById(CAT_ID)).thenReturn(Optional.of(existing));
        when(categoryRepository.findAllByMenuId(MENU_ID)).thenReturn(new HashSet<>());

        service.delete(CAT_ID, MENU_ID);

        verify(categoryRepository).deleteById(CAT_ID);
        verify(sortingHelper)
                .reassignDisplayOrders(eq(Collections.emptySet()), captor.capture());

        Set<Category> dummy = Collections.singleton(new Category());
        captor.getValue().accept(dummy);
        verify(categoryRepository).saveAllAndFlush(dummy);
    }

    @Test
    @SuppressWarnings("unchecked")
    void delete_withChildren_shouldCascadeAndDeleteFiles() throws LocalizedException {
        Category existing = new Category();
        existing.setId(CAT_ID);
        existing.setMenu(new Menu());
        existing.getMenu().setId(MENU_ID);

        MenuItem m1 = new MenuItem();
        m1.setId(100L);
        Variant v1 = new Variant();
        m1.setVariants(List.of(v1));
        existing.setMenuItems(new HashSet<>(List.of(m1)));

        when(categoryRepository.findById(CAT_ID)).thenReturn(Optional.of(existing));
        when(categoryRepository.findAllByMenuId(MENU_ID)).thenReturn(Collections.emptySet());

        service.delete(CAT_ID, MENU_ID);

        verify(variantRepository).deleteAll(List.of(v1));
        verify(menuItemRepository).deleteAll(Set.of(m1));
        verify(categoryRepository).deleteById(CAT_ID);

        verify(sortingHelper)
                .reassignDisplayOrders(
                        eq(Collections.emptySet()),
                        any(Consumer.class)
                );

        verify(s3Service).deleteAllFiles("menuItems", List.of(100L));
    }
}