import java.util.BitSet;


public class ColumnCombination {

	private String colName;
	private BitSet bitSet;
	
	public ColumnCombination(String n, BitSet bs)
	{
		setColName(n);
		setBitSet(bs);
	}

	public BitSet getBitSet() {
		return bitSet;
	}

	public void setBitSet(BitSet bitSet) {
		this.bitSet = bitSet;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}
}
