

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
import java.io.IOException;
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

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;


@SuppressWarnings("serial")
public class LiLyPlayER extends JFrame {
    BasicPlayer player;
    JPanel main;
    JButton play;
    JButton pause;
    JButton stop;
    JButton next;
    JButton prev;
    JLabel  nowPlaying;
    ButtonListener bl1;
    ButtonListener bl2;
    ButtonListener bl3;
    ButtonListener bl4;
    ButtonListener bl5;
    
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

    
    public LiLyPlayER() throws SQLException {
    	player = new BasicPlayer();
        
        
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
        refreshTable();
        
        // table added with scroll pane
        
//        table = new JTable(data, columns);
        
        table.setDropTarget(new MyDropTarget());        
        scrollPane = new JScrollPane(table);
        main = new JPanel();
        frame  = new JFrame();
        frame.add(scrollPane);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        textField = new JTextField(500);
        textField.setEditable(false);
        
        
        // menu bar 
        
        var menuBar = new JMenuBar();
        var exitIcon = new ImageIcon("src/resources/exit.png");

        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        var eMenuItem = new JMenuItem("Exit", exitIcon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener((event) -> System.exit(0));
        
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
        
        c.ipady = 40;     
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
               CurrentSelectedRow = table.getSelectedRow();
               System.out.println("Selected index = " + CurrentSelectedRow);
               textField.setText((String) data[CurrentSelectedRow][4]);
            }
        };
        
        table.addMouseListener(mouseListener);	
        
        // right click menu testing
        
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, (String)data[CurrentSelectedRow][4] + " has been DELETED");
                String fp = (String)data[CurrentSelectedRow][4];
                Repository repository = Repository.getInstance();
                try {
					repository.removeSong(fp);														
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
            }
        });
        
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
        
        popupMenu.add(deleteItem);

        
        table.setComponentPopupMenu(popupMenu);
                
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
    }
}


