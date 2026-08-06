-- Roomly test database seed
-- Creates 10 hotels, 30 rooms, photos, amenities, and 90 days of inventory.
-- PostgreSQL / Neon compatible. Safe to rerun for these named demo hotels.

BEGIN;

-- Contact information
INSERT INTO contact_info (complete_address, location, email, phone_number)
VALUES
('18 Marine Drive, Churchgate, Mumbai, Maharashtra 400020', 'Marine Drive', 'testdb.azure@roomly.dev', '+91-9000001001'),
('42 Ashvem Beach Road, Mandrem, Goa 403527', 'Ashvem Beach', 'testdb.casa@roomly.dev', '+91-9000001002'),
('7 Amer View Road, Jaipur, Rajasthan 302028', 'Amer', 'testdb.rajwara@roomly.dev', '+91-9000001003'),
('88 Residency Road, Bengaluru, Karnataka 560025', 'Central Bengaluru', 'testdb.lumina@roomly.dev', '+91-9000001004'),
('15 Diplomatic Enclave, Chanakyapuri, Delhi 110021', 'Chanakyapuri', 'testdb.imperial@roomly.dev', '+91-9000001005'),
('3 Lake View Road, Banjara Hills, Hyderabad 500034', 'Banjara Hills', 'testdb.deccan@roomly.dev', '+91-9000001006'),
('21 Park Street, Kolkata, West Bengal 700016', 'Park Street', 'testdb.bengal@roomly.dev', '+91-9000001007'),
('11 Lake Pichola Road, Udaipur, Rajasthan 313001', 'Lake Pichola', 'testdb.aurelia@roomly.dev', '+91-9000001008'),
('9 Mall Road, Manali, Himachal Pradesh 175131', 'Old Manali', 'testdb.pinecrest@roomly.dev', '+91-9000001009'),
('27 Beach Road, White Town, Puducherry 605001', 'White Town', 'testdb.coromandel@roomly.dev', '+91-9000001010');

-- Hotels
INSERT INTO hotels (hotel_name, city, contact_info_id, active, created_at, updated_at)
SELECT hotel_name, city, ci.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
    VALUES
    ('TestDB - Azure Mumbai', 'Mumbai', 'testdb.azure@roomly.dev'),
    ('TestDB - Casa Mar Goa', 'Goa', 'testdb.casa@roomly.dev'),
    ('TestDB - Rajwara Jaipur', 'Jaipur', 'testdb.rajwara@roomly.dev'),
    ('TestDB - Lumina Bengaluru', 'Bengaluru', 'testdb.lumina@roomly.dev'),
    ('TestDB - Imperial Delhi', 'Delhi', 'testdb.imperial@roomly.dev'),
    ('TestDB - Deccan Courtyard', 'Hyderabad', 'testdb.deccan@roomly.dev'),
    ('TestDB - Bengal House', 'Kolkata', 'testdb.bengal@roomly.dev'),
    ('TestDB - Aurelia Lake Palace', 'Udaipur', 'testdb.aurelia@roomly.dev'),
    ('TestDB - Pinecrest Manali', 'Manali', 'testdb.pinecrest@roomly.dev'),
    ('TestDB - Coromandel Maison', 'Puducherry', 'testdb.coromandel@roomly.dev')
) AS seed(hotel_name, city, email)
JOIN contact_info ci ON ci.email = seed.email;

-- Hotel amenities: each hotel receives six.
INSERT INTO hotel_amenities (hotel_id, amenity)
SELECT h.id, amenity
FROM hotels h
CROSS JOIN LATERAL (
    VALUES
    ('High-speed WiFi'),
    ('24-hour Front Desk'),
    ('Airport Transfer'),
    ('In-house Dining'),
    ('Fitness Centre'),
    (CASE h.city
        WHEN 'Mumbai' THEN 'Rooftop Sea View'
        WHEN 'Goa' THEN 'Private Beach Access'
        WHEN 'Jaipur' THEN 'Heritage Courtyard'
        WHEN 'Bengaluru' THEN 'Business Lounge'
        WHEN 'Delhi' THEN 'Club Lounge'
        WHEN 'Hyderabad' THEN 'Infinity Pool'
        WHEN 'Kolkata' THEN 'Library Lounge'
        WHEN 'Udaipur' THEN 'Lake View Terrace'
        WHEN 'Manali' THEN 'Mountain View Deck'
        ELSE 'French Quarter Cafe'
     END)
) AS amenities(amenity)
WHERE h.hotel_name LIKE 'TestDB - %';

