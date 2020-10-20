
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;


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
    
    public List<List<String>> getSongs() throws SQLException {
    	statement = connection.createStatement();
		List<List<String>> result = new ArrayList<>();

    	
    	if(statement.execute("SELECT * FROM songs")) {
    		ResultSet rs = statement.getResultSet();
    		while(rs.next()) {
    			List<String> thisResult = new ArrayList<>();
    			
    			for (int i = 0; i < 5; i++) {
    			    thisResult.add(rs.getString(i + 1));
    			   }
    			   result.add(thisResult);
    		}    		
    	}
    	return result;
    }

    
    public void addSong(String fn) throws SQLException, UnsupportedTagException, InvalidDataException, IOException {
        String artist = new String();
        String title = new String();
        String album = new String();
        String year = new String();
        //String = genre;
        String songID = fn;
        
        Mp3File mp3file = new Mp3File(fn.toString());
    	if (mp3file.hasId3v1Tag()) {
    		System.out.println(fn.toString());
    		ID3v1 id3v1Tag = mp3file.getId3v1Tag();
    		System.out.println("Artist: " + id3v1Tag.getArtist());
    		artist = id3v1Tag.getArtist();
    		System.out.println("Title: " + id3v1Tag.getTitle());
    		title = id3v1Tag.getTitle();
    		System.out.println("Album: " + id3v1Tag.getAlbum());
    		album = id3v1Tag.getAlbum();
    		System.out.println("Year: " + id3v1Tag.getYear());
    		year = id3v1Tag.getYear();
    		//System.out.println("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
    		//genre = id3v1Tag.getGenre();
    	}
        
        try {
            String sql = "INSERT INTO songs(Artist,Title,Album,Year,SongID)" + "VALUES (?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
 
            statement.setString(1, artist);
            statement.setString(2, title);
            statement.setString(3, album);
            statement.setString(4, year);
            statement.setString(5, songID);
 
            int row = statement.executeUpdate();
            if (row > 0) {
                System.out.println("A Song was added to the library.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void removeSong(String fn) throws SQLException {
        
        try { 
            String sql = "Delete FROM songs WHERE SongID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
 
            statement.setString(1, fn);
 
            int row = statement.executeUpdate();
            if (row > 0) {
                System.out.println("A Song was deleted the library.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
