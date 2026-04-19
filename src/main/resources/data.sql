-- =====================================================
-- AI Shopping Bot - Seed Data
-- =====================================================

-- Insert products only if table is empty
INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'iPhone 14', 'phone', 79900.00, '6.1-inch Super Retina XDR, A15 Bionic chip, 12MP camera', 'Apple', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=iPhone') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'iPhone 14') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'Samsung Galaxy S23', 'phone', 74999.00, '6.1-inch Dynamic AMOLED, Snapdragon 8 Gen 2, 50MP camera', 'Samsung', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=Samsung') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Samsung Galaxy S23') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'OnePlus 11', 'phone', 61999.00, '6.7-inch AMOLED, Snapdragon 8 Gen 2, Hasselblad camera', 'OnePlus', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=OnePlus') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'OnePlus 11') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'Redmi Note 12 Pro', 'phone', 19999.00, '6.67-inch AMOLED, MediaTek Helio G99, 50MP camera', 'Xiaomi', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=Redmi') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Redmi Note 12 Pro') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'Realme 11 Pro', 'phone', 17999.00, '6.7-inch Super AMOLED, MediaTek Helio G99, 100MP camera', 'Realme', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=Realme') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Realme 11 Pro') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'Samsung Galaxy A34', 'phone', 18999.00, '6.6-inch Super AMOLED, Dimensity 1080, 48MP camera', 'Samsung', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=GalaxyA') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Samsung Galaxy A34') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'Motorola Edge 40', 'phone', 15999.00, '6.55-inch pOLED, Dimensity 8020, 50MP camera, IP68', 'Motorola', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=Moto') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Motorola Edge 40') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'iQOO Z7 Pro', 'phone', 14999.00, '6.78-inch AMOLED, Dimensity 7200, 64MP camera', 'iQOO', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=iQOO') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'iQOO Z7 Pro') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'Poco X5 Pro', 'phone', 12999.00, '6.67-inch AMOLED, Snapdragon 778G, 108MP camera', 'Poco', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=Poco') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Poco X5 Pro') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'Tecno Spark 20 Pro', 'phone', 9999.00, '6.78-inch AMOLED, Helio G99, 108MP camera', 'Tecno', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=Tecno') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Tecno Spark 20 Pro') LIMIT 1;

INSERT INTO product (name, category, price, description, brand, image_url)
SELECT * FROM (SELECT 'Infinix Note 30', 'phone', 8999.00, '6.78-inch AMOLED, Helio G99, 108MP camera, 45W charging', 'Infinix', 'https://via.placeholder.com/80x80/1a1a2e/00d4ff?text=Infinix') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Infinix Note 30') LIMIT 1;
