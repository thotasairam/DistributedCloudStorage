package CloudEvents;

public class Events {
	
	int total_available_space;
	int total_required_space;
	
	public int calculateTotalSpace(int filesize, int nodeid)
	{
		for(int i=0;i<Constants.NUMBER_NODES;i++)
		{
			if(Constants.Nodelist[i].status == true && Constants.Nodelist[i].node_id != nodeid)
			total_available_space += Constants.Nodelist[i].getAvailableMemory();
		}

			total_required_space = filesize * Constants.REPLICATION_FACTOR;
			
			System.out.println("total_available_space : "+total_available_space);
			if(total_required_space > total_available_space)
			{
				System.out.println("No enough Space on Nodes");
				return -1;
			}
			
			return 1;
	}
	
	public String uploadFile(int sourcenode,int fileId, int filesize)
	{
		String msg = "";
		
      System.out.println("In upload file of : "+sourcenode);
	
		int x = Constants.Nodelist[sourcenode-1].split(sourcenode,filesize,fileId);
		
		if(x == 1)
		{
			msg = "Successfully calculated splits" + "\n\n"+"SPLITS :"+"\n";
			for(int i=0;i<Constants.temp_Splits.size();i++)
			{
				String str = "";
				NodeFile nf = Constants.temp_Splits.get(i);
				str = str + nf.file_split+"  -  " + "Node : "+nf.node_id + "  -  "+"Size : "+nf.bytes + " bytes\n";
				msg = msg + str;
			}
			
			msg = msg + "\nREPLICAS:\n";
			
			for(int i=0;i<Constants.replica_Splits.size();i++)
			{
				String str = "";
				Replica_Splits rs = Constants.replica_Splits.get(i);
				str = rs.file_split_part + " : ";
				for(int j=0;j<rs.node_list.size();j++)
				{
				 str = str + "Node : "+rs.node_list.get(j)+", ";
				}
				msg = msg + str + "\n";
				System.out.println("msg replicas : "+msg);
			}
		}
		
		
		else
		{
			msg = "Failed to calculate splits" + "\n";
		}
		
		Constants.temp_Splits.removeAll(Constants.temp_Splits);
		Constants.replica_Splits.removeAll(Constants.replica_Splits);
		
		return msg;
	}
	
	public String retrieveFile(int sourcenode, int fileId)
	{
		String x = Constants.Nodelist[sourcenode-1].retrieveFileInfoFromNodes(sourcenode, fileId);
		
		if(x == "")
			x = "Invalid File ID/Cannot retrieve file currently";
		
		return x;
	}
	
}
