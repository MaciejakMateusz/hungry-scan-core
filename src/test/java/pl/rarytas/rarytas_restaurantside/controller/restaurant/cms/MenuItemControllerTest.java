package pl.rarytas.rarytas_restaurantside.controller.restaurant.cms;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenuItemControllerTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MenuItemService menuItemService;

    @Test
    @Order(1)
    public void shouldReturnAll() {
        List<MenuItem> menuItems = List.of(
                createMenuItem(1, "Krewetki marynowane w cytrynie", 1, "Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.",
                        "Krewetki, cytryna, oliwa z oliwek, czosnek, przyprawy", new BigDecimal("19.99")),

                createMenuItem(2, "Carpaccio z polędwicy wołowej", 1,
                        "Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.",
                        "Polędwica wołowa, rukola, parmezan, kapary, oliwa z oliwek", new BigDecimal("24.50")),

                createMenuItem(3, "Krewetki w tempurze", 1, "Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym",
                        "Krewetki, mąka, jajko, olej roślinny, sos słodko-kwaśny", new BigDecimal("22.00")),

                createMenuItem(4, "Roladki z bakłażana z feta i suszonymi pomidorami", 1,
                        "Bakłażany zawijane w roladki z feta i suszonymi pomidorami, pieczone w piecu.",
                        "Bakłażan, ser feta, suszone pomidory, oliwa z oliwek", new BigDecimal("18.75")),

                createMenuItem(5, "Nachos z sosem serowym", 1,
                        "Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.",
                        "Nachos, ser, śmietana, awokado, pomidory, cebula, papryczki chili", new BigDecimal("16.99")),

                createMenuItem(6, "Spaghetti Bolognese", 2,
                        "Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.",
                        "Spaghetti, mięso mielone, pomidory, cebula, marchewka, seler, przyprawy", new BigDecimal("24.00")),

                createMenuItem(7, "Penne Carbonara", 2,
                        "Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.",
                        "Penne, boczek, jajka, ser parmezan, śmietana, czosnek", new BigDecimal("22.50")),

                createMenuItem(8, "Lasagne warzywna", 2,
                        "Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.",
                        "Makaron lasagne, pomidory, cukinia, bakłażan, beszamel, ser", new BigDecimal("23.25")),

                createMenuItem(9, "Tagliatelle z łososiem i szpinakiem", 2,
                        "Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.",
                        "Tagliatelle, łosoś, szpinak, śmietana, czosnek, cebula", new BigDecimal("26.50")),

                createMenuItem(10, "Rigatoni z kurczakiem i brokułami", 2,
                        "Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.",
                        "Rigatoni, kurczak, brokuły, śmietana, cebula, bulion", new BigDecimal("21.75")),

                createMenuItem(11, "Sałatka grecka", 3,
                        "Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.",
                        "Pomidory, ogórki, cebula, oliwki, ser feta, oliwa, oregano", new BigDecimal("18.50")),

                createMenuItem(12, "Sałatka z grillowanym kurczakiem i awokado", 3,
                        "Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.",
                        "Kurczak, awokado, pomidory, sałata, orzechy, sos vinegrette", new BigDecimal("20.75")),

                createMenuItem(13, "Sałatka z rukolą, serem kozim i suszonymi żurawinami", 3,
                        "Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.",
                        "Rukola, ser kozi, żurawiny, orzechy włoskie, dressing balsamiczny", new BigDecimal("22.00")),

                createMenuItem(14, "Sałatka z grillowanym ananasem i kurczakiem", 3,
                        "Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.",
                        "Ananas, kurczak, sałata mieszana, cebula, sos winegret", new BigDecimal("21.50")),

                createMenuItem(15, "Sałatka z quinoa i pieczonymi warzywami", 3,
                        "Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.",
                        "Quinoa, marchewki, buraki, suszone pomidory, mix sałat", new BigDecimal("23.75")),

                createMenuItem(16, "Krem z pomidorów", 4,
                        "Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.",
                        "Pomidory, cebula, czosnek, śmietana, bazylia", new BigDecimal("15.50")),

                createMenuItem(17, "Rosołek z kury", 4,
                        "Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.",
                        "Rosół z kury, marchew, pietruszka, seler, makaron", new BigDecimal("16.25")),

                createMenuItem(18, "Zupa krem z dyni", 4,
                        "Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.",
                        "Dynia, cebula, czosnek, bulion warzywny, cynamon, pestki dyni", new BigDecimal("17.50")),

                createMenuItem(19, "Zupa pomidorowa z ryżem", 4,
                        "Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.",
                        "Pomidory, ryż, cebula, czosnek, koper", new BigDecimal("15.75")),

                createMenuItem(20, "Zupa krem z brokułów", 4,
                        "Delikatny krem z zielonych brokułów podawany z grzankami.",
                        "Brokuły, cebula, czosnek, śmietana, grzanki", new BigDecimal("18.00")),

                createMenuItem(21, "Pizza Margherita", 5,
                        "Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.",
                        "Sos pomidorowy, mozzarella, bazylia", new BigDecimal("26.00")),

                createMenuItem(22, "Pizza Pepperoni", 5,
                        "Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.",
                        "Sos pomidorowy, mozzarella, pepperoni, papryka", new BigDecimal("28.50")),

                createMenuItem(23, "Pizza Capricciosa", 5,
                        "Pizza z szynką, pieczarkami, karczochami i oliwkami.",
                        "Sos pomidorowy, mozzarella, szynka, pieczarki, karczochy, oliwki", new BigDecimal("30.25")),

                createMenuItem(24, "Pizza Hawajska", 5,
                        "Pizza z szynką, ananasem i kukurydzą.",
                        "Sos pomidorowy, mozzarella, szynka, ananas, kukurydza", new BigDecimal("27.75")),

                createMenuItem(25, "Pizza Quattro Formaggi", 5,
                        "Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.",
                        "Sos pomidorowy, mozzarella, gorgonzola, parmezan, camembert", new BigDecimal("29.00")),

                createMenuItem(26, "Risotto z grzybami leśnymi", 6,
                        "Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.",
                        "Ryż, grzyby leśne, cebula, czosnek, bulion warzywny, parmezan", new BigDecimal("24.50")),

                createMenuItem(27, "Curry z ciecierzycą i szpinakiem", 6,
                        "Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.",
                        "Ciecierzyca, szpinak, pomidory, mleko kokosowe, curry", new BigDecimal("22.75")),

                createMenuItem(28, "Naleśniki z serem i szpinakiem", 6,
                        "Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.",
                        "Naleśniki, ser, szpinak, sos pomidorowy", new BigDecimal("20.00")),

                createMenuItem(29, "Falafel w pitce z hummusem", 6,
                        "Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.",
                        "Falafel, pita, hummus, pomidory, ogórki, cebula", new BigDecimal("21.25")),

                createMenuItem(30, "Makaron z pesto bazyliowym", 6,
                        "Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.",
                        "Spaghetti, pesto bazyliowe, parmezan, orzechy włoskie", new BigDecimal("23.00")),

                createMenuItem(31, "Mini hamburgery z frytkami", 7,
                        "Dwa mini hamburgerki z wołowiną, serem, sałatą i frytkami.",
                        "Wołowina, bułka, ser, sałata, ziemniaki", new BigDecimal("18.75")),

                createMenuItem(32, "Kurczak w panierce z ketchupem", 7,
                        "Kruszone kawałki kurczaka podawane z ketchupem i plasterkami marchewki.",
                        "Kurczak, panierka, ketchup, marchewka", new BigDecimal("17.50")),

                createMenuItem(33, "Kanapki z nutellą i bananem", 7,
                        "Puchate kanapki z kremową nutellą i plasterkami świeżego banana.",
                        "Chleb, Nutella, banany", new BigDecimal("14.00")),

                createMenuItem(34, "Ryż z warzywami i jajkiem sadzonym", 7,
                        "Delikatny ryż z kawałkami warzyw i jajkiem sadzonym.",
                        "Ryż, marchew, groszek, kukurydza, jajko.", new BigDecimal("16.25")),

                createMenuItem(35, "Mini pizza z szynką i serem", 7,
                        "Mała pizza z szynką, serem i pomidorowym sosem.",
                        "Ciasto na pizzę, szynka, ser, sos pomidorowy", new BigDecimal("15.50")),

                createMenuItem(36, "Lemoniada cytrynowa", 8,
                        "Orzeźwiająca lemoniada z dodatkiem cytryny i mięty.",
                        "Woda, cytryna, cukier, mięta", new BigDecimal("8.50")),

                createMenuItem(37, "Koktajl truskawkowy", 8,
                        "Pyszny koktajl z truskawkami, jogurtem i miodem.",
                        "Truskawki, jogurt, miód", new BigDecimal("9.75")),

                createMenuItem(38, "Smoothie z mango i bananem", 8,
                        "Płynny smoothie z dojrzałym mango i bananem.",
                        "Mango, banan, sok pomarańczowy", new BigDecimal("10.00")),

                createMenuItem(39, "Piwo rzemieślnicze IPA", 8,
                        "Zrównoważone piwo rzemieślnicze typu IPA.",
                        "Jęczmień, chmiele, woda", new BigDecimal("12.50")),

                createMenuItem(40, "Herbata zielona", 8,
                        "Zdrowa herbata zielona, lekko zaparzona.",
                        "Liście herbaty zielonej", new BigDecimal("7.25")));
        assertEquals(40, getMenuItems().size());
        assertEquals(menuItems.toString(), getMenuItems().toString(), assertEqualsMessage(menuItems.toString(), getMenuItems().toString()));
    }

    @Test
    @Order(2)
    public void shouldInsertNew() throws IOException {
        MenuItem newMenuItem = createMenuItem("Burger", 2, "Z mięsem wegańskim", "Bułka, mięso sojowe, sałata, ogórek konserwowy, chrzan żurawinowy", BigDecimal.valueOf(20.00));
        menuItemService.save(newMenuItem, null);
        MenuItem menuItem = menuItemService.findById(newMenuItem.getId()).orElse(new MenuItem());
        assertEquals(newMenuItem.getId(), menuItem.getId(), assertEqualsMessage(newMenuItem.getId(), menuItem.getId()));
    }

    @Test
    public void shouldNotInsertNew() {
        MenuItem menuItem = createMenuItem("Burger", 2, "Z mięsem wegańskim", "Bułka, mięso sojowe, sałata, ogórek konserwowy, chrzan żurawinowy", BigDecimal.valueOf(20.00));;

        menuItem.setName("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setName(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setName("Test");
        menuItem.setPrice(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setPrice(BigDecimal.valueOf(0.5));
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setDescription("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setDescription(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));
    }

    @Test
    @Order(3)
    public void shouldUpdate() throws IOException {
        MenuItem existingMenuItem = menuItemService.findById(41).orElse(new MenuItem());
        existingMenuItem.setName("Burger wege");
        menuItemService.save(existingMenuItem, null);
        MenuItem updatedMenuItem = menuItemService.findById(41).orElse(new MenuItem());
        assertEquals("Burger wege", updatedMenuItem.getName(), assertEqualsMessage("Burger wege", updatedMenuItem.getName()));
    }

    @Test
    @Order(4)
    public void shouldDelete() {
        MenuItem menuItem = menuItemService.findById(41).orElseThrow();
        assertEquals("Burger wege", menuItem.getName(), assertEqualsMessage("Burger wege", menuItem.getName()));
        menuItemService.delete(menuItem);
        assertThrows(NoSuchElementException.class, () -> menuItemService.findById(41).orElseThrow());
    }

    private List<MenuItem> getMenuItems() {
        return menuItemService.findAll();
    }

    private MenuItem createMenuItem(int id,
                                    String name,
                                    int categoryId,
                                    String description,
                                    String ingredients,
                                    BigDecimal price) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(id);
        menuItem.setName(name);
        menuItem.setCategory(categoryService.findById(categoryId).orElseThrow());
        menuItem.setDescription(description);
        menuItem.setIngredients(ingredients);
        menuItem.setPrice(price);
        return menuItem;
    }

    private MenuItem createMenuItem(String name,
                                    int categoryId,
                                    String description,
                                    String ingredients,
                                    BigDecimal price) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setCategory(categoryService.findById(categoryId).orElseThrow());
        menuItem.setDescription(description);
        menuItem.setIngredients(ingredients);
        menuItem.setPrice(price);
        return menuItem;
    }

    private String assertEqualsMessage(String expectedOutput, String input) {
        return "Result expected was " + expectedOutput + " but got " + input;
    }

    private String assertEqualsMessage(int expectedOutput, int input) {
        return "Result expected was " + expectedOutput + " but got " + input;
    }
}