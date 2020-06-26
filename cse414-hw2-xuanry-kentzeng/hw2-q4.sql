SELECT DISTINCT name
FROM Carriers AS C, Flights AS F
WHERE C.cid = F.carrier_id
GROUP BY month_id, day_of_month, carrier_id
HAVING COUNT(*) > 1000;