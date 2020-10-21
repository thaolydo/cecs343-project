
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
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
<<<<<<< HEAD
import java.io.IOException;
=======
import java.nio.file.Path;
import java.nio.file.Paths;
>>>>>>> refs/heads/master_NB
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
//import javax.swing.text.TableView.TableRow;
//import javax.swing.table.TableModel;

<<<<<<< HEAD
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
=======
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.Mp3File;
>>>>>>> refs/heads/master_NB

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import models.Song;

<<<<<<< HEAD

=======
>>>>>>> refs/heads/master_NB
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
<<<<<<< HEAD
    
    // new table variables 
    
    JTable table;
    DefaultTableModel tableModel;
    JTextField textField;
    JScrollPane scrollPane; 
    int CurrentSelectedRow;
    
    // moved outside for scope purposes
    
    /*
     *  data needs to hold the the access urls to the database, probably should be an ArrayList
     */
    
    String[] columns = {"Artist", "Song Title", "Album", "Year", "File Location"};					//can be changed to dynamically based on database columns
    Object[][] data;
    List<List<String>> tempAL = new ArrayList<List<String>>();
    
    JFrame frame;
    //Object f;
    //File[] files;
=======
>>>>>>> refs/heads/master_NB

<<<<<<< HEAD
    
    public LiLyPlayER() throws SQLException {
    	player = new BasicPlayer();
        
        
        // buttons 
=======
    // new table variables

    JTable table;
    DefaultTableModel tableModel;
    JTextField textField;
    JScrollPane scrollPane;
    int currentSelectedRow;

    // moved outside for scope purposes

    /*
     * data needs to hold the the access urls to the database, probably should be an
     * ArrayList
     */

    String[] columns = { "Artist", "Song Title", "Album", "Year", "File Location" }; // can be changed to dynamically
                                                                                     // based on database columns
    List<Song> tempAL = new ArrayList<>();

    JFrame frame;
    // Object f;
    // File[] files;

    private static final Repository repository = Repository.getInstance();

    public LiLyPlayER() throws SQLException {
        player = new BasicPlayer();

        // buttons
>>>>>>> refs/heads/master_NB
        bl1 = new ButtonListener();
        play = new JButton("Play");
        play.addActionListener(bl1);

        bl2 = new ButtonListener();
        pause = new JButton("Pause");
        pause.addActionListener(bl2);
<<<<<<< HEAD
        
=======

>>>>>>> refs/heads/master_NB
        bl3 = new ButtonListener();
        stop = new JButton("Stop");
        stop.addActionListener(bl3);
<<<<<<< HEAD
        
=======

>>>>>>> refs/heads/master_NB
        bl4 = new ButtonListener();
        next = new JButton("Next");
        next.addActionListener(bl4);
<<<<<<< HEAD
        
        bl5 = new ButtonListener();
        prev = new JButton("Prev");
        prev.addActionListener(bl5);
        
        // initialize and populate table with data from database
        
        table = new JTable();
        refreshTable();
        
=======

        bl5 = new ButtonListener();
        prev = new JButton("Prev");
        prev.addActionListener(bl5);

        // initialize and populate table with data from database

        table = new JTable();
        initTable();

>>>>>>> refs/heads/master_NB
        // table added with scroll pane
<<<<<<< HEAD
        
//        table = new JTable(data, columns);
        
        table.setDropTarget(new MyDropTarget());        
=======

        // table = new JTable(data, columns);

        table.setDropTarget(new MyDropTarget());
>>>>>>> refs/heads/master_NB
        scrollPane = new JScrollPane(table);
        main = new JPanel();
<<<<<<< HEAD
        frame  = new JFrame();
=======
        frame = new JFrame();
>>>>>>> refs/heads/master_NB
        frame.add(scrollPane);
        frame.setSize(400, 400);
<<<<<<< HEAD
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
=======
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
>>>>>>> refs/heads/master_NB
        textField = new JTextField(500);
        textField.setEditable(false);
<<<<<<< HEAD
        
        
        // menu bar 
        
=======

        // menu bar

>>>>>>> refs/heads/master_NB
        var menuBar = new JMenuBar();
        var exitIcon = new ImageIcon("src/resources/exit.png");

        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        var eMenuItem = new JMenuItem("Exit", exitIcon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener((event) -> System.exit(0));
<<<<<<< HEAD
        
        var addMenuItem = new JMenuItem("Add Song", exitIcon);
        addMenuItem.setMnemonic(KeyEvent.VK_A);
        addMenuItem.setToolTipText("Add song to library");
        addMenuItem.addActionListener((event) -> {
        	JFileChooser jfc = new JFileChooser();
        	jfc.showOpenDialog(null);
        	File inFile = jfc.getSelectedFile();
        	Repository repository = Repository.getInstance();
        	try {
				repository.addSong(inFile.toString());
			} catch (UnsupportedTagException | InvalidDataException | SQLException | IOException ex) {
				ex.printStackTrace();
			}
        });
        
        fileMenu.add(addMenuItem);
        fileMenu.add(eMenuItem);        
=======

        var addMenuItem = new JMenuItem("Add Song", exitIcon);
        addMenuItem.setMnemonic(KeyEvent.VK_A);
        addMenuItem.setToolTipText("Add song to library");
        addMenuItem.addActionListener((event) -> {
            JFileChooser jfc = new JFileChooser("src/resources");
            jfc.showOpenDialog(null);
            File inFile = jfc.getSelectedFile();
            Path currentDirectory = Paths.get(".").toAbsolutePath();
            if (inFile != null) {
                String relativeFileName = currentDirectory.relativize(inFile.toPath().toAbsolutePath()).toString();
                addSong(relativeFileName);
            }
        });

        fileMenu.add(addMenuItem);
        fileMenu.add(eMenuItem);
>>>>>>> refs/heads/master_NB
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        setTitle("Simple menu");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // layout stuff

        main.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        main.add(prev, c);
        c.gridx = 1;
        c.gridy = 0;
        main.add(play, c);
        c.gridx = 2;
        c.gridy = 0;
        main.add(pause, c);
        c.gridx = 3;
        c.gridy = 0;
        main.add(stop, c);
        c.gridx = 4;
        c.gridy = 0;
        main.add(next, c);
        c.gridx = 5;
        c.gridy = 0;
        c.weightx = 3.0;
        main.add(textField, c);
<<<<<<< HEAD
        
        c.ipady = 40;     
=======

        c.ipady = 40;
>>>>>>> refs/heads/master_NB
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 50;
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.BOTH;
        main.add(scrollPane, c);
        main.revalidate();
        scrollPane.revalidate();

        // added mouse listener

        MouseListener mouseListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
<<<<<<< HEAD
               CurrentSelectedRow = table.getSelectedRow();
               System.out.println("Selected index = " + CurrentSelectedRow);
               textField.setText((String) data[CurrentSelectedRow][4]);
=======
                currentSelectedRow = table.getSelectedRow();
                System.out.println("Selected index = " + currentSelectedRow);
                textField.setText((String) tableModel.getValueAt(currentSelectedRow, 4));
>>>>>>> refs/heads/master_NB
            }
        };

        table.addMouseListener(mouseListener);

        // right click menu testing

        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
<<<<<<< HEAD
                JOptionPane.showMessageDialog(frame, (String)data[CurrentSelectedRow][4] + " has been DELETED");
                String fp = (String)data[CurrentSelectedRow][4];
                Repository repository = Repository.getInstance();
=======
                JOptionPane.showMessageDialog(frame, (String) tableModel.getValueAt(currentSelectedRow, 4) + " has been DELETED");
                String fp = (String) tableModel.getValueAt(currentSelectedRow, 4);
>>>>>>> refs/heads/master_NB
                try {
<<<<<<< HEAD
					repository.removeSong(fp);														
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
=======
                    repository.removeSong(fp);
                    tableModel.removeRow(currentSelectedRow);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
>>>>>>> refs/heads/master_NB
            }
        });
