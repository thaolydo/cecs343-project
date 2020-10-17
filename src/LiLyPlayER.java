

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
//import javax.swing.text.TableView.TableRow;


import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;


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
    JTextField textField;
    JScrollPane scrollPane; 
    int CurrentSelectedRow;
    
    // moved outside for scope purposes
    
    /*
     *  database interaction needs to be coded into here, currently junk text
     */
    
    String[] columns = {"Song Title", "Description"}; 
    Object[][] data = {{"some database link to song 1","Rock"},
    		{"some database link to song 2", "Rock"},
    		{"some database link to song 3","Rock"},
    		{"some database link to song 4","Rock"},
    		{"some database link to song 5","Rock"},};
    
    JFrame frame = new JFrame();
    Object f;
    File[] files;

    
    public LiLyPlayER() {
    	player = new BasicPlayer();
        
        main = new JPanel();
        
        // buttons removed and edited
        bl1 = new ButtonListener();
        play = new JButton("Play");
        play.addActionListener(bl1);
        
        bl2 = new ButtonListener();
        pause = new JButton("Pause");
        play.addActionListener(bl2);
        
        bl3 = new ButtonListener();
        stop = new JButton("Stop");
        play.addActionListener(bl3);
        
        bl4 = new ButtonListener();
        next = new JButton("Next");
        play.addActionListener(bl4);
        
        bl5 = new ButtonListener();
        prev = new JButton("Previous");
        play.addActionListener(bl5);
        

        

        
        // table added with scroll pane
        table = new JTable(data, columns);
        table.setDropTarget(new MyDropTarget());        
        scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        textField = new JTextField(20);
        textField.setEditable(false);
        
        
        //  menu bar testing
        
        var menuBar = new JMenuBar();
        var exitIcon = new ImageIcon("src/resources/exit.png");

        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        var eMenuItem = new JMenuItem("Exit", exitIcon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener((event) -> System.exit(0));

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
        c.weightx = 3.0;
        c.weighty = 3.0;
        c.gridwidth = 50;
        c.gridx = 0;
        c.gridy = 1;
        main.add(scrollPane, c);
        main.revalidate();
        scrollPane.revalidate();
        
        
 
        // added mouse listener from example
        MouseListener mouseListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
               CurrentSelectedRow = table.getSelectedRow();
               System.out.println("Selected index = " + CurrentSelectedRow);
               textField.setText((String) data[CurrentSelectedRow][0]);
            }
        };
        
        table.addMouseListener(mouseListener);	
        
        
        
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(200);
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(100);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        

        this.setTitle("LiLy PlayER");//change the name to yours
        this.setSize(1000, 575);
        this.add(main);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String url=null;
            if("Play".equals(e.getActionCommand())){
            	System.out.println("Playing :" + data[CurrentSelectedRow][0]);
                url = (String)data[CurrentSelectedRow][0];   
                
                try {
                    player.open(new URL(url));
                    player.play();
                    textField.setText("Playing :" + data[CurrentSelectedRow][0]);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Malformed url");
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if("pause".equals(e.getActionCommand())){
            	if (player.getStatus() == 4 ) {
            		try {
						player.resume();
						textField.setText("Playing :" + data[CurrentSelectedRow][0]);                        //fix
					} catch (BasicPlayerException ex) {
						System.out.println("BasicPlayer exception");
	                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
					}
            	}
            	else {
            		try {
						player.pause();
						textField.setText("Paused :" + data[CurrentSelectedRow][0]);                         //fix
					} catch (BasicPlayerException ex) {
						System.out.println("BasicPlayer exception");
	                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
					}
            	}
            }
            if("stop".equals(e.getActionCommand())){
            	System.out.println("Stopping playback");
            	try {
					player.stop();
				} catch (BasicPlayerException ex) {
					System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
				}  
            }
            if("next".equals(e.getActionCommand())){
            	System.out.println("Playing next song");
                url = (String)data[CurrentSelectedRow + 1][0];  
                try {
                    player.open(new URL(url));
                    player.play();
                    textField.setText("Playing :" + data[CurrentSelectedRow][0]);                               //fix
                } catch (MalformedURLException ex) {
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Malformed url");
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if("prev".equals(e.getActionCommand())){
            	System.out.println("Playing previous song");
                url = (String)data[CurrentSelectedRow - 1][0];
                try {
                    player.open(new URL(url));
                    player.play();
                    textField.setText("Playing :" + data[CurrentSelectedRow][0]);                                 //fix
                } catch (MalformedURLException ex) {
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Malformed url");
                } catch (BasicPlayerException ex) {
                    System.out.println("BasicPlayer exception");
                    Logger.getLogger(LiLyPlayER.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    class MyDropTarget extends DropTarget {
        public  void drop(DropTargetDropEvent evt) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_COPY);
               
                List result = new ArrayList();
                result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                

                for(Object o : result)
                    System.out.println(o.toString());
              
                        }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}


