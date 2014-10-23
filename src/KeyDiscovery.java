import java.io.Console;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;



public class KeyDiscovery {
	
	public static Lattice lt;
	
	public static ColumnCombination maxEGCC = null;
	
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
		//System.out.println(columns[3].toString());
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
			//System.out.println(combination);
			int level = combination.length() - 1;
			//System.out.println(level);
			//System.out.println(lt.levelStructure[0]);
			lt.levelStructure[level].add(cc);
		}
		
		
	}

	public static String getColumnCombination(String binary, String[] columns)
	{
		//System.out.println(binary);
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
		//System.out.println(cc.getColName());
		//System.out.println(index.length);
		for (int i = 1; i < table.length; i++)
		{
			String mapKey = "";
			for (int j = index.length - 1; j >= 1; j--)
			{
				int colId = table[0].length - Integer.parseInt(index[j]) - 1;
				 mapKey += table[i][colId];
			}
			//System.out.println(mapKey);
			map.put(mapKey, 1);
		}
		
		//System.out.println(map.size());
		return map.size();
		//return 0;
	}
	
	public static void computeEGForLattice() 
	{
		maxEGCC = null;
		allPrunedFlag = true;
		float maxEG = 0;
		for(int i = 0; i < table[0].length; i++)
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
					//System.out.print(lt.levelStructure[i].get(j).getColName());
					//System.out.println(lt.levelStructure[i].get(j).getExpectedGain());
					//System.out.println(probabOfYes);
					allPrunedFlag = false;
					System.out.println("For: "+ lt.levelStructure[i].get(j).getColName() + " " + nbOfSubsets + " " + nbOfSupersets + " " + getUniqueCount(lt.levelStructure[i].get(j)) + " " + lt.levelStructure[i].get(j).getExpectedGain() + " " + probabOfYes);
				}
			}
		}
	}
	
	public static void pruneFor(ColumnCombination cc)
	{
		int level = cc.getColName().length() - 1;
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
		}
		
		System.out.println("Unique key is " + maxEGCC.getColName() + " a unique column?");
		/*for(int i = 0; i < table[0].length; i++)
		{
			for(int j = 0; j < lt.levelStructure[i].size(); j++)
			{
				System.out.print(lt.levelStructure[i].get(j).getBitSet().toString());
				System.out.println(lt.levelStructure[i].get(j).getColName());
			}
		}*/
		//lt.getNbOfSupersets(lt.levelStructure[0].get(2), 0);
		//System.out.println(lt.getNbOfSubsets(3));
		
		//getUniqueCount(lt.levelStructure[0].get(2));
	}

}
