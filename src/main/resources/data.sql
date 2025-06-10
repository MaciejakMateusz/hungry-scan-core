INSERT IGNORE INTO restaurants (id, address, name, postal_code, city, token, created)
VALUES (1, 'Turystyczna 12/12', 'Rarytas', '44-335', 'Jastrzębie-Zdrój', '3f979e48-e7eb-4669-8084-72543c8538f0',
        '2023-11-12T00:00:00Z'),
       (2, 'Katowicka 12', 'Tajska', '40-004', 'Katowice', null, '2024-05-01T00:00:00Z');

INSERT IGNORE INTO price_plan_types (name, price)
VALUES ('free', 0.00),
       ('basic', 99.00);

INSERT IGNORE INTO price_plans (restaurant_id, plan_type_id, activation_date, renewal_date, billing_period,
                                payment_method)
VALUES (1, 1, null, null, null, null),
       (2, 1, null, null, null, null);

INSERT IGNORE INTO settings (id, booking_duration, opening_time, closing_time, language, capacity,
                             customer_session_time,
                             employee_session_time, order_comment_allowed, waiter_comment_allowed, restaurant_id)
VALUES (1, 3, '10:00:00', '23:00:00', 1, 120, 20, 20, true, true, 1);

INSERT IGNORE INTO settings_operating_hours
    (settings_id, day_of_week, start_time, end_time, available)
VALUES (1, 'MONDAY', '12:00:00', '22:00:00', false),
       (1, 'TUESDAY', '12:00:00', '22:00:00', true),
       (1, 'WEDNESDAY', '12:00:00', '22:00:00', true),
       (1, 'THURSDAY', '12:00:00', '22:00:00', true),
       (1, 'FRIDAY', '12:00:00', '22:00:00', true),
       (1, 'SATURDAY', '12:00:00', '03:00:00', true),
       (1, 'SUNDAY', '12:00:00', '03:00:00', true);

INSERT IGNORE INTO translatable(id, pl, en)
VALUES (163, 'Smacznego!', 'Enjoy your meal!'),
       (164, 'Smacznego!', 'Enjoy your meal!'),
       (165, 'Smacznego!', 'Enjoy your meal!');

INSERT IGNORE INTO menus (id, standard, name, restaurant_id, theme, translatable_message_id)
VALUES (1, true, 'Całodniowe', 1, 'COLOR_318E41', 163),
       (2, false, 'Śniadaniowe', 1, 'COLOR_318E41', 164),
       (3, true, 'Menu', 2, 'COLOR_318E41', 165);

INSERT IGNORE INTO translatable (id, pl, en)
VALUES (1, 'Kelner', 'Waiter'),
       (2, 'Administrator', 'Admin'),
       (3, 'Menadżer', 'Manager'),
       (4, 'Kucharz', 'Cook'),
       (5, 'Klient', 'Customer'),
       (6, 'Klient (tylko odczyt)', 'Customer (read only)');

INSERT IGNORE INTO role (id, name, translatable_displayed_name_id)
VALUES (1, 'ROLE_WAITER', 1),
       (2, 'ROLE_ADMIN', 2),
       (3, 'ROLE_MANAGER', 3),
       (4, 'ROLE_COOK', 4),
       (5, 'ROLE_CUSTOMER', 5),
       (6, 'ROLE_CUSTOMER_READONLY', 6);

INSERT IGNORE INTO users (id, organization_id, created, email, enabled, password, updated, username, email_token,
                          jwt_token_id, forename, phone_number, surname, active_restaurant_id, active_menu_id)
VALUES (1, 1, NOW(), 'admin@example.com', 1,
        '$2y$10$S4Qu.8BEsEqHftYQmDcQ2.mKi5yXi9XRU8IlHBgvQ./N/UYIVhXAG', null, 'admin@example.com', null, null, 'Admin',
        '',
        'Admin', 1, 1),
       (2, 2, NOW(), 'mati@test.com', 1,
        '$2y$10$S4Qu.8BEsEqHftYQmDcQ2.mKi5yXi9XRU8IlHBgvQ./N/UYIVhXAG', null, 'mati@test.com', null, null, '', '',
        '', null, null);

