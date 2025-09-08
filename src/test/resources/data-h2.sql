INSERT INTO restaurants (address, name, postal_code, city, token, created, updated, qr_version)
VALUES ('ul. Główna 123, Miastowo, Województwo, 54321', 'Rarytas', '12-1234', 'TEST',
        '3d90381d-80d2-48f8-80b3-d237d5f0a8ed', '2024-01-15T00:00:00Z', NOW(), 1),
       ('ul. Dębowa 456, Miasteczko, Wiejskie, 98765', 'Wykwintna Bistro', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 111', 'Test 1', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 222', 'Test 2', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 333', 'Test 3', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 444', 'Test 4', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 555', 'Test 5', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 666', 'Test 6', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 777', 'Test 7', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 888', 'Test 8', '12-1234', 'TEST', null, NOW(), NOW(), 1),
       ('Test address, 999', 'Test 9', '12-1234', 'TEST', null, NOW(), NOW(), 1);

INSERT INTO price_plan_types (name, price)
VALUES ('free', 0.00),
       ('basic', 99.00);

INSERT INTO price_plans (restaurant_id, plan_type_id, activation_date, renewal_date, billing_period,
                         payment_method)
VALUES (1, 2, '2024-01-15', '2025-01-15', 'yearly', 'card'),
       (2, 1, null, null, null, null),
       (3, 1, null, null, null, null),
       (4, 1, null, null, null, null),
       (5, 1, null, null, null, null),
       (6, 1, null, null, null, null),
       (7, 1, null, null, null, null),
       (8, 1, null, null, null, null),
       (9, 1, null, null, null, null),
       (10, 1, null, null, null, null),
       (11, 1, null, null, null, null);

INSERT INTO settings (booking_duration, language, capacity, customer_session_time,
                      employee_session_time, order_comment_allowed, waiter_comment_allowed, restaurant_id)
VALUES (3, 1, 120, 3, 20, true, true, 1),
       (3, 1, 312, 3, 20, false, false, 2);

INSERT INTO settings_operating_hours
    (settings_id, day_of_week, start_time, end_time, available)
VALUES (1, 'MONDAY', '12:00:00', '22:00:00', false),
       (1, 'TUESDAY', '12:00:00', '22:00:00', true),
       (1, 'WEDNESDAY', '12:00:00', '22:00:00', true),
       (1, 'THURSDAY', '12:00:00', '22:00:00', true),
       (1, 'FRIDAY', '12:00:00', '22:00:00', true),
       (1, 'SATURDAY', '12:00:00', '03:00:00', true),
       (1, 'SUNDAY', '12:00:00', '03:00:00', true),
       (2, 'MONDAY', '12:00:00', '22:00:00', false),
       (2, 'TUESDAY', '12:00:00', '22:00:00', true),
       (2, 'WEDNESDAY', '12:00:00', '22:00:00', true),
       (2, 'THURSDAY', '12:00:00', '22:00:00', true),
       (2, 'FRIDAY', '12:00:00', '22:00:00', true),
       (2, 'SATURDAY', '12:00:00', '22:00:00', true),
       (2, 'SUNDAY', '12:00:00', '22:00:00', true);


INSERT INTO translatable(pl, en)
VALUES ('Smacznego!', 'Enjoy your meal!'),
       ('Smacznego!', 'Enjoy your meal!'),
       ('Smacznego!', 'Enjoy your meal!'),
       ('Smacznego!', 'Enjoy your meal!'),
       ('Smacznego!', 'Enjoy your meal!'),
       ('Smacznego!', 'Enjoy your meal!'),
       ('Smacznego!', 'Enjoy your meal!');

INSERT INTO menu_colors (hex)
VALUES ('#1A1A1A'),
       ('#003366'),
       ('#4B0082'),
       ('#800000'),
       ('#2F4F4F'),
       ('#8B0000'),
       ('#006400'),
       ('#2C3E50'),
       ('#9746FF'),
       ('#016DFF');

INSERT INTO menus (standard, name, restaurant_id, theme, color_id, translatable_message_id)
VALUES (true, 'Całodniowe', 1, 'COLOR_318E41', 9, 1),
       (true, 'Menu', 2, 'COLOR_318E41', 9, 2),
       (false, 'Śniadaniowe', 2, 'COLOR_318E41', 10, 3),
       (false, 'Obiadowe', 2, 'COLOR_318E41', 10, 4),
       (false, 'Kolacyjne', 2, 'COLOR_318E41', 10, 5),
       (false, 'Ziemniaczane', 5, 'COLOR_318E41', 10, 6),
       (false, 'Menu', 11, 'COLOR_318E41', 10, 7);

