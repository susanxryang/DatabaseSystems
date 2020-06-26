SELECT c.name AS carrier, MIN(F.price) AS min_price
FROM Carriers AS C, Flights AS F
WHERE C.cid = F.carrier_id AND
      ((F.origin_city = "Seattle WA" AND F.dest_city = "New York NY") OR
      (F.origin_city = "New York NY" AND F.dest_city = "Seattle WA"))
GROUP BY F.carrier_id;
