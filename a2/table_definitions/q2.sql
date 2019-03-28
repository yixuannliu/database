-- Winners

SET SEARCH_PATH TO parlgov;
drop table if exists q2 cascade;

-- You must not change this table definition.

create table q2(
countryName VARCHaR(100),
partyName VARCHaR(100),
partyFamily VARCHaR(100),
wonElections INT,
mostRecentlyWonElectionId INT,
mostRecentlyWonElectionYear INT
);


-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)
--DROP VIEW IF EXISTS viewOne CASCADE;

-- Define views for your intermediate steps here.
--The same country, won more than 3* [(total # of elections) / # of all parties] 


--# of election for each country 
CREATE VIEW countryElectionTimes AS (
SELECT country_id,COUNT(*) AS electionTimes
FROM election
GROUP BY country_id
);

--# of parties for each country 
CREATE VIEW numPartiesForCountries AS (
SELECT country_id,COUNT(*) AS partiesNum
FROM party
GROUP BY country_id
);

CREATE VIEW avgElectionForEachC AS (
SELECT country_id,electionTimes,partiesNum,electionTimes/partiesNum AS averageElection
FROM countryElectionTimes NATURAL JOIN numPartiesForCountries
ORDER BY country_id
);

--for each party, # of times win
--Firstly find the winning party for each election
CREATE VIEW winningPartyTemp AS (
SELECT election_id,party_id,votes
FROM election_result
WHERE votes is not null
EXCEPT		
SELECT e1.election_id ,e1.party_id,e1.votes
FROM election_result e1, election_result e2
WHERE e1.election_id = e2.election_id AND e1.votes < e2.votes 
ORDER BY election_id 
);

CREATE VIEW winningParty AS (
SELECT election_id,party_id,votes
FROM election_result
WHERE votes is not null
EXCEPT		
SELECT e1.election_id ,e1.party_id,e1.votes
FROM election_result e1, election_result e2
WHERE e1.election_id = e2.election_id AND e1.votes < e2.votes 
ORDER BY election_id 
);

CREATE VIEW WinningPartyTime AS (
SELECT party_id,MAX(e_date) AS winDate
FROM winningParty,election
WHERE election.id = winningParty.election_id
GROUP BY party_id
);

CREATE VIEW partyWinTime AS (
SELECT party_id, COUNT(*) AS partyWinNum
FROM winningParty
GROUP BY party_id
);


CREATE VIEW result AS (
SELECT DISTINCT country.name AS countryName,party.name, party_family.family, partyWinNum, party.id,EXTRACT(YEAR from winDate )
FROM (party NATURAL JOIN avgElectionForEachC),partyWinTime,party_family,country,WinningPartyTime 
WHERE partyWinTime.party_id = party.ID AND
	 party_family.party_id = party.ID AND
      country.ID = party.country_id AND
	WinningPartyTime.party_id = party.ID AND
	 partyWinNum > (averageElection *3)
--ORDER BY country_id
);


-- the answer to the query 
insert into q2 
SELECT *
FROM result;

SELECT *
FROM result;



DROP VIEW IF EXISTS result CASCADE;
DROP VIEW IF EXISTS partyWinTime CASCADE;
DROP VIEW IF EXISTS WinningPartyTime CASCADE;
DROP VIEW IF EXISTS winningParty CASCADE;
DROP VIEW IF EXISTS winningPartyTemp CASCADE;
DROP VIEW IF EXISTS avgElectionForEachC CASCADE;
DROP VIEW IF EXISTS numPartiesForCountries CASCADE;
DROP VIEW IF EXISTS countryElectionTimes CASCADE;