/* CATEGORIES TRANSLATIONS */
INSERT INTO translatable(pl, en)
VALUES ('Przystawki', 'Starters'),
       ('Makarony', 'Pastas'),
       ('Sałatki', 'Salads'),
       ('Zupy', 'Soups'),
       ('Pizza', 'Pizza'),
       ('Wegetariańskie', null),
       ('Dla dzieci', 'For kids'),
       ('Napoje', 'Drinks'),
       ('Pusta', null);
/* END OF CATEGORIES TRANSLATIONS */

/* MENU ITEMS TRANSLATIONS */
INSERT INTO translatable(pl)
VALUES ('Krewetki marynowane w cytrynie'),
       ('Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.'),
       ('Carpaccio z polędwicy wołowej'),
       ('Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.'),
       ('Krewetki w tempurze'),
       ('Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym'),
       ('Roladki z bakłażana'),
       ('Dostępne w różnych wariantach'),
       ('Nachos z sosem serowym'),
       ('Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.'),
       ('Spaghetti Bolognese'),
       ('Długie spaghetti podane z aromatycznym sosem bolognese na bazie mięsa mielonego i pomidorów.'),
       ('Penne Carbonara'),
       ('Penne z sosem carbonara na bazie jajek, boczku, sera parmezan i śmietanki.'),
       ('Lasagne warzywna'),
       ('Warstwy makaronu lasagne przeplatane warzywami, beszamelem i sosem pomidorowym.'),
       ('Tagliatelle z łososiem i szpinakiem'),
       ('Cienkie tagliatelle z kawałkami łososia i świeżym szpinakiem w sosie śmietanowym.'),
       ('Rigatoni z kurczakiem i brokułami'),
       ('Rurki rigatoni z duszonym kurczakiem, brokułami i sosem śmietanowym.'),
       ('Sałatka grecka'),
       ('Tradycyjna grecka sałatka z pomidorami, ogórkiem, cebulą, oliwkami, serem feta i sosem vinegrette.'),
       ('Sałatka z grillowanym kurczakiem i awokado'),
       ('Sałatka z grillowanymi kawałkami kurczaka, awokado, pomidorami i orzechami.'),
       ('Sałatka z rukolą, serem kozim i suszonymi żurawinami'),
       ('Świeża rukola z serem kozim, prażonymi orzechami włoskimi i suszonymi żurawinami.'),
       ('Sałatka z grillowanym ananasem i kurczakiem'),
       ('Sałatka z soczystym grillowanym ananasem, kawałkami kurczaka i mieszanką sałat.'),
       ('Sałatka z quinoa i pieczonymi warzywami'),
       ('Sałatka z quinoa, pieczonymi marchewkami, burakami i suszonymi pomidorami.'),
       ('Krem z pomidorów'),
       ('Gładki krem z pomidorów z dodatkiem śmietany i świeżego bazylia.'),
       ('Rosołek z kury'),
       ('Tradycyjny rosół z kury z makaronem, warzywami i natką pietruszki.'),
       ('Zupa krem z dyni'),
       ('Kremowa zupa z dyni z nutą cynamonu i prażonymi pestkami dyni.'),
       ('Zupa pomidorowa z ryżem'),
       ('Zupa pomidorowa z dodatkiem ryżu i świeżego kopru.'),
       ('Zupa krem z brokułów'),
       ('Delikatny krem z zielonych brokułów podawany z grzankami.'),
       ('Pizza Margherita'),
       ('Klasyka włoskiej kuchni - sos pomidorowy, mozzarella i świeża bazylia.'),
       ('Pizza Pepperoni'),
       ('Pizza z pikantnym salami pepperoni, serem mozzarella i papryką.'),
       ('Pizza Capricciosa'),
       ('Pizza z szynką, pieczarkami, karczochami i oliwkami.'),
       ('Pizza Hawajska'),
       ('Pizza z szynką, ananasem i kukurydzą.'),
       ('Pizza Quattro Formaggi'),
       ('Pizza z 4 rodzajami sera: mozzarella, gorgonzola, parmezan i camembert.'),
       ('Risotto z grzybami leśnymi'),
       ('Klasyczne włoskie risotto z wybornymi grzybami leśnymi i parmezanem.'),
       ('Curry z ciecierzycą i szpinakiem'),
       ('Aromatyczne curry z ciecierzycą, świeżym szpinakiem i mlekiem kokosowym.'),
       ('Naleśniki z serem i szpinakiem'),
       ('Delikatne naleśniki nadziewane serem i szpinakiem, podane z sosem pomidorowym.'),
       ('Falafel w pitce z hummusem'),
       ('Smakowite kulki falafel w cieście pita z sosem hummus i warzywami.'),
       ('Makaron z pesto bazyliowym'),
       ('Makaron spaghetti z pysznym pesto bazyliowym, parmezanem i prażonymi orzechami.'),
       ('Kawa'),
       ('Czarna sypana.'),
       ('Sok pomarańczowy'),
       ('Świeżo wyciskany.'),
       ('Coca-cola'),
       ('250ml');

