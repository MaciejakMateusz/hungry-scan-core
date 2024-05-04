INSERT IGNORE INTO settings (id, booking_duration, opening_time, closing_time, language, capacity,
                             customer_session_time,
                             employee_session_time, is_order_comment_allowed)
VALUES (1, 3, '10:00:00', '23:00:00', 1, 120, 3, 20, false);

INSERT IGNORE INTO role (id, name, displayed_name)
VALUES (1, 'ROLE_WAITER', 'Kelner');
INSERT IGNORE INTO role (id, name, displayed_name)
VALUES (2, 'ROLE_ADMIN', 'Administrator');
INSERT IGNORE INTO role (id, name, displayed_name)
VALUES (3, 'ROLE_MANAGER', 'Menadżer');
INSERT IGNORE INTO role (id, name, displayed_name)
VALUES (4, 'ROLE_COOK', 'Kucharz');
INSERT IGNORE INTO role (id, name, displayed_name)
VALUES (5, 'ROLE_CUSTOMER', 'Klient');
INSERT IGNORE INTO role (id, name, displayed_name)
VALUES (6, 'ROLE_CUSTOMER_READONLY', 'Klient (tylko odczyt)');

INSERT IGNORE INTO users (id, created, email, enabled, password, updated, username, email_token, jwt_token_id, name, phone_number, surname)
VALUES (1, NOW(), 'admin@example.com', 1,
        '$2y$10$S4Qu.8BEsEqHftYQmDcQ2.mKi5yXi9XRU8IlHBgvQ./N/UYIVhXAG', null, 'admin',null, null, '', '', '');

INSERT IGNORE INTO user_role(user_id, role_id) VALUES (1, 2);

INSERT IGNORE INTO allergens(id, description, icon_name, name)
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

INSERT IGNORE INTO labels (id, description, icon_name, name)
VALUES (1, 'Dla osób unikających mięsa i/lub produktów pochodzenia zwierzęcego.', 'icon_leaf', 'Wegetariańskie / Wegańskie'),
       (2, 'Ważne dla osób z nietolerancją glutenu lub celiakią.', 'icon_no_gluten', 'Bezglutenowe'),
       (3, 'Dla tych, którzy lubią pikantne potrawy.', 'icon_chili_pepper', 'Ostre / Pikantne'),
       (4,
        'Niektórzy nie lubią smaku kolendry, więc taka etykieta pomaga uniknąć potraw zawierających tę przyprawę.',
        'icon_no_cilantro', 'Kolendra / Bez kolendry'),
       (5, 'Odpowiednie dla osób świadomych kalorii.', 'icon_light', 'Niskokaloryczne / Light'),
       (6, 'Wskazuje na potrawy charakterystyczne dla danej kuchni regionalnej.', 'icon_world_map',
        'Tradycyjne / Regionalne'),
       (7, 'Ważne dla osób z nietolerancją laktozy.', 'icon_no_lactose', 'Bez laktozy'),
       (8, 'Oznaczenie produktów z sezonu lub potraw przygotowanych z świeżych składników.', 'icon_fresh',
        'Świeże / Sezonowe'),
       (9, 'Odpowiednie dla osób ograniczających spożycie sodu.', 'icon_low_sodium', 'Niskosodowe');

