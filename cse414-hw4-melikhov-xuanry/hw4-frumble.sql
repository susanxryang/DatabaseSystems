CREATE TABLE Frumble (
  name VARCHAR,
  discount VARCHAR,
  month VARCHAR,
  price INT
)

-- name -> price
SELECT Dependence.count - Presedence.count
FROM (SELECT COUNT(*) AS count
      FROM
        (SELECT COUNT(*)
         FROM FRUMBLE F1
         GROUP BY F1.price, F1.name)) AS Dependence,
      (SELECT COUNT(*) AS count
      FROM (
        SELECT COUNT(*)
        FROM FRUMBLE F2
        GROUP BY F2.name)) AS Presedence;

-- month -> discount
SELECT Dependence.count - Presedence.count
FROM (SELECT COUNT(*) AS count
      FROM
        (SELECT COUNT(*)
          FROM FRUMBLE F1
          GROUP BY F1.discount, F1.month)) AS Dependence,
        (SELECT COUNT(*) AS count
          FROM (
          SELECT COUNT(*)
          FROM FRUMBLE F2
          GROUP BY F2.month)) AS Presedence;

-- discount, month -> price
/*NOT A FUNCTIONAL DEPENDENCY*/
SELECT Dependence.count - Presedence.count
FROM (SELECT COUNT(*) AS count
      FROM
        (SELECT COUNT(*)
         FROM FRUMBLE F1
         GROUP BY F1.discount, F1.month, F1.price)) AS Dependence,
      (SELECT COUNT(*) AS count
      FROM (
        SELECT COUNT(*)
        FROM FRUMBLE F2
        GROUP BY F2.discount, F2.month)) AS Presedence;

-- name, discount -> month
/*NOT A FUNCTIONAL DEPENDENCY*/
SELECT Dependence.count - Presedence.count
FROM (SELECT COUNT(*) AS count
      FROM
        (SELECT COUNT(*)
         FROM FRUMBLE F1
         GROUP BY F1.discount, F1.name, F1.month)) AS Dependence,
      (SELECT COUNT(*) AS count
      FROM (
        SELECT COUNT(*)
        FROM FRUMBLE F2
        GROUP BY F2.name, F2.discount)) AS Presedence;

-- 3.
CREATE TABLE Name_price (
  name VARCHAR PRIMARY KEY,
  price INT
);

CREATE TABLE Name_month (
  name VARCHAR REFERENCES Name_price(name),
  month VARCHAR REFERENCES Month_discount(month)
);

CREATE TABLE Month_discount (
  month VARCHAR PRIMARY KEY,
  discount VARCHAR
);

-- 4.
INSERT INTO Name_price
SELECT DISTINCT F.name, F.price
FROM FRUMBLE F;

INSERT INTO Month_discount
SELECT DISTINCT F.month, F.discount
FROM FRUMBLE F;

INSERT INTO Name_month
SELECT DISTINCT F.name, F.month
FROM FRUMBLE F;

SELECT COUNT(*) FROM Name_price;
-- 36

SELECT COUNT(*) FROM Month_discount;
-- 12

SELECT COUNT(*) FROM Name_month;
-- 426
