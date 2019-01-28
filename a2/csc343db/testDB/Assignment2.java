import java.sql.*;
import java.util.List;

// If you are looking for Java data structures, these are highly useful.
// Remember that an important part of your mark is for doing as much in SQL (not Java) as you can.
// Solutions that use only or mostly Java will not receive a high mark.
import java.util.ArrayList;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.Set;
//import java.util.HashSet;
public class Assignment2 extends JDBCSubmission {

    public Assignment2() throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
    }

    @Override
    public boolean connectDB(String url, String username, String password) {
        // Implement this method!
        try{
            // public Connection variable
            connection = DriverManager.getConnection(url, username, password);
            return true;
        } catch(SQLException e){
            return false;
        }
    }

    @Override
    public boolean disconnectDB() {
        // Implement this method!
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

    @Override
    public ElectionCabinetResult electionSequence(String countryName) {
        // Implement this method!

        List<Integer> elections = new ArrayList<Integer>();
        List<Integer> cabinets = new ArrayList<Integer>();

        try {
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

            ElectionCabinetResult result = new ElectionCabinetResult(elections, cabinets);
            return result;

        } catch(SQLException e) {
            return null;
        }
    }


    @Override
    public List<Integer> findSimilarPoliticians(Integer politicianId, Float threshold) {
        // Implement this method!
        try {
            PreparedStatement execStat = connection.prepareStatement(
                    "SELECT P1.id, P1.description as d1, P1.comment as c1, P2.description as d2, P2.comment as c2 " +
                    "FROM parlgov.politician_president AS P1, parlgov.politician_president AS P2 " +
                    "WHERE P1.id != P2.id " +
                    "and P2.id = " + politicianId);

            ResultSet politicians = execStat.executeQuery();

            List<Integer> resultPoliticians = new ArrayList<Integer>();

            while (politicians.next()) {
                Integer new_id = politicians.getInt("id");

                String d1 = politicians.getString("d1");
                String c1 = politicians.getString("c1");
                String d2 = politicians.getString("d2");
                String c2 = politicians.getString("c2");

                double jaccardSimilarity = similarity(d1 + c1, d2 + c2);
                if (jaccardSimilarity >= threshold) {
                    resultPoliticians.add(new_id);
                }
            }
            return resultPoliticians;
        } catch(SQLException e) {
            return null;
        }
    }


    public static void main(String[] args) {
        // You can put testing code in here. It will not affect our autotester.
	    System.out.println("Hello");
    }


}

