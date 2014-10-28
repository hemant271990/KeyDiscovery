import java.io.Console;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;



public class KeyDiscovery {
	
	public static Lattice lt;
	
	public static ColumnCombination maxEGCC = null;
	
	public static ColumnCombination uniqueKeyAns = null;
	
	public static boolean allPrunedFlag = true;
	
	public static Hashtable<String, Integer> goldenStd = new Hashtable<String, Integer>();
	
	public static Hashtable<String, Integer> discoveredKeys = new Hashtable<String, Integer>();
	
	/*public static String[][] table = new String[][]{
			  { "A", "B", "C", "D" },
			  { "1", "2", "4", "1" },
			  { "2", "2", "5", "3" },
			  { "2", "2", "4", "3" }
			  //{ "1", "2", "4", "3" }
			};*/
	
	public static String[] keys = new String[] { "AD" };
	
	public static String[][] table = new String[][]{
		{ "A", "B", "C", "D", "E" },
		{"1","6","1","5","1"}, 
		{"1","3","1","6","4"}, 
		{"1","3","2","1","3"}, 
		{"1","6","3","5","4"}, 
		{"1","3","4","5","2"}, 
		{"1","4","6","2","4"}, 
		{"2","5","6","6","1"}, 
		{"3","6","1","1","4"}, 
		{"3","6","1","4","5"}, 
		{"3","4","2","1","2"}, 
		{"3","3","3","5","4"}, 
		{"3","5","4","5","4"}, 
		{"3","3","4","3","6"}, 
		{"3","2","6","5","2"}, 
		{"3","4","6","2","5"}, 
		{"4","2","1","5","2"}, 
		{"4","4","2","5","3"}, 
		{"4","4","5","2","3"}, 
		{"4","4","6","6","3"}, 
		{"4","5","6","1","5"}, 
		{"4","5","6","1","6"}, 
		{"5","2","1","5","1"}, 
		{"5","3","1","3","2"}, 
		{"5","4","1","1","3"}, 
		{"5","2","1","4","4"}, 
		{"5","5","2","6","5"}, 
		{"5","1","3","6","2"}, 
		{"5","4","4","6","4"}, 
		{"5","1","4","1","5"}, 
		{"5","3","5","2","4"}, 
		{"5","2","5","5","6"}, 
		{"5","2","6","3","3"}, 
		{"5","6","6","2","5"}, 
		{"6","2","1","5","3"}, 
		{"6","6","1","1","5"}, 
		{"6","1","2","6","3"}, 
		{"6","1","3","5","3"}, 
		{"6","3","5","1","6"} 
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
					float EG = (nbOfSupersets)*probabOfYes + (nbOfSubsets)*probabOfNo;
					if(EG > maxEG)
					{
						maxEG = EG;
						maxEGCC = lt.levelStructure[i].get(j);
					}
					lt.levelStructure[i].get(j).setExpectedGain(EG);
					allPrunedFlag = false;
					System.out.println("For: "+ lt.levelStructure[i].get(j).getColName() + " " + nbOfSubsets + " " + nbOfSupersets + " uniques: " + getUniqueCount(lt.levelStructure[i].get(j)) + " EG= " + lt.levelStructure[i].get(j).getExpectedGain() );
				}
			}
		}
	}
	
	public static void main(String[] args) {

//		goldenStd.put("AC", 1);
//		goldenStd.put("AD", 1);
//		goldenStd.put("ABC", 1);
//		goldenStd.put("ACD", 1);
//		goldenStd.put("ABD", 1);
//		goldenStd.put("ABCD", 1);
		
//		goldenStd.put("C", 1);
//		goldenStd.put("AC", 1);
//		goldenStd.put("AD", 1);
//		goldenStd.put("BC", 1);
//		goldenStd.put("CD", 1);
//		goldenStd.put("AD", 1);
//		goldenStd.put("ABC", 1);
//		goldenStd.put("BCD", 1);
//		goldenStd.put("ACE", 1);
//		goldenStd.put("ABD", 1);
//		goldenStd.put("ADE", 1);
//		goldenStd.put("ABDE", 1);
//		goldenStd.put("ACDE", 1);
//		goldenStd.put("ABCE", 1);
//		goldenStd.put("ABCDE", 1);

		DataGenerator dg = new DataGenerator(keys);
		
/*		for(int i = 0; i < keys.length; i++)
		{
			goldenStd.put(keys[i], 1);
		}
		
		int numQ = 0;
		lt = new Lattice(table[0].length);
		generatePowerLattice(table[0]);
		computeEGForLattice();
		
		while(!allPrunedFlag)
		{
			//Simulate user answers by verifying them against goldenStd
			maxEGCC.setPruned(true);
			if(goldenStd.containsKey(maxEGCC.getColName()))
			{
				int level = maxEGCC.getColName().length() - 1;
				lt.pruneSuperset(maxEGCC, level);
				discoveredKeys.put(maxEGCC.getColName(), 1);
			} else
			{
				lt.pruneSubsets(maxEGCC);
			}
			numQ++;
			computeEGForLattice();
		}
		
		Enumeration<String> discKeys = discoveredKeys.keys();
		while(discKeys.hasMoreElements())
			System.out.println("#####Discovered key is " + discKeys.nextElement());
		
		System.out.println("NUMBER OF QUESTIONS ASKED " + numQ);
*/		
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
