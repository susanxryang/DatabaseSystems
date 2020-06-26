WITH CityPairs AS (
SELECT F1.dest_city, F1.origin_city
FROM Flights AS F1
UNION
SELECT F2.origin_city, F2.dest_city
FROM Flights AS F2
)
SELECT COUNT(*) AS num_connected_cities
FROM CityPairs AS CP
WHERE CP.dest_city > CP.origin_city;


-- Affected Rows: 1
-- Query succeeded | 39s

-- 2351