package CloudEvents;

/**
 * Split & Replicate class for the cloud storage program
 * Handles file splits and replication factor for a file
 * Author: Prasanna
 */

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
	double UseFraction = Constants.OCC_FRACTION;
	LinkedHashMap<Integer,Integer> replicate_nodes_space;
	int counter = 0;
	
	
	public void reinitialize()
	{
		nodes_space = new LinkedHashMap<Integer,Integer>();
		defaultSplit = 4;
		total_available_space = 0;
		total_required_space = 0;
		maxNodes = 0;
		Constants.temp_Splits = new ArrayList<NodeFile>();
		Constants.replica_Splits = new ArrayList<Replica_Splits>();
		arrangeNodesonSpace();
	}
	
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
		
		sortNodesOnSpace();
	}
	
	public void sortNodesOnSpace()
	{
		
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
	      //  System.out.println("Result : "+entry.getKey() + " - "+entry.getValue());
	    }
	    
	    nodes_space = null;
	    nodes_space = new LinkedHashMap<Integer,Integer>(result);
	    
	    
	    System.out.println("After ordering nodes space");
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
	
	
	public int getNodeSpaceonId(int nid, LinkedHashMap<Integer,Integer> node_space_map)
	{
		LinkedHashMap<Integer,Integer> map= node_space_map;
		int space = 0;
		
		int count = 0;
		
		for (Entry<Integer, Integer> entry : map.entrySet())
		{
			if(nid == entry.getKey())
			{
			space = entry.getValue();
		    break;
			}
		}
	return space;
	}
	
	public int getNodeSpace(int position, LinkedHashMap<Integer,Integer> node_space_map)
	{
		LinkedHashMap<Integer,Integer> map= node_space_map;
		int space = 0;
		
		int count = 0;
		
		for (Entry<Integer, Integer> entry : map.entrySet())
		{
			if(count == position)
			{
			space = entry.getValue();
		    break;
			}
			count++;
		}
		
		
	//	ArrayList<Integer> values = (ArrayList<Integer>) node_space_map.values();
		//space = values.get(position);
		
	return space;
	}

	
	public int getNodeId(int position, LinkedHashMap<Integer,Integer> node_space_map)
	{
		LinkedHashMap<Integer,Integer> map= node_space_map;
		int space = getNodeSpace(position,node_space_map);
		int id = 0;	
		int count = 0;	
		Set<Integer> keys = node_space_map.keySet();
		
		for(Integer k:keys){
            if(space == node_space_map.get(k) && position == count)
            {
            	id = k;
            	break;
            }
            count++;
        }
		
	return id;
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
		System.out.println("Cannot allocate splits");
		return -1;
		}
		
		return 1;
	}
	
	public int getSplits()
	{
		int current;
		int flag = 0;
		int statusFlag = -1;
		boolean smallFile = false;
		
		if(defaultSplit > nodes_space.size())
		{
			defaultSplit = nodes_space.size();
		}

		int leftover = fileSize%defaultSplit;
		System.out.println("left over : "+leftover);
		
		while(defaultSplit >=2 && defaultSplit <= nodes_space.size())
		{
			System.out.println("Node space value  "+getNodeSpace(defaultSplit-1,nodes_space));
			System.out.println("Needed : "+((fileSize/defaultSplit) + leftover));
			
			
			if(((fileSize/defaultSplit) + leftover) <= getNodeSpace(defaultSplit-1,nodes_space))
			{
				flag = 1;
				break;
			}

			if(defaultSplit <= nodes_space.size())
			defaultSplit--;
			else			
			defaultSplit++;
		}
		
		if(fileSize/defaultSplit <= 0)
		{
			defaultSplit = fileSize;
			smallFile = true;
		}
		
		if(flag == 0)
		{
			statusFlag = dynamicSplits(Constants.OCC_FRACTION);
		}
		
		else
		{
			type = "static";
			int fraction = fileSize/defaultSplit;
			int remaining_bytes = 0;
			Constants.temp_Splits.removeAll(Constants.temp_Splits);
			
			for(int i=0;i<defaultSplit;i++)
			{
				tempNodeFile = new NodeFile();
				tempNodeFile.file_id = fileid;
				tempNodeFile.file_split = "Part "+(i+1);
				tempNodeFile.node_id = getNodeId(i,nodes_space);
				
				
				if(i == 0)
				{
				if(smallFile)
				tempNodeFile.bytes = fraction;
				
				else					
				tempNodeFile.bytes = fraction+leftover;
				}
				else
				tempNodeFile.bytes = fraction;
				
				System.out.println("i : "+i + " Node id : "+getNodeId(i,nodes_space) + "bytes : "+tempNodeFile.bytes);
				
				Constants.temp_Splits.add(tempNodeFile);
				statusFlag = 1;
				
			}
		}
			
			if(statusFlag == 1)
			{
			updateNodeSpace();
			
			if(Constants.REPLICATION_FACTOR > 1)
			statusFlag = replicateSplits();
			}
			
			if(statusFlag == 1)
			return 1;
			
			else
				return -1;
		}


	
	
	public int dynamicSplits(double cur_fraction)
	{
		ArrayList<Double> fraction = new ArrayList<Double>();
		int count = 0;
		double total = 0;
		int i;
		String status = "fail";
		
		UseFraction = cur_fraction;
		
		if(UseFraction <= 0.1)
		{
			System.out.println("Cannot allocate splits");
			return -1;
		}
		
		System.out.println("Using fraction : "+cur_fraction);
		
		for(i=0;i<nodes_space.size();i++)
		{
			count++;
		double size = getNodeSpace(i,nodes_space);
		double fract = (double)size/fileSize;
		
		if(fract > UseFraction)
		{
		fraction.add(UseFraction);
		total = total + UseFraction;
		}
		else
		{
		fraction.add(fract);
		total = total + fract;
		}
		
		if(total >= 1)
		{	
		status = "success";
		break;
		}//end if
		}
		
		if(status.equals("success"))
		{
			type = "dynamic";
			System.out.println("Can be allocated upto nodes : "+count);
			int totalBytes = 0;
			
			for(int j=0;j<count;j++)
			{
				tempNodeFile = new NodeFile();
				tempNodeFile.file_id = fileid;
				tempNodeFile.file_split = "Part "+(j+1);
				tempNodeFile.node_id = getNodeId(j,nodes_space);
				
				if(j == count-1)
				{
					tempNodeFile.bytes = fileSize-totalBytes;
				}
				
				else
				{
				int bytes = (int) (fraction.get(j)*fileSize);
				tempNodeFile.bytes = bytes;
				totalBytes = totalBytes + bytes;
				}
				Constants.temp_Splits.add(tempNodeFile);
			}//end for
			
			return 1;
			
		}//end if("success")
		
		else
		{
			System.out.println("Cannot be allocated");
			return -1;
		}
		
		}
		
	
	public int replicateSplits()
	{

		Constants.replica_Splits.removeAll(Constants.replica_Splits);
		ArrayList<NodeFile> splits = new ArrayList<NodeFile>(Constants.temp_Splits);
		System.out.println("Splits Size : "+splits.size());
		
		ArrayList<Integer> allocatedNodes = new ArrayList<Integer>();
		
		for(int i=0;i<splits.size();i++)
		{
			replicate_nodes_space = null;
			NodeFile tempNodeFile = splits.get(i);
			int byts = tempNodeFile.bytes;
			int nid = tempNodeFile.node_id;
			String file_split = tempNodeFile.file_split;
			
			replicate_nodes_space = new LinkedHashMap<Integer,Integer>(nodes_space);
			replicate_nodes_space.remove(nid);
			
			if(replicate_nodes_space.size() < Constants.REPLICATION_FACTOR-1)
			{
				System.out.println("Failed to replicate Splits");
				return -1;
			}
			
			ArrayList<Integer> partNodes = new ArrayList<Integer>();
			ArrayList<Integer> replica_node_list = new ArrayList<Integer>();
			
			for(int j=0;j<Constants.REPLICATION_FACTOR-1;j++)
			{
				int k =0;
				
				
				while(k < replicate_nodes_space.size())
				{
					int rnid = getNodeId(k,replicate_nodes_space);
					k++;
					if(partNodes.contains(rnid))
						continue;
					
					
					int occurrences = Collections.frequency(allocatedNodes, rnid);
					
					if(occurrences >= 2)
					{
					continue;
					}
					
               int available_bytes = getNodeSpace(k,replicate_nodes_space);
					
					if(byts > available_bytes)
					{
						reinitialize();
						int f = dynamicSplits(UseFraction-0.1);

						
						if(f == -1)
						{
							System.out.println("Failed to allocate replicas even after updating fraction");
							return -1;
						}
						replicateSplits();
					}
					
					else
					{
						System.out.println("Chk 6 : "+counter);
					System.out.println("Adding node : "+rnid);
					replica_node_list.add(rnid);
					int cur_space = getNodeSpaceonId(rnid,replicate_nodes_space);;
					int new_space = cur_space-byts;

					
					if(new_space <= 0)
					nodes_space.remove(rnid);
					
					else
					nodes_space.put(rnid,new_space);
					
					allocatedNodes.add(rnid);
					partNodes.add(rnid);
					break;
					}

				}//end while
				
				if(j == Constants.REPLICATION_FACTOR-2)
				{
				if(replica_node_list.size() < Constants.REPLICATION_FACTOR-1)
				{
					reinitialize();
					int f = dynamicSplits(UseFraction-0.1);
					
					if(f == -1)
					{
						System.out.println("Falied to allocate replicas even after updating fraction");
						return -1;
					}
					replicateSplits();
				}
				} //end of if(j == Constants.REPLICATION_FACTOR-2)
				
			}//end of replication factor for;
		Replica_Splits rs = new Replica_Splits();
		rs.file_split_part = file_split;
		rs.node_list = new ArrayList<Integer>(replica_node_list);
		Constants.replica_Splits.add(rs);
		
			if(i == splits.size()-1)
			{
				System.out.println("Replica Splits Size : "+Constants.replica_Splits.size());
				if(Constants.replica_Splits.size() < splits.size())
				{
				reinitialize();
				int f = dynamicSplits(UseFraction-0.1);
				
				if(f == -1)
				{
					System.out.println("Falied to allocate replicas even after updating fraction");
					return -1;
				}
				replicateSplits();
			}

			}//end of if(i) is last index		
		}//end of for (all splits)
		return 1;
	}
	
	public void updateNodeSpace()
	{		
		ArrayList<NodeFile> splits = new ArrayList<NodeFile>(Constants.temp_Splits);
		for(int i=0;i<splits.size();i++)
		{
			NodeFile tempNodeFile = splits.get(i);
			int byts = tempNodeFile.bytes;
			int nid = tempNodeFile.node_id;
			
			System.out.println("Nid  : "+nid);
			
			int cur_space = getNodeSpaceonId(nid,nodes_space);
			
		//	System.out.println("Old space : "+cur_space);
			
			int new_space = cur_space-byts;
			if(new_space <= 0)
			nodes_space.remove(nid);
			else			
			nodes_space.put(nid, new_space);
		//	System.out.println("New space : "+new_space);
			
		}
		
		
	}
}
