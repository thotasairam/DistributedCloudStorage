package CloudEvents;

import java.util.ArrayList;
import java.util.LinkedHashMap;

//Data Structure maintained by each node.
//The primary and replica splits of the file uploaded by the node are maintained here

public class Directory {
int file_id;
ArrayList<NodeFile> splits = new ArrayList<NodeFile>();
ArrayList<Replica_Splits> replicas_of_splits = new ArrayList<Replica_Splits>();
}