/* END OF MENU ITEMS TRANSLATIONS */

INSERT INTO categories (translatable_name_id, available, created, updated, bar_served, display_order, menu_id)
VALUES (8, true, '2024-10-27T11:24:07.783228', null, false, 1, 1),
       (9, true, '2024-10-27T11:24:07.783228', null, false, 2, 1),
       (10, true, '2024-10-27T11:24:07.783228', null, false, 3, 1),
       (11, true, '2024-10-27T11:24:07.783228', null, false, 4, 1),
       (12, true, '2024-10-27T11:24:07.783228', null, false, 5, 1),
       (13, true, '2024-10-27T11:24:07.783228', null, false, 6, 1),
       (14, true, '2024-10-27T11:24:07.783228', null, false, 7, 1),
       (15, true, '2024-10-27T11:24:07.783228', null, true, 8, 1),
       (16, false, '2024-10-27T11:24:07.783228', null, false, 9, 1);

INSERT INTO menu_items (translatable_name_id, display_order, translatable_description_id, created, updated,
                        available, visible, price, counter, bar_served, category_id)
VALUES (17, 1, 18, null, null, false, true, 19.99, 0, false, 1),
       (19, 2, 20, null, null, true, false, 24.50, 0, false, 1),
       (21, 3, 22, null, null, true, true, 22.00, 0, false, 1),
       (23, 4, 24, null, null, true, true, 18.75, 0, false, 1),
       (25, 5, 26, null, null, true, true, 16.99, 0, false, 1),
       (27, 1, 28, null, null, true, true, 24.00, 0, false, 2),
       (29, 2, 30, null, null, true, true, 22.50, 0, false, 2),
       (31, 3, 32, null, null, true, true, 23.25, 0, false, 2),
       (33, 4, 34, null, null, true, true, 26.50, 0, false, 2),
       (35, 5, 36, null, null, true, true, 21.75, 0, false, 2),
       (37, 1, 38, null, null, true, true, 18.50, 0, false, 3),
       (39, 2, 40, null, null, true, true, 20.75, 0, false, 3),
       (41, 3, 42, null, null, true, true, 22.00, 0, false, 3),
       (43, 4, 44, null, null, true, true, 21.50, 0, false, 3),
       (45, 5, 46, null, null, true, true, 23.75, 0, false, 3),
       (47, 1, 48, null, null, true, true, 15.50, 0, false, 4),
       (49, 2, 50, null, null, true, true, 16.25, 0, false, 4),
       (51, 3, 52, null, null, true, true, 17.50, 0, false, 4),
       (53, 4, 54, null, null, true, true, 15.75, 0, false, 4),
       (55, 5, 56, null, null, true, true, 18.00, 0, false, 4),
       (57, 1, 58, null, null, true, true, 26.00, 0, false, 5),
       (59, 2, 60, null, null, true, true, 28.50, 0, false, 5),
       (61, 3, 62, null, null, true, true, 30.25, 0, false, 5),
       (63, 4, 64, null, null, true, true, 27.75, 0, false, 5),
       (65, 5, 66, null, null, true, true, 29.00, 0, false, 5),
       (67, 1, 68, null, null, true, true, 24.50, 0, false, 6),
       (69, 2, 70, null, null, true, true, 22.75, 0, false, 6),
       (71, 3, 72, null, null, true, true, 20.00, 0, false, 6),
       (73, 4, 74, null, null, true, true, 21.25, 0, false, 6),
       (75, 5, 76, null, null, true, true, 23.00, 0, false, 6),
       (77, 1, 78, null, null, true, true, 9.00, 0, true, 8),
       (79, 2, 80, null, null, true, true, 7.00, 0, true, 8),
       (81, 3, 82, null, null, true, true, 7.00, 0, true, 8);

