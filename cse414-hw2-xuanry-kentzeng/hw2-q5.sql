SELECT C.name AS name, SUM(F.canceled) * 1.0 /COUNT(*) * 100 AS percent
FROM Carriers AS C, Flights AS F
WHERE C.cid = F.carrier_id AND F.origin_city = "Seattle WA"
GROUP BY F.carrier_id
HAVING (SUM(F.canceled) * 1.0 /COUNT(*)) > 0.006
ORDER BY SUM(F.canceled) * 1.0 /COUNT(*) DESC;