INSERT IGNORE INTO users_restaurants (user_id, restaurant_id)
VALUES (1, 1);

INSERT IGNORE INTO user_role (user_id, role_id)
VALUES (1, 2),
       (2, 2);

INSERT IGNORE INTO translatable (id, pl, en)
VALUES (7, 'Gluten', 'Gluten'),
       (8,
        'Zboża zawierające gluten (tj. pszenica, żyto, jęczmień, owies, pszenica orkisz, lub ich odmiany hybrydowe) oraz produkty pochodne.',
        'Cereals containing gluten (i.e. wheat, rye, barley, oats, spelled wheat, or their hybrid varieties) and derived products.'),
       (9, 'Skorupiaki', 'Crustaceans'),
       (10, 'Skorupiaki i produkty pochodne.', 'Crustaceans and derived products.'),
       (11, 'Jaja', 'Eggs'),
       (12, 'Jaja i produkty pochodne.', 'Eggs and derived products.'),
       (13, 'Ryby', 'Fish'),
       (14, 'Ryby i produkty pochodne.', 'Fish and derived products.'),
       (15, 'Orzeszki ziemne', 'Peanuts'),
       (16, 'Orzeszki ziemne (orzeszki arachidowe) i produkty pochodne.', 'Peanuts and related products.'),
       (17, 'Soja', 'Soybeans'),
       (18, 'Soja i produkty pochodne.', 'Soybeans and derived products.'),
       (19, 'Mleko', 'Milk'),
       (20, 'Mleko i produkty pochodne (łącznie z laktozą).', 'Milk and derived products (including lactose).'),
       (21, 'Orzechy', 'Nuts'),
       (22,
        'Orzechy, tj. migdały, orzechy laskowe, orzechy włoskie, orzechy nerkowca, orzechy pekan, orzechy brazylijskie, pistacje/orzech pistacjowy, orzechy makadamia i produkty pochodne.',
        'Nuts, i.e. almonds, hazelnuts, walnuts, cashews, pecans, Brazil nuts, pistachios/pistachio nuts, macadamia nuts and related products.'),
       (23, 'Seler', 'Celery'),
       (24, 'Seler i produkty pochodne.', 'Celery amd derived products.'),
       (25, 'Gorczyca', 'Mustard'),
       (26, 'Gorczyca i produkty pochodne.', 'Mustard and derived products.'),
       (27, 'Nasiona sezamu', 'Sesame'),
       (28, 'Nasiona sezamu i produkty pochodne.', 'Sesame and derived products.'),
       (29, 'Dwutlenek siarki', 'Sulfur dioxide'),
       (30, 'Dwutlenek siarki i siarczyny w stężeniach powyżej 10 mg/kg lub 10 mg/l w przeliczeniu na SO2.',
        'Sulfur dioxide and sulphites in concentrations above 10 mg/kg or 10 mg/l expressed as SO2.'),
       (31, 'Łubin', 'Lupin'),
       (32, 'Łubin i produkty pochodne.', 'Lupin and derived products.'),
       (33, 'Mięczaki', 'Molluscs'),
       (34, 'Mięczaki i produkty pochodne.', 'Molluscs and derived products.');


INSERT IGNORE INTO allergens(id, translatable_description_id, icon_name, translatable_name_id)
VALUES (1, 8, 'gluten.svg', 7),
       (2, 10, 'crustaceans.svg', 9),
       (3, 12, 'eggs.svg', 11),
       (4, 14, 'fish.svg', 13),
       (5, 16, 'peanuts.svg', 15),
       (6, 18, 'soy.svg', 17),
       (7, 20, 'milk.svg', 19),
       (8, 22, 'nuts.svg', 21),
       (9, 24, 'celery.svg', 23),
       (10, 26, 'mustard.svg', 25),
       (11, 28, 'sesame.svg', 27),
       (12, 30, 'SO2.svg', 29),
       (13, 32, 'lupine.svg', 31),
       (14, 34, 'molluscs.svg', 33);

