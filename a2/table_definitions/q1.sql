-- VoteRange

SET SEARCH_PATH TO parlgov;
drop table if exists q1 cascade;

-- You must not change this table definition.

create table q1(
year INT,
countryName VARCHAR(50),
voteRange VARCHAR(20),
partyName VARCHAR(100)
);



-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)
DROP VIEW IF EXISTS intermediate_step CASCADE;
-- Define views for your intermediate steps here.



-- Find all the valid vote range 
CREATE VIEW Content AS
-- CAST FUNCTION CHANGE INT TO DECIMAL FOR DIVISION
-- EXTRACT year from e_date
	SELECT DISTINCT country.name as cn, party.name_short as pn, EXTRACT(YEAR FROM e_date) as y, (CAST(election_result.votes AS DECIMAL(19,3))/ votes_cast) as ValidPecent
	FROM election, country, party,election_result
	WHERE election.id = election_result.election_id and election_result. party_id = party.id and election.country_id = country.id and election_result.votes IS NOT NULL and e_date <= '2016-12-31' and e_date >= '1996-01-01' and votes_cast IS NOT NULL;
	
CREATE VIEW TakeAvg AS
	SELECT Distinct cn,pn,y, avg(ValidPecent) as AvgPercent
	FROM Content
	GROUP BY cn,pn,y;

CREATE VIEW BelowFive AS
	SELECT DISTINCT cn,pn,y, '(0-5]' as v
	FROM TakeAvg
	WHERE AvgPercent <= 0.05 and AvgPercent  > 0;

CREATE VIEW FiveToTen AS	
	SELECT DISTINCT cn,pn,y, '(5-10]' as v
	FROM TakeAvg
	WHERE AvgPercent <= 0.10 and AvgPercent  > 0.05;

CREATE VIEW TenToTwenty AS
	SELECT DISTINCT cn,pn,y, '(10-20]' as v
	FROM TakeAvg
	WHERE AvgPercent <= 0.20 and AvgPercent  > 0.10;


CREATE VIEW TwentyToThirty AS
	SELECT DISTINCT cn,pn,y, '(20-30]' as v
	FROM TakeAvg
	WHERE AvgPercent <= 0.30 and AvgPercent  > 0.20;


CREATE VIEW ThirtyToForty AS
	SELECT DISTINCT cn,pn,y, '(30-40]' as v
	FROM TakeAvg
	WHERE AvgPercent <= 0.40 and AvgPercent  > 0.30;

CREATE VIEW AboveForty AS
	SELECT DISTINCT cn,pn,y, '(40-100]' as v
	FROM TakeAvg
	WHERE AvgPercent  > 0.40;




-- the answer to the query 


insert into q1 (countryName, partyName,year,voteRange)
SELECT *
FROM Belowfive;

insert into q1 (countryName, partyName,year,voteRange)
SELECT *
FROM FiveToTen ;

insert into q1 (countryName, partyName,year,voteRange)
SELECT *
FROM TenToTwenty;

insert into q1 (countryName, partyName,year,voteRange)
SELECT *
FROM TwentyToThirty ;

insert into q1 (countryName, partyName,year,voteRange)
SELECT *
FROM ThirtyToForty ;

insert into q1 (countryName, partyName,year,voteRange)
SELECT *
FROM AboveForty ;


SELECT *
FROM q1
ORDER BY year,countryName,voteRange,partyName;






DROP VIEW Content,TakeAvg, BelowFive,FiveToTen,TenToTwenty,TwentyToThirty,ThirtyToForty ,AboveForty Cascade;


