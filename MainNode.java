package CloudEvents;

public class MainNode {

	
	public void initilizeNodes()
	{
		for (int i=0;i<Constants.NUMBER_NODES;i++)
		{
			Constants.Nodelist[i] = new Node();
			Constants.Nodelist[i].status = true;
			Constants.Nodelist[i].total_memory = 500 * (i+1) ;
			Constants.Nodelist[i].occupied_memory = 0;
			Constants.Nodelist[i].node_id = i+1;
		}
	}
	
	
	public void uploadFile(int n)
	{
		Constants.Nodelist[n].split(n,900,123);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainNode mn = new MainNode();
		mn.initilizeNodes();
		mn.uploadFile(2);
	}

}