INSERT IGNORE INTO translatable (id, pl, en)
VALUES (35, 'Bez glutenu', 'Gluten free'),
       (36, 'Wegańskie', 'Vegan'),
       (37, 'Wegetariańskie', 'Vegetarian'),
       (38, 'Kolendra', 'Coriander'),
       (39, 'Bez laktozy', 'Lactose free'),
       (40, 'Ostre', 'Spicy');

INSERT IGNORE INTO labels (id, icon_name, translatable_name_id)
VALUES (1, 'gluten-free.svg', 35),
       (2, 'vegan.svg', 36),
       (3, 'vegetarian.svg', 37),
       (4, 'coriander.svg', 38),
       (5, 'lactose-free.svg', 39),
       (6, 'spicy.svg', 40);

INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number,
                                      is_visible,
                                      zone_id, has_qr_code, qr_name)
VALUES (true, '19436a86-e200-400d-aa2e-da4686805d00', false, false, 999, 1, true, null, false, null);

INSERT IGNORE INTO translatable(id, pl, en)
VALUES (41, 'Przystawki', 'Starters'),
       (42, 'Makarony', 'Pastas'),
       (43, 'Sałatki', 'Salads'),
       (44, 'Zupy', 'Soups'),
       (45, 'Pizza', 'Pizza'),
       (46, 'Wegetariańskie', null),
       (47, 'Dla dzieci', 'For kids'),
       (48, 'Napoje', 'Drinks'),
       (49, 'Pusta', null);

INSERT IGNORE INTO translatable(id, pl)
VALUES (50, 'Krewetki marynowane w cytrynie'),
       (51, 'Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.'),
       (52, 'Carpaccio z polędwicy wołowej'),
       (53, 'Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.'),
       (54, 'Krewetki w tempurze'),
       (55, 'Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym'),
       (56, 'Roladki z bakłażana'),
       (57, 'Dostępne w różnych wariantach'),
       (58, 'Nachos z sosem serowym'),
       (59, 'Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.'),
       (60, 'Spaghetti Bolognese'),
       (61, 'Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.'),
       (62, 'Penne Carbonara'),
       (63, 'Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.'),
       (64, 'Lasagne warzywna'),
       (65, 'Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.'),
       (66, 'Tagliatelle z łososiem i szpinakiem'),
       (67, 'Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.'),
       (68, 'Rigatoni z kurczakiem i brokułami'),
       (69, 'Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.'),
       (70, 'Sałatka grecka'),
       (71, 'Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.'),
       (72, 'Sałatka z grillowanym kurczakiem i awokado'),
       (73, 'Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.'),
       (74, 'Sałatka z rukolą, serem kozim i suszonymi żurawinami'),
       (75, 'Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.'),
       (76, 'Sałatka z grillowanym ananasem i kurczakiem'),
       (77, 'Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.'),
       (78, 'Sałatka z quinoa i pieczonymi warzywami'),
       (79, 'Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.'),
       (80, 'Krem z pomidorów'),
       (81, 'Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.'),
       (82, 'Rosołek z kury'),
       (83, 'Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.'),
       (84, 'Zupa krem z dyni'),
       (85, 'Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.'),
       (86, 'Zupa pomidorowa z ryżem'),
       (87, 'Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.'),
       (88, 'Zupa krem z brokułów'),
       (89, 'Delikatny krem z zielonych brokułów podawany z grzankami.'),
       (90, 'Pizza Margherita'),
       (91, 'Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.'),
       (92, 'Pizza Pepperoni'),
       (93, 'Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.'),
       (94, 'Pizza Capricciosa'),
       (95, 'Pizza z szynką, pieczarkami, karczochami i oliwkami.'),
       (96, 'Pizza Hawajska'),
       (97, 'Pizza z szynką, ananasem i kukurydzą.'),
       (98, 'Pizza Quattro Formaggi'),
       (99, 'Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.'),
       (100, 'Risotto z grzybami leśnymi'),
       (101, 'Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.'),
       (102, 'Curry z ciecierzycą i szpinakiem'),
       (103, 'Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.'),
       (104, 'Naleśniki z serem i szpinakiem'),
       (105, 'Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.'),
       (106, 'Falafel w pitce z hummusem'),
       (107, 'Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.'),
       (108, 'Makaron z pesto bazyliowym'),
       (109, 'Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.'),
       (110, 'Kawa'),
       (111, 'Czarna sypana.'),
       (112, 'Sok pomarańczowy'),
       (113, 'Świeżo wyciskany.'),
       (114, 'Coca-cola'),
       (115, '250ml');


