// A simple JDBC example.

// Remember that you need to put the jdbc postgresql driver in your class path
// when you run this code.
// See /local/packages/jdbc-postgresql on cdf for the driver, another example
// program, and a how-to file.

// To compile and run this program on cdf:
// (1) Compile the code in Example.java.
//     javac Example
// This creates the file Example.class.
// (2) Run the code in Example.class.
// Normally, you would run a Java program whose main method is in a class 
// called Example as follows:
//     java Example
// But we need to also give the class path to where JDBC is, so we type:
//     java -cp /local/packages/jdbc-postgresql/postgresql-42.2.4.jar: Example
// Alternatively, we can set our CLASSPATH variable in linux.  (See
// /local/packages/jdbc-postgresql/HelloPostgresql.txt on cdf for how.)

import java.sql.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Set;

class Example {

    public static class ElectionCabinetResult implements Serializable {
        public List<Integer> elections;
        public List<Integer> cabinets;
        public ElectionCabinetResult(List<Integer> elections, List<Integer> cabinets) {
            this.elections = elections;
            this.cabinets = cabinets;
        }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ElectionCabinetResult)) {
                return false;
            }
            ElectionCabinetResult other = (ElectionCabinetResult) obj;
            return this.elections.equals(other.elections) &&
                    this.cabinets.equals(other.cabinets);
        }
        @Override
        public String toString() {
            return "e: " + this.elections.toString() + " c: " + this.cabinets.toString();
        }
    }
    
    public static void main(String args[]) throws IOException
        {
            String url;
            Connection connection;
            List<Integer> elections = new ArrayList<Integer>();
            List<Integer> cabinets = new ArrayList<Integer>();


            try {
                Class.forName("org.postgresql.Driver");
            }
            catch (ClassNotFoundException e) {
                System.out.println("Failed to find the JDBC driver");
            }


	    try {
            url = "jdbc:postgresql://localhost:5432/csc343h-liuyixu6";
            connection = DriverManager.getConnection(url, "liuyixu6", "");


            String countryName = new String("Japan");
            PreparedStatement execStat = connection.prepareStatement(
                    "SELECT CB.election_id, CB.id " +
                            "FROM parlgov.country AS CT, parlgov.cabinet AS CB " +
                            "WHERE CT.id = CB.country_id " +
                            "and CT.name = '" + countryName + "'");

            ResultSet resultSet = execStat.executeQuery();

            int electionID = -1;
            int cabinetID = -1;

            while(resultSet.next()){
                electionID = resultSet.getInt("election_id");
                cabinetID = resultSet.getInt("id");

                elections.add(electionID);
                cabinets.add(cabinetID);
            }
//
//
//
//            PreparedStatement execStatCountryId = connection.prepareStatement(
//                    "SELECT id FROM parlgov.country WHERE name = '" + countryName + "'");
//
//            ResultSet countryIdSet = execStatCountryId.executeQuery();
//
//            int countryID = -1;
//
//            while(countryIdSet.next()){
//                countryID = countryIdSet.getInt("id");
//            }
//
//
//            PreparedStatement execStatElection = connection.prepareStatement(
//                    "SELECT id FROM parlgov.election WHERE country_id = " + countryID);
//            ResultSet electionSet = execStatElection.executeQuery();
//
//
//            int electionID = -1;
//            int cabinetID = -1;
//
//            while(electionSet.next()){
//                electionID = electionSet.getInt("id");
//
//                PreparedStatement execStatCabinets = connection.prepareStatement(
//                        "SELECT id FROM parlgov.cabinet WHERE country_id = " + countryID + " and election_id = " + electionID);
//                ResultSet cabinetsSet = execStatCabinets.executeQuery();
//
//                while(cabinetsSet.next()){
//                    cabinetID = cabinetsSet.getInt("id");
//                    elections.add(electionID);
//                    cabinets.add(cabinetID);
//                }
//            }

            ElectionCabinetResult result = new ElectionCabinetResult(elections, cabinets);

            System.out.println(result);

        } catch(SQLException e) {

		    System.out.println(e);
            
        }


        }
        
}
