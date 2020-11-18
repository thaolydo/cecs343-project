
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.Mp3File;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
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
    
    String[] columns = { "Artist", "Song Title", "Album", "Year", "Genre", "Comment", "File Location" }; // column names
    List<Song> tempAL; // Temporarily stores database information
    JFileChooser jfc;

    private static final Repository repository = Repository.getInstance();

    public LiLyPlayER() throws SQLException {
        player = new BasicPlayer();
        jfc = new JFileChooser("src/resources");
        tempAL = new ArrayList<>();

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

        // initialize and populate table with data from database
        
        table = new JTable();
        initTable();
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
                }
                JOptionPane.showMessageDialog(frame, inFile + " has been ADDED to library");
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

        fileMenu.add(playMenuItem);
        fileMenu.add(addMenuItem);
        fileMenu.add(deleteMenuItem);
        fileMenu.add(eMenuItem);        
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
        setTitle("File Menu");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // right click pop up menu testing

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addItemPUM = new JMenuItem("Add song");   
        addItemPUM.addActionListener(new ActionListener() {
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
        
        JMenuItem deleteItemPUM = new JMenuItem("Delete");
        deleteItemPUM.addActionListener(new ActionListener() {
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
        
        popupMenu.add(addItemPUM);
        popupMenu.add(deleteItemPUM);
        table.setComponentPopupMenu(popupMenu);
        
        /*
         *  add keyboard delete?
         */
        
        //tree stuff

        JTree tree;
        

        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        DefaultMutableTreeNode lib = new DefaultMutableTreeNode("Library");
        DefaultMutableTreeNode pl = new DefaultMutableTreeNode("PlayList");
        DefaultMutableTreeNode testPLChild = new DefaultMutableTreeNode("test child");
        root.add(lib);
        root.add(pl);
        tree = new JTree(root);
        tree.setRootVisible(false);

        pl.add(testPLChild);
        
        
        // layout stuff
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JTree Example");       
        this.pack();
        this.setVisible(true);

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
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        main.add(tree, c);
        c.ipady = 40;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 50;
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        main.add(scrollPane, c);
//        main.revalidate();
//        scrollPane.revalidate();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JTree Example");       
        this.pack();
        this.setVisible(true);

        // added mouse listener

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

        this.setTitle("LiLy PlayER");
        this.setSize(1000, 575);
        this.add(main);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    void initTable() {
        tempAL = repository.getAllSongs();
        Object[][] data = new Object[tempAL.size()][];
        int i = 0;
        for (Song song : tempAL) {
            data[i++] = songToTableRow(song);
        }
        tableModel = new DefaultTableModel(data, columns);
        table = new JTable(tableModel);
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
}