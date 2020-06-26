CREATE TABLE Edges(
    Source INT,
    Destination INT
);
INSERT INTO Edges
VALUES (8, 5);

INSERT INTO Edges
VALUES (6, 22);

INSERT INTO Edges
VALUES (1, 3);

INSERT INTO Edges
VALUES (5, 5);

SELECT *
FROM Edges;

SELECT E.Source
FROM Edges AS E;

SELECT E.Source, E.Destination
FROM Edges AS E
WHERE E.Source > E.Destination;
/*
    This statement runs without error.
    SQLite supports the concept of "type affinity" on columns.
    Each column in an SQLite 3 database is assigned
    one of the following type affinities:
        TEXT
        NUMERIC
        INTEGER
        REAL
        BLOB
    A column with NUMERIC affinity may contain values using all five storage classes.
    When text data is inserted into a NUMERIC column,
    the storage class of the text is converted to INTEGER or REAL.
*/
INSERT INTO Edges
VALUES ('-1', '2000');