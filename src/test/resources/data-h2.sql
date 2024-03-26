INSERT INTO categories (name, description)
VALUES ('Przystawki',
        'Rozpocznij swoją kulinarną podróż od pysznych przystawek, które skradną Twoje podniebienie. Wybierz spośród aromatycznych krewetek marynowanych w cytrynie, wyrafinowanego carpaccio z polędwicy wołowej lub chrupiących nachos z soczystym sosem serowym.');
INSERT INTO categories (name, description)
VALUES ('Makarony',
        'Ciesz się smakiem Włoch dzięki naszym wyśmienitym makaronom. Wybierz klasykę - spaghetti bolognese, uwielbiane przez wszystkich penne carbonara lub wykwintne tagliatelle z łososiem i szpinakiem.');
INSERT INTO categories (name, description)
VALUES ('Sałatki',
        'Zachwyć swoje zmysły zdrowymi i świeżymi sałatkami. Spróbuj wyjątkowej sałatki greckiej z serem feta, orzeźwiającej sałatki z grillowanym ananasem i kurczakiem lub bogatej w białko sałatki z quinoa i pieczonymi warzywami.');
INSERT INTO categories (name, description)
VALUES ('Zupy',
        'Rozgrzej się pysznymi zupami. Spróbuj gładkiego kremu z pomidorów, tradycyjnego rosołu z kury lub pysznego kremu z dyni z nutą cynamonu.');
INSERT INTO categories (name, description)
VALUES ('Pizza',
        'Zanurz się w prawdziwym smaku pizzy. Wybierz klasyczną margheritę, pikantną pepperoni, pyszną capricciosę lub egzotyczną hawajską.');
INSERT INTO categories (name, description)
VALUES ('Wegetariańskie',
        'Odkryj różnorodność wegetariańskich smaków. Skosztuj aromatycznego risotto z grzybami leśnymi, egzotycznego curry z ciecierzycą i szpinakiem lub chrupiącego falafela w pitce z hummusem.');
INSERT INTO categories (name, description)
VALUES ('Dla dzieci',
        'Zaspokój apetyt najmłodszych przyjemnymi dla podniebienia daniami. Wybierz mini hamburgery z frytkami, krówki z kurczaka w panierce z ketchupem lub słodkie kanapki z nutellą i bananem.');
INSERT INTO categories (name, description)
VALUES ('Napoje',
        'Uzupełnij swoje doznania smakowe pysznymi napojami. Odkryj orzeźwiającą lemoniadę cytrynową, owocowy koktajl truskawkowy, a dla dorosłych - piwo rzemieślnicze IPA.');

INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Krewetki marynowane w cytrynie', 1, 'Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.',
        'Krewetki, cytryna, oliwa z oliwek, czosnek, przyprawy', 19.99, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Carpaccio z polędwicy wołowej', 1, 'Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.',
        'Polędwica wołowa, rukola, parmezan, kapary, oliwa z oliwek', 24.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Krewetki w tempurze', 1, 'Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym',
        'Krewetki, mąka, jajko, olej roślinny, sos słodko-kwaśny', 22.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Roladki z bakłażana z feta i suszonymi pomidorami', 1,
        'Bakłażany zawijane w roladki z feta i suszonymi pomidorami, pieczone w piecu.',
        'Bakłażan, ser feta, suszone pomidory, oliwa z oliwek', 18.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Nachos z sosem serowym', 1, 'Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.',
        'Nachos, ser, śmietana, awokado, pomidory, cebula, papryczki chili', 16.99, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Spaghetti Bolognese', 2,
        'Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.',
        'Spaghetti, mięso mielone, pomidory, cebula, marchewka, seler, przyprawy', 24.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Penne Carbonara', 2, 'Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.',
        'Penne, boczek, jajka, ser parmezan, śmietana, czosnek', 22.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Lasagne warzywna', 2, 'Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.',
        'Makaron lasagne, pomidory, cukinia, bakłażan, beszamel, ser', 23.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Tagliatelle z łososiem i szpinakiem', 2,
        'Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.',
        'Tagliatelle, łosoś, szpinak, śmietana, czosnek, cebula', 26.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Rigatoni z kurczakiem i brokułami', 2, 'Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.',
        'Rigatoni, kurczak, brokuły, śmietana, cebula, bulion', 21.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Sałatka grecka', 3,
        'Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.',
        'Pomidory, ogórki, cebula, oliwki, ser feta, oliwa, oregano', 18.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Sałatka z grillowanym kurczakiem i awokado', 3,
        'Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.',
        'Kurczak, awokado, pomidory, sałata, orzechy, sos vinegrette', 20.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Sałatka z rukolą, serem kozim i suszonymi żurawinami', 3,
        'Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.',
        'Rukola, ser kozi, żurawiny, orzechy włoskie, dressing balsamiczny', 22.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Sałatka z grillowanym ananasem i kurczakiem', 3,
        'Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.',
        'Ananas, kurczak, sałata mieszana, cebula, sos winegret', 21.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Sałatka z quinoa i pieczonymi warzywami', 3,
        'Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.',
        'Quinoa, marchewki, buraki, suszone pomidory, mix sałat', 23.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Krem z pomidorów', 4, 'Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.',
        'Pomidory, cebula, czosnek, śmietana, bazylia', 15.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Rosołek z kury', 4, 'Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.',
        'Rosół z kury, marchew, pietruszka, seler, makaron', 16.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Zupa krem z dyni', 4, 'Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.',
        'Dynia, cebula, czosnek, bulion warzywny, cynamon, pestki dyni', 17.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Zupa pomidorowa z ryżem', 4, 'Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.',
        'Pomidory, ryż, cebula, czosnek, koper', 15.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Zupa krem z brokułów', 4, 'Delikatny krem z zielonych brokułów podawany z grzankami.',
        'Brokuły, cebula, czosnek, śmietana, grzanki', 18.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Pizza Margherita', 5, 'Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.',
        'Sos pomidorowy, mozzarella, bazylia', 26.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Pizza Pepperoni', 5, 'Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.',
        'Sos pomidorowy, mozzarella, pepperoni, papryka', 28.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Pizza Capricciosa', 5, 'Pizza z szynką, pieczarkami, karczochami i oliwkami.',
        'Sos pomidorowy, mozzarella, szynka, pieczarki, karczochy, oliwki', 30.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Pizza Hawajska', 5, 'Pizza z szynką, ananasem i kukurydzą.',
        'Sos pomidorowy, mozzarella, szynka, ananas, kukurydza', 27.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Pizza Quattro Formaggi', 5, 'Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.',
        'Sos pomidorowy, mozzarella, gorgonzola, parmezan, camembert', 29.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Risotto z grzybami leśnymi', 6, 'Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.',
        'Ryż, grzyby leśne, cebula, czosnek, bulion warzywny, parmezan', 24.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Curry z ciecierzycą i szpinakiem', 6,
        'Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.',
        'Ciecierzyca, szpinak, pomidory, mleko kokosowe, curry', 22.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Naleśniki z serem i szpinakiem', 6,
        'Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.',
        'Naleśniki, ser, szpinak, sos pomidorowy', 20.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Falafel w pitce z hummusem', 6, 'Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.',
        'Falafel, pita, hummus, pomidory, ogórki, cebula', 21.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Makaron z pesto bazyliowym', 6,
        'Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.',
        'Spaghetti, pesto bazyliowe, parmezan, orzechy włoskie', 23.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Mini hamburgery z frytkami', 7, 'Dwa mini hamburgerki z wołowiną, serem, sałatą i frytkami.',
        'Wołowina, bułka, ser, sałata, ziemniaki', 18.75, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Kurczak w panierce z ketchupem', 7, 'Kruszone kawałki kurczaka podawane z ketchupem i plasterkami marchewki.',
        'Kurczak, panierka, ketchup, marchewka', 17.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Kanapki z nutellą i bananem', 7, 'Puchate kanapki z kremową nutellą i plasterkami świeżego banana.',
        'Chleb, Nutella, banany', 14.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Ryż z warzywami i jajkiem sadzonym', 7, 'Delikatny ryż z kawałkami warzyw i jajkiem sadzonym.',
        'Ryż, marchew, groszek, kukurydza, jajko.', 16.25, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Mini pizza z szynką i serem', 7, 'Mała pizza z szynką, serem i pomidorowym sosem.',
        'Ciasto na pizzę, szynka, ser, sos pomidorowy', 15.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Lemoniada cytrynowa', 8, 'Orzeźwiająca lemoniada z dodatkiem cytryny i mięty.', 'Woda, cytryna, cukier, mięta',
        8.50, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Koktajl truskawkowy', 8, 'Pyszny koktajl z truskawkami, jogurtem i miodem.', 'Truskawki, jogurt, miód', 9.75,
        1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Smoothie z mango i bananem', 8, 'Płynny smoothie z dojrzałym mango i bananem.',
        'Mango, banan, sok pomarańczowy', 10.00, 1, null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Piwo rzemieślnicze IPA', 8, 'Zrównoważone piwo rzemieślnicze typu IPA.', 'Jęczmień, chmiele, woda', 12.50, 1,
        null, null);
