WITH MinTime AS (
    SELECT DISTINCT F2.origin_city AS origin_city, MIN(F2.actual_time) AS time
    FROM Flights AS F2
    WHERE F2.canceled = 0
    GROUP BY F2.origin_city)
SELECT F.origin_city, F.dest_city, F1.time AS time
FROM Flights AS F, MinTime AS F1
WHERE F.origin_city = F1.origin_city AND F1.time = F.actual_time
GROUP BY F.origin_city, F.dest_city, F1.time
ORDER BY time ASC, F.origin_city ASC;


-- Affected rows: 339
-- Query succeeded | 27s

-- origin_city      dest_city       time
-- Bend/Redmond OR  Los Angeles CA   10
-- Burbank CA       New York NY      10
-- Las Vegas NV     Chicago IL       10
-- New York NY      Nashville TN     10
-- Newark NJ        Detroit MI       10
-- Sacramento CA    Atlanta GA       10
-- Washington DC    Minneapolis MN   10
-- Boise ID         Chicago IL       11
-- Boston MA        Philadelphia PA  11
-- Buffalo NY       Orlando FL       11
-- Cincinnati OH    New Haven CT     11
-- Denver CO        Honolulu HI      11
-- Denver CO        Orlando FL       11
-- Denver CO        Philadelphia PA  11
-- Fort Myers FL    Chicago IL       11
-- Houston TX Salt  Lake City UT     11
-- Minneapolis MN   Newark NJ        11
-- Pittsburgh PA    Dallas/Fort Worth TX 11
-- Indianapolis IN  Houston TX           12
-- Phoenix AZ       Dallas/Fort Worth TX 12