-- Three hotel photos per property.
INSERT INTO hotel_photos (hotel_id, photo_url)
SELECT h.id, photo_url
FROM hotels h
JOIN (
    VALUES
    ('TestDB - Azure Mumbai', 'https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Azure Mumbai', 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Azure Mumbai', 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Casa Mar Goa', 'https://images.unsplash.com/photo-1540541338287-41700207dee6?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Casa Mar Goa', 'https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Casa Mar Goa', 'https://images.unsplash.com/photo-1573843981267-be1999ff37cd?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Rajwara Jaipur', 'https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Rajwara Jaipur', 'https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Rajwara Jaipur', 'https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Lumina Bengaluru', 'https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Lumina Bengaluru', 'https://images.unsplash.com/photo-1615874694520-474822394e73?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Lumina Bengaluru', 'https://images.unsplash.com/photo-1600607688969-a5bfcd646154?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Imperial Delhi', 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Imperial Delhi', 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Imperial Delhi', 'https://images.unsplash.com/photo-1590381105924-c72589b9ef3f?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Deccan Courtyard', 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Deccan Courtyard', 'https://images.unsplash.com/photo-1568084680786-a84f91d1153c?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Deccan Courtyard', 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Bengal House', 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Bengal House', 'https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Bengal House', 'https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Aurelia Lake Palace', 'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Aurelia Lake Palace', 'https://images.unsplash.com/photo-1586611292717-f828b167408c?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Aurelia Lake Palace', 'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Pinecrest Manali', 'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Pinecrest Manali', 'https://images.unsplash.com/photo-1601918774946-25832a4be0d6?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Pinecrest Manali', 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Coromandel Maison', 'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Coromandel Maison', 'https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?auto=format&fit=crop&w=1400&q=85'),
    ('TestDB - Coromandel Maison', 'https://images.unsplash.com/photo-1600566753051-f0b89df2dd90?auto=format&fit=crop&w=1400&q=85')
) AS photos(hotel_name, photo_url) ON photos.hotel_name = h.hotel_name;

-- Three rooms per hotel.
INSERT INTO rooms (hotel_id, type, base_price, total_count, capacity, created_at, updated_at)
SELECT h.id, seed.type, seed.base_price, seed.total_count, seed.capacity,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
    VALUES
    ('TestDB - Azure Mumbai', 'Marine Drive King', 7800.00, 8, 2),
    ('TestDB - Azure Mumbai', 'Arabian Sea Deluxe', 11200.00, 5, 3),
    ('TestDB - Azure Mumbai', 'Skyline Executive Suite', 18500.00, 3, 4),
    ('TestDB - Casa Mar Goa', 'Garden Cabana', 6200.00, 10, 2),
    ('TestDB - Casa Mar Goa', 'Poolside Villa', 12800.00, 5, 3),
    ('TestDB - Casa Mar Goa', 'Beachfront Residence', 22400.00, 2, 4),
    ('TestDB - Rajwara Jaipur', 'Heritage Courtyard Room', 5900.00, 9, 2),
    ('TestDB - Rajwara Jaipur', 'Maharaja Deluxe', 10400.00, 5, 3),
    ('TestDB - Rajwara Jaipur', 'Royal Haveli Suite', 19600.00, 3, 4),
    ('TestDB - Lumina Bengaluru', 'Urban Studio', 5200.00, 12, 2),
    ('TestDB - Lumina Bengaluru', 'Club Business Room', 8600.00, 8, 2),
    ('TestDB - Lumina Bengaluru', 'Panorama Tech Suite', 14900.00, 4, 3),
    ('TestDB - Imperial Delhi', 'Diplomatic King', 8400.00, 10, 2),
    ('TestDB - Imperial Delhi', 'Imperial Club Room', 13200.00, 6, 3),
    ('TestDB - Imperial Delhi', 'Presidential Residence', 28500.00, 2, 5),
    ('TestDB - Deccan Courtyard', 'Courtyard Queen', 5600.00, 10, 2),
    ('TestDB - Deccan Courtyard', 'Nizam Deluxe', 9800.00, 6, 3),
    ('TestDB - Deccan Courtyard', 'Deccan Terrace Suite', 17200.00, 3, 4),
    ('TestDB - Bengal House', 'Colonial Classic', 5400.00, 9, 2),
    ('TestDB - Bengal House', 'Park Street Deluxe', 8900.00, 6, 3),
    ('TestDB - Bengal House', 'Bengal Heritage Suite', 15800.00, 3, 4),
    ('TestDB - Aurelia Lake Palace', 'Lake Garden Room', 9200.00, 7, 2),
    ('TestDB - Aurelia Lake Palace', 'Pichola Balcony Room', 14800.00, 5, 3),
    ('TestDB - Aurelia Lake Palace', 'Royal Lake Suite', 26500.00, 2, 4),
    ('TestDB - Pinecrest Manali', 'Cedar Room', 4800.00, 12, 2),
    ('TestDB - Pinecrest Manali', 'Snow View Deluxe', 7600.00, 7, 3),
    ('TestDB - Pinecrest Manali', 'Alpine Fireplace Suite', 13900.00, 4, 4),
    ('TestDB - Coromandel Maison', 'French Quarter Room', 5100.00, 10, 2),
    ('TestDB - Coromandel Maison', 'Courtyard Deluxe', 8300.00, 6, 3),
    ('TestDB - Coromandel Maison', 'Coromandel Maison Suite', 15100.00, 3, 4)
) AS seed(hotel_name, type, base_price, total_count, capacity)
JOIN hotels h ON h.hotel_name = seed.hotel_name;