INSERT INTO translatable(pl)
VALUES ('Z szpinakiem'),
       ('Z konfiturą cebulową'),
       ('Mała'),
       ('Średnia'),
       ('Duża'),
       ('Mała'),
       ('Średnia'),
       ('Duża'),
       ('Mała'),
       ('Średnia'),
       ('Duża'),
       ('Mała'),
       ('Średnia'),
       ('Duża'),
       ('Mała'),
       ('Średnia'),
       ('Duża');

INSERT INTO variants(created, available, default_variant, translatable_name_id, price, updated, menu_item_id,
                     display_order)
VALUES (NOW(), true, true, 83, '0.00', null, 4, 1),
       (NOW(), true, false, 84, '4.00', null, 4, 2),
       (NOW(), true, true, 85, '0.00', null, 21, 1),
       (NOW(), true, false, 86, '5.00', null, 21, 2),
       (NOW(), true, false, 87, '10.00', null, 21, 3),
       (NOW(), true, true, 88, '0.00', null, 22, 1),
       (NOW(), true, false, 89, '5.00', null, 22, 2),
       (NOW(), true, false, 90, '10.00', null, 22, 3),
       (NOW(), true, true, 91, '0.00', null, 23, 1),
       (NOW(), true, false, 92, '5.00', null, 23, 2),
       (NOW(), true, false, 93, '10.00', null, 23, 3),
       (NOW(), true, true, 94, '0.00', null, 24, 1),
       (NOW(), true, false, 95, '5.00', null, 24, 2),
       (NOW(), true, false, 96, '10.00', null, 24, 3),
       (NOW(), true, true, 97, '0.00', null, 25, 1),
       (NOW(), true, false, 98, '5.00', null, 25, 2),
       (NOW(), true, false, 99, '10.00', null, 25, 3);

INSERT INTO translatable (pl, en)
VALUES ('Kelner', 'Waiter'),
       ('Administrator', 'Admin'),
       ('Menadżer', 'Manager'),
       ('Kucharz', 'Cook'),
       ('Klient', 'Customer'),
       ('Klient (tylko odczyt)', 'Customer (read only)');

INSERT INTO role (name, translatable_displayed_name_id)
VALUES ('ROLE_WAITER', 100),
       ('ROLE_ADMIN', 101),
       ('ROLE_MANAGER', 102),
       ('ROLE_COOK', 103),
       ('ROLE_CUSTOMER', 104),
       ('ROLE_CUSTOMER_READONLY', 105);

INSERT INTO translatable (pl, en)
VALUES ('Gluten', 'Gluten'),
       ('Zboża zawierające gluten (tj. pszenica, żyto, jęczmień, owies, pszenica orkisz, lub ich odmiany hybrydowe) oraz produkty pochodne.',
        'Cereals containing gluten (i.e. wheat, rye, barley, oats, spelled wheat, or their hybrid varieties) and derived products.'),
       ('Skorupiaki', 'Crustaceans'),
       ('Skorupiaki i produkty pochodne.', 'Crustaceans and derived products.'),
       ('Jaja', 'Eggs'),
       ('Jaja i produkty pochodne.', 'Eggs and derived products.'),
       ('Ryby', 'Fish'),
       ('Ryby i produkty pochodne.', 'Fish and derived products.'),
       ('Orzeszki ziemne', 'Peanuts'),
       ('Orzeszki ziemne (orzeszki arachidowe) i produkty pochodne.', 'Peanuts and related products.'),
       ('Soja', 'Soybeans'),
       ('Soja i produkty pochodne.', 'Soybeans and derived products.'),
       ('Mleko', 'Milk'),
       ('Mleko i produkty pochodne (łącznie z laktozą).', 'Milk and derived products (including lactose).'),
       ('Orzechy', 'Nuts'),
       ('Orzechy, tj. migdały, orzechy laskowe, orzechy włoskie, orzechy nerkowca, orzechy pekan, orzechy brazylijskie, pistacje/orzech pistacjowy, orzechy makadamia i produkty pochodne.',
        'Nuts, i.e. almonds, hazelnuts, walnuts, cashews, pecans, Brazil nuts, pistachios/pistachio nuts, macadamia nuts and related products.'),
       ('Seler', 'Celery'),
       ('Seler i produkty pochodne.', 'Celery amd derived products.'),
       ('Gorczyca', 'Mustard'),
       ('Gorczyca i produkty pochodne.', 'Mustard and derived products.'),
       ('Nasiona sezamu', 'Sesame'),
       ('Nasiona sezamu i produkty pochodne.', 'Sesame and derived products.'),
       ('Dwutlenek siarki', 'Sulfur dioxide'),
       ('Dwutlenek siarki i siarczyny w stężeniach powyżej 10 mg/kg lub 10 mg/l w przeliczeniu na SO2.',
        'Sulfur dioxide and sulphites in concentrations above 10 mg/kg or 10 mg/l expressed as SO2.'),
       ('Łubin', 'Lupin'),
       ('Łubin i produkty pochodne.', 'Lupin and derived products.'),
       ('Mięczaki', 'Molluscs'),
       ('Mięczaki i produkty pochodne.', 'Molluscs and derived products.');

