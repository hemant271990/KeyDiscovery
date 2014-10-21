

import java.util.ArrayList;
import java.util.BitSet;

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
				BitSet nodeBs = levelNodes.get(j).getBitSet();
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
	
	
}
