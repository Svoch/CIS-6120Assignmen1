public class TimesNode extends Node {

	public TimesNode() {
		super();
		this.setType(NodeType.TimesNode);
		this.setLabel("*");
	}

	@Deprecated
	public TimesNode(int id) {
		super(id,"*");
		this.setType(NodeType.TimesNode);
		this.setLabel("*");
	}
	public TimesNode clone() {
		TimesNode clone = new TimesNode(this.getId());
		clone.setCoordinate(this.getCoordinate());
		return clone;
	}
	
	public static void main(String[] args) {
		

		@SuppressWarnings("deprecation")
		VarNode cold = new VarNode("cold");
		@SuppressWarnings("deprecation")
		VarNode light = new VarNode("light");
		TimesNode times = new TimesNode();
		
		cold.setValue(2);
		light.setValue(1);
		
		cold.addParent(times);
		times.addChild(cold);
		
		light.addParent(times);
		times.addChild(light);
		
		System.out.println(cold);
		System.out.println("-------------------------------");
		System.out.println(light);
		System.out.println("-------------------------------");
		System.out.println(times);
		System.out.println("-------------------------------");

		
	}
	
	public float compute(int[] dataline) {
		float result = 1;
		for(Node n : getChild()) {
			result *= n.compute(dataline);
		}
//		System.err.println("result in node " + this.getId() + " is " + result);
		return result;
	}

}
