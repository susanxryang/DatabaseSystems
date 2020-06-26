The tables we created to be stored long-term are Users and Reservations. 
1. The Users table has 4 attributes: 
2. the primary key username(not encrypted)
3. the password(encrypted using the hashPassword() function)
4. the salt(retrieved from the same hashPassword() function call)
the balance(not encrypted)

The Reservations table has 6 attributes: 
1. The primary key rid, which is the reservation id, this is incremented by each reservation
2. the username, which is a foreign key that’s referenced from the Users table
3. the it_date, which is the Itinerary date, we decided to include this information in the database because this allows us to easily retrieve the date of the given itinerary, in order to prevent the user from booking two flights on the same day
4. the itinerary, which contains a string of the flight(s) in the itinerary. If there is one flight in the itinerary,  it is “fid”; if there are two flights it is “fid1, fid2”
5. paid, initially 0; if paid, paid = 1
6. Canceled, initially 0; if canceled, canceled = 1

The static variables in our program include the login status of the current session, the username of the user that’s currently logged in, and the user’s last search. We decided to store these variables in-memory because they change between each transaction and don’t need to be retrieved across different transactions. 
Besides, we implemented an itinerary class that allows us to store data about flights from the last search more efficiently. The capacity function calculates the current capacity for a given flight, using the difference between original capacity and an aggregate of a given fid in the Reservations table, this allows us to use current capacity transiently without having to update any persistent data.
