SELECT DISTINCT flight_num AS flight_num
FROM Flights AS F, Carriers AS C, Weekdays AS W
WHERE F.origin_city = "Seattle WA" AND F.dest_city = "Boise ID"
      AND C.name = "Alaska Airlines Inc." AND C.cid = F.carrier_id
      AND W.day_of_week = "Friday" AND W.did = F.day_of_week_id;
