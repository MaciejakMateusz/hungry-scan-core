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
                             employee_session_time, is_order_comment_allowed)
VALUES (1, 3, '10:00:00', '23:00:00', 1, 120, 3, 20, false);

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

INSERT IGNORE INTO users (id, created, email, enabled, password, updated, username, email_token, jwt_token_id, name, phone_number, surname)
VALUES (1, NOW(), 'admin@example.com', 1,
        '$2y$10$S4Qu.8BEsEqHftYQmDcQ2.mKi5yXi9XRU8IlHBgvQ./N/UYIVhXAG', null, 'admin',null, null, '', '', '');

INSERT IGNORE INTO user_role(user_id, role_id) VALUES (1, 2);

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

INSERT IGNORE INTO allergens(id, translatable_description_id, icon_name, translatable_name_id)
VALUES (1, 8, 'icon_gluten', 7),
       (2, 10, 'icon_crustaceans', 9),
       (3, 12, 'icon_eggs', 11),
       (4, 14, 'icon_fish', 13),
       (5, 16, 'icon_peanuts', 15),
       (6, 18, 'icon_soybeans', 17),
       (7, 20, 'icon_milk', 19),
       (8, 22, 'icon_nuts', 21),
       (9, 24, 'icon_celery', 23),
       (10, 26, 'icon_mustard', 25),
       (11, 28, 'icon_sesame', 27),
       (12, 30, 'icon_sulfur_dioxide', 29),
       (13, 32, 'icon_lupin', 31),
       (14, 34, 'icon_molluscs', 33);

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

INSERT IGNORE INTO labels (id, icon_name, translatable_name_id)
VALUES (1, 'icon_gluten_free', 35),
       (2, 'icon_vegan', 36),
       (3, 'icon_vegetarian', 37),
       (4, 'icon_coriander', 38),
       (5, 'icon_lactose_free', 39),
       (6, 'icon_spicy', 40);

INSERT IGNORE INTO themes(id, name)
VALUES (1, 'green'),
       (2, 'pink'),
       (3, 'grey'),
       (4, 'orange');

INSERT IGNORE INTO onboarding_images(id, image_name)
VALUES (1, 'default');

INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '19436a86-e200-400d-aa2e-da4686805d00', false, false, 4, 1, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '79d8684f-333e-4275-a317-fa06d46fa6b6', false, false, 4, 2, true, 2, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '0ce8beb3-6fb1-42f1-9c95-05cf9fb88d27', false, false, 4, 3, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '5afb9629-990a-4934-87f2-793b1aa2f35e', false, false, 4, 4, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '58d77e24-6b8c-41a9-b24c-a67602deacdd', false, false, 4, 5, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '59ebc00c-b580-4dff-9788-2df90b1d4bba', false, false, 4, 6, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, 'ef303854-6faa-4615-8d47-6f3686086586', false, false, 4, 7, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '97cba027-ae47-4c42-8828-f4b3b3506d0c', false, false, 4, 8, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, 'fe2cce7c-7c4c-4076-9eb4-3e91b440fec2', false, false, 4, 9, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '88ca9c82-e630-40f2-9bf9-47f7d14f6bff', false, false, 4, 10, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, 'c88a6029-4f29-4ee1-8d8f-f31f7a554301', false, false, 4, 11, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, 'd565c73a-8d87-4a79-9e3f-7b6a02520e71', false, false, 4, 12, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '6696c583-a312-4b24-9716-430826ad1e96', false, false, 4, 13, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, 'a65896cb-805d-4d7b-849b-1d53e78f3191', false, false, 4, 14, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '65b6bb94-da99-4ced-8a94-5860fe95e708', false, false, 4, 15, true, 2, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '2fd07320-a841-48ad-9f3f-35b307014b2a', false, false, 4, 16, true, 2, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '3740c35f-5759-4eb8-ab00-cb3807707235', false, false, 4, 17, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (false, '480407f1-13bd-45a7-bad7-d0e2b76e5ebf', false, false, 4, 18, true, null, false, null);
INSERT IGNORE INTO restaurant_tables (is_active, token, waiter_called, bill_requested, max_num_of_ppl, number, is_visible,
                               zone_id, has_qr_code, qr_name)
VALUES (true, '96fb4431-af22-48f4-9e4c-40b5774d9ab2', false, false, 1, 19, true, null, false, null);