/**
 * Control class for the cloud storage program
 * Creates a GUI with the number of Nodes given in Settings.java
 * Author: Carson
 */
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Overseer extends JPanel
                      implements ListSelectionListener {
    // Keep track of events and their relevent data
    //      success/failure, total time to complete
    ArrayList<Events> eventsLog;
    long eventTime;
    boolean eventSuccess;
    // List of all nodes
    DefaultListModel<Node> nodeList = new DefaultListModel<Node>();
    DefaultListModel nodeNames = new DefaultListModel();
    JList list;
    // Buttons
    private JButton storeData;
    private JButton retriveData;
    private JButton togglePower;
    private JButton displayNodeInfo;
    private JButton randomizeEvents;
    private JButton printData;
    
    public Overseer(){
       super(new BorderLayout());
        
        int numOfNodes = Settings.NUMBER_NODES;
        Node[] data = new Node[numOfNodes];
        for(int i =0; i < numOfNodes; i++){
            data[i] = new Node();
            data[i].name = "Node " + (i + 1);
            nodeList.addElement(data[i]);
            nodeNames.addElement("Node " + (i + 1));
        }
                
        //Create the list and put it in a scroll pane.
        list = new JList(nodeNames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
        
        //Sets buttons
        JButton storeData = new JButton("Store Data");
        StoreListener storeListener = new StoreListener(storeData);
        storeData.setActionCommand("Store Data");
        storeData.addActionListener(storeListener);
        
        JButton retriveData = new JButton("Retrive Data");
        RetriveListener retriveListener = new RetriveListener(retriveData);
        retriveData.setActionCommand("Retrive Data");
        retriveData.addActionListener(retriveListener);
        
        JButton togglePower = new JButton("Toggle Power");
        PowerListener powerListener = new PowerListener(togglePower);
        togglePower.setActionCommand("Toggle Power");
        togglePower.addActionListener(powerListener);
        
        JButton displayNodeInfo = new JButton("Show Node Info");
        InfoListener infoListener = new InfoListener(displayNodeInfo);
        displayNodeInfo.setActionCommand("Show Node Info");
        displayNodeInfo.addActionListener(infoListener);
        
        JButton randomizeEvents = new JButton("Randomize");
        RandomListener randomListener = new RandomListener(randomizeEvents);
        randomizeEvents.setActionCommand("Randomize");
        randomizeEvents.addActionListener(randomListener);
        
        JButton printData = new JButton("Print Data");
        PrintListener printListener = new PrintListener(printData);
        printData.setActionCommand("Print Data");
        printData.addActionListener(printListener);
        
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(storeData);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(retriveData);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(togglePower);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(displayNodeInfo);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(randomizeEvents);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(printData);
        
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
    
    ///////////////////////////////////////////////////////////////
    // Line: String test = nodeList.get(index).name;
    // Can be replaces with calls to the node at index
    ///////////////////////////////////////////////////////////////
    class StoreListener implements ActionListener{
        public StoreListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            int index = list.getSelectedIndex();
            String test = nodeList.get(index).name;
            JOptionPane.showMessageDialog(null, "Node " + (index + 1) + " Stores");
        }
    }
    
    class RetriveListener implements ActionListener{
        public RetriveListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            int index = list.getSelectedIndex();
            String test = nodeList.get(index).name;
            JOptionPane.showMessageDialog(null, "Node " + (index + 1) + " Retrives");
        }
    }
    
    class PowerListener implements ActionListener{
        public PowerListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            int index = list.getSelectedIndex();
            String test = nodeList.get(index).name;
            JOptionPane.showMessageDialog(null, "Node " + (index + 1) + " Toggles Power");
        }
    }
    
    class InfoListener implements ActionListener{
        public InfoListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            int index = list.getSelectedIndex();
            String test = nodeList.get(index).name;
            JOptionPane.showMessageDialog(null, "Node " + (index + 1) + " Displays Info");
        }
    }
    
    class RandomListener implements ActionListener{
        public RandomListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            JOptionPane.showMessageDialog(null, "Random Events");
        }
    }
    
    class PrintListener implements ActionListener{
        public PrintListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            JOptionPane.showMessageDialog(null, "Event Log");
        }
    }
    
    public void valueChanged(ListSelectionEvent e){
        /*if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
            //No selection, disable fire button.
                displayNodeInfo.setEnabled(false);

            } else {
            //Selection, enable the fire button.
                displayNodeInfo.setEnabled(true);
            }
        }*/
    }
    
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Cloud Storage Controller");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new Overseer();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
