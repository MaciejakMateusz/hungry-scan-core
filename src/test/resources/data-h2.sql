INSERT INTO categories (id, name, description) VALUES (1, 'Przystawki', 'Rozpocznij swoją kulinarną podróż od pysznych przystawek, które skradną Twoje podniebienie. Wybierz spośród aromatycznych krewetek marynowanych w cytrynie, wyrafinowanego carpaccio z polędwicy wołowej lub chrupiących nachos z soczystym sosem serowym.');
INSERT INTO categories (id, name, description) VALUES (2, 'Makarony', 'Ciesz się smakiem Włoch dzięki naszym wyśmienitym makaronom. Wybierz klasykę - spaghetti bolognese, uwielbiane przez wszystkich penne carbonara lub wykwintne tagliatelle z łososiem i szpinakiem.');
INSERT INTO categories (id, name, description) VALUES (3, 'Sałatki', 'Zachwyć swoje zmysły zdrowymi i świeżymi sałatkami. Spróbuj wyjątkowej sałatki greckiej z serem feta, orzeźwiającej sałatki z grillowanym ananasem i kurczakiem lub bogatej w białko sałatki z quinoa i pieczonymi warzywami.');
INSERT INTO categories (id, name, description) VALUES (4, 'Zupy', 'Rozgrzej się pysznymi zupami. Spróbuj gładkiego kremu z pomidorów, tradycyjnego rosołu z kury lub pysznego kremu z dyni z nutą cynamonu.');
INSERT INTO categories (id, name, description) VALUES (5, 'Pizza', 'Zanurz się w prawdziwym smaku pizzy. Wybierz klasyczną margheritę, pikantną pepperoni, pyszną capricciosę lub egzotyczną hawajską.');
INSERT INTO categories (id, name, description) VALUES (6, 'Wegetariańskie', 'Odkryj różnorodność wegetariańskich smaków. Skosztuj aromatycznego risotto z grzybami leśnymi, egzotycznego curry z ciecierzycą i szpinakiem lub chrupiącego falafela w pitce z hummusem.');
INSERT INTO categories (id, name, description) VALUES (7, 'Dla dzieci', 'Zaspokój apetyt najmłodszych przyjemnymi dla podniebienia daniami. Wybierz mini hamburgery z frytkami, krówki z kurczaka w panierce z ketchupem lub słodkie kanapki z nutellą i bananem.');
INSERT INTO categories (id, name, description) VALUES (8, 'Napoje', 'Uzupełnij swoje doznania smakowe pysznymi napojami. Odkryj orzeźwiającą lemoniadę cytrynową, owocowy koktajl truskawkowy, a dla dorosłych - piwo rzemieślnicze IPA.');

INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (1, 'Krewetki marynowane w cytrynie', 1, 'Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.', 'Krewetki, cytryna, oliwa z oliwek, czosnek, przyprawy', 19.99, 1, '2023-08-02 04:05:13', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (2, 'Carpaccio z polędwicy wołowej', 1, 'Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.', 'Polędwica wołowa, rukola, parmezan, kapary, oliwa z oliwek', 24.50, 1, '2023-08-02 04:06:03', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (3, 'Krewetki w tempurze', 1, 'Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym', 'Krewetki, mąka, jajko, olej roślinny, sos słodko-kwaśny', 22.00, 1, '2023-08-02 04:06:24', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (4, 'Roladki z bakłażana z feta i suszonymi pomidorami', 1, 'Bakłażany zawijane w roladki z feta i suszonymi pomidorami, pieczone w piecu.', 'Bakłażan, ser feta, suszone pomidory, oliwa z oliwek', 18.75, 1, '2023-08-02 04:06:47', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (5, 'Nachos z sosem serowym', 1, 'Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.', 'Nachos, ser, śmietana, awokado, pomidory, cebula, papryczki chili', 16.99, 1, '2023-08-02 04:07:09', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (6, 'Spaghetti Bolognese', 2, 'Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.', 'Spaghetti, mięso mielone, pomidory, cebula, marchewka, seler, przyprawy', 24.00, 1, '2023-08-02 04:07:39', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (7, 'Penne Carbonara', 2, 'Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.', 'Penne, boczek, jajka, ser parmezan, śmietana, czosnek', 22.50, 1, '2023-08-02 04:08:00', '2023-08-02 04:31:49');
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (8, 'Lasagne warzywna', 2, 'Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.', 'Makaron lasagne, pomidory, cukinia, bakłażan, beszamel, ser', 23.25, 1, '2023-08-02 04:08:45', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (9, 'Tagliatelle z łososiem i szpinakiem', 2, 'Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.', 'Tagliatelle, łosoś, szpinak, śmietana, czosnek, cebula', 26.50, 1, '2023-08-02 04:09:05', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (10, 'Rigatoni z kurczakiem i brokułami', 2, ' Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.', 'Rigatoni, kurczak, brokuły, śmietana, cebula, bulion', 21.75, 1, '2023-08-02 04:09:28', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (11, 'Sałatka grecka', 3, 'Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.', 'Pomidory, ogórki, cebula, oliwki, ser feta, oliwa, oregano', 18.50, 1, '2023-08-02 04:10:03', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (12, 'Sałatka z grillowanym kurczakiem i awokado', 3, 'Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.', 'Kurczak, awokado, pomidory, sałata, orzechy, sos vinegrette', 20.75, 1, '2023-08-02 04:10:28', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (13, 'Sałatka z rukolą, serem kozim i suszonymi żurawinami', 3, 'Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.', 'Rukola, ser kozi, żurawiny, orzechy włoskie, dressing balsamiczny', 22.00, 1, '2023-08-02 04:11:14', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (14, 'Sałatka z grillowanym ananasem i kurczakiem', 3, 'Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.', 'Ananas, kurczak, sałata mieszana, cebula, sos winegret', 21.50, 1, '2023-08-02 04:12:08', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (15, 'Sałatka z quinoa i pieczonymi warzywami', 3, 'Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.', 'Quinoa, marchewki, buraki, suszone pomidory, mix sałat', 23.75, 1, '2023-08-02 04:12:39', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (16, 'Krem z pomidorów', 4, 'Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.', 'Pomidory, cebula, czosnek, śmietana, bazylia', 15.50, 1, '2023-08-02 04:13:07', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (17, 'Rosołek z kury', 4, 'Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.', 'Rosół z kury, marchew, pietruszka, seler, makaron', 16.25, 1, '2023-08-02 04:13:35', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (18, 'Zupa krem z dyni', 4, 'Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.', 'Dynia, cebula, czosnek, bulion warzywny, cynamon, pestki dyni', 17.50, 1, '2023-08-02 04:14:02', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (19, 'Zupa pomidorowa z ryżem', 4, 'Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.', 'Pomidory, ryż, cebula, czosnek, koper', 15.75, 1, '2023-08-02 04:14:33', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (20, 'Zupa krem z brokułów', 4, 'Delikatny krem z zielonych brokułów podawany z grzankami.', 'Brokuły, cebula, czosnek, śmietana, grzanki', 18.00, 1, '2023-08-02 04:14:58', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (21, 'Pizza Margherita', 5, 'Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.', 'Sos pomidorowy, mozzarella, bazylia', 26.00, 1, '2023-08-02 04:15:37', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (22, 'Pizza Pepperoni', 5, 'Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.', 'Sos pomidorowy, mozzarella, pepperoni, papryka', 28.50, 1, '2023-08-02 04:16:06', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (23, 'Pizza Capricciosa', 5, 'Pizza z szynką, pieczarkami, karczochami i oliwkami.', 'Sos pomidorowy, mozzarella, szynka, pieczarki, karczochy, oliwki', 30.25, 1, '2023-08-02 04:16:29', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (24, 'Pizza Hawajska', 5, 'Pizza z szynką, ananasem i kukurydzą.', 'Sos pomidorowy, mozzarella, szynka, ananas, kukurydza', 27.75, 1, '2023-08-02 04:16:52', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (25, 'Pizza Quattro Formaggi', 5, 'Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.', 'Sos pomidorowy, mozzarella, gorgonzola, parmezan, camembert', 29.00, 1, '2023-08-02 04:17:15', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (26, 'Risotto z grzybami leśnymi', 6, 'Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.', 'Ryż, grzyby leśne, cebula, czosnek, bulion warzywny, parmezan', 24.50, 1, '2023-08-02 04:17:45', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (27, 'Curry z ciecierzycą i szpinakiem', 6, 'Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.', 'Ciecierzyca, szpinak, pomidory, mleko kokosowe, curry', 22.75, 1, '2023-08-02 04:18:14', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (28, 'Naleśniki z serem i szpinakiem', 6, 'Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.', 'Naleśniki, ser, szpinak, sos pomidorowy', 20.00, 1, '2023-08-02 04:18:42', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (29, 'Falafel w pitce z hummusem', 6, 'Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.', 'Falafel, pita, hummus, pomidory, ogórki, cebula', 21.25, 1, '2023-08-02 04:20:01', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (30, 'Makaron z pesto bazyliowym', 6, 'Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.', 'Spaghetti, pesto bazyliowe, parmezan, orzechy włoskie', 23.00, 1, '2023-08-02 04:20:39', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (31, 'Mini hamburgery z frytkami', 7, 'Dwa mini hamburgerki z wołowiną, serem, sałatą i frytkami.', 'Wołowina, bułka, ser, sałata, ziemniaki', 18.75, 1, '2023-08-02 04:21:46', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (32, 'Kurczak w panierce z ketchupem', 7, 'Kruszone kawałki kurczaka podawane z ketchupem i plasterkami marchewki.', 'Kurczak, panierka, ketchup, marchewka', 17.50, 1, '2023-08-02 04:22:06', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (33, 'Kanapki z nutellą i bananem', 7, 'Puchate kanapki z kremową nutellą i plasterkami świeżego banana.', 'Chleb, Nutella, banany', 14.00, 1, '2023-08-02 04:22:24', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (34, 'Ryż z warzywami i jajkiem sadzonym', 7, 'Delikatny ryż z kawałkami warzyw i jajkiem sadzonym.', 'Ryż, marchew, groszek, kukurydza, jajko.', 16.25, 1, '2023-08-02 04:22:57', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (35, 'Mini pizza z szynką i serem', 7, 'Mała pizza z szynką, serem i pomidorowym sosem.', 'Ciasto na pizzę, szynka, ser, sos pomidorowy', 15.50, 1, '2023-08-02 04:23:19', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (36, 'Lemoniada cytrynowa', 8, 'Orzeźwiająca lemoniada z dodatkiem cytryny i mięty.', 'Woda, cytryna, cukier, mięta', 8.50, 1, '2023-08-02 04:23:44', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (37, 'Koktajl truskawkowy', 8, 'Pyszny koktajl z truskawkami, jogurtem i miodem.', 'Truskawki, jogurt, miód', 9.75, 1, '2023-08-02 04:24:04', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (38, 'Smoothie z mango i bananem', 8, 'Płynny smoothie z dojrzałym mango i bananem.', 'Mango, banan, sok pomarańczowy', 10.00, 1, '2023-08-02 04:24:23', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (39, 'Piwo rzemieślnicze IPA', 8, 'Zrównoważone piwo rzemieślnicze typu IPA.', 'Jęczmień, chmiele, woda', 12.50, 1, '2023-08-02 04:24:42', null);
INSERT INTO menu_items (id, name, category_id, description, ingredients, price, is_available, created, updated) VALUES (40, 'Herbata zielona', 8, 'Zdrowa herbata zielona, lekko zaparzona.', 'Liście herbaty zielonej',7.25, 1, '2023-08-02 04:25:04', null);

INSERT INTO restaurants (address, name) VALUES ('ul. Główna 123, Miastowo, Województwo, 54321', 'Rarytas');
INSERT INTO restaurants (address, name) VALUES ('ul. Dębowa 456, Miasteczko, Wiejskie, 98765', 'Wykwintna Bistro');

INSERT INTO restaurant_tables (id) VALUES (1);
INSERT INTO restaurant_tables (id) VALUES (2);
INSERT INTO restaurant_tables (id) VALUES (3);
INSERT INTO restaurant_tables (id) VALUES (4);
INSERT INTO restaurant_tables (id) VALUES (5);
INSERT INTO restaurant_tables (id) VALUES (6);
INSERT INTO restaurant_tables (id) VALUES (7);
INSERT INTO restaurant_tables (id) VALUES (8);
INSERT INTO restaurant_tables (id) VALUES (9);
INSERT INTO restaurant_tables (id) VALUES (10);
INSERT INTO restaurant_tables (id) VALUES (11);
INSERT INTO restaurant_tables (id) VALUES (12);
INSERT INTO restaurant_tables (id) VALUES (13);
INSERT INTO restaurant_tables (id) VALUES (14);
INSERT INTO restaurant_tables (id) VALUES (15);
INSERT INTO restaurant_tables (id) VALUES (16);
INSERT INTO restaurant_tables (id) VALUES (17);
INSERT INTO restaurant_tables (id) VALUES (18);
INSERT INTO restaurant_tables (id) VALUES (19);

INSERT INTO role (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, name) VALUES (2, 'ROLE_ADMIN');

INSERT INTO ordered_items (quantity, menu_item_id) VALUES (2, 3);
INSERT INTO ordered_items (quantity, menu_item_id) VALUES (2, 3);
INSERT INTO ordered_items (quantity, menu_item_id) VALUES (2, 3);
INSERT INTO ordered_items (quantity, menu_item_id) VALUES (3, 2);
INSERT INTO ordered_items (quantity, menu_item_id) VALUES (2, 3);

INSERT INTO orders (bill_requested,
                    take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    `waiter_called`,
                    restaurant_id,
                    table_id) VALUES (false, false, false, 1, NOW(), false, null, null, false, 1, 1);
INSERT INTO orders (bill_requested,
                    take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    `waiter_called`,
                    restaurant_id,
                    table_id) VALUES (true, false, false, 2, NOW(), false, 'cash', null, false, 1, 2);
INSERT INTO orders (bill_requested,
                    take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    `waiter_called`,
                    restaurant_id,
                    table_id) VALUES (true, false, true, 3, NOW(), true, 'card', null, false, 1, 3);
INSERT INTO orders (bill_requested,
                    take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    `waiter_called`,
                    restaurant_id,
                    table_id) VALUES (true, false, true, 4, NOW(), true, 'card', null, false, 1, 4);
INSERT INTO orders (bill_requested,
                    take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    `waiter_called`,
                    restaurant_id,
                    table_id) VALUES (false, false, false, 5, NOW(), false, null, null, true, 1, 5);
INSERT INTO orders (bill_requested,
                    take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    `waiter_called`,
                    restaurant_id,
                    table_id) VALUES (false, true, false, 6, NOW(), true, 'online', null, true, 1, 19);

INSERT INTO orders_ordered_items (order_id, ordered_items_id) VALUES (1, 1);
INSERT INTO orders_ordered_items (order_id, ordered_items_id) VALUES (2, 2);
INSERT INTO orders_ordered_items (order_id, ordered_items_id) VALUES (3, 3);
INSERT INTO orders_ordered_items (order_id, ordered_items_id) VALUES (4, 4);
INSERT INTO orders_ordered_items (order_id, ordered_items_id) VALUES (5, 5);