INSERT INTO menu_items (name, category_id, description, ingredients, price, is_available, created, updated)
VALUES ('Herbata zielona', 8, 'Zdrowa herbata zielona, lekko zaparzona.', 'Liście herbaty zielonej', 7.25, 1, null,
        null);

INSERT INTO restaurants (address, name)
VALUES ('ul. Główna 123, Miastowo, Województwo, 54321', 'Rarytas');
INSERT INTO restaurants (address, name)
VALUES ('ul. Dębowa 456, Miasteczko, Wiejskie, 98765', 'Wykwintna Bistro');

INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (1, true, '19436a86-e200-400d-aa2e-da4686805d00', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (2, true, '79d8684f-333e-4275-a317-fa06d46fa6b6', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (3, false, '0ce8beb3-6fb1-42f1-9c95-05cf9fb88d27', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (4, false, '5afb9629-990a-4934-87f2-793b1aa2f35e', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (5, true, '58d77e24-6b8c-41a9-b24c-a67602deacdd', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (6, false, '59ebc00c-b580-4dff-9788-2df90b1d4bba', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (7, false, 'ef303854-6faa-4615-8d47-6f3686086586', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (8, false, '97cba027-ae47-4c42-8828-f4b3b3506d0c', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (9, false, 'fe2cce7c-7c4c-4076-9eb4-3e91b440fec2', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (10, false, '88ca9c82-e630-40f2-9bf9-47f7d14f6bff', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (11, false, 'c88a6029-4f29-4ee1-8d8f-f31f7a554301', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (12, true, 'd565c73a-8d87-4a79-9e3f-7b6a02520e71', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (13, false, '6696c583-a312-4b24-9716-430826ad1e96', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (14, false, 'a65896cb-805d-4d7b-849b-1d53e78f3191', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (15, false, '65b6bb94-da99-4ced-8a94-5860fe95e708', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (16, false, '2fd07320-a841-48ad-9f3f-35b307014b2a', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (17, false, '3740c35f-5759-4eb8-ab00-cb3807707235', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (18, false, '480407f1-13bd-45a7-bad7-d0e2b76e5ebf', false, false);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested)
VALUES (19, true, '96fb4431-af22-48f4-9e4c-40b5774d9ab2', false, false);

INSERT INTO role (id, name, displayed_name)
VALUES (1, 'ROLE_WAITER', 'Kelner');
INSERT INTO role (id, name, displayed_name)
VALUES (2, 'ROLE_ADMIN', 'Administrator');
INSERT INTO role (id, name, displayed_name)
VALUES (3, 'ROLE_MANAGER', 'Menadżer');
INSERT INTO role (id, name, displayed_name)
VALUES (4, 'ROLE_COOK', 'Kucharz');
INSERT INTO role (id, name, displayed_name)
VALUES (5, 'ROLE_CUSTOMER', 'Klient');


INSERT INTO ordered_items (quantity, menu_item_id, is_ready_to_serve)
VALUES (2, 3, false);
INSERT INTO ordered_items (quantity, menu_item_id, is_ready_to_serve)
VALUES (3, 2, false);
INSERT INTO ordered_items (quantity, menu_item_id, is_ready_to_serve)
VALUES (2, 3, false);
INSERT INTO ordered_items (quantity, menu_item_id, is_ready_to_serve)
VALUES (4, 5, false);
INSERT INTO ordered_items (quantity, menu_item_id, is_ready_to_serve)
VALUES (2, 5, false);

INSERT INTO orders (take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    tip_amount,
                    restaurant_id,
                    table_id)
VALUES (false, false, 1, '2024-01-29 08:29:20.738823', false, 'NONE', 44.00, 0.00, 1, 1);
INSERT INTO orders (take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    tip_amount,
                    restaurant_id,
                    table_id)
VALUES (false, false, 322, '2024-01-29 08:29:20.738823', false, 'CASH', 49.00, 0.00, 1, 2);
INSERT INTO orders (take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    tip_amount,
                    restaurant_id,
                    table_id)
VALUES (false, false, 421, '2024-01-29 08:29:20.738823', false, 'NONE', 44.00, 0.00, 1, 5);
INSERT INTO orders (take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    tip_amount,
                    restaurant_id,
                    table_id)
VALUES (true, false, 6, '2024-01-29 08:29:20.738823', true, 'ONLINE', 67.96, 0.00, 1, 19);
INSERT INTO orders (take_away,
                    is_resolved,
                    order_number,
                    order_time,
                    is_paid,
                    payment_method,
                    total_amount,
                    tip_amount,
                    restaurant_id,
                    table_id)
VALUES (false, false, 7, '2024-01-29 08:29:20.738823', false, 'NONE', 84.95, 0.00, 1, 12);

INSERT INTO orders_ordered_items (order_id, ordered_items_id)
VALUES (1, 1);
INSERT INTO orders_ordered_items (order_id, ordered_items_id)
VALUES (2, 2);
INSERT INTO orders_ordered_items (order_id, ordered_items_id)
VALUES (3, 3);
INSERT INTO orders_ordered_items (order_id, ordered_items_id)
VALUES (4, 4);
INSERT INTO orders_ordered_items (order_id, ordered_items_id)
VALUES (5, 5);

INSERT INTO waiter_calls(call_time, is_resolved, resolved_time, order_id)
VALUES ('2024-01-29 08:41:20.738823', false, '2024-01-29 08:43:20.738823', 3);
INSERT INTO waiter_calls(call_time, is_resolved, resolved_time, order_id)
VALUES ('2024-01-29 08:41:20.738823', false, '2024-01-29 08:43:20.738823', 5);

INSERT INTO history_ordered_items (id, quantity, menu_item_id, is_ready_to_serve)
VALUES (222, 2, 3, false);
INSERT INTO history_ordered_items (id, quantity, menu_item_id, is_ready_to_serve)
VALUES (333, 3, 2, false);
INSERT INTO history_ordered_items (id, quantity, menu_item_id, is_ready_to_serve)
VALUES (444, 2, 3, false);
INSERT INTO history_ordered_items (id, quantity, menu_item_id, is_ready_to_serve)
VALUES (555, 3, 2, false);

INSERT INTO history_orders (id,
                            take_away,
                            is_resolved,
                            order_number,
                            order_date,
                            order_time,
                            is_paid,
                            payment_method,
                            total_amount,
                            tip_amount,
                            restaurant_id,
                            table_id)
VALUES (12, false, true, 1, '2024-01-29', '08:29:20.738823', true, 'CARD', 44.00, 0.00, 1, 1);
INSERT INTO history_orders (id,
                            take_away,
                            is_resolved,
                            order_number,
                            order_date,
                            order_time,
                            is_paid,
                            payment_method,
                            total_amount,
                            tip_amount,
                            restaurant_id,
                            table_id)
VALUES (13, false, true, 322, '2024-02-21', '08:29:20.738823', true, 'CASH', 93.50, 20.00, 1, 2);
INSERT INTO history_orders (id,
                            take_away,
                            is_resolved,
                            order_number,
                            order_date,
                            order_time,
                            is_paid,
                            payment_method,
                            total_amount,
                            tip_amount,
                            restaurant_id,
                            table_id)
VALUES (14, true, true, 1, '2024-01-29', '08:29:20.738823', true, 'ONLINE', 54.00, 10.00, 1, 19);
INSERT INTO history_orders (id,
                            take_away,
                            is_resolved,
                            order_number,
                            order_date,
                            order_time,
                            is_paid,
                            payment_method,
                            total_amount,
                            tip_amount,
                            restaurant_id,
                            table_id)
VALUES (15, true, true, 322, '2024-02-21', '08:29:20.738823', true, 'ONLINE', 73.50, 0.00, 1, 19);

INSERT INTO history_orders_history_ordered_items (history_order_id, history_ordered_items_id)
VALUES (12, 222);
INSERT INTO history_orders_history_ordered_items (history_order_id, history_ordered_items_id)
VALUES (13, 333);
INSERT INTO history_orders_history_ordered_items (history_order_id, history_ordered_items_id)
VALUES (14, 444);
INSERT INTO history_orders_history_ordered_items (history_order_id, history_ordered_items_id)
VALUES (15, 555);

INSERT INTO bookings (date, expiration_time, num_of_ppl, surname, time)
VALUES ('2024-02-23', '19:00:00', 2, 'Pierwszy', '16:00:00');
INSERT INTO bookings_restaurant_tables(booking_id, restaurant_tables_id)
VALUES (1, 5);
INSERT INTO bookings (date, expiration_time, num_of_ppl, surname, time)
VALUES ('2024-02-28', '14:00:00', 2, 'Drugi', '16:00:00');
INSERT INTO bookings_restaurant_tables(booking_id, restaurant_tables_id)
VALUES (2, 7);

INSERT INTO history_bookings (id, date, expiration_time, num_of_ppl, surname, time)
VALUES (50, '2024-01-19', '19:00:00', 1, 'Alan', '16:00:00');
INSERT INTO history_bookings_restaurant_tables(history_booking_id, restaurant_tables_id)
VALUES (50, 2);
INSERT INTO history_bookings (id, date, expiration_time, num_of_ppl, surname, time)
VALUES (51, '2024-01-21', '19:00:00', 3, 'Gibson', '16:00:00');
INSERT INTO history_bookings_restaurant_tables(history_booking_id, restaurant_tables_id)
VALUES (51, 10);
INSERT INTO history_bookings (id, date, expiration_time, num_of_ppl, surname, time)
VALUES (52, '2024-01-23', '19:00:00', 2, 'Fire', '16:00:00');
INSERT INTO history_bookings_restaurant_tables(history_booking_id, restaurant_tables_id)
VALUES (52, 11);
INSERT INTO history_bookings (id, date, expiration_time, num_of_ppl, surname, time)
VALUES (53, '2024-01-25', '19:00:00', 4, 'Water', '16:00:00');
INSERT INTO history_bookings_restaurant_tables(history_booking_id, restaurant_tables_id)
VALUES (53, 13);
INSERT INTO history_bookings (id, date, expiration_time, num_of_ppl, surname, time)
VALUES (54, '2024-01-27', '19:00:00', 2, 'Earth', '16:00:00');
INSERT INTO history_bookings_restaurant_tables(history_booking_id, restaurant_tables_id)
VALUES (54, 5);

INSERT INTO settings (id, booking_duration, opening_time, closing_time, language, capacity, customer_session_time,
                      employee_session_time)
VALUES (1, 3, '07:00:00', '23:00:00', 1, 120, 3, 20);

INSERT INTO users (created, email, enabled, password, updated, username, token, name, phone_number, surname)
VALUES ('2024-01-20 12:04:00.000000', 'matimemek@test.com', 1,
        '$2a$10$z/0edEimosa3QjYYxjiHuO8bNZHfI3jxDVwqDNd5bc2vCr5TERDz6', '2024-02-02 20:54:41.531670', 'mati', null,
        'Mati', '+48 123 123 123', 'Memek');
INSERT INTO users (created, email, enabled, password, updated, username, token, name, phone_number, surname)
VALUES ('2024-01-20 19:09:00.000000', 'admin@example.com', 1,
        '$2a$04$OI8NalP4M4rxpRFgVR3eO.8C/6hmP.AdYadtTPd3BLHm3zx3wLLWm', '2024-02-04 07:50:29.047589', 'admin',
        null, '', '', '');
INSERT INTO users (created, email, enabled, password, updated, username, token, name, phone_number, surname)
VALUES ('2024-01-24 19:06:36.680304', 'netka@test.com', 1,
        '$2a$10$ViUyMtRUmZgeZWRBME67g.Wp3K9p8UyJPfQd2GB9uXbQnBiDe4FJ.', null, 'neta', null, null, null, null);
INSERT INTO users (created, email, enabled, password, updated, username, token, name, phone_number, surname)
VALUES ('2024-02-03 10:21:00.000000', 'kucharz@antek.pl', 1,
        '$2a$10$.HWarZkysOgBF0/tOXmmtONdRkZHGZCsRFs27Q7FcNrDc4bSzE0fW', '2024-02-03 10:33:07.307903', 'kucharz', null,
        'ada', '', 'asdqwe');
INSERT INTO users (created, email, enabled, password, updated, username, token, name, phone_number, surname)
VALUES ('2024-02-03 10:24:02.744722', 'restaurator@rarytas.pl', 1,
        '$2a$10$tykyevzP4v1WV/FyuYWNOO6wspbmAHnzI.deEAZQU6SA8NSxod3Vy', null, 'owner', null, 'Właściciel', '',
        'Biznesmen');

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);
INSERT INTO user_role (user_id, role_id)
VALUES (3, 1);
INSERT INTO user_role (user_id, role_id)
VALUES (2, 2);
INSERT INTO user_role (user_id, role_id)
VALUES (5, 2);
INSERT INTO user_role (user_id, role_id)
VALUES (3, 3);
INSERT INTO user_role (user_id, role_id)
VALUES (5, 3);
INSERT INTO user_role (user_id, role_id)
VALUES (4, 4);