INSERT INTO allergens(id, translatable_description_id, icon_name, translatable_name_id)
VALUES (1, 107, 'icon_gluten', 106),
       (2, 109, 'icon_crustaceans', 108),
       (3, 111, 'icon_eggs', 110),
       (4, 113, 'icon_fish', 112),
       (5, 115, 'icon_peanuts', 114),
       (6, 117, 'icon_soybeans', 116),
       (7, 119, 'icon_milk', 118),
       (8, 121, 'icon_nuts', 120),
       (9, 123, 'icon_celery', 122),
       (10, 125, 'icon_mustard', 124),
       (11, 127, 'icon_sesame', 126),
       (12, 129, 'icon_sulfur_dioxide', 128),
       (13, 131, 'icon_lupin', 130),
       (14, 133, 'icon_molluscs', 132);

INSERT INTO translatable (pl, en)
VALUES ('Bez glutenu', 'Gluten free'),
       ('Wegańskie', 'Vegan'),
       ('Wegetariańskie', 'Vegetarian'),
       ('Kolendra', 'Coriander'),
       ('Bez laktozy', 'Lactose free'),
       ('Ostre', 'Spicy');

INSERT INTO labels (icon_name, translatable_name_id)
VALUES ('icon_gluten_free', 134),
       ('icon_vegan', 135),
       ('icon_vegetarian', 136),
       ('icon_coriander', 137),
       ('icon_lactose_free', 138),
       ('icon_spicy', 139);

INSERT INTO translatable (pl, en)
VALUES ('Pomidory', 'Tomatoes'),
       ('Cebula', 'Onion'),
       ('Czosnek', 'Garlic'),
       ('Oliwa z oliwek', 'Olive oil'),
       ('Bazylia', 'Basil'),
       ('Mozzarella', 'Mozzarella'),
       ('Makaron penne', 'Pasta penne'),
       ('Mięso mielone', 'Minced meat'),
       ('Papryka', 'Bell pepper'),
       ('Ser parmezan', 'Parmesan cheese'),
       ('Ser parmezan', 'Parmesan cheese'),
       ('Oregano', 'Oregano'),
       ('Sól', 'Salt'),
       ('Pieprz', 'Pepper'),
       ('Kiełbasa', 'Sausage'),
       ('Makaron spaghetti', 'Spaghetti pasta'),
       ('Kurczak', 'Chicken'),
       ('Szpinak', 'Spinach'),
       ('Kapusta', 'Cabbage'),
       ('Masło', 'Butter'),
       ('Marchew', 'Carrot'),
       ('Sos pomidorowy', 'Tomato sauce'),
       ('Cukier', 'Sugar'),
       ('Cukinia', 'Zucchini'),
       ('Pietruszka', 'Parsley'),
       ('Koper', 'Dill'),
       ('Ser biały', 'White cheese');

INSERT INTO ingredients(created, available, translatable_name_id, updated, price, restaurant_id)
VALUES ('2024-03-30', 1, 140, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 141, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 142, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 143, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 144, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 145, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 146, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 147, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 148, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 149, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 150, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 151, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 152, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 153, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 154, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 155, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 156, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 157, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 158, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 159, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 160, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 161, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 162, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 163, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 164, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 165, '2024-03-30', '4.00', 1),
       ('2024-03-30', 1, 166, '2024-03-30', '4.00', 1);

INSERT INTO translatable(pl, en)
VALUES ('Sekcja 1', 'Section 1'),
       ('Sekcja 2', 'Section 2'),
       ('Piętro II', 'Floor 2'),
       ('Loża VIP', 'VIP Lounge');

