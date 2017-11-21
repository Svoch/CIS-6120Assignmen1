public class PlusNode extends Node {

	public PlusNode() {
	
		this.setType(NodeType.PlusNode);
		this.setLabel("+");
	}
	@Deprecated
	public PlusNode(int id) {
		super(id,"+");
		this.setType(NodeType.PlusNode);
		this.setLabel("+");
	}
	public PlusNode clone() {
		PlusNode clone = new PlusNode(this.getId());
		clone.setCoordinate(this.getCoordinate());
		return clone;
	}
	
	public static void main(String[] args) {
		
		@SuppressWarnings("deprecation")
		VarNode cold = new VarNode("cold");
		@SuppressWarnings("deprecation")
		VarNode light = new VarNode("light");
		PlusNode plus = new PlusNode();
		
		cold.setValue(2);
		light.setValue(1);
		
		cold.addParent(plus);
		plus.addChild(cold);
		
		light.addParent(plus);
		plus.addChild(light);
		
		System.out.println(cold);
		System.out.println("-------------------------------");
		System.out.println(light);
		System.out.println("-------------------------------");
		System.out.println(plus);
		System.out.println("-------------------------------");

		
	}
	
	public float compute(int[] dataline) {
		float result = 0;
		for(Node n : getChild()) {
			result += n.compute(dataline);
		}
//		System.err.println("result in node " + this.getId() + " is " + result);
		return result;
	}

}
