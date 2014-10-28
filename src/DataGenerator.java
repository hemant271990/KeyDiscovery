import java.util.Hashtable;


public class DataGenerator {
	
	public static final int MAX_COL = 5;
	
	public static final String[] attribute = new String[] {"A", "B", "C", "D", "E"};
	
	public static final int MAX_ROW = 25;
	
	public static final int MAX_KEYS = 1;
	
	public static final int RAND_MAX = 6;
	
	public static String[][] table = new String[MAX_ROW+1][MAX_COL];
	
	public static Hashtable<String, Integer> uniques = new Hashtable<String, Integer>();

	private String[] keys = new String[MAX_KEYS];
	
	public static int tableIndex = 0;
	
	public DataGenerator(String[] uniqueKeys)
	{
		keys = uniqueKeys;
		int keysCount = 0;
		String keyMap = "";
		for(int i = 0; i < uniqueKeys.length; i++)
		{
			for(int j = 0; j < uniqueKeys[i].length(); j++)
			{
				for(int k = 0; k < attribute.length; k++)
				{
					if(attribute[k].compareTo(uniqueKeys[i].charAt(j)+"") == 0)
					{
						keyMap += k;
					}
				}
			}
			keys[keysCount++] = keyMap;
			keyMap = "";
		}
		
		//System.out.println(keys[1]);
		table[tableIndex++] = attribute;
		generateTable();
		printTable();
	}
	
	public void generateTable()
	{
		while(tableIndex < 20)
		{
			
			String[] row = new String[MAX_COL];
			for(int i = 0; i < MAX_COL; i++)
			{
				row[i] = "" + (int) (Math.random() * (RAND_MAX+1));
			}
			
			String toCheck = "";
			for(int j = 0; j < keys.length; j++)
			{
				for(int k = 0; k < keys[j].length(); k++)
				{
					int idx = Integer.parseInt(keys[j].charAt(k) + "");
					toCheck += row[idx];
				}
				
				if(!uniques.containsKey(toCheck))
				{
					table[tableIndex++] = row;
					uniques.put(toCheck, 1);
				}
			}
		}
	}
	
	public void printTable()
	{
		for(int i = 0; i < table.length; i ++)
		{
			for(int j = 0; j < MAX_COL; j++)
			{
				System.out.print(table[i][j] + " ");
			}
			System.out.println();
		}
	}
}
