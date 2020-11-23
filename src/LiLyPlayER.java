
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.Mp3File;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import models.Playlist;
import models.Song;

@SuppressWarnings("serial")
public class LiLyPlayER extends JFrame {
    BasicPlayer player;
    JPanel main;
    JButton play;
    JButton pause;
    JButton stop;
    JButton next;
    JButton prev;
    JLabel nowPlaying;
    ButtonListener bl1;
    ButtonListener bl2;
    ButtonListener bl3;
    ButtonListener bl4;
    ButtonListener bl5;

    JFrame frame;
    JTable table;
    DefaultTableModel tableModel;
    JTextField textField;
    JScrollPane scrollPane;
    int currentSelectedRow;
    
    JTree libraryTree;
    JTree playlistTree;
    DefaultTreeModel playlistTreeModel;
    DefaultMutableTreeNode playlistNode;
    JMenu addSongToPlaylistMenuItem;

    JSlider volume;
    JLabel volLabel;
    
    String[] columns = { "Artist", "Song Title", "Album", "Year", "Genre", "Comment", "File Location" }; // column names
    List<Playlist> playlists;
    JFileChooser jfc;

    private static final Repository repository = Repository.getInstance();

    public LiLyPlayER(String title, boolean isPlaylistWindow) {
        player = new BasicPlayer();
        jfc = new JFileChooser("src/resources");

        // buttons
        bl1 = new ButtonListener();
        play = new JButton("Play");
        play.addActionListener(bl1);

        bl2 = new ButtonListener();
        pause = new JButton("Pause");
        pause.addActionListener(bl2);

        bl3 = new ButtonListener();
        stop = new JButton("Stop");
        stop.addActionListener(bl3);

        bl4 = new ButtonListener();
        next = new JButton("Next");
        next.addActionListener(bl4);

        bl5 = new ButtonListener();
        prev = new JButton("Prev");
        prev.addActionListener(bl5);
        
        addSongToPlaylistMenuItem = new JMenu("Add Song to Playlist");
        
        volume = new JSlider();
        volLabel = new JLabel("   Volume: ");
        volume.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    double vol = (int) source.getValue();
                    try {
                        player.setGain(vol/100.0);
                    } catch (Exception ex) {
                    }
                }
            }
        });

        // initialize and populate table with data from database
        JPanel leftPanel = new JPanel();
        playlists = repository.getAllPlaylists();
        setAddSongToPlaylistMenuItem();
        if (!isPlaylistWindow) {
            initTable(repository.getAllSongs());
            initLibraryTree();
            initPlaylistTree();

            leftPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            leftPanel.add(libraryTree, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            leftPanel.add(playlistTree, gbc);
        } else {
            initTable(repository.getSongsFromPlaylist(title));
        }
        

        table.setDropTarget(new MyDropTarget());
        
        scrollPane = new JScrollPane(table);
        main = new JPanel();
        frame = new JFrame();
        frame.add(scrollPane);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textField = new JTextField(500);
        textField.setEditable(false);

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        ImageIcon exitIcon = new ImageIcon("src/resources/exit.png");

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem eMenuItem = new JMenuItem("Exit", exitIcon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Daddy");
        eMenuItem.addActionListener((event) -> System.exit(0));

        JMenuItem addMenuItem = new JMenuItem("Add Song");
        addMenuItem.setMnemonic(KeyEvent.VK_A);
        addMenuItem.setToolTipText("Add song to library");
        addMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	jfc.showOpenDialog(null);
                File inFile = jfc.getSelectedFile();
                Path currentDirectory = Paths.get(".").toAbsolutePath();
                if (inFile != null) {
                    String relativeFileName = currentDirectory.relativize(inFile.toPath().toAbsolutePath()).toString();
                    addSong(relativeFileName);
                    JOptionPane.showMessageDialog(frame, inFile + " has been ADDED to library");
                }
            }
        });
        
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setMnemonic(KeyEvent.VK_DELETE);
        deleteMenuItem.setToolTipText("Delete song From library");
        deleteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JOptionPane.showMessageDialog(frame, (String) tableModel.getValueAt(currentSelectedRow, 6) + " has been DELETED");
                String fp = (String) tableModel.getValueAt(currentSelectedRow, 6);
                try {
                    repository.removeSong(fp);
                    tableModel.removeRow(currentSelectedRow);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JMenuItem playMenuItem = new JMenuItem("Open/Play Song");
        playMenuItem.setMnemonic(KeyEvent.VK_O);
        playMenuItem.setToolTipText("Play a song(not added to library)");
        playMenuItem.addActionListener((event) -> {
            jfc.showOpenDialog(null);
            File inFile = jfc.getSelectedFile();
            Path currentDirectory = Paths.get(".").toAbsolutePath();
            if (inFile != null) {
                String relativeFileName = currentDirectory.relativize(inFile.toPath().toAbsolutePath()).toString();
                System.out.println("Playing : " + relativeFileName);

                try {
                    player.open(new File(relativeFileName));
                    player.play();
                    textField.setText("Playing : " + relativeFileName);
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JMenuItem addPLMenuItem = new JMenuItem("Add a Playlist");
        addPLMenuItem.setMnemonic(KeyEvent.VK_J);
        addPLMenuItem.setToolTipText("Add a Playlist");
        addPLMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String s = (String)JOptionPane.showInputDialog(main, "Enter name of playlist:\n" , "Add a Playlist", JOptionPane.PLAIN_MESSAGE);
            	try {
            		addPlaylist(s);            		
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            	
            }
        });

        fileMenu.add(playMenuItem);
        fileMenu.add(addMenuItem);
        fileMenu.add(addPLMenuItem);
        fileMenu.add(deleteMenuItem);
        fileMenu.add(eMenuItem);        
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
        setTitle("File Menu");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // right click pop up menu
        final JPopupMenu songPopupMenu = new JPopupMenu();
        JMenuItem addSongMenuItem = new JMenuItem("Add song");   
        addSongMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	jfc.showOpenDialog(null);
                File inFile = jfc.getSelectedFile();
                Path currentDirectory = Paths.get(".").toAbsolutePath();
                if (inFile != null) {
                    String relativeFileName = currentDirectory.relativize(inFile.toPath().toAbsolutePath()).toString();
                    addSong(relativeFileName);
                }
                JOptionPane.showMessageDialog(frame, inFile + " has been ADDED to library");
            }
        });
        
        JMenuItem deleteSongMenuItem = new JMenuItem("Delete");
        deleteSongMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JOptionPane.showMessageDialog(frame, (String) tableModel.getValueAt(currentSelectedRow, 6) + " has been DELETED");
                String fp = (String) tableModel.getValueAt(currentSelectedRow, 6);
                try {
                    repository.removeSong(fp);
                    tableModel.removeRow(currentSelectedRow);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        
        
        songPopupMenu.add(addSongMenuItem);
        songPopupMenu.add(deleteSongMenuItem);
        songPopupMenu.add(addSongToPlaylistMenuItem);
        table.setComponentPopupMenu(songPopupMenu);
        
        volume = new JSlider();
        volLabel = new JLabel("   Volume: ");
        volume.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    double vol = (int) source.getValue();
                    try {
                        player.setGain(vol/100.0);
                    } catch (Exception ex) {
                    }
                }
            }
        });
        
        main.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        main.add(prev, c);
        c.gridx = 2;
        c.gridy = 1;
        main.add(play, c);
        c.gridx = 3;
        c.gridy = 1;
        main.add(pause, c);
        c.gridx = 4;
        c.gridy = 1;
        main.add(stop, c);
        c.gridx = 5;
        c.gridy = 1;
        main.add(next, c);
        c.gridx = 6;
        c.gridy = 1;
        main.add(volLabel, c);
        c.gridx = 7;
        c.gridy = 1;
        main.add(volume, c);
        if (!isPlaylistWindow) {
            c.gridx = 0;
            c.gridy = 0;
            main.add(leftPanel, c);
        }
        c.ipady = 40;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 50;
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        main.add(scrollPane, c);
        
        // Add mouse listener
        MouseListener mouseListener = new MouseAdapter() {
        	@Override
            public void mousePressed(MouseEvent e) {
                currentSelectedRow = table.getSelectedRow();
                System.out.println("Selected index = " + currentSelectedRow);
                textField.setText((String) tableModel.getValueAt(currentSelectedRow, 6));
            }
        };

        table.addMouseListener(mouseListener);

        // formatting for table
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(200);
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(100);

        // formatting for the general panel
        this.setTitle(title);
        this.setSize(1000, 575);
        this.add(main);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pack();
        this.setVisible(true);
    }
    
    /*
     *  Drag and Drop functionality
     */
    class MyDropTarget extends DropTarget {
        @SuppressWarnings("rawtypes")
        public void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);

                List result;
                result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                for (Object o : result) {
                    addSong(o.toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
     * Function to initialize the table
     */
    void initTable(List<Song> songs) {
        tableModel = new DefaultTableModel();
        setTable(songs);
        table = new JTable(tableModel);
        table.setDragEnabled(true);
    }

    void setTable(List<Song> songs) {
        Object[][] data = new Object[songs.size()][];
        int i = 0;
        for (Song song : songs) {
            data[i++] = songToTableRow(song);
        }
        tableModel.setDataVector(data, columns);
    }

    void initLibraryTree() {
        DefaultMutableTreeNode libNode = new DefaultMutableTreeNode("Library");
        libraryTree = new JTree(libNode);
        libraryTree.setRootVisible(true);
        libraryTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                playlistTree.clearSelection();
                setTable(repository.getAllSongs());
            }
        });
    }

    void setAddSongToPlaylistMenuItem() {
        for (Playlist playlist : playlists) {
            JMenuItem menuItem = new JMenuItem(playlist.playlistName());
            menuItem.addActionListener((event) -> {
                String songLocation = (String) tableModel.getValueAt(currentSelectedRow, 6);
                String songTitle = (String) tableModel.getValueAt(currentSelectedRow, 1);
                System.out.printf("Adding song %s to the playlist %s\n", songTitle, playlist.playlistName());
                repository.addSongToPlaylist(songLocation, playlist.playlistName());
            });
            addSongToPlaylistMenuItem.add(menuItem);
        }
    }
    
    void initPlaylistTree() {
        playlistNode = new DefaultMutableTreeNode("PlayList");
        for (Playlist p : playlists) {
        	playlistNode.add(new DefaultMutableTreeNode(p.playlistName()));
        }
        playlistTree = new JTree(playlistNode);
        playlistTreeModel = (DefaultTreeModel) playlistTree.getModel();
        playlistTree.setRootVisible(true);

        // Add selection listener
        playlistTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                libraryTree.clearSelection();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) playlistTree.getLastSelectedPathComponent();
        
                /* if nothing is selected */ 
                if (node == null || "PlayList".equals(node.getUserObject().toString())) {
                    return;
                }
        
                /* retrieve the node that was selected */ 
                String playlistName = node.getUserObject().toString();

                /* React to the node selection. */
                setTable(repository.getSongsFromPlaylist(playlistName));
            }
        });

        // Add pop up menu
        final JPopupMenu PLpopupMenu = new JPopupMenu();
        JMenuItem newWindowItemPUM = new JMenuItem("Open In New Window");   
        newWindowItemPUM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) playlistTree.getLastSelectedPathComponent();
                String playlistName = node.getUserObject().toString();
                new LiLyPlayER(playlistName, true);
            }
        });
        
        JMenuItem removePLItemPUM = new JMenuItem("Delete Playlist");   
        removePLItemPUM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					removePlaylist();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
        });
        
        PLpopupMenu.add(newWindowItemPUM);
        PLpopupMenu.add(removePLItemPUM);
        playlistTree.setComponentPopupMenu(PLpopupMenu);
    }
    
    /*
     * Action listener to handle the event of each respective button pressed
     */
    class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String url = null;
            
            if ("Play".equals(e.getActionCommand())) {
                System.out.println("Playing : " + tableModel.getValueAt(currentSelectedRow, 6));
                url = (String) tableModel.getValueAt(currentSelectedRow, 6);

                try {
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing : " + tableModel.getValueAt(currentSelectedRow, 6));
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if ("Pause".equals(e.getActionCommand())) {
                if (player.getStatus() == 1) {
                    try {
                        player.resume();
                        textField.setText("Playing : " + tableModel.getValueAt(currentSelectedRow, 6));
                    } catch (BasicPlayerException ex) {
                        System.out.println("BasicPlayer exception");
                        Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        player.pause();
                        textField.setText("Paused : " + tableModel.getValueAt(currentSelectedRow, 6));
                    } catch (BasicPlayerException ex) {
                        System.out.println("BasicPlayer exception");
                        Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            if ("Stop".equals(e.getActionCommand())) {
                System.out.println("Stopping playback");
                try {
                    player.stop();
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if ("Next".equals(e.getActionCommand())) {
                System.out.println("Playing next song");
                if (currentSelectedRow == tableModel.getRowCount() - 1) {
                    currentSelectedRow = 0;
                } else {
                    currentSelectedRow++;
                }
                url = (String) tableModel.getValueAt(currentSelectedRow, 6);
                table.setRowSelectionInterval(currentSelectedRow, currentSelectedRow);
                try {
                    player.stop();
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing :" + tableModel.getValueAt(currentSelectedRow, 6));
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if ("Prev".equals(e.getActionCommand())) {
                System.out.println("Playing previous song");
                if (currentSelectedRow == 0) {
                    currentSelectedRow = tableModel.getRowCount() - 1;
                } else {
                    currentSelectedRow--;
                }
                url = (String) tableModel.getValueAt(currentSelectedRow, 6);
                table.setRowSelectionInterval(currentSelectedRow, currentSelectedRow);
                try {
                    player.stop();
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing :" + tableModel.getValueAt(currentSelectedRow, 6));
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /*
     * A function to extracts the metadata from the .mp3 and 
     * stores the information into an object. This object is 
     * then passed to database handling to be have the data
     * sorted databases' table
     */
    void addSong(String fileName) {
        System.out.printf("Adding song: %s/n", fileName);

        try {
            Mp3File mp3file = new Mp3File(fileName.toString());
            if (!mp3file.hasId3v1Tag()) {
                System.out.println("Unable to add the song due to no tags available for this song");
                return;
            }
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            Song song = Song.builder()
                .title(id3v1Tag.getTitle())
                .artist(id3v1Tag.getArtist())
                .album(id3v1Tag.getAlbum())
                .fileLocation(fileName)
                .genre(id3v1Tag.getGenre())
                .genreDesc(id3v1Tag.getGenreDescription())
                .year(Integer.parseInt(id3v1Tag.getYear()))
                .build();
            
            repository.addSong(song);
            tableModel.addRow(songToTableRow(song));
            System.out.printf("Successfully added the song %s\n", fileName);
        } catch (Exception e) {
            throw new RuntimeException("Unable to add the song " + fileName, e);
        }
    }
    
    void addPlaylist(String plName) throws SQLException {
        System.out.printf("Adding Playlist: %s\n", plName);
        repository.addPlaylist(plName);
        System.out.printf("\nSuccessfully added the playlist %s\n", plName);
        playlistTreeModel.insertNodeInto(
            new DefaultMutableTreeNode(plName),
            playlistNode,
            playlistNode.getChildCount());
        playlists = repository.getAllPlaylists();
        setAddSongToPlaylistMenuItem();
    }
    
    void removePlaylist() throws SQLException {
    	DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) playlistTree.getLastSelectedPathComponent();
        String playlistName = selectedNode.getUserObject().toString();
        System.out.printf("Deleting Playlist: %s/n", playlistName);
        repository.removePlaylist(playlistName);
        System.out.printf("\nSuccessfully deleted the playlist %s\n", playlistName);
        playlistTreeModel.removeNodeFromParent(selectedNode);

        // TODO: remove the playlist from addSongToPlaylistMenuItem
    }
    
    /*
     * stores the information of the song into an object array
     */
    private Object[] songToTableRow(Song song) {
        return new Object[] {
            song.artist(),
            song.title(),
            song.album(),
            song.year(),
            song.genreDesc(),
            song.comment(),
            song.fileLocation()
        };
    }
    
    private Object[] playlistToNode(Playlist playlist) {
        return new Object[] {
            playlist.playlistName()
        };
    }
}