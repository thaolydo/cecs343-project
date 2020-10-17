
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Person;

/**
 * This class is to communicate with Database.
 */
public class Repository {
    private static volatile Repository repository;
    
    private Connection connection;
    private static final String ENDPOINT = "project-1.cdrsstcipmfu.us-east-1.rds.amazonaws.com";
    private static final String DB_NAME = "project1";
    private static final String DB_URL = String.format("jdbc:mysql://%s/%s", ENDPOINT, DB_NAME);
    private static final String DB_USER = "admin";
    private static final String DB_PASSWD = "Test1234";
    private static final String SCHEMA_NAME = "APP";

    // Prepared statement string
    private static final String GET_PERSON_QUERY = "SELECT * FROM person";


    /** Private default constructor for singleton pattern */
    private Repository() {
        try {
            System.out.printf("Connecting to %s\n", ENDPOINT);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
            connection.setSchema(SCHEMA_NAME);
            System.out.println("Connected!");
        } catch (SQLException e) {
            throw new RuntimeException("Unable to make a connection to the server", e);
        }
    }

    /** Close connection */
    public void closeConnection() {
        try {
            System.out.printf("Closing connection to %s\n", ENDPOINT);
            this.connection.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
            throw new RuntimeException("Unable to close connection", e);
        }
    }

    /**
     * This method is to get the singleton instance for this class.
     * 
     * @return singleton instance for Repository
     */
    public static Repository getInstance() {
        if (repository == null) {
            synchronized (Repository.class) {
                if (repository == null) {
                    repository = new Repository();
                }
            }
        }
        return repository;
    }

    /**
     * This method is to retrieve all records in the WritingGroup table.
     * 
     * @return all records in the WritingGroup table.
     */
    public List<String> getAllPersonNames() {
        List<String> result = new ArrayList<>();
        try (Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(GET_PERSON_QUERY)) {
            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute the query " + GET_PERSON_QUERY, e);
        }
        return result;
    }

}