<<<<<<< HEAD
        
//        refreshTable.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(frame, "Right-click performed on table and choose refresh");
//                Repository repository = Repository.getInstance();
//                try {
//					tempAL = repository.getSongs();
//				} catch (SQLException ex) {
//					ex.printStackTrace();
//				}
//                Object[][] tempTable = new Object[tempAL.size()][];
//                int i = 0;
//                for (List<String> o : tempAL) {
//                	tempTable[i++] = o.toArray(new String[o.size()]);
//                }
//            }
//        });
//        popupMenu.add(refreshTable);
        
=======

        // refreshTable.addActionListener(new ActionListener() {
        //
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // JOptionPane.showMessageDialog(frame, "Right-click performed on table and
        // choose refresh");
        // try {
        // tempAL = repository.getAllSongs();
        // } catch (SQLException ex) {
        // ex.printStackTrace();
        // }
        // Object[][] tempTable = new Object[tempAL.size()][];
        // int i = 0;
        // for (List<String> o : tempAL) {
        // tempTable[i++] = o.toArray(new String[o.size()]);
        // }
        // }
        // });
        // popupMenu.add(refreshTable);

>>>>>>> refs/heads/master_NB
        popupMenu.add(deleteItem);

<<<<<<< HEAD
        
=======
>>>>>>> refs/heads/master_NB
        table.setComponentPopupMenu(popupMenu);
<<<<<<< HEAD
                
=======

>>>>>>> refs/heads/master_NB
        // formatting for table

        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(200);
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(100);
<<<<<<< HEAD
        
        
=======

>>>>>>> refs/heads/master_NB
        // formatting for the general panel

        this.setTitle("LiLy PlayER");
        this.setSize(1000, 575);
        this.add(main);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