INSERT IGNORE INTO categories (id, menu_id, translatable_name_id, available, created, updated, bar_served,
                               display_order)
VALUES (1, 1, 41, true, NOW(), null, false, 1),
       (2, 1, 42, true, NOW(), null, false, 2),
       (3, 1, 43, true, NOW(), null, false, 3),
       (4, 1, 44, true, NOW(), null, false, 4),
       (5, 1, 45, true, NOW(), null, false, 5),
       (6, 1, 46, true, NOW(), null, false, 6),
       (7, 1, 47, true, NOW(), null, false, 7),
       (8, 1, 48, true, NOW(), null, true, 8),
       (9, 1, 49, true, NOW(), null, false, 9);

INSERT IGNORE INTO menu_items (id, translatable_name_id, display_order, translatable_description_id, created, updated,
                               available, visible, price, counter, bar_served,
                               category_id)
VALUES (1, 50, 1, 51, null, null, true, true, 19.99, 0, false, 1),
       (2, 52, 2, 53, null, null, true, true, 24.50, 0, false, 1),
       (3, 54, 3, 55, null, null, true, true, 22.00, 0, false, 1),
       (4, 56, 4, 57, null, null, true, true, 18.75, 0, false, 1),
       (5, 58, 5, 59, null, null, true, true, 16.99, 0, false, 1),
       (6, 60, 1, 61, null, null, true, true, 24.00, 0, false, 2),
       (7, 62, 2, 63, null, null, true, true, 22.50, 0, false, 2),
       (8, 64, 3, 65, null, null, true, true, 23.25, 0, false, 2),
       (9, 66, 4, 67, null, null, true, true, 26.50, 0, false, 2),
       (10, 68, 5, 69, null, null, true, true, 21.75, 0, false, 2),
       (11, 70, 1, 71, null, null, true, true, 18.50, 0, false, 3),
       (12, 72, 2, 73, null, null, true, true, 20.75, 0, false, 3),
       (13, 74, 3, 75, null, null, true, true, 22.00, 0, false, 3),
       (14, 76, 4, 77, null, null, true, true, 21.50, 0, false, 3),
       (15, 78, 5, 79, null, null, true, true, 23.75, 0, false, 3),
       (16, 80, 1, 81, null, null, true, true, 15.50, 0, false, 4),
       (17, 82, 2, 83, null, null, true, true, 16.25, 0, false, 4),
       (18, 84, 3, 85, null, null, true, true, 17.50, 0, false, 4),
       (19, 86, 4, 87, null, null, true, true, 15.75, 0, false, 4),
       (20, 88, 5, 89, null, null, true, true, 18.00, 0, false, 4),
       (21, 90, 1, 91, null, null, true, true, 26.00, 0, false, 5),
       (22, 92, 2, 93, null, null, true, true, 28.50, 0, false, 5),
       (23, 94, 3, 95, null, null, true, true, 30.25, 0, false, 5),
       (24, 96, 4, 97, null, null, true, true, 27.75, 0, false, 5),
       (25, 98, 5, 99, null, null, true, true, 29.00, 0, false, 5),
       (26, 100, 1, 101, null, null, true, true, 24.50, 0, false, 6),
       (27, 102, 2, 103, null, null, true, true, 22.75, 0, false, 6),
       (28, 104, 3, 105, null, null, true, true, 20.00, 0, false, 6),
       (29, 106, 4, 107, null, null, true, true, 21.25, 0, false, 6),
       (30, 108, 5, 109, null, null, true, true, 23.00, 0, false, 6),
       (31, 110, 1, 111, null, null, true, true, 9.00, 0, true, 8),
       (32, 112, 2, 113, null, null, true, true, 7.00, 0, true, 8),
       (33, 114, 3, 115, null, null, true, true, 7.00, 0, true, 8);

