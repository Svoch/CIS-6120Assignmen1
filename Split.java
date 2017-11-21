
public class Split {
	
	private int d;
	private int v;
	
	public static int[][] data;
	public static int[] schema;
	public static DAG dag;
	public static Circuit circuit;
	
	public Split(int d, int v) {
		this.d  = d;
		this.v = v;
	}
	
	public static boolean isValid(int d,int v/*, DAG dag*/) {
		if( dag.isAncestor(  dag.getNode(d)  , dag.getNode(v) ) )
			return false;
		if( dag.getNode(v).isParent( dag.getNode(d) ))
			return false;
		return true;
	}
	
	public int getVar() {
		return v;
	}
	public void setVar(int var) {
		this.v = var;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}
	
	
	
	
	
}