<<<<<<< HEAD
        
        
    }
    
    class MyDropTarget extends DropTarget {
    	@SuppressWarnings("rawtypes")
		public  void drop(DropTargetDropEvent evt) {
    		try {
    			evt.acceptDrop(DnDConstants.ACTION_COPY);
               
                List result = new ArrayList();
                result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                for(Object o : result) {
                	Repository repository = Repository.getInstance();
                	repository.addSong(o.toString());
                	}
                refreshTable();
                }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    void refreshTable() throws SQLException {
        Repository repository = Repository.getInstance();
        tempAL = repository.getSongs();
        data = new Object[tempAL.size()][];
        int i = 0;
        for (List<String> o : tempAL) {
        	data[i++] = o.toArray(new Object[o.size()]);
        }
        table.invalidate();
        table = new JTable(data, columns);
    }
    
    
    class ButtonListener implements ActionListener {
=======
>>>>>>> refs/heads/master_NB

<<<<<<< HEAD
        @Override
        public void actionPerformed(ActionEvent e) {
            String url=null;
            if("Play".equals(e.getActionCommand())){
            	System.out.println("Playing :" + data[CurrentSelectedRow][4]);
                url = (String)data[CurrentSelectedRow][4];   
                
                try {
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing :" + data[CurrentSelectedRow][4]);
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if("Pause".equals(e.getActionCommand())){
            	if (player.getStatus() == 1) {
            		try {
						player.resume();
						textField.setText("Playing :" + data[CurrentSelectedRow][4]);
					} catch (BasicPlayerException ex) {
						System.out.println("BasicPlayer exception");
	                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
					}
            	}
            	else {
            		try {
						player.pause();
						textField.setText("Paused :" + data[CurrentSelectedRow][4]);
					} catch (BasicPlayerException ex) {
						System.out.println("BasicPlayer exception");
	                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
					}
            	}
            }
            if("Stop".equals(e.getActionCommand())){
            	System.out.println("Stopping playback");
            	try {
					player.stop();
				} catch (BasicPlayerException ex) {
					System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
				}  
            }
            if("Next".equals(e.getActionCommand())){
            	System.out.println("Playing next song");
            	if (CurrentSelectedRow == data.length - 1) {
            		CurrentSelectedRow = 0;
            	} else {
            		CurrentSelectedRow++;
            	}
                url = (String)data[CurrentSelectedRow][4];  
                try {
                	player.stop();
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing :" + data[CurrentSelectedRow][4]);
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if("Prev".equals(e.getActionCommand())){
            	System.out.println("Playing previous song");
            	if (CurrentSelectedRow == 0 ) {
            		CurrentSelectedRow = data.length -1;
            	} else {
            		CurrentSelectedRow--;
            	}
                url = (String)data[CurrentSelectedRow][4];
                try {
                	player.stop();
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing :" + data[CurrentSelectedRow][4]);
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
=======
>>>>>>> refs/heads/master_NB
    }
<<<<<<< HEAD
=======

    class MyDropTarget extends DropTarget {
        @SuppressWarnings("rawtypes")
        public void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);

                List result = new ArrayList();
                result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                for (Object o : result) {
                    addSong(o.toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

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

    class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String url = null;
            if ("Play".equals(e.getActionCommand())) {
                System.out.println("Playing :" + tableModel.getValueAt(currentSelectedRow, 4));
                url = (String) tableModel.getValueAt(currentSelectedRow, 4);

                try {
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing :" + tableModel.getValueAt(currentSelectedRow, 4));
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if ("Pause".equals(e.getActionCommand())) {
                if (player.getStatus() == 1) {
                    try {
                        player.resume();
                        textField.setText("Playing :" + tableModel.getValueAt(currentSelectedRow, 4));
                    } catch (BasicPlayerException ex) {
                        System.out.println("BasicPlayer exception");
                        Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        player.pause();
                        textField.setText("Paused :" + tableModel.getValueAt(currentSelectedRow, 4));
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
                url = (String) tableModel.getValueAt(currentSelectedRow, 4);
                try {
                    player.stop();
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing :" + tableModel.getValueAt(currentSelectedRow, 4));
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
                url = (String) tableModel.getValueAt(currentSelectedRow, 4);
                try {
                    player.stop();
                    player.open(new File(url));
                    player.play();
                    textField.setText("Playing :" + tableModel.getValueAt(currentSelectedRow, 4));
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    void addSong(String fileName) {
        System.out.printf("Adding song: %s\n", fileName);

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
                .year(Integer.parseInt(id3v1Tag.getYear()))
                .build();
            
            repository.addSong(song);
            tableModel.addRow(songToTableRow(song));
            System.out.printf("Successfully added the song %s\n", fileName);
        } catch (Exception e) {
            throw new RuntimeException("Unable to add the song " + fileName, e);
        }
    }

    private Object[] songToTableRow(Song song) {
        return new Object[] {
            song.artist(),
            song.title(),
            song.album(),
            song.year(),
            song.fileLocation()
        };
    }
>>>>>>> refs/heads/master_NB
}
