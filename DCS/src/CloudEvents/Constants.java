package CloudEvents;

import java.util.ArrayList;

//Constants class
//Consists of Network initialization parameters
//Number of nodes in network, Replication factor, etc are maintained here
public class Constants {
	
	static int NUMBER_NODES = 15;
	static int REPLICATION_FACTOR = 3;
	//static int SPLIT_FACTOR = 2;
	//static boolean SPLIT_TYPE_DYNAMIC = true;
    static Node Nodelist[]= new Node[NUMBER_NODES];
	static int MEMORYLIST[] = {1024,2048,1512,4670,9900,1000,6560,6780}; //bytes
	static int FILE_ID = 0;
	static double OCC_FRACTION = 0.90;
	static ArrayList<NodeFile> temp_Splits = new ArrayList<NodeFile>();
	static ArrayList<Replica_Splits> replica_Splits = new ArrayList<Replica_Splits>();
}


