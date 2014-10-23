import java.io.Console;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;



public class KeyDiscovery {
	
	public static Lattice lt;
	
	public static ColumnCombination maxEGCC = null;
	
	public static ColumnCombination uniqueKeyAns = null;
	
	public static boolean allPrunedFlag = true;
	
	public static String[][] table = new String[][]{
			  { "A", "B", "C", "D" },
			  { "1", "2", "4", "1" },
			  { "2", "2", "6", "3" },
			  { "2", "2", "4", "1" },
			  { "3", "2", "9", "2" }
			};
	
	public static void generatePowerLattice(String[] columns)
	{
		int count = (int) Math.pow(2, columns.length);
		String combination;
		for (int i = 1; i < count; i ++)
		{
			String bitString = Integer.toBinaryString(i);
			while (bitString.length() < columns.length) {
		        bitString = "0" + bitString;
			}
			BitSet bs = BitSet.valueOf(new long[] { i });
			combination = getColumnCombination(bitString, columns);
			
			ColumnCombination cc = new ColumnCombination(combination, bs);
			int level = combination.length() - 1;
			lt.levelStructure[level].add(cc);
		}
		
		
	}

	public static String getColumnCombination(String binary, String[] columns)
	{
		String combination = "";
		for (int i = 0; i < binary.length(); i++)
		{
			if(binary.charAt(i) == '1')
				combination += columns[i];
		}
		return combination;
	}
	
	public static int getUniqueCount(ColumnCombination cc)
	{
		Hashtable<String, Integer> map = new Hashtable<String, Integer>();
		String columns = cc.getBitSet().toString();
		String delims = "[{,} ]+";
		String[] index = columns.split(delims);
		for (int i = 1; i < table.length; i++)
		{
			String mapKey = "";
			for (int j = index.length - 1; j >= 1; j--)
			{
				int colId = table[0].length - Integer.parseInt(index[j]) - 1;
				 mapKey += table[i][colId];
			}
			map.put(mapKey, 1);
		}
		
		return map.size();
	}
	
	public static void computeEGForLattice() 
	{
		uniqueKeyAns = maxEGCC;
		maxEGCC = null;
		allPrunedFlag = true;
		float maxEG = 0;
		for(int i = 0; i < table[0].length-1; i++)
		{
			for(int j = 0; j < lt.levelStructure[i].size(); j++)
			{
				if(!lt.levelStructure[i].get(j).isPruned())
				{
					float nbOfSubsets = lt.getNbOfSubsets(i);
					float nbOfSupersets = lt.getNbOfSupersets(lt.levelStructure[i].get(j), i);
					float probabOfYes = (float) getUniqueCount(lt.levelStructure[i].get(j)) / (float) (table.length - 1);
					float probabOfNo = 1 - probabOfYes;
					float EG = (nbOfSubsets + nbOfSupersets)*probabOfYes + (nbOfSubsets)*probabOfNo;
					if(EG > maxEG)
					{
						maxEG = EG;
						maxEGCC = lt.levelStructure[i].get(j);
					}
					lt.levelStructure[i].get(j).setExpectedGain(EG);
					allPrunedFlag = false;
					System.out.println("For: "+ lt.levelStructure[i].get(j).getColName() + " " + nbOfSubsets + " " + nbOfSupersets + " " + getUniqueCount(lt.levelStructure[i].get(j)) + " EG= " + lt.levelStructure[i].get(j).getExpectedGain() );
				}
			}
		}
	}
	
	public static void pruneFor(ColumnCombination cc)
	{
		int level = cc.getColName().length() - 1;
		cc.setPruned(true);
		lt.pruneSuperset(cc, level);
		lt.pruneSubsets(cc);
		
	}
	
	
	
	public static void main(String[] args) {

		lt = new Lattice(table[0].length);
		generatePowerLattice(table[0]);
		//System.out.println("For: "+ cc2.getColName() + " " + nbSub + " " + nbSup + " " + c);
		computeEGForLattice();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*System.out.println("Is " + maxEGCC.getColName() + " a unique column?");
		Console console = System.console();
		String ans = console.readLine();
		if(ans.equalsIgnoreCase("yes"))
		{
			pruneFor(maxEGCC);
		}*/
		while(!allPrunedFlag)
		{
			pruneFor(maxEGCC);
			computeEGForLattice();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Unique key is " + uniqueKeyAns.getColName());
		/*for(int i = 0; i < table[0].length; i++)
		{
			for(int j = 0; j < lt.levelStructure[i].size(); j++)
			{
				System.out.print(lt.levelStructure[i].get(j).isPruned() + " ");
				System.out.println(lt.levelStructure[i].get(j).getColName());
			}
		}*/
		
	}

}
