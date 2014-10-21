package keyDiscovery;

import java.util.BitSet;

public class KeyDiscovery {
	
	private static Lattice lt;
	
	public void generatePowerLattice(String[] columns)
	{
		int count = 2^(columns.length);
		for (int i = 0; i < count; i ++)
		{
			getColumnCombination(Integer.toBinaryString(i), columns);
		}
		
		
	}

	public String getColumnCombination(String binary, String[] columns)
	{
		String combination;
		for (int i = 0; i < binary.length(); i++)
		{
			if(binary[i])
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String[][] table = new String[][]{
				  { "A", "B", "C", "D" },
				  { "1", "2", "4", "1" },
				  { "2", "1", "6", "3" },
				  { "2", "3", "4", "1" },
				  { "3", "2", "9", "2" }
				};
		
		lt = new Lattice();
		
	}

}
