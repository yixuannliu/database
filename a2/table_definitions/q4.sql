-- Left-right

SET SEARCH_PATH TO parlgov;
drop table if exists q4 cascade;

-- You must not change this table definition.


CREATE TABLE q4(
        countryName VARCHAR(50),
        r0_2 INT,
        r2_4 INT,
        r4_6 INT,
        r6_8 INT,
        r8_10 INT
);

-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)
DROP VIEW IF EXISTS intermediate_step CASCADE;

-- Define views for your intermediate steps here.
CREATE VIEW CountryPartyNames AS
	SELECT country.name as n, party.id as i, left_right as lr
	FROM country LEFT JOIN party on country.id = party.country_id 
			LEFT JOIN party_position on party_position.party_id = party.id;


CREATE VIEW A AS
SELECT n, count(i) as c1
FROM CountryPartyNames
WHERE lr>=0 and lr<2
GROUP BY n;

CREATE VIEW B AS
SELECT n, count(i) as c2
FROM CountryPartyNames
WHERE lr>=2 and lr<4
GROUP BY n;

CREATE VIEW C AS
SELECT n, count(i) as c3
FROM CountryPartyNames
WHERE lr>=4 and lr<6
GROUP BY n;

CREATE VIEW D AS
SELECT n, count(i) as c4
FROM CountryPartyNames
WHERE lr>=6 and lr<8
GROUP BY n;

CREATE VIEW E AS
SELECT n, count(i) as c5
FROM CountryPartyNames
WHERE lr>=8 and lr<10
GROUP BY n;



-- the answer to the query 

-- WHERE clause is applied first, then the results grouped, and finally the groups filtered according to the HAVING clause.
-- Having can filter groups and filter based on aggregate function results



INSERT INTO q4 
SELECT B.n, c1,c2,c3,c4,c5
FROM A
	LEFT JOIN B ON A.n = B.n
	LEFT JOIN C ON B.n = C.n
	LEFT JOIN D ON C.n = D.n
	LEFT JOIN E ON D.n= E.n;





SELECT *
From q4;






DROP VIEW A,B,C,D,E CASCADE;
DROP VIEW CountryPartyNames CASCADE;
