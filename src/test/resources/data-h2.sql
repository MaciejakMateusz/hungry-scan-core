INSERT INTO allergens(id, description, icon_name, name)
VALUES (1,
        'Zboża zawierające gluten (tj. pszenica, żyto, jęczmień, owies, pszenica orkisz, lub ich odmiany hybrydowe) oraz produkty pochodne.',
        'icon_gluten', 'Gluten'),
       (2, 'Skorupiaki i produkty pochodne.', 'icon_crustaceans', 'Skorupiaki'),
       (3, 'Jaja i produkty pochodne.', 'icon_eggs', 'Jaja'),
       (4, 'Ryby i produkty pochodne.', 'icon_fish', 'Ryby'),
       (5, 'Orzeszki ziemne (orzeszki arachidowe) i produkty pochodne.', 'icon_peanuts', 'Orzeszki ziemne'),
       (6, 'Soja i produkty pochodne.', 'icon_soybeans', 'Soja'),
       (7, 'Mleko i produkty pochodne (łącznie z laktozą).', 'icon_milk', 'Mleko'),
       (8,
        'Orzechy, tj. migdały, orzechy laskowe, orzechy włoskie, orzechy nerkowca, orzechy pekan, orzechy brazylijskie, pistacje/orzech pistacjowy, orzechy makadamia i produkty pochodne.',
        'icon_nuts', 'Orzechy'),
       (9, 'Seler i produkty pochodne.', 'icon_celery', 'Seler'),
       (10, 'Gorczyca i produkty pochodne.', 'icon_mustard', 'Gorczyca'),
       (11, 'Nasiona sezamu i produkty pochodne.', 'icon_sesame', 'Nasiona sezamu'),
       (12, 'Dwutlenek siarki i siarczyny w stężeniach powyżej 10 mg/kg lub 10 mg/l w przeliczeniu na SO2.',
        'icon_sulfur_dioxide', 'Dwutlenek siarki'),
       (13, 'Łubin i produkty pochodne.', 'icon_lupin', 'Łubin'),
       (14, 'Mięczaki i produkty pochodne.', 'icon_molluscs', 'Mięczaki');

INSERT INTO labels (id, description, icon_name, name)
VALUES (1, 'Dla osób unikających mięsa i/lub produktów pochodzenia zwierzęcego.', 'icon_leaf',
        'Wegetariańskie / Wegańskie'),
       (2, 'Ważne dla osób z nietolerancją glutenu lub celiakią.', 'icon_no_gluten', 'Bezglutenowe'),
       (3, 'Dla tych, którzy lubią pikantne potrawy.', 'icon_chili_pepper', 'Ostre / Pikantne'),
       (4,
        'Niektórzy ludzie nie lubią smaku kolendry, więc taka etykieta może pomóc im uniknąć potraw zawierających tę przyprawę.',
        'icon_no_cilantro', 'Kolendra / Bez kolendry'),
       (5, 'Odpowiednie dla osób świadomych kalorii.', 'icon_light', 'Niskokaloryczne / Light'),
       (6, 'Wskazuje na potrawy charakterystyczne dla danej kuchni regionalnej.', 'icon_world_map',
        'Tradycyjne / Regionalne'),
       (7, 'Ważne dla osób z nietolerancją laktozy.', 'icon_no_lactose', 'Bez laktozy'),
       (8, 'Oznaczenie produktów z sezonu lub potraw przygotowanych z świeżych składników.', 'icon_fresh',
        'Świeże / Sezonowe'),
       (9, 'Odpowiednie dla osób ograniczających spożycie sodu.', 'icon_low_sodium', 'Niskosodowe');

