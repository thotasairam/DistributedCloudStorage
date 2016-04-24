package CloudEvents;

import java.util.Random;

public class MainNode {

	Random rn = new Random();

	
	public void initilizeNodes()
	{
		for (int i=0;i<Constants.NUMBER_NODES;i++)
		{
			Constants.Nodelist[i] = new Node();
			Constants.Nodelist[i].status = true;
			int pos = rn.nextInt(8);
			
			System.out.println("POS : "+pos);
			
			Constants.Nodelist[i].total_memory = Constants.MEMORYLIST[pos] ;
			Constants.Nodelist[i].occupied_memory = 0;
			Constants.Nodelist[i].node_id = i+1;	
		}
		
	}
	
	
	public void uploadFile(int n)
	{
		System.out.println("In upload file");
		
		int x = Constants.Nodelist[n].split(n,900,123);
		
		if(x == 1)
		{
			System.out.println("Successfully calculated splits");
		}
		
		
		else
		{
			System.out.println("Failed to calculate splits");
		}
		
		System.out.println("Splits");
		for(int i=0;i<Constants.temp_Splits.size();i++)
		{
			NodeFile nf = Constants.temp_Splits.get(i);
			System.out.println("File ID : "+nf.file_id);
			System.out.println(nf.file_split);
			System.out.println("Node id :"+nf.node_id);
			System.out.println("Size : "+nf.bytes);
			
			System.out.println();
			
		}
		
		System.out.println("Replicas");
		
		for(int i=0;i<Constants.replica_Splits.size();i++)
		{
			Replica_Splits rs = Constants.replica_Splits.get(i);
			System.out.println(rs.file_split_part);
			for(int j=0;j<rs.node_list.size();j++)
			{
				System.out.print(rs.node_list.get(j)+" - ");
			}
			System.out.println();
			
		}
		
		Constants.temp_Splits.removeAll(Constants.temp_Splits);
		Constants.replica_Splits.removeAll(Constants.replica_Splits);
	}
	
	public void retrieveFile(int nid,int fid)
	{
		
		String x = Constants.Nodelist[nid].retrieveFileInfoFromNodes(nid, fid);
		System.out.println("Retrieval : \n"+x);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainNode mn = new MainNode();
		mn.initilizeNodes();
		mn.uploadFile(2);
		mn.retrieveFile(2,123);
	}

}
