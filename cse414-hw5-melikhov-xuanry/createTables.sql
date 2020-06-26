CREATE TABLE Users(
  username VARCHAR(20) PRIMARY KEY NOT NULL,
  password VARBINARY(20) NOT NULL,
  salt VARBINARY(20) NOT NULL,
  balance INT
);


CREATE TABLE Reservations(
  rid INT PRIMARY KEY NOT NULL,
  username VARCHAR(20) FOREIGN KEY REFERENCES Users,
  it_date INT, /* the date of the itinerary, stored to avoid duplicate */
  itinerary VARCHAR(20),
  /*1 = true, 0 = false*/
  paid INT,
  canceled INT
);
