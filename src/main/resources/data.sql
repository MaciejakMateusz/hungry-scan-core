INSERT IGNORE INTO menu_colors (id, hex)
VALUES (1, '#078480'),
       (2, '#266DD7'),
       (3, '#318E41'),
       (4, '#7737B3'),
       (5, '#DA8414'),
       (6, '#DD4B10'),
       (7, '#F7C911'),
       (8, '#C41E20'),
       (9, '#152966'),
       (10, '#27343B');

INSERT IGNORE INTO translatable (id, pl, en)
VALUES (1, 'Personel', 'Staff'),
       (2, 'Administrator', 'Admin'),
       (3, 'Menadżer', 'Manager'),
       (4, 'Klient', 'Customer'),
       (5, 'Klient (tylko odczyt)', 'Customer (read only)');

INSERT IGNORE INTO role (id, name, translatable_displayed_name_id)
VALUES (1, 'ROLE_STAFF', 1),
       (2, 'ROLE_ADMIN', 2),
       (3, 'ROLE_MANAGER', 3),
       (4, 'ROLE_CUSTOMER', 4),
       (5, 'ROLE_CUSTOMER_READONLY', 5);

INSERT IGNORE INTO users (id, organization_id, active, created, email, enabled, password, updated, username,
                          email_token,
                          jwt_token_id, forename, phone_number, surname, active_restaurant_id, active_menu_id,
                          signed_in, last_seen_at)
VALUES (1, 1, true, NOW(), 'maciejakmateusz@gmail.com', 1,
        '$2y$10$S4Qu.8BEsEqHftYQmDcQ2.mKi5yXi9XRU8IlHBgvQ./N/UYIVhXAG', null, 'maciejakmateusz@gmail.com', null, null,
        'Mateusz',
        '',
        'Maciejak', 1, 1, false, NOW()),
       (2, 1, true, NOW(), 'maciejak.neta@gmail.com', 1,
        '$2y$10$S4Qu.8BEsEqHftYQmDcQ2.mKi5yXi9XRU8IlHBgvQ./N/UYIVhXAG', null, 'maciejak.neta@gmail.com', null, null,
        'Aneta',
        '',
        'Maciejak', 1, 1, false, NOW());

INSERT IGNORE INTO user_role (user_id, role_id)
VALUES (1, 2),
       (2, 2);

INSERT IGNORE INTO translatable (id, pl, en)
VALUES (6, 'Gluten', 'Gluten'),
       (7,
        'Zboża zawierające gluten (tj. pszenica, żyto, jęczmień, owies, pszenica orkisz, lub ich odmiany hybrydowe) oraz produkty pochodne.',
        'Cereals containing gluten (i.e. wheat, rye, barley, oats, spelled wheat, or their hybrid varieties) and derived products.'),
       (8, 'Skorupiaki', 'Crustaceans'),
       (9, 'Skorupiaki i produkty pochodne.', 'Crustaceans and derived products.'),
       (10, 'Jaja', 'Eggs'),
       (11, 'Jaja i produkty pochodne.', 'Eggs and derived products.'),
       (12, 'Ryby', 'Fish'),
       (13, 'Ryby i produkty pochodne.', 'Fish and derived products.'),
       (14, 'Orzeszki ziemne', 'Peanuts'),
       (15, 'Orzeszki ziemne (orzeszki arachidowe) i produkty pochodne.', 'Peanuts and related products.'),
       (16, 'Soja', 'Soybeans'),
       (17, 'Soja i produkty pochodne.', 'Soybeans and derived products.'),
       (18, 'Mleko', 'Milk'),
       (19, 'Mleko i produkty pochodne (łącznie z laktozą).', 'Milk and derived products (including lactose).'),
       (20, 'Orzechy', 'Nuts'),
       (21,
        'Orzechy, tj. migdały, orzechy laskowe, orzechy włoskie, orzechy nerkowca, orzechy pekan, orzechy brazylijskie, pistacje/orzech pistacjowy, orzechy makadamia i produkty pochodne.',
        'Nuts, i.e. almonds, hazelnuts, walnuts, cashews, pecans, Brazil nuts, pistachios/pistachio nuts, macadamia nuts and related products.'),
       (22, 'Seler', 'Celery'),
       (23, 'Seler i produkty pochodne.', 'Celery amd derived products.'),
       (24, 'Gorczyca', 'Mustard'),
       (25, 'Gorczyca i produkty pochodne.', 'Mustard and derived products.'),
       (26, 'Nasiona sezamu', 'Sesame'),
       (27, 'Nasiona sezamu i produkty pochodne.', 'Sesame and derived products.'),
       (28, 'Dwutlenek siarki', 'Sulfur dioxide'),
       (29, 'Dwutlenek siarki i siarczyny w stężeniach powyżej 10 mg/kg lub 10 mg/l w przeliczeniu na SO2.',
        'Sulfur dioxide and sulphites in concentrations above 10 mg/kg or 10 mg/l expressed as SO2.'),
       (30, 'Łubin', 'Lupin'),
       (31, 'Łubin i produkty pochodne.', 'Lupin and derived products.'),
       (32, 'Mięczaki', 'Molluscs'),
       (33, 'Mięczaki i produkty pochodne.', 'Molluscs and derived products.');


INSERT IGNORE INTO allergens(id, translatable_description_id, icon_name, translatable_name_id)
VALUES (1, 7, 'gluten.svg', 6),
       (2, 9, 'crustaceans.svg', 8),
       (3, 11, 'eggs.svg', 10),
       (4, 13, 'fish.svg', 12),
       (5, 15, 'peanuts.svg', 14),
       (6, 17, 'soy.svg', 16),
       (7, 19, 'milk.svg', 18),
       (8, 21, 'nuts.svg', 20),
       (9, 23, 'celery.svg', 22),
       (10, 25, 'mustard.svg', 24),
       (11, 27, 'sesame.svg', 26),
       (12, 29, 'SO2.svg', 28),
       (13, 31, 'lupine.svg', 30),
       (14, 33, 'molluscs.svg', 32);

INSERT IGNORE INTO translatable (id, pl, en)
VALUES (34, 'Bez glutenu', 'Gluten free'),
       (35, 'Wegańskie', 'Vegan'),
       (36, 'Wegetariańskie', 'Vegetarian'),
       (37, 'Kolendra', 'Coriander'),
       (38, 'Bez laktozy', 'Lactose free'),
       (39, 'Ostre', 'Spicy');

INSERT IGNORE INTO labels (id, icon_name, translatable_name_id)
VALUES (1, 'gluten-free.svg', 34),
       (2, 'vegan.svg', 35),
       (3, 'vegetarian.svg', 36),
       (4, 'coriander.svg', 37),
       (5, 'lactose-free.svg', 38),
       (6, 'spicy.svg', 39);

INSERT IGNORE INTO translatable(id, pl, en)
VALUES (40, 'Nowość', 'New'),
       (41, 'Bestseller', 'Bestseller'),
       (42, 'Promocja', 'Discount');

INSERT IGNORE INTO banners (id, translatable_name_id)
VALUES ('new', 40),
       ('bestseller', 41),
       ('promo', 42);