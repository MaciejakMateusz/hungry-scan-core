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

INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Przystawki', true, NOW(), null, false, 1);
INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Makarony', true, NOW(), null, false, 2);
INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Sałatki', true, NOW(), null, false, 3);
INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Zupy', true, NOW(), null, false, 4);
INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Pizza', true, NOW(), null, false, 5);
INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Wegetariańskie', true, NOW(), null, false, 6);
INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Dla dzieci', true, NOW(), null, false, 7);
INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Napoje', true, NOW(), null, true, 8);
INSERT INTO categories (name, is_available, created, updated, is_bar_served, display_order)
VALUES ('Pusta', true, NOW(), null, false, 9);

INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Pomidory', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Cebula', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Czosnek', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Oliwa z oliwek', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Bazylia', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Mozzarella', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Makaron penne', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Mięso mielone', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Papryka', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Ser parmezan', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Oregano', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Sól', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Pieprz', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Kiełbasa', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Makaron spaghetti', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Kurczak', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Szpinak', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Kapusta', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Masło', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Marchew', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Sos pomidorowy', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Cukier', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Cukinia', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Pietruszka', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Koper', '2024-03-30', '4.00');
INSERT INTO ingredients(created, is_available, name, updated, price)
VALUES ('2024-03-30', 1, 'Ser biały', '2024-03-30', '4.00');

INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Krewetki marynowane w cytrynie', 1, 1, 'Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.', null,
        null, false, false, true, 19.99, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Carpaccio z polędwicy wołowej', 1, 2, 'Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.',
        null, null, false, false, true, 24.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Krewetki w tempurze', 1, 3, 'Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym', null, null,
        false, false, true, 22.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Roladki z bakłażana', 1, 4,
        'Dostępne w różnych wariantach', null, null, false, false, true,
        18.75, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Nachos z sosem serowym', 1, 5, 'Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.',
        null, null, false, false, true, 16.99, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Spaghetti Bolognese', 2, 1,
        'Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.',
        null, null, false, false, true, 24.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Penne Carbonara', 2, 2, 'Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.',
        null, null, false, false, true, 22.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Lasagne warzywna', 2, 3, 'Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.',
        null, null, false, false, true, 23.25, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Tagliatelle z łososiem i szpinakiem', 2, 4,
        'Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.', null, null, false, false,
        true, 26.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Rigatoni z kurczakiem i brokułami', 2, 5, 'Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.',
        null, null, false, false, true, 21.75, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Sałatka grecka', 3, 1,
        'Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.', null,
        null, false, false, true, 18.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Sałatka z grillowanym kurczakiem i awokado', 3, 2,
        'Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.', null, null, false, false, true,
        20.75, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Sałatka z rukolą, serem kozim i suszonymi żurawinami', 3, 3,
        'Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.', null, null, false, false,
        true, 22.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Sałatka z grillowanym ananasem i kurczakiem', 3, 4,
        'Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.', null, null, false, false,
        true, 21.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Sałatka z quinoa i pieczonymi warzywami', 3, 5,
        'Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.', null, null, false, false, true,
        23.75, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Krem z pomidorów', 4, 1, 'Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.', null, null, false,
        false, true, 15.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Rosołek z kury', 4, 2, 'Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.', null, null, false,
        false, true, 16.25, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Zupa krem z dyni', 4, 3, 'Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.', null, null, false,
        false, true, 17.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Zupa pomidorowa z ryżem', 4, 4, 'Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.', null, null, false, false,
        true, 15.75, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Zupa krem z brokułów', 4, 5, 'Delikatny krem z zielonych brokułów podawany z grzankami.', null, null, false,
        false, true, 18.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Pizza Margherita', 5, 1, 'Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.', null, null,
        false, false, true, 26.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Pizza Pepperoni', 5, 2, 'Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.', null, null, false,
        false, true, 28.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Pizza Capricciosa', 5, 3, 'Pizza z szynką, pieczarkami, karczochami i oliwkami.', null, null, false, false, true,
        30.25, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Pizza Hawajska', 5, 4, 'Pizza z szynką, ananasem i kukurydzą.', null, null, false, false, true, 27.75, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Pizza Quattro Formaggi', 5, 5, 'Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.', null,
        null, false, false, true, 29.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Risotto z grzybami leśnymi', 6, 1, 'Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.',
        null, null, false, false, true, 24.50, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Curry z ciecierzycą i szpinakiem', 6, 2,
        'Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.', null, null, false, false, true,
        22.75, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Naleśniki z serem i szpinakiem', 6, 3,
        'Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.', null, null, false, false,
        true, 20.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Falafel w pitce z hummusem', 6, 4, 'Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.',
        null, null, false, false, true, 21.25, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Makaron z pesto bazyliowym', 6, 5,
        'Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.', null, null, false, false,
        true, 23.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Kawa', 8, 1,
        'Czarna sypana.', null, null, false, false, true, 9.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Sok pomarańczowy', 8, 2,
        'Świeżo wyciskany.', null, null, false, false, true, 7.00, 0);