INSERT INTO categories (name, is_available, created, updated)
VALUES ('Przystawki', true, NOW(), null);
INSERT INTO categories (name, is_available, created, updated)
VALUES ('Makarony', true, NOW(), null);
INSERT INTO categories (name, is_available, created, updated)
VALUES ('Sałatki', true, NOW(), null);
INSERT INTO categories (name, is_available, created, updated)
VALUES ('Zupy', true, NOW(), null);
INSERT INTO categories (name, is_available, created, updated)
VALUES ('Pizza', true, NOW(), null);
INSERT INTO categories (name, is_available, created, updated)
VALUES ('Wegetariańskie', true, NOW(), null);
INSERT INTO categories (name, is_available, created, updated)
VALUES ('Dla dzieci', true, NOW(), null);
INSERT INTO categories (name, is_available, created, updated)
VALUES ('Napoje', true, NOW(), null);

INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Pomidory', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Cebula', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Czosnek', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Oliwa z oliwek', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Bazylia', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Mozzarella', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Makaron penne', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Mięso mielone', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Papryka', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Ser parmezan', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Oregano', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Sól', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Pieprz', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Kiełbasa', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Makaron spaghetti', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Kurczak', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Szpinak', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Kapusta', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Masło', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Marchew', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Sos pomidorowy', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Cukier', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Cukinia', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Pietruszka', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Koper', '2024-03-30', '10.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Ser biały', '2024-03-30', '10.00');

INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Krewetki marynowane w cytrynie', 1, 'Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.', null,
        null, false, false, true, 19.99);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Carpaccio z polędwicy wołowej', 1, 'Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.',
        null, null, false, false, true, 24.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Krewetki w tempurze', 1, 'Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym', null, null,
        false, false, true, 22.00);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Roladki z bakłażana z feta i suszonymi pomidorami', 1,
        'Bakłażany zawijane w roladki z feta i suszonymi pomidorami, pieczone w piecu.', null, null, false, false, true,
        18.75);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Nachos z sosem serowym', 1, 'Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.',
        null, null, false, false, true, 16.99);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Spaghetti Bolognese', 2,
        'Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.',
        null, null, false, false, true, 24.00);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Penne Carbonara', 2, 'Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.',
        null, null, false, false, true, 22.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Lasagne warzywna', 2, 'Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.',
        null, null, false, false, true, 23.25);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Tagliatelle z łososiem i szpinakiem', 2,
        'Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.', null, null, false, false,
        true, 26.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Rigatoni z kurczakiem i brokułami', 2, 'Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.',
        null, null, false, false, true, 21.75);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Sałatka grecka', 3,
        'Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.', null,
        null, false, false, true, 18.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Sałatka z grillowanym kurczakiem i awokado', 3,
        'Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.', null, null, false, false, true,
        20.75);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Sałatka z rukolą, serem kozim i suszonymi żurawinami', 3,
        'Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.', null, null, false, false,
        true, 22.00);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Sałatka z grillowanym ananasem i kurczakiem', 3,
        'Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.', null, null, false, false,
        true, 21.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Sałatka z quinoa i pieczonymi warzywami', 3,
        'Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.', null, null, false, false, true,
        23.75);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Krem z pomidorów', 4, 'Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.', null, null, false,
        false, true, 15.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Rosołek z kury', 4, 'Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.', null, null, false,
        false, true, 16.25);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Zupa krem z dyni', 4, 'Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.', null, null, false,
        false, true, 17.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Zupa pomidorowa z ryżem', 4, 'Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.', null, null, false, false,
        true, 15.75);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Zupa krem z brokułów', 4, 'Delikatny krem z zielonych brokułów podawany z grzankami.', null, null, false,
        false, true, 18.00);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Pizza Margherita', 5, 'Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.', null, null,
        false, false, true, 26.00);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Pizza Pepperoni', 5, 'Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.', null, null, false,
        false, true, 28.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Pizza Capricciosa', 5, 'Pizza z szynką, pieczarkami, karczochami i oliwkami.', null, null, false, false, true,
        30.25);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Pizza Hawajska', 5, 'Pizza z szynką, ananasem i kukurydzą.', null, null, false, false, true, 27.75);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Pizza Quattro Formaggi', 5, 'Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.', null,
        null, false, false, true, 29.00);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Risotto z grzybami leśnymi', 6, 'Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.',
        null, null, false, false, true, 24.50);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Curry z ciecierzycą i szpinakiem', 6,
        'Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.', null, null, false, false, true,
        22.75);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Naleśniki z serem i szpinakiem', 6,
        'Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.', null, null, false, false,
        true, 20.00);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Falafel w pitce z hummusem', 6, 'Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.',
        null, null, false, false, true, 21.25);
