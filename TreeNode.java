
public class TreeNode {
	String name;
	float probability;
	TreeNode left;
	TreeNode right;
	
	public TreeNode(String n, float p){
		name = n;
		probability = p;
		left = null;
		right = null;
	}
	
	public TreeNode(String n, float p, TreeNode l, TreeNode r){
		name = n;
		probability = p;
		left = l;
		right = r;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public float getProb(){
		return probability;
	}
	
	public void setProb(float p){
		probability = p;
	}
	
	public TreeNode getLeft(){
		return this.left;
	}
	
	public TreeNode getRight(){
		return this.left;
	}
	
	public void setLeft(TreeNode l){
		left = l;
	}
	public void setRight(TreeNode r){
		right = r;
	}
	
	public boolean isLeaf(){
		return (right == null && left == null) ? true : false;
	}
}
