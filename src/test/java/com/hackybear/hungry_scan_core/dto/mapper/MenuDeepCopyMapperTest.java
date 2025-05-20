package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MenuDeepCopyMapperTest {

    private MenuDeepCopyMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MenuDeepCopyMapper.class);
    }

    @Test
    void duplicateMenu_shouldDeepCopyAllLevels() {
        Translatable menuMsg = new Translatable();
        menuMsg.setDefaultTranslation("menu-default");
        menuMsg.setTranslationEn("menu-en");

        Translatable catName = new Translatable();
        catName.setDefaultTranslation("cat-default");
        catName.setTranslationEn("cat-en");

        Translatable itemName = new Translatable();
        itemName.setDefaultTranslation("item-default");
        itemName.setTranslationEn("item-en");
        Translatable itemDesc = new Translatable();
        itemDesc.setDefaultTranslation("desc-default");
        itemDesc.setTranslationEn("desc-en");

        Translatable varName = new Translatable();
        varName.setDefaultTranslation("var-default");
        varName.setTranslationEn("var-en");

        Variant srcVar = new Variant();
        srcVar.setId(99L);
        srcVar.setName(varName);

        MenuItem srcItem = new MenuItem();
        srcItem.setId(77L);
        srcItem.setName(itemName);
        srcItem.setDescription(itemDesc);
        srcItem.setVariants(new LinkedHashSet<>(Set.of(srcVar)));

        Category srcCat = new Category();
        srcCat.setId(55L);
        srcCat.setName(catName);
        srcCat.setMenuItems(new LinkedHashSet<>(Set.of(srcItem)));

        Menu srcMenu = new Menu();
        srcMenu.setId(33L);
        srcMenu.setRestaurant(new Restaurant());
        srcMenu.setStandard(true);
        srcMenu.setName("original-name");
        srcMenu.setMessage(menuMsg);
        srcMenu.setCategories(new LinkedHashSet<>(Set.of(srcCat)));

        Menu copy = mapper.duplicateMenu(srcMenu);

        assertNull(copy.getId(), "id should be ignored");
        assertNull(copy.getRestaurant(), "restaurant should be ignored");
        assertFalse(copy.isStandard(), "standard flag should be ignored");
        assertNull(copy.getName(), "name should be ignored");

        assertNotNull(copy.getMessage());
        assertNotSame(srcMenu.getMessage(), copy.getMessage(), "should be a new Translatable");
        assertEquals("menu-default", copy.getMessage().getDefaultTranslation());
        assertEquals("menu-en", copy.getMessage().getTranslationEn());

        assertNotSame(srcMenu.getCategories(), copy.getCategories());
        assertEquals(1, copy.getCategories().size());

        Category copyCat = copy.getCategories().iterator().next();
        assertNull(copyCat.getId());
        assertNull(copyCat.getMenu(), "back‐reference should be ignored");
        assertNotSame(srcCat, copyCat);
        assertNotSame(srcCat.getName(), copyCat.getName());
        assertEquals("cat-default", copyCat.getName().getDefaultTranslation());
        assertEquals("cat-en", copyCat.getName().getTranslationEn());

        assertNotNull(copyCat.getMenuItems());
        assertEquals(1, copyCat.getMenuItems().size());
        MenuItem copyItem = copyCat.getMenuItems().iterator().next();
        assertNull(copyItem.getId());
        assertNull(copyItem.getCategory());
        assertNotSame(srcItem, copyItem);

        assertNotSame(srcItem.getName(), copyItem.getName());
        assertEquals("item-default", copyItem.getName().getDefaultTranslation());
        assertEquals("item-en", copyItem.getName().getTranslationEn());

        assertNotSame(srcItem.getDescription(), copyItem.getDescription());
        assertEquals("desc-default", copyItem.getDescription().getDefaultTranslation());
        assertEquals("desc-en", copyItem.getDescription().getTranslationEn());

        assertNotNull(copyItem.getVariants());
        assertEquals(1, copyItem.getVariants().size());
        Variant copyVar = copyItem.getVariants().iterator().next();
        assertNull(copyVar.getId());
        assertNull(copyVar.getMenuItem());
        assertNotSame(srcVar, copyVar);
        assertNotSame(srcVar.getName(), copyVar.getName());
        assertEquals("var-default", copyVar.getName().getDefaultTranslation());
        assertEquals("var-en", copyVar.getName().getTranslationEn());

        srcVar.getName().setDefaultTranslation("hacked");
        assertEquals("var-default", copyVar.getName().getDefaultTranslation());
    }

    @Test
    void deepCopyTranslatable_shouldProduceIndependentCopy() {
        Translatable orig = new Translatable();
        orig.setDefaultTranslation("foo");
        orig.setTranslationEn("bar");

        Translatable dup = mapper.deepCopyTranslatable(orig);

        assertNotNull(dup);
        assertNotSame(orig, dup);
        assertEquals("foo", dup.getDefaultTranslation());
        assertEquals("bar", dup.getTranslationEn());

        orig.setDefaultTranslation("changed");
        assertEquals("foo", dup.getDefaultTranslation());
    }
}
