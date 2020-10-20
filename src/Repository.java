
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is to communicate with Database.
 */
public class Repository {
    private static volatile Repository repository;
    
    private Connection connection;
    Statement statement;
    
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
    
    public void getSongs() throws SQLException {
    	statement = connection.createStatement();
    	
    	if(statement.execute("SELECT * FROM song")) {
    		ResultSet rs = statement.getResultSet();
    		while(rs.next()) {
    			// put songs from library into the jTable somehow
    		}
    	}
    }

    
    public void addSong(String fn) throws SQLException {
        String filePath = fn;
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
 
            String sql = "INSERT INTO project1.songs (File) values (LOAD_FILE(?))";
            PreparedStatement statement = conn.prepareStatement(sql);
 
            statement.setString(1, filePath);
 
            int row = statement.executeUpdate();
            if (row > 0) {
                System.out.println("A Song was added to the library.");
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void removeSong(String fn) throws SQLException {
    	String filePath = fn;
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
 
            String sql = "DELETE FROM project1.songs (File) WHERE File=?";
            PreparedStatement statement = conn.prepareStatement(sql);
 
            statement.setString(1, filePath);
 
            int row = statement.executeUpdate();
            if (row > 0) {
                System.out.println("A Song was deleted the library.");
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
