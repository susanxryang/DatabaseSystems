SELECT C.name AS name, SUM(F.departure_delay) AS delay
FROM Carriers AS C, Flights AS F
WHERE C.cid = F.carrier_id AND F.canceled = 0
GROUP BY F.carrier_id;
