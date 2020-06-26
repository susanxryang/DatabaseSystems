SELECT DISTINCT F.origin_city AS city
FROM Flights AS F
WHERE F.origin_city NOT IN (SELECT F1.origin_city
                    FROM Flights AS F1
                    WHERE F1.canceled = 0 AND F1.actual_time >= 180)
ORDER BY city;


-- Affected rows: 109
-- Query succeeded | 31s

-- city
-- Aberdeen SD
-- Abilene TX
-- Alpena MI
-- Ashland WV
-- Augusta GA
-- Barrow AK
-- Beaumont/Port Arthur TX
-- Bemidji MN
-- Bethel AK
-- Binghamton NY
-- Brainerd MN
-- Bristol/Johnson City/Kingsport TN
-- Butte MT
-- Carlsbad CA
-- Casper WY
-- Cedar City UT
-- Chico CA
-- College Station/Bryan TX
-- Columbia MO
-- Columbus GA
