package CloudEvents;

import java.util.ArrayList;

public class Node {

	boolean status;
	int node_id;
	int total_memory;
	int occupied_memory;
	ArrayList<NodeFile> nodes_file_info = new ArrayList<NodeFile>();
	ArrayList<Directory> node_directory = new ArrayList<Directory>();
 
	public void startUp()
	{
		status = true;
	}
	
	public void shutDown()
	{
		status = false;
	}
	
	public int getAvailableMemory()
	{
		return (total_memory-occupied_memory );
	}

	public boolean storeData(NodeFile nf, int bytes)
	{
		nodes_file_info.add(nf);
		occupied_memory += bytes;
		return true;
	}
	
	public String retrieveData(int nodeId, int fileId)
	{
		String filesplit = new String();
		for(int i=0; i<nodes_file_info.size();i++)
		{
			NodeFile nf = nodes_file_info.get(i);
			if(nf.node_id == nodeId && nf.file_id == fileId)
			return nf.file_split;
		}
		
		return filesplit;
	}
	
	public void updateNodeDirectory(Directory d)
	{
		node_directory.add(d);
	}
	
	public int split(int nodeid, int filesize,int fileid)
	{
		Splitter s = new Splitter();
		s.split(nodeid,filesize,fileid);
		return -1;
	}
}