-- Common room amenities plus category-specific amenities.
INSERT INTO room_amenities (room_id, amenity)
SELECT r.id, amenity
FROM rooms r
JOIN hotels h ON h.id = r.hotel_id
CROSS JOIN LATERAL (
    VALUES
    ('High-speed WiFi'),
    ('Air Conditioning'),
    ('Smart TV'),
    ('Tea and Coffee Station'),
    (CASE
        WHEN LOWER(r.type) LIKE '%suite%' OR LOWER(r.type) LIKE '%residence%' THEN 'Separate Living Area'
        WHEN LOWER(r.type) LIKE '%deluxe%' OR LOWER(r.type) LIKE '%club%' THEN 'Premium Lounge Access'
        ELSE 'Dedicated Work Desk'
     END),
    (CASE
        WHEN h.city IN ('Goa', 'Udaipur', 'Mumbai') THEN 'Scenic View'
        WHEN h.city = 'Manali' THEN 'Room Heating'
        ELSE 'Rainfall Shower'
     END)
) AS amenities(amenity)
WHERE h.hotel_name LIKE 'TestDB - %';

-- One distinct primary image for every room.
INSERT INTO room_photos (room_id, photo_url)
SELECT r.id,
       'https://images.unsplash.com/' ||
       CASE ROW_NUMBER() OVER (ORDER BY h.id, r.id)
         WHEN 1 THEN 'photo-1566665797739-1674de7a421a'
         WHEN 2 THEN 'photo-1591088398332-8a7791972843'
         WHEN 3 THEN 'photo-1609949279531-cf48d64bed89'
         WHEN 4 THEN 'photo-1560448204-e02f11c3d0e2'
         WHEN 5 THEN 'photo-1611892440504-42a792e24d32'
         WHEN 6 THEN 'photo-1616594039964-ae9021a400a0'
         WHEN 7 THEN 'photo-1598928506311-c55ded91a20c'
         WHEN 8 THEN 'photo-1631049307264-da0ec9d70304'
         WHEN 9 THEN 'photo-1578683010236-d716f9a3f461'
         WHEN 10 THEN 'photo-1595526114035-0d45ed16cfbf'
         WHEN 11 THEN 'photo-1616486338812-3dadae4b4ace'
         WHEN 12 THEN 'photo-1600210492493-0946911123ea'
         WHEN 13 THEN 'photo-1617103996702-96ff29b1c467'
         WHEN 14 THEN 'photo-1618220179428-22790b461013'
         WHEN 15 THEN 'photo-1560185007-c5ca9d2c014d'
         WHEN 16 THEN 'photo-1600566753086-00f18fb6b3ea'
         WHEN 17 THEN 'photo-1600566753190-17f0baa2a6c3'
         WHEN 18 THEN 'photo-1600566753051-f0b89df2dd90'
         WHEN 19 THEN 'photo-1600607687939-ce8a6c25118c'
         WHEN 20 THEN 'photo-1600607687920-4e2a09cf159d'
         WHEN 21 THEN 'photo-1600607688969-a5bfcd646154'
         WHEN 22 THEN 'photo-1615874694520-474822394e73'
         WHEN 23 THEN 'photo-1600210492486-724fe5c67fb0'
         WHEN 24 THEN 'photo-1618219740975-d40978bb7378'
         WHEN 25 THEN 'photo-1615529162924-f8605388461d'
         WHEN 26 THEN 'photo-1616137422495-1e9e46e2aa77'
         WHEN 27 THEN 'photo-1618221195710-dd6b41faaea6'
         WHEN 28 THEN 'photo-1601918774946-25832a4be0d6'
         WHEN 29 THEN 'photo-1505691938895-1758d7feb511'
         ELSE 'photo-1615874694520-474822394e73'
       END ||
       '?auto=format&fit=crop&w=1200&q=85'
