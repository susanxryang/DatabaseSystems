SELECT DISTINCT C.name AS carrier
FROM Carriers AS C, Flights AS F
WHERE C.cid = F.carrier_id AND F.origin_city = 'Seattle WA' AND
F.dest_city = 'San Francisco CA'
ORDER BY C.name;

-- Affected rows: 4
-- Query succeeded | 4s

-- carrier
-- Alaska Airlines Inc.
-- SkyWest Airlines Inc.
-- United Air Lines Inc.
-- Virgin America