INSERT IGNORE INTO menu_items_allergens (menu_item_id, allergens_id)
VALUES (1, 2),
       (3, 1),
       (3, 2),
       (3, 3),
       (5, 7),
       (6, 1),
       (7, 1),
       (7, 3),
       (8, 1),
       (8, 7),
       (9, 1),
       (9, 4),
       (11, 7),
       (13, 7),
       (16, 7),
       (21, 1),
       (21, 7),
       (22, 1),
       (22, 7),
       (23, 1),
       (23, 7),
       (24, 1),
       (24, 7),
       (25, 1),
       (25, 7),
       (28, 1),
       (28, 3),
       (28, 7),
       (29, 1),
       (30, 1);

INSERT IGNORE INTO menu_items_labels (menu_item_id, labels_id)
VALUES (1, 1),
       (1, 6),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 1),
       (7, 2),
       (8, 3),
       (9, 4),
       (10, 5);

INSERT IGNORE INTO translatable(id, pl)
VALUES (116, 'Z szpinakiem'),
       (117, 'Z konfiturą cebulową'),
       (118, 'Mała'),
       (119, 'Średnia'),
       (120, 'Duża'),
       (121, 'Mała'),
       (122, 'Średnia'),
       (123, 'Duża'),
       (124, 'Mała'),
       (125, 'Średnia'),
       (126, 'Duża'),
       (127, 'Mała'),
       (128, 'Średnia'),
       (129, 'Duża'),
       (130, 'Mała'),
       (131, 'Średnia'),
       (132, 'Duża');

INSERT IGNORE INTO variants(id, created, available, default_variant, translatable_name_id, price, updated,
                            menu_item_id, display_order)
VALUES (1, NOW(), true, true, 116, '0.00', null, 4, 1),
       (2, NOW(), true, false, 117, '4.00', null, 4, 2),
       (3, NOW(), true, true, 118, '0.00', null, 21, 1),
       (4, NOW(), true, false, 119, '5.00', null, 21, 2),
       (5, NOW(), true, false, 120, '10.00', null, 21, 3),
       (6, NOW(), true, true, 121, '0.00', null, 22, 1),
       (7, NOW(), true, false, 122, '5.00', null, 22, 2),
       (8, NOW(), true, false, 123, '10.00', null, 22, 3),
       (9, NOW(), true, true, 124, '0.00', null, 23, 1),
       (10, NOW(), true, false, 125, '5.00', null, 23, 2),
       (11, NOW(), true, false, 126, '10.00', null, 23, 3),
       (12, NOW(), true, true, 127, '0.00', null, 24, 1),
       (13, NOW(), true, false, 128, '5.00', null, 24, 2),
       (14, NOW(), true, false, 129, '10.00', null, 24, 3),
       (15, NOW(), true, true, 130, '0.00', null, 25, 1),
       (16, NOW(), true, false, 131, '5.00', null, 25, 2),
       (17, NOW(), true, false, 132, '10.00', null, 25, 3);


