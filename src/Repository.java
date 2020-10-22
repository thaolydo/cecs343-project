import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import models.Song;


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

    // Statement string
    private static final String GET_SONGS_QUERY = "SELECT * FROM songs";
    private static final String INSERT_SONG_STATEMENT =
        "INSERT INTO songs(Artist, Title, Album, Location, Year) VALUES (?,?,?,?,?)";
    private static final String DELETE_SONG_STATEMENT = "Delete FROM songs WHERE Location = ?";


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
    
    public List<Song> getAllSongs() {
		List<Song> result = new ArrayList<>();
        try (Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(GET_SONGS_QUERY)) {
            while (rs.next()) {
                Song song = Song.builder()
                    .title(rs.getString("Title"))
                    .artist(rs.getString("Artist"))
                    .album(rs.getString("Album"))
                    .fileLocation(rs.getString("Location"))
                    .year(rs.getInt("Year"))
                    .build();

                result.add(song);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute the query " + GET_SONGS_QUERY, e);
        }
    }

    public void addSong(Song song) throws SQLException, UnsupportedTagException, InvalidDataException, IOException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SONG_STATEMENT)) {
            statement.setString(1, song.artist());
            statement.setString(2, song.title());
            statement.setString(3, song.album());
            statement.setString(4, song.fileLocation());
            statement.setInt(5, song.year());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute the query " + INSERT_SONG_STATEMENT, e);
        }
    }
    
    public void removeSong(String fileLocation) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SONG_STATEMENT)) {
            statement.setString(1, fileLocation);
            statement.executeUpdate();
        }  catch (SQLException e) {
            throw new RuntimeException("Unable to execute the query " + DELETE_SONG_STATEMENT);
        }

    }

}