package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
class SortingHelperTest {

    @Autowired
    SortingHelper sortingHelper;

    private List<MenuItem> menuItems;
    private List<Category> categories;
    private List<Variant> variants;
    private Method setDisplayOrderMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        menuItems = prepareMenuItems();
        categories = prepareCategories();
        variants = prepareVariants();
        setDisplayOrderMethod = SortingHelper.class.getDeclaredMethod("setDisplayOrder", Object.class, Integer.class);
        setDisplayOrderMethod.setAccessible(true);
    }

    @Test
    void shouldReassignDisplayOrdersForMenuItems() {
        sortingHelper.reassignDisplayOrders(menuItems, list -> {
        });
        assertDisplayOrders(menuItems);
    }

    @Test
    void shouldReassignDisplayOrdersForCategories() {
        sortingHelper.reassignDisplayOrders(categories, list -> {
        });
        assertDisplayOrders(categories);
    }

    @Test
    void shouldReassignDisplayOrdersForVariants() {
        sortingHelper.reassignDisplayOrders(variants, list -> {
        });
        assertDisplayOrders(variants);
    }

    @Test
    void shouldSetDisplayOrderForMenuItem() throws InvocationTargetException, IllegalAccessException {
        MenuItem menuItem = new MenuItem();
        setDisplayOrderMethod.invoke(sortingHelper, menuItem, 10);
        assertEquals(10, menuItem.getDisplayOrder());
    }

    @Test
    void shouldSetDisplayOrderForCategory() throws InvocationTargetException, IllegalAccessException {
        Category category = new Category();
        setDisplayOrderMethod.invoke(sortingHelper, category, 20);
        assertEquals(20, category.getDisplayOrder());
    }

    @Test
    void shouldSetDisplayOrderForVariant() throws InvocationTargetException, IllegalAccessException {
        Variant variant = new Variant();
        setDisplayOrderMethod.invoke(sortingHelper, variant, 30);
        assertEquals(30, variant.getDisplayOrder());
    }

    @Test
    void shouldNotSetDisplayOrderForUnsupportedType() {
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () ->
                setDisplayOrderMethod.invoke(sortingHelper, new Object(), 1)
        );
        Throwable cause = exception.getCause();
        assertInstanceOf(IllegalArgumentException.class, cause);
        assertEquals("Unsupported type", cause.getMessage());
    }

    private <T> void assertDisplayOrders(List<T> items) {
        for (int i = 0; i < items.size(); i++) {
            assertEquals(i + 1, getDisplayOrder(items.get(i)));
        }
    }

    private Integer getDisplayOrder(Object obj) {
        if (obj instanceof MenuItem) {
            return ((MenuItem) obj).getDisplayOrder();
        } else if (obj instanceof Category) {
            return ((Category) obj).getDisplayOrder();
        } else if (obj instanceof Variant) {
            return ((Variant) obj).getDisplayOrder();
        }
        throw new IllegalArgumentException("Unsupported type");
    }

    private List<MenuItem> prepareMenuItems() {
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setDisplayOrder(3);
        MenuItem menuItem2 = new MenuItem();
        menuItem2.setDisplayOrder(2);
        MenuItem menuItem3 = new MenuItem();
        menuItem3.setDisplayOrder(1);
        return List.of(menuItem1, menuItem2, menuItem3);
    }

    private List<Category> prepareCategories() {
        Category category1 = new Category();
        category1.setDisplayOrder(42);
        Category category2 = new Category();
        category2.setDisplayOrder(12);
        Category category3 = new Category();
        category3.setDisplayOrder(-300);
        return List.of(category1, category2, category3);
    }

    private List<Variant> prepareVariants() {
        Variant variant1 = new Variant();
        variant1.setDisplayOrder(Integer.MIN_VALUE);
        Variant variant2 = new Variant();
        variant2.setDisplayOrder(Integer.MAX_VALUE);
        Variant variant3 = new Variant();
        variant3.setDisplayOrder(4597);
        return List.of(variant1, variant2, variant3);
    }

}