FROM rooms r
JOIN hotels h ON h.id = r.hotel_id
WHERE h.hotel_name LIKE 'TestDB - %';

-- Add a second room photo using a stable alternate image.
INSERT INTO room_photos (room_id, photo_url)
SELECT r.id,
       CASE
         WHEN r.capacity >= 4
           THEN 'https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?auto=format&fit=crop&w=1200&q=85'
         WHEN r.base_price >= 10000
           THEN 'https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=1200&q=85'
         ELSE 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=1200&q=85'
       END
FROM rooms r
JOIN hotels h ON h.id = r.hotel_id
WHERE h.hotel_name LIKE 'TestDB - %';

-- 90 days of inventory for every room, beginning today.
-- booked_count varies slightly to exercise occupancy pricing.
-- surge_factor varies on weekends to exercise dynamic pricing.
INSERT INTO inventory (
    hotel_id,
    room_id,
    date,
    booked_count,
    total_count,
    surge_factor,
    closed,
    created_at,
    updated_at
)
SELECT
    r.hotel_id,
    r.id,
    inventory_date::date,
    CASE
      WHEN EXTRACT(DOW FROM inventory_date) IN (5, 6)
        THEN LEAST(2, GREATEST(r.total_count - 1, 0))
      ELSE 0
    END,
    r.total_count,
    CASE
      WHEN EXTRACT(DOW FROM inventory_date) IN (5, 6) THEN 1.15
      ELSE 1.00
    END,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM rooms r
JOIN hotels h ON h.id = r.hotel_id
CROSS JOIN generate_series(
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '89 days',
    INTERVAL '1 day'
) AS inventory_date
WHERE h.hotel_name LIKE 'TestDB - %';

COMMIT;

-- Verification
SELECT COUNT(*) AS seeded_hotels
FROM hotels
WHERE hotel_name LIKE 'TestDB - %';

SELECT COUNT(*) AS seeded_rooms
FROM rooms r
JOIN hotels h ON h.id = r.hotel_id
WHERE h.hotel_name LIKE 'TestDB - %';

SELECT COUNT(*) AS seeded_inventory_rows
FROM inventory i
JOIN hotels h ON h.id = i.hotel_id
WHERE h.hotel_name LIKE 'TestDB - %';