INSERT INTO menu_items (name, category_id, display_order, description, created, updated, is_bestseller, is_new, is_available, price, counter)
VALUES ('Coca-cola', 8, 3,
        '250ml', null, null, false, false, true, 7.00, 0);

INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (4, 1);
INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (4, 5);
INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (4, 6);
INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (21, 6);
INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (21, 16);
INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (21, 2);
INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (22, 6);
INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (22, 16);
INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id) VALUES (22, 2);

INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Z szpinakiem', '0.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Z konfiturą cebulową', '4.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '0.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '5.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '10.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '0.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '5.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '10.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '0.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '5.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '10.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '0.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '5.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '10.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, true, 'Mała', '0.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Średnia', '5.00', null);
INSERT INTO variants(created, is_available, is_default_variant, name, price, updated)
VALUES (NOW(), true, false, 'Duża', '10.00', null);

INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (4, 1);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (4, 2);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (21, 3);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (21, 4);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (21, 5);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (22, 6);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (22, 7);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (22, 8);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (23, 9);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (23, 10);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (23, 11);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (24, 12);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (24, 13);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (24, 14);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (25, 15);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (25, 16);
INSERT INTO menu_items_variants(menu_item_id, variants_id)
VALUES (25, 17);

INSERT INTO restaurants (address, name)
VALUES ('ul. Główna 123, Miastowo, Województwo, 54321', 'Rarytas');
INSERT INTO restaurants (address, name)
VALUES ('ul. Dębowa 456, Miasteczko, Wiejskie, 98765', 'Wykwintna Bistro');

INSERT INTO zones(name, created, updated, display_order, is_visible)
VALUES ('Sekcja 1', NOW(), null, 1, true);
INSERT INTO zones(name, created, updated, display_order, is_visible)
VALUES ('Sekcja 2', NOW(), null, 2, true);
INSERT INTO zones(name, created, updated, display_order, is_visible)
VALUES ('Piętro II', NOW(), null, 4, true);
INSERT INTO zones(name, created, updated, display_order, is_visible)
VALUES ('Loża VIP', NOW(), null, 3, true);

INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '19436a86-e200-400d-aa2e-da4686805d00', false, false, 4, 1, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '79d8684f-333e-4275-a317-fa06d46fa6b6', false, false, 4, 2, true, 2, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '0ce8beb3-6fb1-42f1-9c95-05cf9fb88d27', false, false, 4, 3, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '5afb9629-990a-4934-87f2-793b1aa2f35e', false, false, 4, 4, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '58d77e24-6b8c-41a9-b24c-a67602deacdd', false, false, 4, 5, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '59ebc00c-b580-4dff-9788-2df90b1d4bba', false, false, 4, 6, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, 'ef303854-6faa-4615-8d47-6f3686086586', false, false, 4, 7, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '97cba027-ae47-4c42-8828-f4b3b3506d0c', false, false, 4, 8, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, 'fe2cce7c-7c4c-4076-9eb4-3e91b440fec2', false, false, 4, 9, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '88ca9c82-e630-40f2-9bf9-47f7d14f6bff', false, false, 4, 10, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, 'c88a6029-4f29-4ee1-8d8f-f31f7a554301', false, false, 4, 11, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, 'd565c73a-8d87-4a79-9e3f-7b6a02520e71', false, false, 4, 12, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '6696c583-a312-4b24-9716-430826ad1e96', false, false, 4, 13, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, 'a65896cb-805d-4d7b-849b-1d53e78f3191', false, false, 4, 14, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '65b6bb94-da99-4ced-8a94-5860fe95e708', false, false, 4, 15, true, 2, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '2fd07320-a841-48ad-9f3f-35b307014b2a', false, false, 4, 16, true, 2, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '3740c35f-5759-4eb8-ab00-cb3807707235', false, false, 4, 17, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '480407f1-13bd-45a7-bad7-d0e2b76e5ebf', false, false, 4, 18, true, null, false, null);
INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '96fb4431-af22-48f4-9e4c-40b5774d9ab2', false, false, 1, 19, true, null, false, null);

