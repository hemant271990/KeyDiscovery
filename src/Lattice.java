

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;

public class Lattice {

	public ArrayList<ColumnCombination>[] levelStructure ;
	
	@SuppressWarnings("unchecked")
	public Lattice(int levels)
	{
		levelStructure = (ArrayList<ColumnCombination>[])new ArrayList[levels];
		for(int i = 0; i < levels; i++)
			levelStructure[i] = new ArrayList<ColumnCombination>();
	}

	public int getNbOfSupersets(ColumnCombination cc, int level)
	{
		int count = 0;
		for(int i = level+1; i < levelStructure.length; i++)
		{
			ArrayList<ColumnCombination> levelNodes = levelStructure[i];
			for (int j = 0; j < levelNodes.size(); j++) {
				BitSet tempBs = cc.getBitSet();
				BitSet nodeBs = new BitSet();
				nodeBs.or(levelNodes.get(j).getBitSet());
				//System.out.println(tempBs.toString() + " "+ nodeBs.toString());
				nodeBs.and(tempBs);
				if(nodeBs.equals(tempBs))
				{
					//System.out.println("Matched---");
					//System.out.println("toCheck: "+cc.getColName() + " checked: " + levelNodes.get(j).getColName());
					count++;
				}
			}
		}
		//System.out.println(count);
		return count;
	}
	
	public int getNbOfSubsets(int level)
	{
		return (int) (Math.pow(2, level+1) - 1);
	}
	
	public void pruneSuperset(ColumnCombination cc, int level)
	{
		for(int i = level+1; i < levelStructure.length; i++)
		{
			ArrayList<ColumnCombination> levelNodes = levelStructure[i];
			for (int j = 0; j < levelNodes.size(); j++) {
				BitSet tempBs = cc.getBitSet();
				BitSet nodeBs = new BitSet();
				nodeBs.or(levelNodes.get(j).getBitSet());
				nodeBs.and(tempBs);
				if(nodeBs.equals(tempBs))
				{
					levelStructure[i].get(j).setPruned(true);
				}
			}
		}
		System.out.println("Prune Superset called");
	}
	
	public void pruneSubsets(ColumnCombination cc)
	{
		Queue<ColumnCombination> queue = new LinkedList<ColumnCombination>();
		queue.add(cc);
		
		while(!queue.isEmpty())
		{
			ColumnCombination curr = (ColumnCombination) queue.poll();
			int level = curr.getBitSet().cardinality() - 1;
			ArrayList<ColumnCombination> levelNodes = levelStructure[level];
			for (int j = 0; j < levelNodes.size(); j++) {
				int size = cc.getBitSet().size();
				BitSet tempBs = cc.getBitSet();
				tempBs.flip(0, size);
				
				BitSet nodeBs = levelNodes.get(j).getBitSet();
				nodeBs.and(tempBs);
				if(nodeBs.cardinality() == 0)
				{
					levelStructure[level].get(j).setPruned(true);
					if((levelStructure[level].get(j).getBitSet().cardinality() - 1) != 0)
						queue.add(levelStructure[level].get(j));
				}
			}
		}
	}
}
