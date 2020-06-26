SELECT SUM(capacity) AS capacity
FROM Flights AS F, Months AS M
WHERE F.month_id = M.mid AND M.month = "July" AND F.day_of_month = 11 AND
      ((origin_city = "San Francisco CA" AND dest_city = "Seattle WA") OR
      (origin_city = "Seattle WA" AND dest_city = "San Francisco CA"));
