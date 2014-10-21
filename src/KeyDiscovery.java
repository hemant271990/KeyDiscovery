
import java.util.BitSet;

public class KeyDiscovery {
	
	private Lattice lt;
	
	public void generatePowerLattice(String[] columns)
	{
		int count = (int) Math.pow(2, columns.length);
		//System.out.println(columns[3].toString());
		String combination;
		for (int i = 1; i < count; i ++)
		{
			combination = getColumnCombination(Integer.toBinaryString(i), columns);
			//System.out.println(combination);
			//int level = combination.length() - 1;
			//lt.levelStructure[level].add(combination);
		}
		
		
	}

	public String getColumnCombination(String binary, String[] columns)
	{
		System.out.println(binary);
		String combination = "";
		for (int i = 0; i < binary.length(); i++)
		{
			if(binary.charAt(i) == '1')
				combination += columns[i];
		}
		return combination;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		KeyDiscovery kd = new KeyDiscovery();
		
		String[][] table = new String[][]{
				  { "A", "B", "C", "D" },
				  { "1", "2", "4", "1" },
				  { "2", "1", "6", "3" },
				  { "2", "3", "4", "1" },
				  { "3", "2", "9", "2" }
				};
		
		kd.lt = new Lattice(table[0].length);
		kd.generatePowerLattice(table[0]);
		
//		for(int i = 0; i < table[0].length; i++)
//		{
//			for(int j = 0; j < kd.lt.levelStructure[i].size(); j++)
//				System.out.println(kd.lt.levelStructure[i].get(j));
//		}
	}

}
