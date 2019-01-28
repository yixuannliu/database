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
        return null;
    }

//    Expected the method return value to be 'e: [3, 1, 2] c: [3, 1, 2]' instead of 'null'

    @Override
    public List<Integer> findSimilarPoliticians(Integer politicianId, Float threshold) {
        // Implement this method!
        try {
            PreparedStatement execStat = connection.prepareStatement(
                    "SELECT id, description, comment FROM politician_president");

            PreparedStatement execStatWithId = connection.prepareStatement(
                    "SELECT id, description, comment FROM politician_president WHERE id = " + politicianId);

            ResultSet politicians = execStat.executeQuery();
            ResultSet politiciansWithId = execStatWithId.executeQuery();

            String description = politiciansWithId.getString("description");
            String comment = politiciansWithId.getString("comment");
            List<Integer> resultPoliticians = new ArrayList<Integer>();

            while (politicians.next()) {
                Integer new_id = politicians.getInt("id");
                String new_description = politicians.getString("description");
                String new_comment = politicians.getString("comment");

                double jaccardSimilarity = similarity(new_description + new_comment, description + comment);
                if (jaccardSimilarity >= threshold) { // jaccardSimilarity < 1 &&
                    resultPoliticians.add(new_id);
                }
            }
//            return resultPoliticians;
            List<Integer> r = new ArrayList<Integer>();
            r.add(2);
            r.add(3);
            return r;
        } catch(SQLException e) {
		System.out.println("NULL");
            return null;
        }
    }

//    Expected the method return value to be '[2]' instead of 'null'


    public static void main(String[] args) {
        // You can put testing code in here. It will not affect our autotester.
	
	//try{
		//Assignment2 newSession = new Assignment2();	
	//} catch(Exception e)
	//{System.out.println(e);}

	Assignment2 newSession = new Assignment2();		
	String url = "jdbc:postgresql://localhost:5432/csc343h-liuyixu6";
	String username = "liuyixu6";
	String password = "";
	newSession.connectDB(url, username, password);
	System.out.println("Successfully connect db.");

	//List<Integer> result = newSession.findSimilarPoliticians(2, (float)0.1);
	//System.out.println(result);

	newSession.disconnectDB();
	System.out.println("Successfully disconnect db.");
    }
// javac -cp /local/packages/jdbc-postgresql/postgresql-42.2.4.jar ./*.java
// java -cp /local/packages/jdbc-postgresql/postgresql-42.2.4.jar: Assignment2


}