INSERT INTO zones(translatable_name_id, created, updated, display_order, is_visible)
VALUES (167, NOW(), null, 1, true),
       (168, NOW(), null, 2, true),
       (169, NOW(), null, 4, true),
       (170, NOW(), null, 3, true);

INSERT INTO translatable(pl, en)
VALUES ('Nowość', 'New'),
       ('Bestseller', 'Bestseller'),
       ('Promocja', 'Discount');

INSERT INTO banners (id, translatable_name_id)
VALUES ('new', 171),
       ('bestseller', 172),
       ('promo', 173);

INSERT INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '19436a86-e200-400d-aa2e-da4686805d00', false, false, 4, 1, true, null, false, null),
       (true, '79d8684f-333e-4275-a317-fa06d46fa6b6', false, false, 4, 2, true, 2, false, null),
       (false, '0ce8beb3-6fb1-42f1-9c95-05cf9fb88d27', false, false, 4, 3, true, null, false, null),
       (false, '5afb9629-990a-4934-87f2-793b1aa2f35e', false, false, 4, 4, true, null, false, null),
       (true, '58d77e24-6b8c-41a9-b24c-a67602deacdd', false, false, 4, 5, true, null, false, null),
       (false, '59ebc00c-b580-4dff-9788-2df90b1d4bba', false, false, 4, 6, true, null, false, null),
       (false, 'ef303854-6faa-4615-8d47-6f3686086586', false, false, 4, 7, true, null, false, null),
       (false, '97cba027-ae47-4c42-8828-f4b3b3506d0c', false, false, 4, 8, true, null, false, null),
       (false, 'fe2cce7c-7c4c-4076-9eb4-3e91b440fec2', false, false, 4, 9, true, null, false, null),
       (false, '88ca9c82-e630-40f2-9bf9-47f7d14f6bff', false, false, 4, 10, true, null, false, null),
       (false, 'c88a6029-4f29-4ee1-8d8f-f31f7a554301', false, false, 4, 11, true, null, false, null),
       (true, 'd565c73a-8d87-4a79-9e3f-7b6a02520e71', false, false, 4, 12, true, null, false, null),
       (false, '6696c583-a312-4b24-9716-430826ad1e96', false, false, 4, 13, true, null, false, null),
       (false, 'a65896cb-805d-4d7b-849b-1d53e78f3191', false, false, 4, 14, true, null, false, null),
       (false, '65b6bb94-da99-4ced-8a94-5860fe95e708', false, false, 4, 15, true, 2, false, null),
       (false, '2fd07320-a841-48ad-9f3f-35b307014b2a', false, false, 4, 16, true, 2, false, null),
       (false, '3740c35f-5759-4eb8-ab00-cb3807707235', false, false, 4, 17, true, null, false, null),
       (false, '480407f1-13bd-45a7-bad7-d0e2b76e5ebf', false, false, 4, 18, true, null, false, null),
       (true, '96fb4431-af22-48f4-9e4c-40b5774d9ab2', false, false, 1, 19, true, null, false, null);

INSERT INTO menu_items_additional_ingredients(menu_item_id, additional_ingredients_id)
VALUES (4, 1),
       (4, 5),
       (4, 6),
       (21, 6),
       (21, 16),
       (21, 2),
       (22, 6),
       (22, 16),
       (22, 2);

INSERT INTO bookings (date, expiration_time, num_of_ppl, surname, time)
VALUES ('2024-02-23', '19:00:00', 2, 'Pierwszy', '16:00:00'),
       ('2024-02-28', '14:00:00', 2, 'Drugi', '16:00:00');

INSERT INTO bookings_restaurant_tables(booking_id, restaurant_tables_id)
VALUES (1, 5),
       (2, 7);

INSERT INTO history_bookings (id, date, expiration_time, num_of_ppl, surname, time)
VALUES (50, '2024-01-19', '19:00:00', 1, 'Alan', '16:00:00'),
       (51, '2024-01-21', '19:00:00', 3, 'Gibson', '16:00:00'),
       (52, '2024-01-23', '19:00:00', 2, 'Fire', '16:00:00'),
       (53, '2024-01-25', '19:00:00', 4, 'Water', '16:00:00'),
       (54, '2024-01-27', '19:00:00', 2, 'Earth', '16:00:00');

INSERT INTO history_bookings_restaurant_tables(history_booking_id, restaurant_tables_id)
VALUES (50, 2),
       (51, 10),
       (52, 11),
       (53, 13),
       (54, 5);

