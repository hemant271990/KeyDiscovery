import java.util.BitSet;


public class ColumnCombination {

	private String colName;
	private BitSet bitSet;
	private float expectedGain;
	private boolean pruned = false;
	
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
	
	public void setExpectedGain(float gain) {
		this.expectedGain = gain;
	}
	
	public float getExpectedGain() {
		return expectedGain;
	}

	public boolean isPruned() {
		return pruned;
	}

	public void setPruned(boolean pruned) {
		this.pruned = pruned;
	}
	
}