INSERT INTO menu_items (name, category_id, description, created, updated, is_bestseller, is_new, is_available, price)
VALUES ('Makaron z pesto bazyliowym', 6,
        'Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.', null, null, false, false,
        true, 23.00);

INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Z szpinakiem', '20.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Z konfiturą cebulową', '24.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '28.50', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '32.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '38.90', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '30.25', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '35.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '41.20', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '27.75', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '32.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '38.20', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '29.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '35.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '40.20', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '24.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '29.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '35.20', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '19.99', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '24.50', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '22.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '18.75', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '16.99', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '24.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '22.50', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '26.50', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '21.75', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '18.50', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '20.75', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '22.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '21.50', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '23.75', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '15.50', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '16.25', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '17.50', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '15.75', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '18.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '29.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '22.75', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '20.00', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '21.25', null);
INSERT INTO menu_item_variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, '', '23.00', null);

INSERT INTO restaurants (address, name)
VALUES ('ul. Główna 123, Miastowo, Województwo, 54321', 'Rarytas');
INSERT INTO restaurants (address, name)
VALUES ('ul. Dębowa 456, Miasteczko, Wiejskie, 98765', 'Wykwintna Bistro');

INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (1, true, '19436a86-e200-400d-aa2e-da4686805d00', false, false, 4, 1);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (2, true, '79d8684f-333e-4275-a317-fa06d46fa6b6', false, false, 4, 2);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (3, false, '0ce8beb3-6fb1-42f1-9c95-05cf9fb88d27', false, false, 4, 3);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (4, false, '5afb9629-990a-4934-87f2-793b1aa2f35e', false, false, 4, 4);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (5, true, '58d77e24-6b8c-41a9-b24c-a67602deacdd', false, false, 4, 5);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (6, false, '59ebc00c-b580-4dff-9788-2df90b1d4bba', false, false, 4, 6);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (7, false, 'ef303854-6faa-4615-8d47-6f3686086586', false, false, 4, 7);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (8, false, '97cba027-ae47-4c42-8828-f4b3b3506d0c', false, false, 4, 8);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (9, false, 'fe2cce7c-7c4c-4076-9eb4-3e91b440fec2', false, false, 4, 9);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (10, false, '88ca9c82-e630-40f2-9bf9-47f7d14f6bff', false, false, 4, 10);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (11, false, 'c88a6029-4f29-4ee1-8d8f-f31f7a554301', false, false, 4, 11);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (12, true, 'd565c73a-8d87-4a79-9e3f-7b6a02520e71', false, false, 4, 12);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (13, false, '6696c583-a312-4b24-9716-430826ad1e96', false, false, 4, 13);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (14, false, 'a65896cb-805d-4d7b-849b-1d53e78f3191', false, false, 4, 14);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (15, false, '65b6bb94-da99-4ced-8a94-5860fe95e708', false, false, 4, 15);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (16, false, '2fd07320-a841-48ad-9f3f-35b307014b2a', false, false, 4, 16);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (17, false, '3740c35f-5759-4eb8-ab00-cb3807707235', false, false, 4, 17);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (18, false, '480407f1-13bd-45a7-bad7-d0e2b76e5ebf', false, false, 4, 18);
INSERT INTO restaurant_tables (id, is_active, token, waiter_called, bill_requested, max_num_of_ppl, number)
VALUES (19, true, '96fb4431-af22-48f4-9e4c-40b5774d9ab2', false, false, 1, 19);

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

