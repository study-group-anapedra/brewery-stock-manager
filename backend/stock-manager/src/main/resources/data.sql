-- ==========================
-- Roles
-- ==========================
INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_CLIENT');

-- ==========================
-- Users
-- CORREÇÃO: Colunas renomeadas para moment_registration e moment_update
-- CORREÇÃO: Separando inserts para maior compatibilidade com H2
-- ==========================
INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(1, 'Luiza Brandão', 'luiza@gmail.com', '8177906788', '2015-02-23', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '307.460.850-00');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(2, 'Mara Vascon', 'vascon@gmail.com', '81999887766', '2019-02-23', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '488.298.100-10');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(3, 'Lucas Marcone Silva', 'lucas@gmail.com', '81988776655', '2015-05-22', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '598.566.190-20');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(4, 'Ana Bragalha', 'bragalha@gmail.com', '12366444755', '2019-09-22', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '407.242.380-30');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(5, 'Mari Ferreira', 'ferreira@gmail.com', '35988664588', '2011-02-12', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '499.367.000-40');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(6, 'Sandra Megal Simão', 'megal@gmail.com', '62933446855', '2017-01-20', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '515.504.430-50');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(7, 'Helena Saldanha Filipa', 'helena@gmail.com', '61996585744', '2018-02-21', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '353.586.090-60');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(8, 'Mariana Prado', 'mariana@gmail.com', '87999435555', '2011-10-18', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '918.324.580-70');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(9, 'Socorro Ribeiro', 'socorro@gmail.com', '61869389211', '2019-01-23', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '564.677.060-80');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(10, 'Ana Prado Santana', 'anaprado@gmail.com', '12236554788', '2017-06-02', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '948.320.260-90');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(11, 'Sandra Marcodes', 'saidra@gmail.com', '78889654211', '2013-02-28', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '861.322.570-00');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(12, 'Ana Matias', 'anamartins@gmail.com', '12936554788', '2018-09-20', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '371.032.700-11');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(13, 'Mari Samaria', 'mari@gmail.com', '45986987122', '2000-09-20', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '653.812.100-22');

INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
(14, 'Sandra Osca Sintra', 'sanfraosca@gmail.com', '78789654211', '2017-10-21', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '779.187.500-33');

-- ==========================
-- User Roles
-- ==========================
INSERT INTO tb_user_role (user_id, role_id) VALUES
(1, 1), (1, 2),
(2, 1), (2, 2),
(3, 2),
(4, 2),
(5, 2),
(6, 2),
(7, 2),
(8, 2),
(9, 2),
(10, 2),
(11, 2),
(12, 2),
(13, 2),
(14, 2);

---

-- ==========================
-- Categories
-- ==========================
INSERT INTO tb_category (id, name, description) VALUES
(1, 'Lagers', 'Pilsen e American Lager'),
(2, 'Ales', 'Pale Ale, IPA'),
(3, 'Wheat Beers', 'Witbier, Weizenbier'),
(4, 'Stouts & Porters', 'Cervejas Escuras'),
(5, 'Sour Beers', 'Cervejas Ácidas'),
(6, 'Belgian Styles', 'Tripel e Dubbel'),
(7, 'High ABV', 'Cervejas fortes'),
(8, 'Fruit Beers', 'Cervejas com frutas'),
(9, 'Specialty', 'Cervejas Especiais'),
(10, 'Low Carb', 'Cervejas Leves');

---

-- ==========================
-- Beers
-- CORREÇÃO: Usando a função DATEADD do H2 para calcular a data de expiração.
-- ==========================
INSERT INTO tb_beers (id, name, url_img, alcohol_content, price, manufacture_date, expiration_date) VALUES
(1, 'Pilsen Leve', 'imgUrl1.com/pilsen.jpg', 4.5, 7.50, CURRENT_DATE, DATEADD('MONTH', 6, CURRENT_DATE)),
(2, 'IPA Artesanal', 'imgUrl2.com/ipa.jpg', 6.2, 18.00, CURRENT_DATE, DATEADD('MONTH', 9, CURRENT_DATE)),
(3, 'Weizen Tradicional', 'imgUrl3.com/weizen.jpg', 5.5, 12.50, CURRENT_DATE, DATEADD('MONTH', 4, CURRENT_DATE)),
(4, 'Stout de Café', 'imgUrl4.com/stout.jpg', 7.0, 22.00, CURRENT_DATE, DATEADD('MONTH', 12, CURRENT_DATE)),
(5, 'Gose Salgada', 'imgUrl5.com/gose.jpg', 4.8, 15.00, CURRENT_DATE, DATEADD('MONTH', 6, CURRENT_DATE)),
(6, 'Tripel Clássica', 'imgUrl6.com/tripel.jpg', 9.0, 25.00, CURRENT_DATE, DATEADD('MONTH', 18, CURRENT_DATE)),
(7, 'Double IPA', 'imgUrl7.com/dipa.jpg', 8.5, 28.00, CURRENT_DATE, DATEADD('MONTH', 9, CURRENT_DATE)),
(8, 'Witbier Cítrica', 'imgUrl8.com/witbier.jpg', 4.9, 14.00, CURRENT_DATE, DATEADD('MONTH', 4, CURRENT_DATE)),
(9, 'Porter Defumada', 'imgUrl9.com/porter.jpg', 5.8, 19.50, CURRENT_DATE, DATEADD('MONTH', 12, CURRENT_DATE)),
(10, 'Saison de Frutas', 'imgUrl10.com/saison.jpg', 6.5, 17.00, CURRENT_DATE, DATEADD('MONTH', 6, CURRENT_DATE));
-- ==========================
-- Beer Categories (M:N)
-- ==========================
-- Estoque Saldo Atual (tb_stock)
-- ID (beer_id) | QUANTITY | LAST_UPDATE | STATUS
-- Status Codes: 0 = OK, 1 = Low Stock (< 10), 2 = Out of Stock (= 0)
-- ==========================
INSERT INTO tb_stock (beer_id, quantity, last_update, status) VALUES
-- 1. Estoque OK (quantity > 10)
(1, 150, CURRENT_TIMESTAMP, 0), -- Pilsen Leve: OK
(2, 50, CURRENT_TIMESTAMP, 0),  -- IPA Artesanal: OK
(3, 12, CURRENT_TIMESTAMP, 0),  -- Weizen Tradicional: OK (acima do limite 10)

