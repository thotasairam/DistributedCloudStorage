package CloudEvents;

/**
 * Node class for the cloud storage program
 * Different methods/functions on a node are handled here
 * Author: Prasanna
 */

import java.util.ArrayList;

public class Node {

	 boolean status;
	 int node_id;
	 int total_memory;
	 int occupied_memory;
	 ArrayList<NodeFile> nodes_file_info = new ArrayList<NodeFile>();
	 ArrayList<Directory> node_directory = new ArrayList<Directory>();
	 int cur_node_id;
	 String name;
 
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

	public boolean storeData(NodeFile nf, int bytes,int storing_node_id)
	{	
		nf.node_id = storing_node_id;
		nodes_file_info.add(nf);
		occupied_memory += bytes;
		return true;
	}
	
	public String retrieveData(int nodeId, int fileId,String part)
	{
		String filesplit = "Cannot retrieve file split";
		
		for(int i=0; i<nodes_file_info.size();i++)
		{
			NodeFile nf = nodes_file_info.get(i);
			if(nf.node_id == nodeId && nf.file_id == fileId && nf.file_split.equalsIgnoreCase(part))
			{
			filesplit = nf.file_split + ": " + nf.bytes + " bytes from Node : "+node_id;
			}
			
		}
		
		return filesplit;
	}
	
	public void updateNodeDirectory(Directory d)
	{
		node_directory.add(d);		
		storeFileInfoIntoNodes();
		
	}
	
	public int split(int nodeid, int filesize,int fileid)
	{
		Splitter s = new Splitter();
		int status = s.split(nodeid,filesize,fileid);
		cur_node_id = nodeid;
		
		if(status != -1)
		{
			System.out.println("Successfully added the allocated splits to Node");
			
			Directory d = new Directory();
			
			d.file_id = fileid;
			d.splits = new ArrayList<NodeFile>(Constants.temp_Splits);
			d.replicas_of_splits = new ArrayList<Replica_Splits>(Constants.replica_Splits);
			updateNodeDirectory(d);
			return 1;
		}
	
		return -1;
	
	}
	
	public void storeFileInfoIntoNodes()
	{
		//Update splits info into the nodes.
		
		for(int i=0;i<Constants.temp_Splits.size();i++)
		{
			NodeFile nf = new NodeFile();
			nf.file_id = Constants.temp_Splits.get(i).file_id;
			nf.node_id = Constants.temp_Splits.get(i).node_id;
			nf.bytes = Constants.temp_Splits.get(i).bytes;
			nf.file_split = Constants.temp_Splits.get(i).file_split;
			
			Constants.Nodelist[nf.node_id-1].storeData(nf,nf.bytes,cur_node_id);
		}
		
	//Update replication splits info into the nodes.
		
		for(int i=0;i<Constants.replica_Splits.size();i++)
		{
			NodeFile nf = new NodeFile();
			String part = Constants.replica_Splits.get(i).file_split_part;
			int byts=0;
			int fid = 0;
			
			for(int j=0;i<Constants.temp_Splits.size();j++)
			{
			NodeFile nf1 = Constants.temp_Splits.get(j);
			if(nf1.file_split.equalsIgnoreCase(part))
			{
			byts = nf1.bytes;
			fid = nf1.file_id;
			break;
			}
			}
			nf.bytes = byts;
			nf.file_id = fid;
			nf.file_split = part;
			ArrayList<Integer> allnodes = new ArrayList<Integer>(Constants.replica_Splits.get(i).node_list);
			
			for(int k=0;k<allnodes.size();k++)
			{
				int cur_nid = allnodes.get(k);
				nf.node_id = cur_nid;
				Constants.Nodelist[nf.node_id-1].storeData(nf,nf.bytes,cur_node_id);
			}
		}
		
	}
	
	public String retrieveFileInfoFromNodes(int nodeid,int fileid)
	{
		int count =0;
		String failure_message = "";
		ArrayList<NodeFile> splits_info;
		ArrayList<Replica_Splits> replica_splits_info;
		ArrayList<String> offlineSplits = new ArrayList<String>();
		
		String fileInfo = "";
		for(int i=0;i<node_directory.size();i++)
		{
			Directory d = node_directory.get(i);
			//System.out.println(i);
			
			if(d.file_id == fileid)
			{
				splits_info = new ArrayList<NodeFile>(d.splits);
				replica_splits_info = new ArrayList<Replica_Splits>(d.replicas_of_splits);
				
				for(int j=0;j<splits_info.size();j++)
				{
					NodeFile nf = splits_info.get(j);
					int nid = nf.node_id;
					String part = nf.file_split;
					
					if(Constants.Nodelist[nid-1].status == false)
					{
						offlineSplits.add(part);
				//		System.out.println("Added to offline splits part : "+part);
					}
					else
					{
						fileInfo = fileInfo + Constants.Nodelist[nid-1].retrieveData(nodeid, fileid,part) + "\n";
					//	System.out.println("Fileinfo  : "+fileInfo+ "nid : "+(nid-1));
					//	System.out.println("R STring  : "+Constants.Nodelist[nid-1].retrieveData(nodeid, fileid));
					}
				}
				
				//Retrieve replicas from replica splits nodes if main splits nodes offline
				if(offlineSplits.size() > 0)
				{
					int flag = -1;
					for(int k=0;k<offlineSplits.size();k++)
					{
						flag = -1;
					String part = offlineSplits.get(k);
					
					for(int m=0;m<replica_splits_info.size();m++)
					{
						if(replica_splits_info.get(m).file_split_part.equals(part))
						{
							ArrayList<Integer> nodelist = replica_splits_info.get(m).node_list;
							for(int n=0;n<nodelist.size();n++)
							{
								flag = -1;
								int nid = nodelist.get(n);
								if(Constants.Nodelist[nid-1].status == false)
								continue;
								else
								{
								fileInfo = fileInfo + Constants.Nodelist[nid-1].retrieveData(nodeid, fileid,part) + "\n";
								flag = 1;
								break;
								}
							}//end of for for checking all replica nodes
							
							if(flag == -1)
							{
								fileInfo = "Cannot retrieve all Splits";
								return fileInfo;
							}//end of if for any node offline for replica splits
						}//end of match part split part
						
						if(flag == 1)
						break;
						
					}//end of for(m) loop : retrieving splits from replica splits nodes
					
					}//end of for(k) loop : retrieving splits for parts : offline nodes
				} //end of check of offline splits part if any
				
			} //end if file_id match
		}//end of for for NodeDirectory
		
	 return fileInfo;
	}
}
