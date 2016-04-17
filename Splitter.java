package CloudEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Splitter {
	
	LinkedHashMap<Integer,Integer> nodes_space = new LinkedHashMap<Integer,Integer>();
	int defaultSplit = 4;
	long total_available_space = 0;
	long total_required_space = 0;
	int maxNodes = 0;
	int fileSize= 0;
	int fileid = 0;
	int currentNode = 0;
	String type = "static";
	NodeFile tempNodeFile;
	
	public void arrangeNodesonSpace()
	{
		for(int i=0;i<Constants.NUMBER_NODES;i++)
		{
			if(Constants.Nodelist[i].status == true && Constants.Nodelist[i].node_id != currentNode)
			{
			int freespace = Constants.Nodelist[i].getAvailableMemory();
			nodes_space.put(Constants.Nodelist[i].node_id, freespace);
			}
		}
		
		List<Map.Entry<Integer,Integer>> list = new LinkedList<>(nodes_space.entrySet());
	    Collections.sort(list, new Comparator<Map.Entry<Integer,Integer>>() {

	        @Override
	        public int compare(Map.Entry<Integer,Integer> o1, Map.Entry<Integer,Integer> o2) {
	            return (o2.getValue()).compareTo(o1.getValue());
	        }
	    });

	    Map<Integer,Integer> result = new LinkedHashMap<>();	
	    for (Entry<Integer, Integer> entry : list) {
	        result.put(entry.getKey(), entry.getValue());
	    }
	    
	    nodes_space = null;
	    nodes_space = new LinkedHashMap<Integer,Integer>(result);
	    
	    
	    System.out.println("After ordering");
	    Set<Entry<Integer, Integer>> set = nodes_space.entrySet();
	      // Get an iterator
	      Iterator<Entry<Integer, Integer>> i = set.iterator();
	      // Display elements
	      while(i.hasNext()) {
	         Map.Entry me = (Map.Entry)i.next();
	         System.out.print(me.getKey() + ": ");
	         System.out.println(me.getValue());
	      }
	}
	
	public int getNodeSpace(int nodeid)
	{
		LinkedHashMap<Integer,Integer> map= nodes_space;
		int space = 0;
		
		for (Entry<Integer, Integer> entry : map.entrySet())
		{
			if(entry.getKey() == nodeid)
			{
			space = entry.getValue();
		    break;
			}
		}
	return space;
		
	}

	public int split(int nodeid, int filesize, int fileid)
	{
		this.fileSize = filesize;
		currentNode = nodeid;
		this.fileid = fileid;
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
		arrangeNodesonSpace();
		int flag = getSplits();
		if(flag == -1)
		{
		System.out.println("Cannot split");
		}
		
		return -1;
	}
	
	public int getSplits()
	{
		int current;
		int flag = 0;
		
		if(defaultSplit > nodes_space.size())
		{
			defaultSplit = nodes_space.size();
		}
		
		while(defaultSplit >=2 && defaultSplit <= nodes_space.size())
		{
			if(fileSize/defaultSplit <= getNodeSpace(defaultSplit))
			{
				flag = 1;
				break;
			}
			
			if(defaultSplit <= nodes_space.size())
			defaultSplit--;
			else			
			defaultSplit++;
		}
		
		if(flag == 0)
		{
			dynamicSplits();
		}
		
		else
		{
			type = "static";
			
			
			return 1;
		}
		
		return -1;
	}
	
	
	public void dynamicSplits()
	{
		ArrayList<Double> fraction = new ArrayList<Double>();
		int count = 0;
		double total = 0;
		int i;
		
		for(i=0;i<nodes_space.size();i++)
		{
			count++;
		double size = getNodeSpace(i+1);
		double fract = (double)size/fileSize;
		
		if(fract > Constants.OCC_FRACTION)
		{
		fraction.add(Constants.OCC_FRACTION);
		total = total + Constants.OCC_FRACTION;
		}
		else
		{
		fraction.add(fract);
		total = total + fract;
		}
		
		if(total >= 1)
		break;
		
		}
		
		if(i != nodes_space.size())
		{
			type = "dynamic";
			System.out.println("Can be allocated upto nodes : "+count);
			for(int j=0;j<count;j++)
			{
				tempNodeFile = new NodeFile();
				tempNodeFile.file_id = fileid;
				tempNodeFile.file_split = "Part "+(j+1);
				tempNodeFile.node_id = nodes_space.get(j);
				
				Constants.temp_Splits.add(tempNodeFile);
			}
			
		}
		
		else
		{
			System.out.println("Cannot be allocated");
		}
		
		}
		
	
}
