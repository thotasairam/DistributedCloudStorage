package CloudEvents;

public class Settings {
	
	int NUMBER_NODES = 6;
	int REPLICATION_FACTOR = 2;
	int SPLIT_FACTOR = 2;
	boolean SPLIT_TYPE_DYNAMIC = true;
	Node Nodelist[]=new Node[NUMBER_NODES];
	int MEMORYLIST[] = {1024,2048,512};
	int FILE_ID = 0;
}
