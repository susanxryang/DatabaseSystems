WITH CRIT AS (SELECT DISTINCT F.origin_city AS city, COUNT(*) AS cnt
              FROM Flights AS F
              WHERE F.canceled = 0 AND F.actual_time < 180
              GROUP BY F.origin_city)

SELECT F.origin_city,
    CASE
        WHEN C.cnt*100.0/COUNT(F.fid) is NULL THEN 0
        ELSE C.cnt*100.0/COUNT(F.fid)
    END AS percentage
FROM Flights AS F
LEFT JOIN CRIT AS C ON F.origin_city = C.city
WHERE F.canceled = 0
GROUP BY F.origin_city, C.cnt
ORDER BY percentage;


-- Affected rows: 327
-- Query succeeded | 60s

-- origin_city,percentage
-- "Guam TT",0.000000000000
-- "Pago Pago TT",0.000000000000
-- "Aguadilla PR",28.8973384030418
-- "Anchorage AK",31.8120805369128
-- "San Juan PR",33.6605316973415
-- "Charlotte Amalie VI",39.5588235294118
-- "Ponce PR",40.9836065573771
-- "Fairbanks AK",50.1165501165501
-- "Kahului HI",53.5144713526285
-- "Honolulu HI",54.7390288236822
-- "San Francisco CA",55.8288645371882
-- "Los Angeles CA",56.0808908229873
-- "Seattle WA",57.6093877922314
-- "Long Beach CA",62.1764395139989
-- "New York NY",62.371834136728
-- "Kona HI",63.1607929515419
-- "Las Vegas NV",64.9202563720376
-- "Christiansted VI",65.1006711409396
-- "Newark NJ",65.8499710969808
-- "Plattsburgh NY",66.6666666666667