INSERT IGNORE INTO translatable (id, pl, en)
VALUES (133, 'Pomidory', 'Tomatoes'),
       (134, 'Cebula', 'Onion'),
       (135, 'Czosnek', 'Garlic'),
       (136, 'Oliwa z oliwek', 'Olive oil'),
       (137, 'Bazylia', 'Basil'),
       (138, 'Mozzarella', 'Mozzarella'),
       (139, 'Makaron penne', 'Pasta penne'),
       (140, 'Mięso mielone', 'Minced meat'),
       (141, 'Papryka', 'Bell pepper'),
       (142, 'Ser parmezan', 'Parmesan cheese'),
       (144, 'Oregano', 'Oregano'),
       (145, 'Sól', 'Salt'),
       (146, 'Pieprz', 'Pepper'),
       (147, 'Kiełbasa', 'Sausage'),
       (148, 'Makaron spaghetti', 'Spaghetti pasta'),
       (149, 'Kurczak', 'Chicken'),
       (150, 'Szpinak', 'Spinach'),
       (151, 'Kapusta', 'Cabbage'),
       (152, 'Masło', 'Butter'),
       (153, 'Marchew', 'Carrot'),
       (154, 'Sos pomidorowy', 'Tomato sauce'),
       (155, 'Cukier', 'Sugar'),
       (156, 'Cukinia', 'Zucchini'),
       (157, 'Pietruszka', 'Parsley'),
       (158, 'Koper', 'Dill'),
       (159, 'Ser biały', 'White cheese');

INSERT IGNORE INTO ingredients(id, restaurant_id, created, available, translatable_name_id, updated, price)
VALUES (1, 1, '2024-03-30', 1, 133, '2024-03-30', '4.00'),
       (2, 1, '2024-03-30', 1, 134, '2024-03-30', '4.00'),
       (3, 1, '2024-03-30', 1, 135, '2024-03-30', '4.00'),
       (4, 1, '2024-03-30', 1, 136, '2024-03-30', '4.00'),
       (5, 1, '2024-03-30', 1, 137, '2024-03-30', '4.00'),
       (6, 1, '2024-03-30', 1, 138, '2024-03-30', '4.00'),
       (7, 1, '2024-03-30', 1, 139, '2024-03-30', '4.00'),
       (8, 1, '2024-03-30', 1, 140, '2024-03-30', '4.00'),
       (9, 1, '2024-03-30', 1, 141, '2024-03-30', '4.00'),
       (10, 1, '2024-03-30', 1, 142, '2024-03-30', '4.00'),
       (11, 1, '2024-03-30', 1, 143, '2024-03-30', '4.00'),
       (12, 1, '2024-03-30', 1, 144, '2024-03-30', '4.00'),
       (13, 1, '2024-03-30', 1, 145, '2024-03-30', '4.00'),
       (14, 1, '2024-03-30', 1, 146, '2024-03-30', '4.00'),
       (15, 1, '2024-03-30', 1, 147, '2024-03-30', '4.00'),
       (16, 1, '2024-03-30', 1, 148, '2024-03-30', '4.00'),
       (17, 1, '2024-03-30', 1, 149, '2024-03-30', '4.00'),
       (18, 1, '2024-03-30', 1, 150, '2024-03-30', '4.00'),
       (19, 1, '2024-03-30', 1, 151, '2024-03-30', '4.00'),
       (20, 1, '2024-03-30', 1, 152, '2024-03-30', '4.00'),
       (21, 1, '2024-03-30', 1, 153, '2024-03-30', '4.00'),
       (22, 1, '2024-03-30', 1, 154, '2024-03-30', '4.00'),
       (23, 1, '2024-03-30', 1, 155, '2024-03-30', '4.00'),
       (24, 1, '2024-03-30', 1, 156, '2024-03-30', '4.00'),
       (25, 1, '2024-03-30', 1, 157, '2024-03-30', '4.00'),
       (26, 1, '2024-03-30', 1, 158, '2024-03-30', '4.00'),
       (27, 1, '2024-03-30', 1, 159, '2024-03-30', '4.00');

INSERT IGNORE INTO translatable(id, pl, en)
VALUES (160, 'Nowość', 'New'),
       (161, 'Bestseller', 'Bestseller'),
       (162, 'Promocja', 'Discount');