INSERT INTO history_ordered_items (id, quantity, menu_item_id, is_ready_to_serve)
VALUES (222, 2, 3, false);
INSERT INTO history_ordered_items (id, quantity, menu_item_id, is_ready_to_serve)
VALUES (333, 3, 2, false);
INSERT INTO history_ordered_items (id, quantity, menu_item_id, is_ready_to_serve)
VALUES (444, 2, 3, false);
INSERT INTO history_ordered_items (id, quantity, menu_item_id, is_ready_to_serve)
VALUES (555, 3, 2, false);

INSERT INTO history_orders (id,
                            is_for_take_away,
                            is_resolved,
                            order_date,
                            order_time,
                            total_amount,
                            restaurant_id,
                            table_id)
VALUES (12, false, true, '2024-01-29', '08:29:20.738823', 44.00, 1, 1);
INSERT INTO history_orders (id,
                            is_for_take_away,
                            is_resolved,
                            order_date,
                            order_time,
                            total_amount,
                            restaurant_id,
                            table_id)
VALUES (13, false, true, '2024-02-21', '08:29:20.738823', 93.50, 1, 2);
INSERT INTO history_orders (id,
                            is_for_take_away,
                            is_resolved,
                            order_date,
                            order_time,
                            total_amount,
                            restaurant_id,
                            table_id)
VALUES (14, true, true, '2024-01-29', '08:29:20.738823', 54.00, 1, 19);
INSERT INTO history_orders (id,
                            is_for_take_away,
                            is_resolved,
                            order_date,
                            order_time,
                            total_amount,
                            restaurant_id,
                            table_id)
VALUES (15, true, true, '2024-02-21', '08:29:20.738823', 73.50, 1, 19);

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
                      employee_session_time, is_order_comment_allowed)
VALUES (1, 3, '07:00:00', '23:00:00', 1, 120, 3, 20, false);

INSERT INTO users (created, email, enabled, password, updated, username, email_token, jwt_token_id, name, phone_number,
                   surname)
VALUES ('2024-01-20 12:04:00.000000', 'matimemek@test.com', 1,
        '$2a$10$z/0edEimosa3QjYYxjiHuO8bNZHfI3jxDVwqDNd5bc2vCr5TERDz6', '2024-02-02 20:54:41.531670', 'mati', null,
        null,
        'Mati', '+48 123 123 123', 'Memek');
INSERT INTO users (created, email, enabled, password, updated, username, email_token, jwt_token_id, name, phone_number,
                   surname)
VALUES ('2024-01-20 19:09:00.000000', 'admin@example.com', 1,
        '$2a$04$OI8NalP4M4rxpRFgVR3eO.8C/6hmP.AdYadtTPd3BLHm3zx3wLLWm', '2024-02-04 07:50:29.047589', 'admin',
        null, null, '', '', '');
INSERT INTO users (created, email, enabled, password, updated, username, email_token, jwt_token_id, name, phone_number,
                   surname)
VALUES ('2024-01-24 19:06:36.680304', 'netka@test.com', 1,
        '$2a$10$ViUyMtRUmZgeZWRBME67g.Wp3K9p8UyJPfQd2GB9uXbQnBiDe4FJ.', null, 'neta', null, null, null, null, null);
INSERT INTO users (created, email, enabled, password, updated, username, email_token, jwt_token_id, name, phone_number,
                   surname)
VALUES ('2024-02-03 10:21:00.000000', 'kucharz@antek.pl', 1,
        '$2a$10$.HWarZkysOgBF0/tOXmmtONdRkZHGZCsRFs27Q7FcNrDc4bSzE0fW', '2024-02-03 10:33:07.307903', 'kucharz', null,
        null,
        'ada', '', 'asdqwe');
INSERT INTO users (created, email, enabled, password, updated, username, email_token, jwt_token_id, name, phone_number,
                   surname)
VALUES ('2024-02-03 10:24:02.744722', 'restaurator@rarytas.pl', 1,
        '$2a$10$tykyevzP4v1WV/FyuYWNOO6wspbmAHnzI.deEAZQU6SA8NSxod3Vy', null, 'owner', null, null, 'Właściciel', '',
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
