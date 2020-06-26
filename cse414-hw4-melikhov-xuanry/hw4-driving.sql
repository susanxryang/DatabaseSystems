CREATE TABLE Person (
  ssn INT PRIMARY KEY,
  name VARCHAR
)

CREATE TABLE Driver (
  ssn INT PRIMARY KEY FOREIGN KEY REFERENCES Person(ssn),
  DriverID INT,
  professional INT
)

CREATE TABLE ProfessionalDriver (
  ssn INT PRIMARY KEY FOREIGN KEY REFERENCES Driver(ssn),
  license VARCHAR FOREIGN KEY REFERENCES Truck(license),
  medicalHistory VARCHAR
)

CREATE TABLE Drives (
  ssn INT FOREIGN KEY REFERENCES Driver(ssn),
  license VARCHAR FOREIGN KEY REFERENCES Car(license)
)

CREATE TABLE Vehicle (
  Year INT,
  licensePlate VARCHAR PRIMARY KEY,
  ssn INT FOREIGN KEY REFERENCES Person(ssn),
  insurance_name VARCHAR FOREIGN KEY REFERENCES InsuranceCo(name),
  maxLiability FLOAT
)

CREATE TABLE Car (
  license VARCHAR PRIMARY KEY FOREIGN KEY REFERENCES Vehicle(licensePlate),
  make VARCHAR
)

CREATE TABLE Truck (
  license VARCHAR PRIMARY KEY FOREIGN KEY REFERENCES Vehicle(licensePlate),
  capacity INT
)

CREATE TABLE InsuranceCo (
  name VARCHAR PRIMARY KEY,
  phone INT
)


-- 2. This relation is stored in the insurance_name of the vehicle table (foreign key references
--    InsuranceCo) and maxLiability attributes in the vehicle table.
--    We stored it this way because a vehicle can only be insured by one insurance company and has
--    only one max liability, ans we decided that we don't need a seperate table for the "insures"
--    relationship.

-- 3. The drives relationship is represented in the "Drives" table, where it stores the Car's
--    license (reference Car table), and the driver's ssn (reference Driver table)
--    The operates relationship is represented in the ProfessionalDriver table, where it stores a
--    foreign key license that references the truck table, and a foreign key ssn that references
--    the Driver table.
--    They are different because the Drives table represents the many to many relationship between
--    cars and non-professional drivers, and it can only reference the license plate of
--    cars; where as a truck can only be operated by one ProfessionalDriver, and
--    ProfessionalDriver table can only reference the license plate of trucks.
