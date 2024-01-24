package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestManuItemRestController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuItemService menuItemService;

    @Test
    public void shouldGetAllFromDB() {
        List<MenuItem> menuItems = menuItemService.findAll();
        assertEquals(40, menuItems.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/items")).andReturn();
        String actualMenuItemJson = result.getResponse().getContentAsString();
        assertEquals("[{\"id\":1,\"name\":\"Krewetki marynowane w cytrynie\",\"description\":\"Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.\",\"ingredients\":\"Krewetki, cytryna, oliwa z oliwek, czosnek, przyprawy\",\"price\":19.99,\"created\":\"2023-08-02 04:05:13\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":2,\"name\":\"Carpaccio z polÄ\u0099dwicy woÅ\u0082owej\",\"description\":\"Cienko pokrojona polÄ\u0099dwica woÅ\u0082owa podana z rukolÄ\u0085, parmezanem i kaparami.\",\"ingredients\":\"PolÄ\u0099dwica woÅ\u0082owa, rukola, parmezan, kapary, oliwa z oliwek\",\"price\":24.50,\"created\":\"2023-08-02 04:06:03\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":\"2023-08-02 04:06:24\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":4,\"name\":\"Roladki z bakÅ\u0082aÅ¼ana z feta i suszonymi pomidorami\",\"description\":\"BakÅ\u0082aÅ¼any zawijane w roladki z feta i suszonymi pomidorami, pieczone w piecu.\",\"ingredients\":\"BakÅ\u0082aÅ¼an, ser feta, suszone pomidory, oliwa z oliwek\",\"price\":18.75,\"created\":\"2023-08-02 04:06:47\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":5,\"name\":\"Nachos z sosem serowym\",\"description\":\"ChrupiÄ\u0085ce nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.\",\"ingredients\":\"Nachos, ser, Å\u009Bmietana, awokado, pomidory, cebula, papryczki chili\",\"price\":16.99,\"created\":\"2023-08-02 04:07:09\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":6,\"name\":\"Spaghetti Bolognese\",\"description\":\"DÅ\u0082ugie spaghetti podane z aromatycznym sosem bolognese na bazie miÄ\u0099sa mielonego i pomidorÃ³w.\",\"ingredients\":\"Spaghetti, miÄ\u0099so mielone, pomidory, cebula, marchewka, seler, przyprawy\",\"price\":24.00,\"created\":\"2023-08-02 04:07:39\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":7,\"name\":\"Penne Carbonara\",\"description\":\"Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i Å\u009Bmietanki.\",\"ingredients\":\"Penne, boczek, jajka, ser parmezan, Å\u009Bmietana, czosnek\",\"price\":22.50,\"created\":\"2023-08-02 04:08:00\",\"updated\":\"2023-08-02 04:31:49\",\"base64Image\":\"empty\",\"available\":true},{\"id\":8,\"name\":\"Lasagne warzywna\",\"description\":\"Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.\",\"ingredients\":\"Makaron lasagne, pomidory, cukinia, bakÅ\u0082aÅ¼an, beszamel, ser\",\"price\":23.25,\"created\":\"2023-08-02 04:08:45\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":9,\"name\":\"Tagliatelle z Å\u0082ososiem i szpinakiem\",\"description\":\"Cienkie tagliatelle z kawaÅ\u0082kami Å\u0082ososia i Å\u009BwieÅ¼ym szpinakiem w sosie Å\u009Bmietanowym.\",\"ingredients\":\"Tagliatelle, Å\u0082osoÅ\u009B, szpinak, Å\u009Bmietana, czosnek, cebula\",\"price\":26.50,\"created\":\"2023-08-02 04:09:05\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":10,\"name\":\"Rigatoni z kurczakiem i brokuÅ\u0082ami\",\"description\":\" Rurki rigatoni z duszonym kurczakiem, brokuÅ\u0082ami i sosem Å\u009Bmietanowym.\",\"ingredients\":\"Rigatoni, kurczak, brokuÅ\u0082y, Å\u009Bmietana, cebula, bulion\",\"price\":21.75,\"created\":\"2023-08-02 04:09:28\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":11,\"name\":\"SaÅ\u0082atka grecka\",\"description\":\"Tradycyjna grecka saÅ\u0082atka z pomidorami, ogÃ³rkiem, cebulÄ\u0085, oliwkami, serem feta i sosem vinegrette.\",\"ingredients\":\"Pomidory, ogÃ³rki, cebula, oliwki, ser feta, oliwa, oregano\",\"price\":18.50,\"created\":\"2023-08-02 04:10:03\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":12,\"name\":\"SaÅ\u0082atka z grillowanym kurczakiem i awokado\",\"description\":\"SaÅ\u0082atka z grillowanymi kawaÅ\u0082kami kurczaka, awokado, pomidorami i orzechami.\",\"ingredients\":\"Kurczak, awokado, pomidory, saÅ\u0082ata, orzechy, sos vinegrette\",\"price\":20.75,\"created\":\"2023-08-02 04:10:28\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":13,\"name\":\"SaÅ\u0082atka z rukolÄ\u0085, serem kozim i suszonymi Å¼urawinami\",\"description\":\"Å\u009AwieÅ¼a rukola z serem kozim, praÅ¼onymi orzechami wÅ\u0082oskimi i suszonymi Å¼urawinami.\",\"ingredients\":\"Rukola, ser kozi, Å¼urawiny, orzechy wÅ\u0082oskie, dressing balsamiczny\",\"price\":22.00,\"created\":\"2023-08-02 04:11:14\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":14,\"name\":\"SaÅ\u0082atka z grillowanym ananasem i kurczakiem\",\"description\":\"SaÅ\u0082atka z soczystym grillowanym ananasem, kawaÅ\u0082kami kurczaka i mieszankÄ\u0085 saÅ\u0082at.\",\"ingredients\":\"Ananas, kurczak, saÅ\u0082ata mieszana, cebula, sos winegret\",\"price\":21.50,\"created\":\"2023-08-02 04:12:08\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":15,\"name\":\"SaÅ\u0082atka z quinoa i pieczonymi warzywami\",\"description\":\"SaÅ\u0082atka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.\",\"ingredients\":\"Quinoa, marchewki, buraki, suszone pomidory, mix saÅ\u0082at\",\"price\":23.75,\"created\":\"2023-08-02 04:12:39\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":16,\"name\":\"Krem z pomidorÃ³w\",\"description\":\"GÅ\u0082adki krem z pomidorÃ³w z dodatkiem Å\u009Bmietany i Å\u009BwieÅ¼ego bazylia.\",\"ingredients\":\"Pomidory, cebula, czosnek, Å\u009Bmietana, bazylia\",\"price\":15.50,\"created\":\"2023-08-02 04:13:07\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":17,\"name\":\"RosoÅ\u0082ek z kury\",\"description\":\"Tradycyjny rosÃ³Å\u0082 z kury z makaronem, warzywami i natkÄ\u0085 pietruszki.\",\"ingredients\":\"RosÃ³Å\u0082 z kury, marchew, pietruszka, seler, makaron\",\"price\":16.25,\"created\":\"2023-08-02 04:13:35\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":18,\"name\":\"Zupa krem z dyni\",\"description\":\"Kremowa zupa z dyni z nutÄ\u0085 cynamonu i praÅ¼onymi pestkami dyni.\",\"ingredients\":\"Dynia, cebula, czosnek, bulion warzywny, cynamon, pestki dyni\",\"price\":17.50,\"created\":\"2023-08-02 04:14:02\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":19,\"name\":\"Zupa pomidorowa z ryÅ¼em\",\"description\":\"Zupa pomidorowa z dodatkiem ryÅ¼u i Å\u009BwieÅ¼ego kopru.\",\"ingredients\":\"Pomidory, ryÅ¼, cebula, czosnek, koper\",\"price\":15.75,\"created\":\"2023-08-02 04:14:33\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":20,\"name\":\"Zupa krem z brokuÅ\u0082Ã³w\",\"description\":\"Delikatny krem z zielonych brokuÅ\u0082Ã³w podawany z grzankami.\",\"ingredients\":\"BrokuÅ\u0082y, cebula, czosnek, Å\u009Bmietana, grzanki\",\"price\":18.00,\"created\":\"2023-08-02 04:14:58\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":21,\"name\":\"Pizza Margherita\",\"description\":\"Klasyka wÅ\u0082oskiej kuchni - sos pomidorowy, mozzarella i Å\u009BwieÅ¼a bazylia.\",\"ingredients\":\"Sos pomidorowy, mozzarella, bazylia\",\"price\":26.00,\"created\":\"2023-08-02 04:15:37\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":22,\"name\":\"Pizza Pepperoni\",\"description\":\"Pizza z pikantnym salami pepperoni, serem mozzarella i paprykÄ\u0085.\",\"ingredients\":\"Sos pomidorowy, mozzarella, pepperoni, papryka\",\"price\":28.50,\"created\":\"2023-08-02 04:16:06\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":23,\"name\":\"Pizza Capricciosa\",\"description\":\"Pizza z szynkÄ\u0085, pieczarkami, karczochami i oliwkami.\",\"ingredients\":\"Sos pomidorowy, mozzarella, szynka, pieczarki, karczochy, oliwki\",\"price\":30.25,\"created\":\"2023-08-02 04:16:29\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":24,\"name\":\"Pizza Hawajska\",\"description\":\"Pizza z szynkÄ\u0085, ananasem i kukurydzÄ\u0085.\",\"ingredients\":\"Sos pomidorowy, mozzarella, szynka, ananas, kukurydza\",\"price\":27.75,\"created\":\"2023-08-02 04:16:52\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":25,\"name\":\"Pizza Quattro Formaggi\",\"description\":\"Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.\",\"ingredients\":\"Sos pomidorowy, mozzarella, gorgonzola, parmezan, camembert\",\"price\":29.00,\"created\":\"2023-08-02 04:17:15\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":26,\"name\":\"Risotto z grzybami leÅ\u009Bnymi\",\"description\":\"Klasyczne wÅ\u0082oskie risotto z wybornymi grzybami leÅ\u009Bnymi i parmezanem.\",\"ingredients\":\"RyÅ¼, grzyby leÅ\u009Bne, cebula, czosnek, bulion warzywny, parmezan\",\"price\":24.50,\"created\":\"2023-08-02 04:17:45\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":27,\"name\":\"Curry z ciecierzycÄ\u0085 i szpinakiem\",\"description\":\"Aromatyczne curry z ciecierzycÄ\u0085, Å\u009BwieÅ¼ym szpinakiem i mlekiem kokosowym.\",\"ingredients\":\"Ciecierzyca, szpinak, pomidory, mleko kokosowe, curry\",\"price\":22.75,\"created\":\"2023-08-02 04:18:14\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":28,\"name\":\"NaleÅ\u009Bniki z serem i szpinakiem\",\"description\":\"Delikatne naleÅ\u009Bniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.\",\"ingredients\":\"NaleÅ\u009Bniki, ser, szpinak, sos pomidorowy\",\"price\":20.00,\"created\":\"2023-08-02 04:18:42\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":29,\"name\":\"Falafel w pitce z hummusem\",\"description\":\"Smakowite kulki falafel w cieÅ\u009Bcie pita z sosem hummus i warzywami.\",\"ingredients\":\"Falafel, pita, hummus, pomidory, ogÃ³rki, cebula\",\"price\":21.25,\"created\":\"2023-08-02 04:20:01\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":30,\"name\":\"Makaron z pesto bazyliowym\",\"description\":\"Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i praÅ¼onymi orzechami.\",\"ingredients\":\"Spaghetti, pesto bazyliowe, parmezan, orzechy wÅ\u0082oskie\",\"price\":23.00,\"created\":\"2023-08-02 04:20:39\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":31,\"name\":\"Mini hamburgery z frytkami\",\"description\":\"Dwa mini hamburgerki z woÅ\u0082owinÄ\u0085, serem, saÅ\u0082atÄ\u0085 i frytkami.\",\"ingredients\":\"WoÅ\u0082owina, buÅ\u0082ka, ser, saÅ\u0082ata, ziemniaki\",\"price\":18.75,\"created\":\"2023-08-02 04:21:46\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":32,\"name\":\"Kurczak w panierce z ketchupem\",\"description\":\"Kruszone kawaÅ\u0082ki kurczaka podawane z ketchupem i plasterkami marchewki.\",\"ingredients\":\"Kurczak, panierka, ketchup, marchewka\",\"price\":17.50,\"created\":\"2023-08-02 04:22:06\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":33,\"name\":\"Kanapki z nutellÄ\u0085 i bananem\",\"description\":\"Puchate kanapki z kremowÄ\u0085 nutellÄ\u0085 i plasterkami Å\u009BwieÅ¼ego banana.\",\"ingredients\":\"Chleb, Nutella, banany\",\"price\":14.00,\"created\":\"2023-08-02 04:22:24\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":34,\"name\":\"RyÅ¼ z warzywami i jajkiem sadzonym\",\"description\":\"Delikatny ryÅ¼ z kawaÅ\u0082kami warzyw i jajkiem sadzonym.\",\"ingredients\":\"RyÅ¼, marchew, groszek, kukurydza, jajko.\",\"price\":16.25,\"created\":\"2023-08-02 04:22:57\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":35,\"name\":\"Mini pizza z szynkÄ\u0085 i serem\",\"description\":\"MaÅ\u0082a pizza z szynkÄ\u0085, serem i pomidorowym sosem.\",\"ingredients\":\"Ciasto na pizzÄ\u0099, szynka, ser, sos pomidorowy\",\"price\":15.50,\"created\":\"2023-08-02 04:23:19\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":36,\"name\":\"Lemoniada cytrynowa\",\"description\":\"OrzeÅºwiajÄ\u0085ca lemoniada z dodatkiem cytryny i miÄ\u0099ty.\",\"ingredients\":\"Woda, cytryna, cukier, miÄ\u0099ta\",\"price\":8.50,\"created\":\"2023-08-02 04:23:44\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":37,\"name\":\"Koktajl truskawkowy\",\"description\":\"Pyszny koktajl z truskawkami, jogurtem i miodem.\",\"ingredients\":\"Truskawki, jogurt, miÃ³d\",\"price\":9.75,\"created\":\"2023-08-02 04:24:04\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":38,\"name\":\"Smoothie z mango i bananem\",\"description\":\"PÅ\u0082ynny smoothie z dojrzaÅ\u0082ym mango i bananem.\",\"ingredients\":\"Mango, banan, sok pomaraÅ\u0084czowy\",\"price\":10.00,\"created\":\"2023-08-02 04:24:23\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":39,\"name\":\"Piwo rzemieÅ\u009Blnicze IPA\",\"description\":\"ZrÃ³wnowaÅ¼one piwo rzemieÅ\u009Blnicze typu IPA.\",\"ingredients\":\"JÄ\u0099czmieÅ\u0084, chmiele, woda\",\"price\":12.50,\"created\":\"2023-08-02 04:24:42\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},{\"id\":40,\"name\":\"Herbata zielona\",\"description\":\"Zdrowa herbata zielona, lekko zaparzona.\",\"ingredients\":\"LiÅ\u009Bcie herbaty zielonej\",\"price\":7.25,\"created\":\"2023-08-02 04:25:04\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true}]",
                actualMenuItemJson);
    }

    @Test
    public void shouldGetByIdFromDB() {
        MenuItem menuItem = menuItemService.findById(6).orElse(new MenuItem());
        assertEquals("Spaghetti Bolognese", menuItem.getName());
    }

    @Test
    public void shouldGetByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/items/6")).andReturn();
        String actualMenuItemJson = result.getResponse().getContentAsString();
        assertTrue(actualMenuItemJson.contains("\"name\":\"Spaghetti Bolognese\""));
    }
}
