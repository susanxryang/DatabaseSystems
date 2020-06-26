WITH ArrFlights AS (SELECT COUNT(*) AS total, F1.dest_city, W.did AS day
                   FROM Flights AS F1, Weekdays AS W
                   WHERE W.did = F1.day_of_week_id
                   GROUP BY F1.dest_city, W.did),

CountWeek AS (SELECT D.day_of_week AS day_of_week, COUNT(*) AS cnt
              FROM (SELECT F2.day_of_week_id AS day_of_week
                    FROM Flights AS F2
                    GROUP BY F2.day_of_month, F2.day_of_week_id) AS D
              GROUP BY D.day_of_week),

MaxFlights AS (SELECT W.day_of_week AS day_of_week, A.dest_city AS dest_city,
                      (A.total/C.cnt) AS avg_flights, W.did AS day_id,
                      ROW_NUMBER() OVER (PARTITION BY W.did ORDER BY W.did ASC, (A.total/C.cnt) DESC)
                      AS row_num
               FROM CountWeek AS C, ArrFlights AS A, Weekdays AS W
               WHERE A.day = C.day_of_week AND W.did = A.day)

SELECT M.day_of_week, M.dest_city, M.avg_flights
FROM MaxFlights AS M
WHERE M.row_num <= 2;


-- Affected rows: 14
-- Query succeeded | 17s

-- day_of_week,dest_city,avg_flights
-- Monday,"Chicago IL",2171
-- Monday,"Atlanta GA",2132
-- Tuesday,"Chicago IL",2400
-- Tuesday,"Atlanta GA",2334
-- Wednesday,"Chicago IL",2450
-- Wednesday,"Atlanta GA",2372
-- Thursday,"Chicago IL",2452
-- Thursday,"Atlanta GA",2348
-- Friday,"Chicago IL",2447
-- Friday,"Atlanta GA",2350
-- Saturday,"Chicago IL",2308
-- Saturday,"Atlanta GA",2286
-- Sunday,"Chicago IL",2320
-- Sunday,"Atlanta GA",2276
