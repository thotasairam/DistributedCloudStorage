package CloudEvents;

/**
 * Control class for the cloud storage program
 * Creates a GUI with the number of Nodes given in Settings.java
 * Author: Carson
 */
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class GUI extends JPanel
                      implements ListSelectionListener {
    // Keep track of events and their relevent data
    //      success/failure, total time to complete
    ArrayList<Events> eventsLog;
    Events evts = new Events();
    long eventTime;
    boolean eventSuccess;
    // List of all nodes
  //  DefaultListModel<Node> Nodelist = new DefaultListModel<Node>();
    DefaultListModel nodeNames = new DefaultListModel();
    JList list;
    // Buttons
    private JButton storeData;
    private JButton retriveData;
    private JButton togglePower;
    private JButton displayNodeInfo;
    private JButton randomizeEvents;
    private JButton printData;
    Random rn = new Random();
    int fileid_counter = 0;
    int filesizes[] = {100, 560, 400, 200, 150, 85};
	
	public void initilizeNodes()
	{
		for (int i=0;i<Constants.NUMBER_NODES;i++)
		{
			Constants.Nodelist[i] = new Node();
			Constants.Nodelist[i].status = true;
			int pos = rn.nextInt(8);
			Constants.Nodelist[i].total_memory = Constants.MEMORYLIST[pos] ;
			Constants.Nodelist[i].occupied_memory = 0;
			Constants.Nodelist[i].node_id = i+1;	
			Constants.Nodelist[i].name = "Node "+(i+1);
		}
		
	}
    
    
    GUI(){
       super(new BorderLayout());
        
        int numOfNodes = Constants.NUMBER_NODES;
        initilizeNodes();
        
     //   Node[] data = new Node[numOfNodes];
        for(int i =0; i < numOfNodes; i++){
       //     data[i] = new Node();
         //   data[i].name = "Node " + (i + 1);
           // Nodelist.addElement(data[i]);
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
        
        JButton retriveData = new JButton("Retrieve Data");
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
     //   buttonPane.add(Box.createHorizontalStrut(5));
      //  buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
      //  buttonPane.add(Box.createHorizontalStrut(5));
        
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
    
    ///////////////////////////////////////////////////////////////
    // Line: String test = Nodelist.get(index).name;
    // Can be replaces with calls to the node at index
    ///////////////////////////////////////////////////////////////
    class StoreListener implements ActionListener{
        public StoreListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            int index = list.getSelectedIndex();
            String test = Constants.Nodelist[index].name;
            String msg = "";
            
            
            JTextField fileid = new JTextField(8);
            JTextField size = new JTextField(8);

            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel("File Id"));
            myPanel.add(fileid);
            myPanel.add(Box.createHorizontalStrut(15)); // a spacer
            myPanel.add(new JLabel("Size : "));
            myPanel.add(size);

            int result = JOptionPane.showConfirmDialog(null, myPanel, 
                     "Please file ID and Size of File", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
            	
            	msg = "FIle ID : "+fileid.getText()+"\n";
            	msg = msg + "File Size : "+size.getText();
            }
            
        //    JFrame frame = new JFrame();
          //  Object result = JOptionPane.showInputDialog(frame, "Enter FileID");
            
            try
            {
            	int fid = Integer.parseInt(fileid.getText());
            	int fsize = Integer.parseInt(size.getText());
            	
            	int flag = evts.calculateTotalSpace(fsize, (index+1));
            	
            	if(flag == -1)
            	JOptionPane.showMessageDialog(null, "File Size exceeds the total Available Space");
            	
            	else
            	{
                String status = evts.uploadFile((index+1),fid,fsize);
                JOptionPane.showMessageDialog(null, status);
            	}
            }
            catch(NumberFormatException ex)
            {
            	JOptionPane.showMessageDialog(null, "Enter Integer Values Only");
            }
          //  JOptionPane.showMessageDialog(null, "Node " + (index + 1) + " Stores");
        }
    }
    
    class RetriveListener implements ActionListener{
        public RetriveListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            int index = list.getSelectedIndex();
            String test = Constants.Nodelist[index].name;
            int nid = Constants.Nodelist[index].node_id;
            int fid = 0;
            String msg = evts.retrieveFile(nid,fid);
            JFrame frame = new JFrame();
            Object result = JOptionPane.showInputDialog(frame, "Enter FileID to be retrieved");
            
            try
            {
            	fid = Integer.parseInt(result.toString());
            	msg = evts.retrieveFile(nid, fid);
            	JOptionPane.showMessageDialog(null, msg);
            }
            catch(Exception exp)
            {
            	msg = "Enter a Valid File Id (Numeric)";
            	JOptionPane.showMessageDialog(null, msg);
            }
            
        }
    }
    
    class PowerListener implements ActionListener{
        public PowerListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            int index = list.getSelectedIndex();
            String node = Constants.Nodelist[index].name;
            String msg = "";
            
            if(Constants.Nodelist[index].status == true)
            {
            	Constants.Nodelist[index].status = false;
            	msg = node + " goes Offline" + "\n";
            }
            else
            {
            	Constants.Nodelist[index].status = true;
            	msg = node + " goes Online" + "\n";
            }
            
            
            JOptionPane.showMessageDialog(null, msg);
        }
    }
    
    class InfoListener implements ActionListener{
        public InfoListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
            int index = list.getSelectedIndex();
           
            String info = getNodeInfo(index);
            
            JOptionPane.showMessageDialog(null, info);
        }
    }
    
    class RandomListener implements ActionListener{
        public RandomListener(JButton button) {
            //this.button = button;
        }

        public void actionPerformed(ActionEvent e){
        	int nid = rn.nextInt(Constants.NUMBER_NODES);
        	
        	String msg = "Event : Triggered Node Info for Node "+(nid+1)+":\n";
        	msg = msg + getNodeInfo(nid);
        	int uploadedNodeId = 0;
        	
        	int len = filesizes.length;
        	int size = filesizes[rn.nextInt(len-1)];
        	fileid_counter++;
        	
        	int ol_index = getAnyOnlineNodeIndex();
        	uploadedNodeId = ol_index;
        	msg = msg + "\nEvent : Triggered Upload File for Node "+(ol_index+1)+":\n";
        	String status = evts.uploadFile((ol_index+1),fileid_counter,size);
        	msg = msg + "\n";
        	msg = msg+status+"\n";
        	
        	nid = rn.nextInt(Constants.NUMBER_NODES);
        	msg = msg + "Event : Toggling Power on Node : "+(nid+1)+":\n";
        	String power_msg = "Node ";
        	if(Constants.Nodelist[nid].status == true)
            {
            	Constants.Nodelist[nid].status = false;
            	power_msg = power_msg+(nid+1) + " goes Offline" + "\n";
            }
            else
            {
            	Constants.Nodelist[nid].status = true;
            	power_msg = power_msg+(nid+1) + " goes Online" + "\n";
            }
        	
        	msg = msg + power_msg+"\n";
        	
        	if(Constants.Nodelist[uploadedNodeId].status == true)
        	ol_index = uploadedNodeId;
        	else
        	ol_index = getAnyOnlineNodeIndex();
        	
        	msg = msg + "Event : Triggered Retrieve File for Node "+(ol_index+1)+":\n";
        	
        	int rfid = getAnyFileIdonNode(ol_index);
        	
        	if(rfid == -1)
        	msg = msg + "No files on Uploaded by this node";
        	
        	else
        	{
        	msg = msg + "Retrieving file with File ID : "+rfid+" uploaded by Node\n";
        	msg = msg + evts.retrieveFile((uploadedNodeId+1), rfid);   
        	}
        	
            JOptionPane.showMessageDialog(null, msg);
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
        JComponent newContentPane = new GUI();
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
    
    public String getNodeInfo(int index)
    {
    	 String test = Constants.Nodelist[index].name;
         int id = Constants.Nodelist[index].node_id;
         String info = "";
         String status="";
         info = info + test + "\n";
         if(Constants.Nodelist[index].status == true)
         status = "Online";
         else
         status = "Offline";
         
         info = info + "Status : "+status + "\n";
         info = info + "Total Memory : "+Constants.Nodelist[index].total_memory+ "\n";
         info = info + "Occupied Memory : "+Constants.Nodelist[index].occupied_memory+ "\n";
         info = info + "Available Memory : "+Constants.Nodelist[index].getAvailableMemory()+ "\n";
         
         ArrayList<Directory> dir = Constants.Nodelist[index].node_directory;
         String file_list = "";
         
         for(int i=0;i<dir.size();i++)
         {
         	file_list = file_list + dir.get(i).file_id + ", ";
         }
         info = info + "Owner of files : "+file_list+ "\n";
         
         return info;
    }
    
    
    public int getAnyOnlineNodeIndex()
    {
    	ArrayList<Integer> olNodes = new ArrayList<Integer>();
    	for(int i=0;i<Constants.Nodelist.length;i++)
    	{
    		if(Constants.Nodelist[i].status == true)
    		olNodes.add(i);	
    	}
    	
    	if(olNodes.size() !=0)
    	{
    	int x = rn.nextInt(olNodes.size()-1);
    	return (olNodes.get(x));
    	}
    	
    	Constants.Nodelist[0].status = true;	
    	return 0;
    }
    
    public int getAnyFileIdonNode(int node_index)
    {
    	ArrayList<Directory> dir = new ArrayList<Directory>(Constants.Nodelist[node_index].node_directory);
    	
    	if(dir.size() == 0)
    	return -1;

    	return(dir.get(0).file_id);
    	}
    }