INSERT INTO jwt_tokens (token, created)
VALUES ('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZjNhYmY4LTliNmEiLCJpYXQiOjE3MTM4MTAzNTUsImV4cCI6MTcxMzg4MjM1NX0.EhIv7CDkpXcfXFHeihyju6bdUS2Te41a-m3GaxRWKHM',
        '2024-04-23 12:50:41.531670'),
       ('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyYzczYmZjLTE2ZmMiLCJpYXQiOjE3MTM4MTA5NDcsImV4cCI6MTcxMzg4Mjk0N30.NExb3606nYuZgxQa4-jOrlk2PM4CoKj9pyz25XtZhl0',
        '2024-04-23 12:50:41.531670'),
       ('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwYzllNjgzLTg1NzYiLCJpYXQiOjE3MTM4ODA5MjMsImV4cCI6MTcxMzk1MjkyM30.M28dOa0W5FApG8p2sgfUhLHylHO4hM5bAgOOgF2k5oU',
        NOW());

INSERT INTO users (organization_id, created, email, enabled, password, updated, username, email_token, jwt_token_id,
                   forename, phone_number, surname, active_menu_id, active_restaurant_id)
VALUES (1, '2024-01-20 12:04:00.000000', 'matimemek@test.com', 1,
        '$2a$10$z/0edEimosa3QjYYxjiHuO8bNZHfI3jxDVwqDNd5bc2vCr5TERDz6', '2024-02-02 20:54:41.531670',
        'matimemek@test.com', null, null, 'mati', '+48 123 123 123', 'Memek', 1, 1),
       (1, '2024-01-20 19:09:00.000000', 'admin@example.com', 1,
        '$2a$04$OI8NalP4M4rxpRFgVR3eO.8C/6hmP.AdYadtTPd3BLHm3zx3wLLWm', '2024-02-04 07:50:29.047589',
        'admin@example.com', null, null, 'edmin', '', 'edminowsky', 1, 1),
       (1, '2024-01-24 19:06:36.680304', 'netka@test.com', 0,
        '$2a$12$SnVI60OEgQMpEA./cc4Sl.G9whg6O2szOnM4BG3ZOYuNpRE3RenpG', null, 'netka@test.com', null, null, 'Neta',
        null, 'Menagera', 1, 1),
       (1, '2024-04-23 12:50:41.531670', 'ff3abf8-9b6a@temp.it', 1,
        '$2a$10$fb4q1jBqnMLDkUBi2YXQ4eHZ0M17bP5gxzwU84UwCkEUbyekGRDlC', null, 'ff3abf8-9b6a@temp.it', null, 1, 'temp',
        null, 'user', 1, 1),
       (1, '2024-04-23 12:50:41.531670', '2c73bfc-16fc@temp.it', 1,
        '$2a$10$0F.xiCJux5So7.C6GJEWyeLkBiKlfYFXUS9jr9W5y4GinZgmxv5v.', null, '2c73bfc-16fc@temp.it', null, 2, 'temp',
        null, 'user', 1, 1);


INSERT INTO users (organization_id, created, email, enabled, password, updated, username, email_token, jwt_token_id,
                   forename, phone_number, surname, active_menu_id, active_restaurant_id)
VALUES (2, '2024-02-03 10:21:00.000000', 'kucharz@antek.pl', 1,
        '$2a$10$.HWarZkysOgBF0/tOXmmtONdRkZHGZCsRFs27Q7FcNrDc4bSzE0fW', '2024-02-03 10:33:07.307903',
        'kucharz@antek.pl', null, null, 'ada', '', 'asdqwe', 2, 2),
       (2, '2024-02-03 10:24:02.744722', 'restaurator@rarytas.pl', 1,
        '$2a$10$tykyevzP4v1WV/FyuYWNOO6wspbmAHnzI.deEAZQU6SA8NSxod3Vy', null, 'restaurator@rarytas.pl', null, null,
        'Właściciel', '', 'Biznesmen', 3, 2),
       (2, NOW(), '0c9e683-8576@temp.it', 1, '$2a$10$cn1IjWjjz4QBcfukawrzw.FkwxgFpYOUs/rBtg2k9b5xoPKiHZsvW', null,
        '0c9e683-8576@temp.it', null, 3, 'temp', null, 'surname', 2, 2),
       (3, '2024-04-23 12:50:41.531670', 'fresh@user.it', 1,
        '$2a$10$0F.xiCJux5So7.C6GJEWyeLkBiKlfYFXUS9jr9W5y4GinZgmxv5v.', null, 'fresh@user.it', null, null, 'Fresh',
        null, 'User', null, null),
       (3, '2024-04-23 12:50:41.531670', 'fresh@user.it', 1,
        '$2a$10$0F.xiCJux5So7.C6GJEWyeLkBiKlfYFXUS9jr9W5y4GinZgmxv5v.', null, 'freeplan@example.com', null, null,
        'Free', null, 'User', null, 10);


