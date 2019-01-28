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

    public static double similarity(String a, String b) {
        a = a.replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();
        b = b.replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();
        final java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\s+");
        Set<?> left = p.splitAsStream(a).collect(java.util.stream.Collectors.toSet());
        Set<?> right = p.splitAsStream(b).collect(java.util.stream.Collectors.toSet());
        final int sa = left.size();
        final int sb = right.size();
        if ((sa - 1 | sb - 1) < 0)
            return 0.0;
        if ((sa + 1 & sb + 1) < 0)
            return 0.0;
        final Set<?> smaller = sa <= sb ? left : right;
        final Set<?> larger  = sa <= sb ? right : left;
        int intersection = 0;
        for (final Object element : smaller) try {
            if (larger.contains(element))
                intersection++;
        } catch (final ClassCastException | NullPointerException e) {}
        final long sum = (sa + 1 > 0 ? sa : left.stream().count())
                + (sb + 1 > 0 ? sb : right.stream().count());
        return 1d / (sum - intersection) * intersection;
    }
    
    public static void main(String args[]) throws IOException
        {
            String url;
            Connection connection;


            try {
                Class.forName("org.postgresql.Driver");
            }
            catch (ClassNotFoundException e) {
                System.out.println("Failed to find the JDBC driver");
            }


	    try {
            int politicianId = 9;
            url = "jdbc:postgresql://localhost:5432/csc343h-liuyixu6";
            connection = DriverManager.getConnection(url, "liuyixu6", "");

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
                if (jaccardSimilarity >= 0.2) {
                    resultPoliticians.add(new_id);
                }
            }

            System.out.println(resultPoliticians);

//            PreparedStatement execStat = connection.prepareStatement(
//                    "SELECT id, description, comment FROM parlgov.politician_president");
//
//            PreparedStatement execStatWithId = connection.prepareStatement(
//                    "SELECT id, description, comment FROM parlgov.politician_president WHERE id = " + 2);
//
//            ResultSet politicians = execStat.executeQuery();
//            ResultSet politiciansWithId = execStatWithId.executeQuery();
//
//            String description = new String("");
//            String comment = new String("");
//
//            while(politiciansWithId.next()){
//                description = politiciansWithId.getString("description");
//                comment = politiciansWithId.getString("comment");
//            }
//
//            List<Integer> resultPoliticians = new ArrayList<Integer>();
//
//            while (politicians.next()) {
//                Integer new_id = politicians.getInt("id");
//                String new_description = politicians.getString("description");
//                String new_comment = politicians.getString("comment");
//
//                double jaccardSimilarity = similarity(new_description + new_comment, description + comment);
//                if (jaccardSimilarity < 1 && jaccardSimilarity >= 0.2) {
//                    resultPoliticians.add(new_id);
//                }
//            }
//
//            System.out.println(resultPoliticians);

        } catch(SQLException e) {

		    System.out.println(e);
            
        }


        }
        
}
