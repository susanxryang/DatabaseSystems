WITH Direct AS (
  SELECT DISTINCT F2.dest_city
  FROM Flights AS F2
  WHERE F2.origin_city = 'Seattle WA'
),
One_stop AS (
  SELECT DISTINCT F3.dest_city
  FROM Flights AS F3
  WHERE F3.origin_city IN (SELECT DISTINCT F4.dest_city AS stop_city
                          FROM Flights AS F4
                          WHERE F4.origin_city = 'Seattle WA')
        AND F3.dest_city != 'Seattle WA'
),
NotSatisfied AS (
    SELECT DISTINCT D.dest_city
    FROM Direct AS D
    UNION
    SELECT DISTINCT OS.dest_city
    FROM One_stop AS OS
),
Cities AS (
    SELECT F4.origin_city
    FROM Flights AS F4
    UNION
    SELECT F5.dest_city
    FROM Flights AS F5
)
SELECT DISTINCT C.origin_city AS city
FROM Cities AS C
WHERE C.origin_city != 'Seattle WA'
EXCEPT
SELECT DISTINCT NotSatisfied.dest_city
FROM NotSatisfied
ORDER BY city;

-- Affected rows: 4
-- Query Succeeded | 34s

-- "Devils Lake ND"
-- "Hattiesburg/Laurel MS"
-- "St. Augustine FL"
-- "Victoria TX"