INSERT INTO role (name, displayed_name)
VALUES ('ROLE_WAITER', 'Kelner');
INSERT INTO role (name, displayed_name)
VALUES ('ROLE_ADMIN', 'Administrator');
INSERT INTO role (name, displayed_name)
VALUES ('ROLE_MANAGER', 'Menadżer');
INSERT INTO role (name, displayed_name)
VALUES ('ROLE_COOK', 'Kucharz');
INSERT INTO role (name, displayed_name)
VALUES ('ROLE_CUSTOMER', 'Klient');
INSERT INTO role (name, displayed_name)
VALUES ('ROLE_CUSTOMER_READONLY', 'Klient (tylko odczyt)');

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

INSERT INTO jwt_tokens (token, created)
VALUES ('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZjNhYmY4LTliNmEiLCJpYXQiOjE3MTM4MTAzNTUsImV4cCI6MTcxMzg4MjM1NX0.EhIv7CDkpXcfXFHeihyju6bdUS2Te41a-m3GaxRWKHM',
        '2024-04-23 12:50:41.531670');
INSERT INTO jwt_tokens (token, created)
VALUES ('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyYzczYmZjLTE2ZmMiLCJpYXQiOjE3MTM4MTA5NDcsImV4cCI6MTcxMzg4Mjk0N30.NExb3606nYuZgxQa4-jOrlk2PM4CoKj9pyz25XtZhl0',
        '2024-04-23 12:50:41.531670');
INSERT INTO jwt_tokens (token, created)
VALUES ('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwYzllNjgzLTg1NzYiLCJpYXQiOjE3MTM4ODA5MjMsImV4cCI6MTcxMzk1MjkyM30.M28dOa0W5FApG8p2sgfUhLHylHO4hM5bAgOOgF2k5oU',
        NOW());

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
INSERT INTO users (created, email, email_token, enabled, name, password, phone_number, surname, updated, username,
                   jwt_token_id)
VALUES ('2024-04-23 12:50:41.531670', 'ff3abf8-9b6a@temp.it', null, 1, null,
        '$2a$10$fb4q1jBqnMLDkUBi2YXQ4eHZ0M17bP5gxzwU84UwCkEUbyekGRDlC',
        null, null, null, 'ff3abf8-9b6a', 1);
INSERT INTO users (created, email, email_token, enabled, name, password, phone_number, surname, updated, username,
                   jwt_token_id)
VALUES ('2024-04-23 12:50:41.531670', '2c73bfc-16fc@temp.it', null, 1, null,
        '$2a$10$0F.xiCJux5So7.C6GJEWyeLkBiKlfYFXUS9jr9W5y4GinZgmxv5v.',
        null, null, null, '2c73bfc-16fc', 2);
INSERT INTO users (created, email, email_token, enabled, name, password, phone_number, surname, updated, username,
                   jwt_token_id)
VALUES (NOW(), '0c9e683-8576@temp.it', null, 1, null, '$2a$10$cn1IjWjjz4QBcfukawrzw.FkwxgFpYOUs/rBtg2k9b5xoPKiHZsvW',
        null, null, null, '0c9e683-8576', 3);

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
INSERT INTO user_role (user_id, role_id)
VALUES (6, 5);
INSERT INTO user_role (user_id, role_id)
VALUES (7, 6);
INSERT INTO user_role (user_id, role_id)
VALUES (8, 5);