INSERT INTO user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 1),
       (3, 3),
       (4, 6),
       (5, 6),
       (6, 4),
       (7, 2),
       (8, 5),
       (9, 2),
       (10, 2);

INSERT INTO users_restaurants (user_id, restaurant_id)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (3, 4),
       (3, 5),
       (4, 1),
       (5, 1),
       (6, 2),
       (6, 3),
       (7, 2),
       (8, 2),
       (10, 10),
       (10, 11);

-- MENU PLANS
INSERT INTO menu_plans (id, day_of_week, menu_id)
VALUES ('fcb2e647-dc7a-4610-baa6-286663599f4c', 'MONDAY', 1),
       ('7e72cab0-8e07-4cc1-b537-e22a6f01ab2c', 'TUESDAY', 1),
       ('f214c7df-e8b1-4c95-b017-295a291b7741', 'WEDNESDAY', 1),
       ('e87cf171-8a83-448c-9415-a9bf82fc3f7b', 'THURSDAY', 1),
       ('f2e8ea5d-5663-49b8-bbfa-7629b79f6395', 'FRIDAY', 1),
       ('07908c0f-45b5-4e84-96c6-2bbc9090b561', 'SATURDAY', 1),
       ('d692f96c-1fe8-4c07-9f24-79bb3dc736d0', 'SUNDAY', 1),
       ('eef8a7e4-3227-4631-a799-40f15f76d19a', 'MONDAY', 2),
       ('4c7167ec-0d14-470b-a94d-47c66d0617c0', 'TUESDAY', 2),
       ('f64244fe-f90d-4964-b82c-5b514443639b', 'WEDNESDAY', 2),
       ('dd2d0091-b2fd-4758-9335-3dd842aecf84', 'THURSDAY', 2),
       ('aa00f80a-31e8-4bdf-8901-2eef9ce7aba3', 'FRIDAY', 2),
       ('dbb40f77-dd6c-40c5-9a0f-41724595a353', 'SATURDAY', 2),
       ('68beec79-0152-46a0-be2f-cfe360c4b6df', 'SUNDAY', 2);

INSERT INTO menu_plan_time_ranges (menu_plan_id, start_time, end_time, available)
VALUES ('fcb2e647-dc7a-4610-baa6-286663599f4c', '00:00:00', '03:00:00', true),
       ('7e72cab0-8e07-4cc1-b537-e22a6f01ab2c', '12:00:00', '22:00:00', true),
       ('f214c7df-e8b1-4c95-b017-295a291b7741', '12:00:00', '22:00:00', true),
       ('e87cf171-8a83-448c-9415-a9bf82fc3f7b', '12:00:00', '22:00:00', true),
       ('f2e8ea5d-5663-49b8-bbfa-7629b79f6395', '12:00:00', '22:00:00', true),
       ('07908c0f-45b5-4e84-96c6-2bbc9090b561', '12:00:00', '00:00:00', true),
       ('d692f96c-1fe8-4c07-9f24-79bb3dc736d0', '12:00:00', '00:00:00', true),
       ('d692f96c-1fe8-4c07-9f24-79bb3dc736d0', '00:00:00', '03:00:00', true),

       ('eef8a7e4-3227-4631-a799-40f15f76d19a', '12:00:00', '22:00:00', true),
       ('4c7167ec-0d14-470b-a94d-47c66d0617c0', '12:00:00', '22:00:00', true),
       ('f64244fe-f90d-4964-b82c-5b514443639b', '12:00:00', '22:00:00', true),
       ('dd2d0091-b2fd-4758-9335-3dd842aecf84', '12:00:00', '22:00:00', true),
       ('aa00f80a-31e8-4bdf-8901-2eef9ce7aba3', '12:00:00', '22:00:00', true),
       ('dbb40f77-dd6c-40c5-9a0f-41724595a353', '12:00:00', '22:00:00', true),
       ('68beec79-0152-46a0-be2f-cfe360c4b6df', '12:00:00', '22:00:00', true);

-- END OF MENU PLANS