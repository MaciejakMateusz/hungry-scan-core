INSERT IGNORE INTO restaurants (id, address, name, postal_code, city, token, created)
VALUES (1, 'Turystyczna 12/12', 'Rarytas', '44-335', 'Jastrzębie-Zdrój', '3f979e48-e7eb-4669-8084-72543c8538f0',
        '2023-11-12T00:00:00Z');
INSERT IGNORE INTO restaurants (id, address, name, postal_code, city, token, created)
VALUES (2, 'Katowicka 12', 'Tajska', '40-004', 'Katowice', null, '2024-05-01T00:00:00Z');

INSERT IGNORE INTO menus (id, is_all_day, name, restaurant_id)
VALUES (1, true, 'Całodniowe', 1);
INSERT IGNORE INTO menus (id, is_all_day, name, restaurant_id)
VALUES (2, true, 'Menu', 2);

INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
(1, 'Kelner', 'Waiter');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
(2, 'Administrator', 'Admin');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
(3, 'Menadżer', 'Manager');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
(4, 'Kucharz', 'Cook');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
(5, 'Klient', 'Customer');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
(6, 'Klient (tylko odczyt)', 'Customer (read only)');

INSERT IGNORE INTO settings (id, booking_duration, opening_time, closing_time, language, capacity,
                             customer_session_time,
                             employee_session_time, order_comment_allowed, waiter_comment_allowed, restaurant_id)
VALUES (1, 3, '10:00:00', '23:00:00', 1, 120, 20, 20, true, true, 1);

INSERT IGNORE INTO role (id, name, translatable_displayed_name_id)
VALUES (1, 'ROLE_WAITER', 1);
INSERT IGNORE INTO role (id, name, translatable_displayed_name_id)
VALUES (2, 'ROLE_ADMIN', 2);
INSERT IGNORE INTO role (id, name, translatable_displayed_name_id)
VALUES (3, 'ROLE_MANAGER', 3);
INSERT IGNORE INTO role (id, name, translatable_displayed_name_id)
VALUES (4, 'ROLE_COOK', 4);
INSERT IGNORE INTO role (id, name, translatable_displayed_name_id)
VALUES (5, 'ROLE_CUSTOMER', 5);
INSERT IGNORE INTO role (id, name, translatable_displayed_name_id)
VALUES (6, 'ROLE_CUSTOMER_READONLY', 6);

INSERT IGNORE INTO users (id, organization_id, created, email, enabled, password, updated, username, email_token,
                          jwt_token_id, forename, phone_number, surname, active_restaurant_id, active_menu_id)
VALUES (1, 1, NOW(), 'admin@example.com', 1,
        '$2y$10$S4Qu.8BEsEqHftYQmDcQ2.mKi5yXi9XRU8IlHBgvQ./N/UYIVhXAG', null, 'admin@example.com', null, null, 'Admin',
        '',
        'Admin', 1, 1);

INSERT IGNORE INTO users (id, organization_id, created, email, enabled, password, updated, username, email_token,
                          jwt_token_id, forename, phone_number, surname, active_restaurant_id, active_menu_id)
VALUES (2, 2, NOW(), 'mati@test.com', 1,
        '$2y$10$S4Qu.8BEsEqHftYQmDcQ2.mKi5yXi9XRU8IlHBgvQ./N/UYIVhXAG', null, 'mati@test.com', null, null, '', '',
        '', null, null);

INSERT IGNORE INTO users_restaurants (user_id, restaurant_id)
VALUES (1, 1);

INSERT IGNORE INTO user_role (user_id, role_id)
VALUES (1, 2);
INSERT IGNORE INTO user_role (user_id, role_id)
VALUES (2, 2);

INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (7, 'Gluten', 'Gluten');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (8, 'Zboża zawierające gluten (tj. pszenica, żyto, jęczmień, owies, pszenica orkisz, lub ich odmiany hybrydowe) oraz produkty pochodne.', 'Cereals containing gluten (i.e. wheat, rye, barley, oats, spelled wheat, or their hybrid varieties) and derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (9, 'Skorupiaki', 'Crustaceans');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (10, 'Skorupiaki i produkty pochodne.', 'Crustaceans and derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (11, 'Jaja', 'Eggs');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (12, 'Jaja i produkty pochodne.', 'Eggs and derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (13, 'Ryby', 'Fish');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (14, 'Ryby i produkty pochodne.', 'Fish and derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (15, 'Orzeszki ziemne', 'Peanuts');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (16, 'Orzeszki ziemne (orzeszki arachidowe) i produkty pochodne.', 'Peanuts and related products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (17, 'Soja', 'Soybeans');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (18, 'Soja i produkty pochodne.', 'Soybeans and derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (19, 'Mleko', 'Milk');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (20, 'Mleko i produkty pochodne (łącznie z laktozą).', 'Milk and derived products (including lactose).');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (21, 'Orzechy', 'Nuts');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (22, 'Orzechy, tj. migdały, orzechy laskowe, orzechy włoskie, orzechy nerkowca, orzechy pekan, orzechy brazylijskie, pistacje/orzech pistacjowy, orzechy makadamia i produkty pochodne.', 'Nuts, i.e. almonds, hazelnuts, walnuts, cashews, pecans, Brazil nuts, pistachios/pistachio nuts, macadamia nuts and related products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (23, 'Seler', 'Celery');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (24, 'Seler i produkty pochodne.', 'Celery amd derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (25, 'Gorczyca', 'Mustard');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (26, 'Gorczyca i produkty pochodne.', 'Mustard and derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (27, 'Nasiona sezamu', 'Sesame');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (28, 'Nasiona sezamu i produkty pochodne.', 'Sesame and derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (29, 'Dwutlenek siarki', 'Sulfur dioxide');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (30, 'Dwutlenek siarki i siarczyny w stężeniach powyżej 10 mg/kg lub 10 mg/l w przeliczeniu na SO2.', 'Sulfur dioxide and sulphites in concentrations above 10 mg/kg or 10 mg/l expressed as SO2.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (31, 'Łubin', 'Lupin');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (32, 'Łubin i produkty pochodne.', 'Lupin and derived products.');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (33, 'Mięczaki', 'Molluscs');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (34, 'Mięczaki i produkty pochodne.', 'Molluscs and derived products.');

INSERT IGNORE INTO allergens(id, restaurant_id, translatable_description_id, icon_name, translatable_name_id)
VALUES (1, 1, 8, 'gluten.svg', 7),
       (2, 1, 10, 'crustaceans.svg', 9),
       (3, 1, 12, 'eggs.svg', 11),
       (4, 1, 14, 'fish.svg', 13),
       (5, 1, 16, 'peanuts.svg', 15),
       (6, 1, 18, 'soy.svg', 17),
       (7, 1, 20, 'milk.svg', 19),
       (8, 1, 22, 'nuts.svg', 21),
       (9, 1, 24, 'celery.svg', 23),
       (10, 1, 26, 'mustard.svg', 25),
       (11, 1, 28, 'sesame.svg', 27),
       (12, 1, 30, 'SO2.svg', 29),
       (13, 1, 32, 'lupine.svg', 31),
       (14, 1, 34, 'molluscs.svg', 33);

INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (35, 'Bez glutenu', 'Gluten free');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (36, 'Wegańskie', 'Vegan');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (37, 'Wegetariańskie', 'Vegetarian');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (38, 'Kolendra', 'Coriander');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (39, 'Bez laktozy', 'Lactose free');
INSERT IGNORE INTO translatable (id, default_translation, translation_en) VALUES
    (40, 'Ostre', 'Spicy');

INSERT IGNORE INTO labels (id, restaurant_id, icon_name, translatable_name_id)
VALUES (1, 1, 'gluten-free.svg', 35),
       (2, 1, 'vegan.svg', 36),
       (3, 1, 'vegetarian.svg', 37),
       (4, 1, 'coriander.svg', 38),
       (5, 1, 'lactose-free.svg', 39),
       (6, 1, 'spicy.svg', 40);

INSERT IGNORE INTO themes(id, name, active, restaurant_id)
VALUES (1, 'green', true, 1),
       (2, 'pink', false, 1),
       (3, 'grey', false, 1),
       (4, 'orange', false, 1);

INSERT IGNORE INTO onboarding_images(id, restaurant_id, image_name, is_active)
VALUES (1, 1, 'default', true);

INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '19436a86-e200-400d-aa2e-da4686805d00', false, false, 999, 1, true, null, false, null);

INSERT IGNORE INTO translatable(id, default_translation, translation_en)
VALUES (41, 'Przystawki', 'Starters');
INSERT IGNORE INTO translatable(id, default_translation, translation_en)
VALUES (42, 'Makarony', 'Pastas');
INSERT IGNORE INTO translatable(id, default_translation, translation_en)
VALUES (43, 'Sałatki', 'Salads');
INSERT IGNORE INTO translatable(id, default_translation, translation_en)
VALUES (44, 'Zupy', 'Soups');
INSERT IGNORE INTO translatable(id, default_translation, translation_en)
VALUES (45, 'Pizza', 'Pizza');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (46, 'Wegetariańskie');
INSERT IGNORE INTO translatable(id, default_translation, translation_en)
VALUES (47, 'Dla dzieci', 'For kids');
INSERT IGNORE INTO translatable(id, default_translation, translation_en)
VALUES (48, 'Napoje', 'Drinks');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (49, 'Pusta');

INSERT IGNORE INTO translatable(id, default_translation)
VALUES (50, 'Krewetki marynowane w cytrynie');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (51, 'Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (52, 'Carpaccio z polędwicy wołowej');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (53, 'Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (54, 'Krewetki w tempurze');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (55, 'Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (56, 'Roladki z bakłażana');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (57, 'Dostępne w różnych wariantach');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (58, 'Nachos z sosem serowym');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (59, 'Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (60, 'Spaghetti Bolognese');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (61, 'Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (62, 'Penne Carbonara');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (63, 'Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (64, 'Lasagne warzywna');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (65, 'Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (66, 'Tagliatelle z łososiem i szpinakiem');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (67, 'Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (68, 'Rigatoni z kurczakiem i brokułami');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (69, 'Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (70, 'Sałatka grecka');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (71, 'Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (72, 'Sałatka z grillowanym kurczakiem i awokado');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (73, 'Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (74, 'Sałatka z rukolą, serem kozim i suszonymi żurawinami');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (75, 'Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (76, 'Sałatka z grillowanym ananasem i kurczakiem');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (77, 'Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (78, 'Sałatka z quinoa i pieczonymi warzywami');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (79, 'Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (80, 'Krem z pomidorów');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (81, 'Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (82, 'Rosołek z kury');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (83, 'Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (84, 'Zupa krem z dyni');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (85, 'Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (86, 'Zupa pomidorowa z ryżem');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (87, 'Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (88, 'Zupa krem z brokułów');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (89, 'Delikatny krem z zielonych brokułów podawany z grzankami.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (90, 'Pizza Margherita');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (91, 'Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (92, 'Pizza Pepperoni');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (93, 'Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (94, 'Pizza Capricciosa');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (95, 'Pizza z szynką, pieczarkami, karczochami i oliwkami.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (96, 'Pizza Hawajska');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (97, 'Pizza z szynką, ananasem i kukurydzą.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (98, 'Pizza Quattro Formaggi');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (99, 'Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (100, 'Risotto z grzybami leśnymi');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (101, 'Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (102, 'Curry z ciecierzycą i szpinakiem');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (103, 'Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (104, 'Naleśniki z serem i szpinakiem');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (105, 'Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (106, 'Falafel w pitce z hummusem');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (107, 'Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (108, 'Makaron z pesto bazyliowym');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (109, 'Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (110, 'Kawa');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (111, 'Czarna sypana.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (112, 'Sok pomarańczowy');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (113, 'Świeżo wyciskany.');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (114, 'Coca-cola');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (115, '250ml');

INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (1, 1, 41, true, NOW(), null, false, 1);
INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (2, 1, 42, true, NOW(), null, false, 2);
INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (3, 1, 43, true, NOW(), null, false, 3);
INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (4, 1, 44, true, NOW(), null, false, 4);
INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (5, 1, 45, true, NOW(), null, false, 5);
INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (6, 1, 46, true, NOW(), null, false, 6);
INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (7, 1, 47, true, NOW(), null, false, 7);
INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (8, 1, 48, true, NOW(), null, true, 8);
INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (9, 1, 49, true, NOW(), null, false, 9);

INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (1, 50, 1, 51, null, null, false, false, true, true, 19.99, 0, false, 1);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (2, 52, 2, 53, null, null, false, false, true, true, 24.50, 0, false, 1);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (3, 54, 3, 55, null, null, false, false, true, true, 22.00, 0, false, 1);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (4, 56, 4, 57, null, null, false, false, true, true, 18.75, 0, false, 1);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (5, 58, 5, 59, null, null, false, false, true, true, 16.99, 0, false, 1);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (6, 60, 1, 61, null, null, false, false, true, true, 24.00, 0, false, 2);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (7, 62, 2, 63, null, null, false, false, true, true, 22.50, 0, false, 2);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (8, 64, 3, 65, null, null, false, false, true, true, 23.25, 0, false, 2);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (9, 66, 4, 67, null, null, false, false, true, true, 26.50, 0, false, 2);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (10, 68, 5, 69, null, null, false, false, true, true, 21.75, 0, false, 2);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (11, 70, 1, 71, null, null, false, false, true, true, 18.50, 0, false, 3);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (12, 72, 2, 73, null, null, false, false, true, true, 20.75, 0, false, 3);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (13, 74, 3, 75, null, null, false, false, true, true, 22.00, 0, false, 3);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (14, 76, 4, 77, null, null, false, false, true, true, 21.50, 0, false, 3);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (15, 78, 5, 79, null, null, false, false, true, true, 23.75, 0, false, 3);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (16, 80, 1, 81, null, null, false, false, true, true, 15.50, 0, false, 4);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (17, 82, 2, 83, null, null, false, false, true, true, 16.25, 0, false, 4);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (18, 84, 3, 85, null, null, false, false, true, true, 17.50, 0, false, 4);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (19, 86, 4, 87, null, null, false, false, true, true, 15.75, 0, false, 4);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (20, 88, 5, 89, null, null, false, false, true, true, 18.00, 0, false, 4);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id,
                               image_name)
VALUES (21, 90, 1, 91, null, null, false, false, true, true, 26.00, 0, false, 5, 'pizza1.jpg');
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id,
                               image_name)
VALUES (22, 92, 2, 93, null, null, false, false, true, true, 28.50, 0, false, 5, 'pizza2.jpg');
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id,
                               image_name)
VALUES (23, 94, 3, 95, null, null, false, false, true, true, 30.25, 0, false, 5, 'pizza3.jpg');
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id,
                               image_name)
VALUES (24, 96, 4, 97, null, null, false, false, true, true, 27.75, 0, false, 5, 'pizza4.jpg');
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id,
                               image_name)
VALUES (25, 98, 5, 99, null, null, false, false, true, true, 29.00, 0, false, 5, 'pizza5.jpg');
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (26, 100, 1, 101, null, null, false, false, true, true, 24.50, 0, false, 6);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (27, 102, 2, 103, null, null, false, false, true, true, 22.75, 0, false, 6);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (28, 104, 3, 105, null, null, false, false, true, true, 20.00, 0, false, 6);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (29, 106, 4, 107, null, null, false, false, true, true, 21.25, 0, false, 6);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (30, 108, 5, 109, null, null, false, false, true, true, 23.00, 0, false, 6);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (31, 110, 1, 111, null, null, false, false, true, true, 9.00, 0, true, 8);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (32, 112, 2, 113, null, null, false, false, true, true, 7.00, 0, true, 8);
INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               is_bestseller, is_new, available, visible, price, counter, bar_served,
                               category_id)
VALUES (33, 114, 3, 115, null, null, false, false, true, true, 7.00, 0, true, 8);

INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (1, 1);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (1, 2);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (1, 3);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (1, 4);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (1, 5);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (2, 6);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (2, 7);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (2, 8);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (2, 9);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (2, 10);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (3, 11);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (3, 12);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (3, 13);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (3, 14);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (3, 15);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (4, 16);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (4, 17);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (4, 18);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (4, 19);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (4, 20);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (5, 21);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (5, 22);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (5, 23);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (5, 24);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (5, 25);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (6, 26);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (6, 27);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (6, 28);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (6, 29);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (6, 30);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (8, 31);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (8, 32);
INSERT IGNORE INTO categories_menu_items(category_id, menu_items_id)
VALUES (8, 33);

INSERT IGNORE INTO translatable(id, default_translation)
VALUES (116, 'Z szpinakiem');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (117, 'Z konfiturą cebulową');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (118, 'Mała');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (119, 'Średnia');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (120, 'Duża');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (121, 'Mała');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (122, 'Średnia');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (123, 'Duża');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (124, 'Mała');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (125, 'Średnia');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (126, 'Duża');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (127, 'Mała');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (128, 'Średnia');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (129, 'Duża');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (130, 'Mała');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (131, 'Średnia');
INSERT IGNORE INTO translatable(id, default_translation)
VALUES (132, 'Duża');

INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (1, NOW(), true, true, 116, '0.00', null, 4, 1);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (2, NOW(), true, false, 117, '4.00', null, 4, 2);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (3, NOW(), true, true, 118, '0.00', null, 21, 1);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (4, NOW(), true, false, 119, '5.00', null, 21, 2);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (5, NOW(), true, false, 120, '10.00', null, 21, 3);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (6, NOW(), true, true, 121, '0.00', null, 22, 1);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (7, NOW(), true, false, 122, '5.00', null, 22, 2);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (8, NOW(), true, false, 123, '10.00', null, 22, 3);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (9, NOW(), true, true, 124, '0.00', null, 23, 1);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (10, NOW(), true, false, 125, '5.00', null, 23, 2);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (11, NOW(), true, false, 126, '10.00', null, 23, 3);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (12, NOW(), true, true, 127, '0.00', null, 24, 1);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (13, NOW(), true, false, 128, '5.00', null, 24, 2);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (14, NOW(), true, false, 129, '10.00', null, 24, 3);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (15, NOW(), true, true, 130, '0.00', null, 25, 1);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (16, NOW(), true, false, 131, '5.00', null, 25, 2);
INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (17, NOW(), true, false, 132, '10.00', null, 25, 3);

INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (133, 'Pomidory', 'Tomatoes');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (134, 'Cebula', 'Onion');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (135, 'Czosnek', 'Garlic');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (136, 'Oliwa z oliwek', 'Olive oil');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (137, 'Bazylia', 'Basil');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (138, 'Mozzarella', 'Mozzarella');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (139, 'Makaron penne', 'Pasta penne');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (140, 'Mięso mielone', 'Minced meat');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (141, 'Papryka', 'Bell pepper');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (142, 'Ser parmezan', 'Parmesan cheese');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (144, 'Oregano', 'Oregano');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (145, 'Sól', 'Salt');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (146, 'Pieprz', 'Pepper');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (147, 'Kiełbasa', 'Sausage');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (148, 'Makaron spaghetti', 'Spaghetti pasta');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (149, 'Kurczak', 'Chicken');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (150, 'Szpinak', 'Spinach');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (151, 'Kapusta', 'Cabbage');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (152, 'Masło', 'Butter');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (153, 'Marchew', 'Carrot');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (154, 'Sos pomidorowy', 'Tomato sauce');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (155, 'Cukier', 'Sugar');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (156, 'Cukinia', 'Zucchini');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (157, 'Pietruszka', 'Parsley');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (158, 'Koper', 'Dill');
INSERT IGNORE INTO translatable (id, default_translation, translation_en)
VALUES (159, 'Ser biały', 'White cheese');

INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (1, 1, '2024-03-30', 1, 133, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (2, 1, '2024-03-30', 1, 134, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (3, 1, '2024-03-30', 1, 135, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (4, 1, '2024-03-30', 1, 136, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (5, 1, '2024-03-30', 1, 137, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (6, 1, '2024-03-30', 1, 138, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (7, 1, '2024-03-30', 1, 139, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (8, 1, '2024-03-30', 1, 140, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (9, 1, '2024-03-30', 1, 141, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (10, 1, '2024-03-30', 1, 142, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (11, 1, '2024-03-30', 1, 143, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (12, 1, '2024-03-30', 1, 144, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (13, 1, '2024-03-30', 1, 145, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (14, 1, '2024-03-30', 1, 146, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (15, 1, '2024-03-30', 1, 147, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (16, 1, '2024-03-30', 1, 148, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (17, 1, '2024-03-30', 1, 149, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (18, 1, '2024-03-30', 1, 150, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (19, 1, '2024-03-30', 1, 151, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (20, 1, '2024-03-30', 1, 152, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (21, 1, '2024-03-30', 1, 153, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (22, 1, '2024-03-30', 1, 154, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (23, 1, '2024-03-30', 1, 155, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (24, 1, '2024-03-30', 1, 156, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (25, 1, '2024-03-30', 1, 157, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (26, 1, '2024-03-30', 1, 158, '2024-03-30', '4.00');
INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (27, 1, '2024-03-30', 1, 159, '2024-03-30', '4.00');

-- January 2024 Data
INSERT IGNORE INTO qr_scan_events(id, footprint, restaurant_id, scanned_at)
VALUES (1, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_A', 1, '2024-01-03 10:00:00'),
       (2, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_B', 1, '2024-01-07 09:00:00'),
       (3, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_A', 1, '2024-01-15 12:00:00'),
       (4, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_C', 1, '2024-01-20 11:00:00'),
       (5, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_C', 1, '2024-01-21 15:00:00'),
       (6, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_D', 1, '2024-01-15 10:00:00'),
       (7, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_D', 1, '2024-01-15 10:15:00'),
       (8, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_E', 1, '2024-01-15 15:00:00');

-- February 2024 Data
INSERT IGNORE INTO qr_scan_events(id, footprint, restaurant_id, scanned_at)
VALUES (9, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_F', 1, '2024-02-10 14:00:00');

-- March 2024 Data
INSERT IGNORE INTO qr_scan_events(id, footprint, restaurant_id, scanned_at)
VALUES (10, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_G', 1, '2024-03-05 09:00:00'),
       (11, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_G', 1, '2024-03-05 15:00:00');

-- April 2024 Data
INSERT IGNORE INTO qr_scan_events(id, footprint, restaurant_id, scanned_at)
VALUES (12, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_H', 1, '2024-04-12 10:30:00'),
       (13, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_I', 1, '2024-04-15 16:45:00'),
       (14, '3d90381d-80d2-48f8-80b3-d237d5f0a8ed_H', 1, '2024-04-20 11:00:00');