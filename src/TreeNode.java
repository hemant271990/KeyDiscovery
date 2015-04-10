import java.util.ArrayList;
import java.util.Hashtable;


public class TreeNode {

	private TreeNode rightChild = null;
	private TreeNode leftChild = null;
	private float currentProbabilityProduct = 1;
	private ArrayList<String> goldenSet = new ArrayList<String>();
	private Hashtable<String, Integer> completeSet = new Hashtable<String, Integer>();
	
	public TreeNode(ArrayList<String> gs, Hashtable<String, Integer> cs)
	{
		setGoldenSet(gs);
		setCompleteSet(cs);
	}

	public ArrayList<String> getGoldenSet() {
		return goldenSet;
	}

	public void setGoldenSet(ArrayList<String> goldenSet) {
		this.goldenSet = goldenSet;
	}

	public Hashtable<String, Integer> getCompleteSet() {
		return completeSet;
	}

	public void setCompleteSet(Hashtable<String, Integer> completeSet) {
		this.completeSet = completeSet;
	}

	public TreeNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(TreeNode rightChild) {
		this.rightChild = rightChild;
	}

	public TreeNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(TreeNode leftChild) {
		this.leftChild = leftChild;
	}

	public float getCurrentProbabilityProduct() {
		return currentProbabilityProduct;
	}

	public void setCurrentProbabilityProduct(float currentProbabilityProduct) {
		this.currentProbabilityProduct = currentProbabilityProduct;
	}
}
