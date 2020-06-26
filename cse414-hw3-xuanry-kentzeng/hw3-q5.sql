SELECT DISTINCT F1.dest_city AS city
FROM Flights AS F1
WHERE F1.origin_city IN (SELECT DISTINCT F.dest_city AS stop_city
                        FROM Flights AS F
                        WHERE F.origin_city = 'Seattle WA')
      AND F1.dest_city != 'Seattle WA'
      AND F1.dest_city NOT IN (SELECT DISTINCT F.dest_city AS stop_city
                              FROM Flights AS F
                              WHERE F.origin_city = 'Seattle WA')
ORDER BY city;


-- Affected rows: 256
-- Query succeeded | 26s
-- city
-- Aberdeen SD
-- Abilene TX
-- Adak Island AK
-- Aguadilla PR
-- Akron OH
-- Albany GA
-- Albany NY
-- Alexandria LA
-- Allentown/Bethlehem/Easton PA
-- Alpena MI
-- Amarillo TX
-- Appleton WI
-- Arcata/Eureka CA
-- Asheville NC
-- Ashland WV
-- Aspen CO
-- Atlantic City NJ
-- Augusta GA
-- Bakersfield CA
-- Bangor ME
