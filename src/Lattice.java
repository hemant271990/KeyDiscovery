

import java.util.ArrayList;

public class Lattice {

	public ArrayList<String>[] levelStructure ;
	
	@SuppressWarnings("unchecked")
	public Lattice(int levels)
	{
		setLevelStructure((ArrayList<String>[])new ArrayList[levels]);
	}

	public ArrayList<String>[] getLevelStructure() {
		return levelStructure;
	}

	public void setLevelStructure(ArrayList<String>[] levelStructure) {
		this.levelStructure = levelStructure;
	}
}
