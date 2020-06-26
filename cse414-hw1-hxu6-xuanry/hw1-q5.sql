SELECT R.Name, R.Distance
FROM MyRestaurants AS R
WHERE R.Distance <= 20
ORDER BY R.Name;