

import java.util.ArrayList;

public class Lattice {

	public ArrayList<String>[] levelStructure ;
	
	@SuppressWarnings("unchecked")
	public Lattice(int levels)
	{
		levelStructure = (ArrayList<String>[])new ArrayList[levels];
		for(int i = 0; i < levels; i++)
			levelStructure[i] = new ArrayList<String>();
	}

	public ArrayList<String>[] getLevelStructure() {
		return levelStructure;
	}

	public void setLevelStructure(ArrayList<String>[] levelStructure) {
		this.levelStructure = levelStructure;
	}
}
