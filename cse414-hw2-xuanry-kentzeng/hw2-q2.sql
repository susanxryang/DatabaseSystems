SELECT C.name, F1.flight_num, F1.origin_city, F1.dest_city, F1.actual_time,
       F2.flight_num, F2.origin_city, F2.dest_city, F2.actual_time
FROM Carriers AS C, Flights AS F1, Flights AS F2, Months AS M
WHERE C.cid = F1.carrier_id
      AND F1.carrier_id = F2.carrier_id
      AND F1.origin_city = "Boston MA" AND F2.dest_city = "Seattle WA"
      AND F1.month_id = M.mid AND M.month = "July" AND F1.day_of_month = 4
      AND F1.month_id = F2.month_id AND F1.day_of_month = F2.day_of_month
      AND F1.dest_city = F2.origin_city AND (F1.actual_time + F2.actual_time) < 480
      AND F1.dest_city != "Seattle WA";
