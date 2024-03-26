package pl.rarytas.rarytas_restaurantside.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CategoryServiceImpTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    void shouldFindAll() {
        List<Category> categories = categoryService.findAll();
        assertEquals(8, categories.size());
    }

    @Test
    void shouldFindById() throws LocalizedException {
        Category category = categoryService.findById(1);
        assertEquals("Przystawki", category.getName());
    }

    @Test
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> categoryService.findById(321));
    }

    @Test
    public void shouldReturnAll() {
        List<Category> categories = List.of(
                createCategory("Przystawki", "Rozpocznij swoją kulinarną podróż..."),
                createCategory("Makarony", "Ciesz się smakiem Włoch dzięki naszym wyśmienitym makaronom."),
                createCategory("Sałatki", "Zachwyć swoje zmysły zdrowymi i świeżymi sałatkami."),
                createCategory("Zupy", "Rozgrzej się pysznymi zupami."),
                createCategory("Pizza", "Zanurz się w prawdziwym smaku pizzy."),
                createCategory("Wegetariańskie", "Odkryj różnorodność wegetariańskich smaków."),
                createCategory("Dla dzieci", "Zaspokój apetyt najmłodszych przyjemnymi dla podniebienia daniami."),
                createCategory("Napoje", "Uzupełnij swoje doznania smakowe pysznymi napojami."));
        assertEquals(categories.toString(), getCategories().toString());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldInsertNew() throws LocalizedException {
        Category newCategory = createCategory("Tajskie", "Ostre, orientalne posiłki prosto z dalekiego kraju");
        categoryService.save(newCategory);
        Category category = categoryService.findById(newCategory.getId());
        assertEquals("Tajskie", category.getName());
    }

    @Test
    public void shouldNotInsertNew() {
        Category category = new Category();

        category.setName("");
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));

        category.setName(null);
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));

        category.setName("Test");
        category.setDescription("Seven11");
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));

        category.setDescription("""
                Indulge your senses in a culinary journey with our exquisite range of delectable delights, carefully curated to tantalize your taste buds and satisfy your cravings. From savory appetizers that awaken your palate to sumptuous main courses that embody culinary perfection, our food category is a symphony of flavors, textures, and aromas. Immerse yourself in the rich tapestry of global cuisines, where each dish is a masterpiece crafted with precision and passion.
                Explore the vibrant tapestry of tastes, from the spicy and aromatic profiles of Asian cuisine to the comforting familiarity of classic Western dishes. Our menu is a celebration of fresh, locally sourced ingredients that elevate every bite, ensuring a harmonious blend of nutrition and indulgence. Whether you seek the bold and spicy, the subtle and sophisticated, or the comforting and hearty, our diverse selection caters to every palate and preference.
                Elevate your dining experience with our artisanal desserts, where sweetness meets innovation. Savor the velvety textures, decadent flavors, and artistic presentations that transform each dessert into a work of edible art. From traditional favorites to avant-garde creations, our dessert collection is a testament to the creativity and skill of our culinary artisans.
                Not just a meal, but a sensorial experience, our food category goes beyond nourishment, offering a symphony of tastes and aromas that transport you to gastronomic bliss. Imbued with a commitment to quality and culinary excellence, our offerings are designed to redefine your dining experience, leaving an indelible mark on your culinary memories. Embrace the extraordinary, savor the exceptional – welcome to a world where food is an art form, and every bite tells a story.
                """);
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdate() throws LocalizedException {
        Category existingCategory = categoryService.findById(7);
        existingCategory.setName("Testowe jedzenie");
        categoryService.save(existingCategory);
        Category updatedCategory = categoryService.findById(7);
        assertEquals("Testowe jedzenie", updatedCategory.getName());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldDelete() throws LocalizedException {
        categoryService.delete(7);
        assertThrows(LocalizedException.class, () -> categoryService.findById(7));
    }

    @Test
    public void shouldNotDelete() {
        assertThrows(DataIntegrityViolationException.class, () -> categoryService.delete(1));
    }

    private List<Category> getCategories() {
        return categoryService.findAll();
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}