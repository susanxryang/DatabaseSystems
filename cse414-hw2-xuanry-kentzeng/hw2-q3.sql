SELECT day_of_week AS day_of_week, AVG(arrival_delay) AS delay
FROM Weekdays AS W, Flights AS F
WHERE W.did = F.day_of_week_id
GROUP BY F.day_of_week_id
ORDER BY AVG(arrival_delay)
LIMIT 1;
