INSERT INTO categories (name, description) VALUES ('Przystawki', 'Rozpocznij swoją kulinarną podróż od pysznych przystawek, które skradną Twoje podniebienie. Wybierz spośród aromatycznych krewetek marynowanych w cytrynie, wyrafinowanego carpaccio z polędwicy wołowej lub chrupiących nachos z soczystym sosem serowym.');
INSERT INTO categories (name, description) VALUES ('Makarony', 'Ciesz się smakiem Włoch dzięki naszym wyśmienitym makaronom. Wybierz klasykę - spaghetti bolognese, uwielbiane przez wszystkich penne carbonara lub wykwintne tagliatelle z łososiem i szpinakiem.');
INSERT INTO categories (name, description) VALUES ('Sałatki', 'Zachwyć swoje zmysły zdrowymi i świeżymi sałatkami. Spróbuj wyjątkowej sałatki greckiej z serem feta, orzeźwiającej sałatki z grillowanym ananasem i kurczakiem lub bogatej w białko sałatki z quinoa i pieczonymi warzywami.');
INSERT INTO categories (name, description) VALUES ('Zupy', 'Rozgrzej się pysznymi zupami. Spróbuj gładkiego kremu z pomidorów, tradycyjnego rosołu z kury lub pysznego kremu z dyni z nutą cynamonu.');
INSERT INTO categories (name, description) VALUES ('Pizza', 'Zanurz się w prawdziwym smaku pizzy. Wybierz klasyczną margheritę, pikantną pepperoni, pyszną capricciosę lub egzotyczną hawajską.');
INSERT INTO categories (name, description) VALUES ('Wegetariańskie', 'Odkryj różnorodność wegetariańskich smaków. Skosztuj aromatycznego risotto z grzybami leśnymi, egzotycznego curry z ciecierzycą i szpinakiem lub chrupiącego falafela w pitce z hummusem.');
INSERT INTO categories (name, description) VALUES ('Dla dzieci', 'Zaspokój apetyt najmłodszych przyjemnymi dla podniebienia daniami. Wybierz mini hamburgery z frytkami, krówki z kurczaka w panierce z ketchupem lub słodkie kanapki z nutellą i bananem.');
INSERT INTO categories (name, description) VALUES ('Napoje', 'Uzupełnij swoje doznania smakowe pysznymi napojami. Odkryj orzeźwiającą lemoniadę cytrynową, owocowy koktajl truskawkowy, a dla dorosłych - piwo rzemieślnicze IPA.');

INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Krewetki marynowane w cytrynie', 1, 'Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.', 'Krewetki, cytryna, oliwa z oliwek, czosnek, przyprawy', 19.99, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Carpaccio z polędwicy wołowej', 1, 'Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.', 'Polędwica wołowa, rukola, parmezan, kapary, oliwa z oliwek', 24.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Krewetki w tempurze', 1, 'Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym', 'Krewetki, mąka, jajko, olej roślinny, sos słodko-kwaśny', 22.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Roladki z bakłażana z feta i suszonymi pomidorami', 1, 'Bakłażany zawijane w roladki z feta i suszonymi pomidorami, pieczone w piecu.', 'Bakłażan, ser feta, suszone pomidory, oliwa z oliwek', 18.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Nachos z sosem serowym', 1, 'Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.', 'Nachos, ser, śmietana, awokado, pomidory, cebula, papryczki chili', 16.99, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Spaghetti Bolognese', 2, 'Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.', 'Spaghetti, mięso mielone, pomidory, cebula, marchewka, seler, przyprawy', 24.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Penne Carbonara', 2, 'Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.', 'Penne, boczek, jajka, ser parmezan, śmietana, czosnek', 22.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Lasagne warzywna', 2, 'Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.', 'Makaron lasagne, pomidory, cukinia, bakłażan, beszamel, ser', 23.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Tagliatelle z łososiem i szpinakiem', 2, 'Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.', 'Tagliatelle, łosoś, szpinak, śmietana, czosnek, cebula', 26.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Rigatoni z kurczakiem i brokułami', 2, 'Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.', 'Rigatoni, kurczak, brokuły, śmietana, cebula, bulion', 21.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Sałatka grecka', 3, 'Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.', 'Pomidory, ogórki, cebula, oliwki, ser feta, oliwa, oregano', 18.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Sałatka z grillowanym kurczakiem i awokado', 3, 'Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.', 'Kurczak, awokado, pomidory, sałata, orzechy, sos vinegrette', 20.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Sałatka z rukolą, serem kozim i suszonymi żurawinami', 3, 'Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.', 'Rukola, ser kozi, żurawiny, orzechy włoskie, dressing balsamiczny', 22.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Sałatka z grillowanym ananasem i kurczakiem', 3, 'Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.', 'Ananas, kurczak, sałata mieszana, cebula, sos winegret', 21.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Sałatka z quinoa i pieczonymi warzywami', 3, 'Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.', 'Quinoa, marchewki, buraki, suszone pomidory, mix sałat', 23.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Krem z pomidorów', 4, 'Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.', 'Pomidory, cebula, czosnek, śmietana, bazylia', 15.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Rosołek z kury', 4, 'Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.', 'Rosół z kury, marchew, pietruszka, seler, makaron', 16.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Zupa krem z dyni', 4, 'Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.', 'Dynia, cebula, czosnek, bulion warzywny, cynamon, pestki dyni', 17.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Zupa pomidorowa z ryżem', 4, 'Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.', 'Pomidory, ryż, cebula, czosnek, koper', 15.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Zupa krem z brokułów', 4, 'Delikatny krem z zielonych brokułów podawany z grzankami.', 'Brokuły, cebula, czosnek, śmietana, grzanki', 18.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Pizza Margherita', 5, 'Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.', 'Sos pomidorowy, mozzarella, bazylia', 26.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Pizza Pepperoni', 5, 'Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.', 'Sos pomidorowy, mozzarella, pepperoni, papryka', 28.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Pizza Capricciosa', 5, 'Pizza z szynką, pieczarkami, karczochami i oliwkami.', 'Sos pomidorowy, mozzarella, szynka, pieczarki, karczochy, oliwki', 30.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Pizza Hawajska', 5, 'Pizza z szynką, ananasem i kukurydzą.', 'Sos pomidorowy, mozzarella, szynka, ananas, kukurydza', 27.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Pizza Quattro Formaggi', 5, 'Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.', 'Sos pomidorowy, mozzarella, gorgonzola, parmezan, camembert', 29.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Risotto z grzybami leśnymi', 6, 'Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.', 'Ryż, grzyby leśne, cebula, czosnek, bulion warzywny, parmezan', 24.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Curry z ciecierzycą i szpinakiem', 6, 'Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.', 'Ciecierzyca, szpinak, pomidory, mleko kokosowe, curry', 22.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Naleśniki z serem i szpinakiem', 6, 'Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.', 'Naleśniki, ser, szpinak, sos pomidorowy', 20.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Falafel w pitce z hummusem', 6, 'Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.', 'Falafel, pita, hummus, pomidory, ogórki, cebula', 21.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Makaron z pesto bazyliowym', 6, 'Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.', 'Spaghetti, pesto bazyliowe, parmezan, orzechy włoskie', 23.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Mini hamburgery z frytkami', 7, 'Dwa mini hamburgerki z wołowiną, serem, sałatą i frytkami.', 'Wołowina, bułka, ser, sałata, ziemniaki', 18.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Kurczak w panierce z ketchupem', 7, 'Kruszone kawałki kurczaka podawane z ketchupem i plasterkami marchewki.', 'Kurczak, panierka, ketchup, marchewka', 17.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Kanapki z nutellą i bananem', 7, 'Puchate kanapki z kremową nutellą i plasterkami świeżego banana.', 'Chleb, Nutella, banany', 14.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Ryż z warzywami i jajkiem sadzonym', 7, 'Delikatny ryż z kawałkami warzyw i jajkiem sadzonym.', 'Ryż, marchew, groszek, kukurydza, jajko.', 16.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Mini pizza z szynką i serem', 7, 'Mała pizza z szynką, serem i pomidorowym sosem.', 'Ciasto na pizzę, szynka, ser, sos pomidorowy', 15.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Lemoniada cytrynowa', 8, 'Orzeźwiająca lemoniada z dodatkiem cytryny i mięty.', 'Woda, cytryna, cukier, mięta', 8.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Koktajl truskawkowy', 8, 'Pyszny koktajl z truskawkami, jogurtem i miodem.', 'Truskawki, jogurt, miód', 9.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Smoothie z mango i bananem', 8, 'Płynny smoothie z dojrzałym mango i bananem.', 'Mango, banan, sok pomarańczowy', 10.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Piwo rzemieślnicze IPA', 8, 'Zrównoważone piwo rzemieślnicze typu IPA.', 'Jęczmień, chmiele, woda', 12.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated) VALUES ('Herbata zielona', 8, 'Zdrowa herbata zielona, lekko zaparzona.', 'Liście herbaty zielonej',7.25, 1, null, null);

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