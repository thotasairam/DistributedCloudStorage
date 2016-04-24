package CloudEvents;

import java.util.ArrayList;

public class Constants {
	
	static int NUMBER_NODES = 15;
	static int REPLICATION_FACTOR = 3;
	static int SPLIT_FACTOR = 2;
	static boolean SPLIT_TYPE_DYNAMIC = true;
    static Node Nodelist[]= new Node[NUMBER_NODES];
	static int MEMORYLIST[] = {1024,2048,512,670,500,1000,560,780};
	static int FILE_ID = 0;
	static double OCC_FRACTION = 0.90;
	static ArrayList<NodeFile> temp_Splits = new ArrayList<NodeFile>();
	static ArrayList<Replica_Splits> replica_Splits = new ArrayList<Replica_Splits>();
}
