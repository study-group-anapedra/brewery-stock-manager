-- ==========================
-- Roles
-- ==========================
INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_CLIENT');

-- ==========================
-- Users
-- ==========================
INSERT INTO tb_user (id, name, email, phone, birth_date, password, moment_registration, moment_update, cpf) VALUES
                                                                                                                (1, 'Luiza Brandão', 'luiza@gmail.com', '8177906788', '2015-02-23', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '307.460.850-00'),
                                                                                                                (2, 'Mara Vascon', 'vascon@gmail.com', '81999887766', '2019-02-23', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '488.298.100-10'),
                                                                                                                (3, 'Lucas Marcone Silva', 'lucas@gmail.com', '81988776655', '2015-05-22', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '598.566.190-20'),
                                                                                                                (4, 'Ana Bragalha', 'bragalha@gmail.com', '12366444755', '2019-09-22', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '407.242.380-30'),
                                                                                                                (5, 'Mari Ferreira', 'ferreira@gmail.com', '35988664588', '2011-02-12', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '499.367.000-40'),
                                                                                                                (6, 'Sandra Megal Simão', 'megal@gmail.com', '62933446855', '2017-01-20', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '515.504.430-50'),
                                                                                                                (7, 'Helena Saldanha Filipa', 'helena@gmail.com', '61996585744', '2018-02-21', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '353.586.090-60'),
                                                                                                                (8, 'Mariana Prado', 'mariana@gmail.com', '87999435555', '2011-10-18', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '918.324.580-70'),
                                                                                                                (9, 'Socorro Ribeiro', 'socorro@gmail.com', '61869389211', '2019-01-23', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '564.677.060-80'),
                                                                                                                (10, 'Ana Prado Santana', 'anaprado@gmail.com', '12236554788', '2017-06-02', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '948.320.260-90'),
                                                                                                                (11, 'Sandra Marcodes', 'saidra@gmail.com', '78889654211', '2013-02-28', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '861.322.570-00'),
                                                                                                                (12, 'Ana Matias', 'anamartins@gmail.com', '12936554788', '2018-09-20', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '371.032.700-11'),
                                                                                                                (13, 'Mari Samaria', 'mari@gmail.com', '45986987122', '2000-09-20', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '653.812.100-22'),
                                                                                                                (14, 'Sandra Osca Sintra', 'sanfraosca@gmail.com', '78789654211', '2017-10-21', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '779.187.500-33');

-- ==========================
-- User Roles
-- ==========================
INSERT INTO tb_user_role (user_id, role_id) VALUES
                                                (1, 1), (1, 2), (2, 1), (2, 2), (3, 2), (4, 2), (5, 2), (6, 2),
                                                (7, 2), (8, 2), (9, 2), (10, 2), (11, 2), (12, 2), (13, 2), (14, 2);

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

-- ==========================
-- Beers
-- CORREÇÃO: Usando INTERVAL para compatibilidade com PostgreSQL (AWS)
-- ==========================
INSERT INTO tb_beers (id, name, url_img, alcohol_content, price, manufacture_date, expiration_date) VALUES
                                                                                                        (1, 'Pilsen Leve', 'imgUrl1.com/pilsen.jpg', 4.5, 7.50, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months'),
                                                                                                        (2, 'IPA Artesanal', 'imgUrl2.com/ipa.jpg', 6.2, 18.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '9 months'),
                                                                                                        (3, 'Weizen Tradicional', 'imgUrl3.com/weizen.jpg', 5.5, 12.50, CURRENT_DATE, CURRENT_DATE + INTERVAL '4 months'),
                                                                                                        (4, 'Stout de Café', 'imgUrl4.com/stout.jpg', 7.0, 22.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '12 months'),
                                                                                                        (5, 'Gose Salgada', 'imgUrl5.com/gose.jpg', 4.8, 15.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months'),
                                                                                                        (6, 'Tripel Clássica', 'imgUrl6.com/tripel.jpg', 9.0, 25.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '18 months'),
                                                                                                        (7, 'Double IPA', 'imgUrl7.com/dipa.jpg', 8.5, 28.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '9 months'),
                                                                                                        (8, 'Witbier Cítrica', 'imgUrl8.com/witbier.jpg', 4.9, 14.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '4 months'),
                                                                                                        (9, 'Porter Defumada', 'imgUrl9.com/porter.jpg', 5.8, 19.50, CURRENT_DATE, CURRENT_DATE + INTERVAL '12 months'),
                                                                                                        (10, 'Saison de Frutas', 'imgUrl10.com/saison.jpg', 6.5, 17.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months');

-- ==========================
-- Stock
-- ==========================
INSERT INTO tb_stock (beer_id, quantity, last_update, status) VALUES
                                                                  (1, 150, CURRENT_TIMESTAMP, 0),
                                                                  (2, 50, CURRENT_TIMESTAMP, 0),
                                                                  (3, 12, CURRENT_TIMESTAMP, 0),
                                                                  (4, 9, CURRENT_TIMESTAMP, 1),
                                                                  (5, 1, CURRENT_TIMESTAMP, 1),
                                                                  (6, 10, CURRENT_TIMESTAMP, 1),
                                                                  (7, 0, CURRENT_TIMESTAMP, 2),
                                                                  (8, 0, CURRENT_TIMESTAMP, 2),
                                                                  (9, 25, CURRENT_TIMESTAMP, 0),
                                                                  (10, 80, CURRENT_TIMESTAMP, 0);

-- ==========================
-- Beer Categories
-- ==========================
INSERT INTO beer_category (beer_id, category_id) VALUES
                                                     (1, 1), (1, 10), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (6, 7),
                                                     (7, 2), (7, 7), (8, 3), (8, 8), (9, 4), (10, 8), (10, 6);