-- 2. Estoque Baixo (1 <= quantity <= 10)
(4, 9, CURRENT_TIMESTAMP, 1),   -- Stout de Café: LOW STOCK
(5, 1, CURRENT_TIMESTAMP, 1),   -- Gose Salgada: LOW STOCK
(6, 10, CURRENT_TIMESTAMP, 1),  -- Tripel Clássica: LOW STOCK (no limite 10)

-- 3. Fora de Estoque (quantity = 0)
(7, 0, CURRENT_TIMESTAMP, 2),   -- Double IPA: OUT OF STOCK
(8, 0, CURRENT_TIMESTAMP, 2),   -- Witbier Cítrica: OUT OF STOCK

-- 4. Mais Estoque OK para completar
(9, 25, CURRENT_TIMESTAMP, 0),  -- Porter Defumada: OK
(10, 80, CURRENT_TIMESTAMP, 0); -- Saison de Frutas: OK
-- ==========================
-- Beer Categories (M:N)
-- CORREÇÃO: Nome da tabela de junção corrigido para 'beer_category'
-- ==========================
INSERT INTO beer_category (beer_id, category_id) VALUES
(1, 1), (1, 10),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6), (6, 7),
(7, 2), (7, 7),
(8, 3), (8, 8),
(9, 4),
(10, 8), (10, 6);

---

-- ==========================
-- Orders
-- ==========================

INSERT INTO tb_order (id, moment_at, client_id) VALUES
(1, '2021-01-01 10:00:00+00', 1),
(2, '2021-01-02 11:00:00+00', 1),
(3, '2021-01-03 12:00:00+00', 1),
(4, '2021-02-01 10:00:00+00', 2),
(5, '2021-02-02 11:00:00+00', 2),
(6, '2021-02-03 12:00:00+00', 2),
(7, '2021-03-01 10:00:00+00', 3),
(8, '2021-03-02 11:00:00+00', 3),
(9, '2021-03-03 12:00:00+00', 3),
(10, '2021-04-01 10:00:00+00', 4),
(11, '2021-04-02 11:00:00+00', 4),
(12, '2021-05-01 10:00:00+00', 5),
(13, '2021-05-02 11:00:00+00', 5),
(14, '2021-06-01 10:00:00+00', 6),
(15, '2021-06-02 11:00:00+00', 6),
(16, '2021-07-01 10:00:00+00', 7),
(17, '2021-07-02 11:00:00+00', 7),
(18, '2021-08-01 10:00:00+00', 8),
(19, '2021-08-02 11:00:00+00', 8),
(20, '2021-09-01 10:00:00+00', 9),
(21, '2021-09-02 11:00:00+00', 9),
(22, '2021-10-01 10:00:00+00', 10),
(23, '2021-10-02 11:00:00+00', 10),
(24, '2021-11-01 10:00:00+00', 11),
(25, '2021-11-02 11:00:00+00', 11),
(26, '2021-12-01 10:00:00+00', 12),
(27, '2021-12-02 11:00:00+00', 12),
(28, '2022-01-01 10:00:00+00', 13),
(29, '2022-01-02 11:00:00+00', 13),
(30, '2022-02-01 10:00:00+00', 14);

---

-- ==========================
-- Order Items
-- ==========================
INSERT INTO tb_order_item (order_id, beer_id, quantity, price) VALUES
(1, 1, 2, 7.50),
(1, 2, 1, 18.00),
(2, 3, 3, 12.50),
(2, 4, 1, 22.00),
(3, 5, 2, 15.00),
(3, 6, 1, 25.00),
(4, 1, 1, 7.50),
(4, 7, 2, 28.00),
(5, 2, 3, 18.00),
(5, 3, 1, 12.50),
(6, 4, 2, 22.00),
(6, 5, 1, 15.00),
(7, 6, 1, 25.00),
(7, 7, 2, 28.00),
(8, 8, 2, 14.00),
(8, 9, 1, 19.50),
(9, 10, 2, 17.00),
(9, 1, 1, 7.50),
(10, 2, 2, 18.00),
(11, 3, 1, 12.50),
(12, 4, 2, 22.00),
(13, 5, 1, 15.00),
(14, 6, 2, 25.00),
(15, 7, 1, 28.00),
(16, 8, 2, 14.00),
(17, 9, 1, 19.50),
(18, 10, 2, 17.00),
(19, 1, 1, 7.50),
(20, 2, 2, 18.00),
(21, 3, 1, 12.50),
(22, 4, 2, 22.00),
(23, 5, 1, 15.00),
(24, 6, 2, 25.00),
(25, 7, 1, 28.00),
(26, 8, 2, 14.00),
(27, 9, 1, 19.50),
(28, 10, 2, 17.00),
(29, 1, 1, 7.50),
(30, 2, 2, 18.00);

---

-- ==========================
-- Payments
-- ==========================
INSERT INTO tb_payment (id, moment, order_id) VALUES
(1, '2021-10-02 08:00:00+00', 1),
(2, '2020-11-12 08:00:00+00', 2),
(3, '2022-01-09 12:00:00+00', 3);