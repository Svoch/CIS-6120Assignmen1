import java.util.HashSet;
import java.util.Set;

public class ConstNode extends Node {

	@Deprecated
	public ConstNode() {
		super();
		this.setType(NodeType.ConstNode);
		this.dag_parents = new HashSet<VarNode>();
	}	
	
	@Deprecated
	public ConstNode(int id, String label) {
		super(id,label);
		this.setType(NodeType.ConstNode);
		this.dag_parents = new HashSet<VarNode>();
	}
	public ConstNode clone() {
		ConstNode clone = new ConstNode(this.getId(),this.getLabel());
		clone.setVar(var);
		clone.setValue(value);
		HashSet<VarNode> clone_dag_parents = new HashSet<VarNode>();
		clone_dag_parents.addAll(this.dag_parents);
		clone.dag_parents = clone_dag_parents;
		clone.setCoordinate(this.getCoordinate());
		return clone;
	}	
	public ConstNode(String label) {
		super(label);
		this.setType(NodeType.ConstNode);
		this.dag_parents = new HashSet<VarNode>();
	}
	
	public ConstNode(float value,int var, int state) {
		super();
		this.var = var;
		this.state = state;
		this.value = value; //(float) Math.exp(value);
		this.setType(NodeType.ConstNode);
		this.dag_parents = new HashSet<VarNode>();
	}

	private int var;
	private int state;
	private float value;
	
	private Set<VarNode> dag_parents;
	
	public float getValue() {
		return value;
	}
	public void setValue(float var_value) {
		this.value = var_value;
	}

	public String toString() {
		String result =  super.toString() + "\n value is: " + value + "\n DAG parents: ";
		for(VarNode vn : dag_parents)
			result  += vn.getLabel() + " ";
		result += "\n";
		return result;
	}
	public int getVar() {
		return var;
	}

	public void setVar(int var) {
		this.var = var;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public float compute(int[] dataline) {
		return value;
	}

	public Set<VarNode> getDag_parents() {
		return dag_parents;
	}

	public void addDag_parent(VarNode dag_parent) {
		this.dag_parents.add(dag_parent);
	}
	
	public void initializeValue(int[][] data, int[] schema) {
		
		// TODO: what?!... this function clearly does not belong here!
		// TODO: move this to Learner.
		boolean[] condition = new boolean[data[0].length];

		condition[var] = true;
	
//		System.err.println("Condition for P(var_" + var + "=" + state + ") is: ");
//		for(int i=0; i<condition.length; i++)
//			System.err.print(condition[i] + " ");
//		System.err.println();
		
		int[] pattern = new int[schema.length];
		for(int i=0; i<pattern.length; i++)
			pattern[i] = -1;
		pattern[var] = state;
		for(VarNode vn : dag_parents) {
			pattern[vn.getVar()] = vn.getValue();
		}
		
//		System.err.println("Pattern for P(var_" + var + "=" + state + ") is: ");
//		for(int i=0; i<pattern.length; i++)
//			System.err.print(pattern[i] + " ");
//		System.err.println();
//		System.err.println("---------------------");

		int count = 0;
		boolean add = false;
		for(int i=0; i<data.length; i++) {
			add = true;
//			System.err.println("dataline[" + i + "] is: ");
			for(int j=0; j<schema.length; j++) {
//				System.err.print(data[i][j] + " ");
				if(pattern[j] != -1)
					if(data[i][j] != pattern[j]) {
						add = false;
						continue;
					}
			}
			if(add)
				count++;
		}
		
//		System.err.println("---------------------");
//		System.err.println("Count for P(var_" + var + "=" + state + ") is: " + count);
//		System.err.println("*********************");

		value = (float) count/data.length;
		
	}
	
}