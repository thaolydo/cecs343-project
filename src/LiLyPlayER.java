

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView.TableRow;

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
    JScrollPane scrollPane; 
    int CurrentSelectedRow;
    
    // moved outside for scope purposes
    
    String[] columns = {"Station URL", "Description"};    
    Object[][] data = {{"https://mp3.ffh.de/ffhchannels/hqrock.mp3","Rock"},
            {"https://mp3.ffh.de/ffhchannels/hq80er.mp3", "The 80's"},
            {"https://mp3.ffh.de/ffhchannels/hqdeutsch.mp3", "Pur Deutsch"},
            {"https://mp3.ffh.de/ffhchannels/hq90er.mp3", "The 90's"}};
    
    public LiLyPlayER() {
    	player = new BasicPlayer();
        
        main = new JPanel();
//        main.setLayout(new FlowLayout());
        
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
        
        String[] columns = {"Song Title", "Description"}; 
        Object[][] data = {{"some database link","Rock"}};
        
        // table added with scroll pane
        table = new JTable(data, columns);
        
        scrollPane = new JScrollPane(table);
        
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
        c.ipady = 40;      //make this component tall
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
            }
        };
        
        table.addMouseListener(mouseListener);
        
        
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(400);
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(200);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        

        this.setTitle("LiLy PlayER");//change the name to yours
        this.setSize(1000, 500);
        this.add(main);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String url=null;
            if("Play".equals(e.getActionCommand())){
            	System.out.println(data[CurrentSelectedRow][0]);
                url = (String)data[CurrentSelectedRow][0];   
            }
            
            try {
                player.open(new URL(url));
                player.play();
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
