package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MenuDeepCopyMapperTest {

    private MenuDeepCopyMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MenuDeepCopyMapper.class);
    }

    @Test
    void deepCopyTranslatable_shouldProduceIndependentCopy_forAllLanguages() {
        Translatable orig = new Translatable()
                .withPl("foo-pl")
                .withEn("foo-en")
                .withFr("foo-fr")
                .withDe("foo-de")
                .withEs("foo-es")
                .withUk("foo-uk");

        Translatable dup = mapper.deepCopyTranslatable(orig);

        assertNotNull(dup);
        assertNotSame(orig, dup);

        assertEquals("foo-pl", dup.getPl());
        assertEquals("foo-en", dup.getEn());
        assertEquals("foo-fr", dup.getFr());
        assertEquals("foo-de", dup.getDe());
        assertEquals("foo-es", dup.getEs());
        assertEquals("foo-uk", dup.getUk());

        orig.setPl("changed-pl");
        orig.setEn("changed-en");
        orig.setFr("changed-fr");
        orig.setDe("changed-de");
        orig.setEs("changed-es");
        orig.setUk("changed-uk");

        assertEquals("foo-pl", dup.getPl());
        assertEquals("foo-en", dup.getEn());
        assertEquals("foo-fr", dup.getFr());
        assertEquals("foo-de", dup.getDe());
        assertEquals("foo-es", dup.getEs());
        assertEquals("foo-uk", dup.getUk());
    }

    @Test
    void duplicateMenu_shouldDeepCopyAllLevels_forAllLanguages() {
        Translatable menuMsg = new Translatable()
                .withPl("menu-pl")
                .withEn("menu-en")
                .withFr("menu-fr")
                .withDe("menu-de")
                .withEs("menu-es")
                .withUk("menu-uk");

        Translatable catName = new Translatable()
                .withPl("cat-pl")
                .withEn("cat-en")
                .withFr("cat-fr")
                .withDe("cat-de")
                .withEs("cat-es")
                .withUk("cat-uk");

        Translatable itemName = new Translatable()
                .withPl("item-pl")
                .withEn("item-en")
                .withFr("item-fr")
                .withDe("item-de")
                .withEs("item-es")
                .withUk("item-uk");

        Translatable itemDesc = new Translatable()
                .withPl("desc-pl")
                .withEn("desc-en")
                .withFr("desc-fr")
                .withDe("desc-de")
                .withEs("desc-es")
                .withUk("desc-uk");

        Translatable varName = new Translatable()
                .withPl("var-pl")
                .withEn("var-en")
                .withFr("var-fr")
                .withDe("var-de")
                .withEs("var-es")
                .withUk("var-uk");

        Variant srcVar = new Variant();
        srcVar.setId(99L);
        srcVar.setName(varName);

        MenuItem srcItem = new MenuItem();
        srcItem.setId(77L);
        srcItem.setName(itemName);
        srcItem.setDescription(itemDesc);
        srcItem.setVariants(new ArrayList<>(List.of(srcVar)));

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
        assertNotSame(srcMenu.getMessage(), copy.getMessage());
        assertEquals("menu-pl", copy.getMessage().getPl());
        assertEquals("menu-en", copy.getMessage().getEn());
        assertEquals("menu-fr", copy.getMessage().getFr());
        assertEquals("menu-de", copy.getMessage().getDe());
        assertEquals("menu-es", copy.getMessage().getEs());
        assertEquals("menu-uk", copy.getMessage().getUk());

        assertNotSame(srcMenu.getCategories(), copy.getCategories());
        assertEquals(1, copy.getCategories().size());
        Category copyCat = copy.getCategories().iterator().next();
        assertNull(copyCat.getId());
        assertNull(copyCat.getMenu());
        assertNotSame(srcCat, copyCat);

        assertNotSame(srcCat.getName(), copyCat.getName());
        assertEquals("cat-pl", copyCat.getName().getPl());
        assertEquals("cat-en", copyCat.getName().getEn());
        assertEquals("cat-fr", copyCat.getName().getFr());
        assertEquals("cat-de", copyCat.getName().getDe());
        assertEquals("cat-es", copyCat.getName().getEs());
        assertEquals("cat-uk", copyCat.getName().getUk());

        assertNotNull(copyCat.getMenuItems());
        assertEquals(1, copyCat.getMenuItems().size());
        MenuItem copyItem = copyCat.getMenuItems().iterator().next();
        assertNull(copyItem.getId());
        assertNull(copyItem.getCategory());
        assertNotSame(srcItem, copyItem);

        assertNotSame(srcItem.getName(), copyItem.getName());
        assertEquals("item-pl", copyItem.getName().getPl());
        assertEquals("item-en", copyItem.getName().getEn());
        assertEquals("item-fr", copyItem.getName().getFr());
        assertEquals("item-de", copyItem.getName().getDe());
        assertEquals("item-es", copyItem.getName().getEs());
        assertEquals("item-uk", copyItem.getName().getUk());

        assertNotSame(srcItem.getDescription(), copyItem.getDescription());
        assertEquals("desc-pl", copyItem.getDescription().getPl());
        assertEquals("desc-en", copyItem.getDescription().getEn());
        assertEquals("desc-fr", copyItem.getDescription().getFr());
        assertEquals("desc-de", copyItem.getDescription().getDe());
        assertEquals("desc-es", copyItem.getDescription().getEs());
        assertEquals("desc-uk", copyItem.getDescription().getUk());

        assertNotNull(copyItem.getVariants());
        assertEquals(1, copyItem.getVariants().size());
        Variant copyVar = copyItem.getVariants().getFirst();
        assertNull(copyVar.getId());
        assertNull(copyVar.getMenuItem());
        assertNotSame(srcVar, copyVar);

        assertNotSame(srcVar.getName(), copyVar.getName());
        assertEquals("var-pl", copyVar.getName().getPl());
        assertEquals("var-en", copyVar.getName().getEn());
        assertEquals("var-fr", copyVar.getName().getFr());
        assertEquals("var-de", copyVar.getName().getDe());
        assertEquals("var-es", copyVar.getName().getEs());
        assertEquals("var-uk", copyVar.getName().getUk());

        srcVar.getName().setPl("hacked-pl");
        srcVar.getName().setUk("hacked-uk");
        assertEquals("var-pl", copyVar.getName().getPl());
        assertEquals("var-uk", copyVar.getName().getUk());
    }
}
