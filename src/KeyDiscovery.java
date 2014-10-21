

public class KeyDiscovery {
	
	public static Lattice lt;
	
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
			combination = getColumnCombination(bitString, columns);
			//System.out.println(combination);
			int level = combination.length() - 1;
			//System.out.println(level);
			//System.out.println(lt.levelStructure[0]);
			lt.levelStructure[level].add(combination);
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
	
	public static void main(String[] args) {

		String[][] table = new String[][]{
				  { "A", "B", "C", "D" },
				  { "1", "2", "4", "1" },
				  { "2", "1", "6", "3" },
				  { "2", "3", "4", "1" },
				  { "3", "2", "9", "2" }
				};
		
		lt = new Lattice(table[0].length);
		generatePowerLattice(table[0]);
		
		for(int i = 0; i < table[0].length; i++)
		{
			for(int j = 0; j < lt.levelStructure[i].size(); j++)
				System.out.println(lt.levelStructure[i].get(j));
		}
	}

}
