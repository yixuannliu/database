CREATE VIEW election_rate AS
SELECT id, country_id, date_part('year', e_date) AS year,
	CAST(votes_cast as FLOAT)/electorate as vote_rate
FROM election
WHERE votes_cast is not NULL 
	and e_date < '2017-01-01' and e_date >= '2001-01-01';

CREATE VIEW election_rate_and_year AS
SELECT country_id, year, AVG(vote_rate) AS ratio
FROM election_rate
GROUP BY year, country_id;

CREATE VIEW ratio_increase AS
SELECT e1.country_id
FROM election_rate_and_year as e1, election_rate_and_year as e2
WHERE e1.country_id = e2.country_id and e1.year < e2.year
	and e1.ratio < e2.ratio
GROUP BY e1.country_id;

CREATE VIEW ratio_increase_year AS
SELECT R.country_id, E.year, E.ratio as participationRatio
FROM ratio_increase as R, election_rate_and_year as E
WHERE R.country_id = E.country_id;

SELECT country.name AS countryName, year, participationRatio
FROM country, ratio_increase_year
WHERE country.id = ratio_increase_year.country_id;

DROP VIEW ratio_increase_year;
DROP VIEW ratio_increase;
DROP VIEW election_rate_and_year;
DROP VIEW election_rate;


