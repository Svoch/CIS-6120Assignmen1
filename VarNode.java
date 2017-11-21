
public class VarNode extends Node {

	@Deprecated
	public VarNode() {
		super();
		this.setType(NodeType.VarNode);
	}

	@Deprecated
	public VarNode(String label) {
		super(label);
		this.setType(NodeType.VarNode);
	}

	@Deprecated
	public VarNode(int id, String label) {
		super(id,label);
		this.setType(NodeType.VarNode);
	}
	
	public VarNode(int var, int value) {
		super();
		this.setType(NodeType.VarNode);
		this.var = var;
		this.value = value;
	}

	private int var;
	private int value;

	public int getValue() {
		return value;
	}
	public void setValue(int var_value) {
		this.value = var_value;
	}
	
	public VarNode clone() {
		VarNode clone = new VarNode(this.getId(),this.getLabel());
		clone.setVar(var);
		clone.setValue(value);
		clone.setCoordinate(this.getCoordinate());
		return clone;
	}
	
	public static void main(String[] args) {
		

		VarNode cold = new VarNode("cold");
		VarNode light = new VarNode("light");
		VarNode sneeze = new VarNode("sneeze");
		
		cold.setValue(2);
		light.setValue(1);
		
		sneeze.setValue(1);
		
		
		cold.addChild(sneeze);
		sneeze.addParent(cold);
		
		light.addChild(sneeze);
		sneeze.addParent(light);
		
		System.out.println(cold);
		System.out.println("-------------------------------");
		System.out.println(light);
		System.out.println("-------------------------------");
		System.out.println(sneeze);
		System.out.println("-------------------------------");

		
	}

	public int getVar() {
		return var;
	}
	public void setVar(int var) {
		this.var = var;
	}
	public String toString() {
		return super.toString() + "\n (Variable,Value) = (" + var + " , " + value +")";
		
	}
	
	public float compute(int[] dataline) {
//		boolean I = (dataline[var]==value) ? true:false ;
//		System.err.println("computin for dataline:\t(var,value)=("+var+","+value+") ->" + I );
		if( dataline[var] == value )
			return 1;
		return 0;
	}

}