INSERT IGNORE INTO banners (id, translatable_name_id)
VALUES ('new', 160),
       ('bestseller', 161),
       ('promo', 162);

-- MENU PLANS
INSERT IGNORE INTO menu_plans (id, day_of_week, menu_id)
VALUES ('7e72cab0-8e07-4cc1-b537-e22a6f01ab2c', 'TUESDAY', 1),
       ('f214c7df-e8b1-4c95-b017-295a291b7741', 'WEDNESDAY', 1),
       ('e87cf171-8a83-448c-9415-a9bf82fc3f7b', 'THURSDAY', 1),
       ('f2e8ea5d-5663-49b8-bbfa-7629b79f6395', 'FRIDAY', 1),
       ('07908c0f-45b5-4e84-96c6-2bbc9090b561', 'SATURDAY', 1),
       ('d692f96c-1fe8-4c07-9f24-79bb3dc736d0', 'SUNDAY', 1),
       ('fcb2e647-dc7a-4610-baa6-286663599f4c', 'MONDAY', 1);

INSERT IGNORE INTO menu_plan_time_ranges (menu_plan_id, start_time, end_time, available)
VALUES ('7e72cab0-8e07-4cc1-b537-e22a6f01ab2c', '12:00:00', '22:00:00', true),
       ('f214c7df-e8b1-4c95-b017-295a291b7741', '12:00:00', '22:00:00', true),
       ('e87cf171-8a83-448c-9415-a9bf82fc3f7b', '12:00:00', '22:00:00', true),
       ('f2e8ea5d-5663-49b8-bbfa-7629b79f6395', '12:00:00', '22:00:00', true),
       ('07908c0f-45b5-4e84-96c6-2bbc9090b561', '12:00:00', '00:00:00', true),
       ('d692f96c-1fe8-4c07-9f24-79bb3dc736d0', '12:00:00', '00:00:00', true),
       ('d692f96c-1fe8-4c07-9f24-79bb3dc736d0', '00:00:00', '03:00:00', true),
       ('413bf80c-fedf-4481-a1a9-a28dd2b075e3', '12:00:00', '00:00:00', true),
       ('fcb2e647-dc7a-4610-baa6-286663599f4c', '00:00:00', '03:00:00', true);

-- END OF MENU PLANS

-- QR SCAN EVENTS:
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

-- MENU ITEM VIEWS EVENTS:
INSERT IGNORE INTO menu_item_view_events (id, menu_id, menu_item_id, viewed_at)
VALUES
    -- Multiple events on the same day (Jan 3) for item 1, to test counting
    (1, 1, 1, '2024-01-03 09:15:00'),
    (2, 1, 1, '2024-01-03 09:30:00'),
    (3, 1, 2, '2024-01-03 10:00:00'),

    -- Different days in January (weeks differ)
    (4, 1, 3, '2024-01-07 08:00:00'),
    (5, 1, 4, '2024-01-07 09:00:00'),
    (6, 1, 1, '2024-01-11 14:00:00'),
    (7, 1, 2, '2024-01-14 14:00:00'),
    (8, 1, 5, '2024-01-20 18:00:00'),
    (9, 1, 1, '2024-01-27 10:00:00'),
    (10, 1, 6, '2024-01-27 10:05:00'),

    -- February
    (11, 1, 7, '2024-02-03 11:00:00'),
    (12, 1, 3, '2024-02-03 11:15:00'),
    (13, 1, 8, '2024-02-14 09:00:00'),

    -- March
    (14, 1, 9, '2024-03-01 10:00:00'),
    (15, 1, 9, '2024-03-01 10:05:00'),
    (16, 1, 1, '2024-03-31 16:00:00'),
    (17, 1, 1, '2024-03-31 16:00:00'), -- duplicate timestamp to test count
    (18, 1, 6, '2024-03-31 17:00:00'),

    -- April
    (19, 1, 2, '2024-04-03 10:00:00'),
    (20, 1, 3, '2024-04-10 12:00:00');