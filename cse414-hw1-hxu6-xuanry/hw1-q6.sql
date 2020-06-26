SELECT *
FROM MyRestaurants AS R
WHERE R.VegetarianFriendly = 1 AND
date('now', '-3 month') > date(R.LastVisitDate);