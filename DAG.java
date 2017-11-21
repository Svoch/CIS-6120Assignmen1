import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The class to show parent/child relationship between variables based on applied splits.
 * Models a directed acyclic graph.
 * 
 * @author siavashnazari
 *
 * TODO: Make this class an implementation of a more general Interface (Graph?).
 */
public class DAG {

	public ArrayList<Node> node;
	
	/** 
	 * Basic constructor.
	 * @param node		ArrayList of nodes in this DAG.
	 */
	public DAG(ArrayList<Node> node) {
		this.node = node;
	}
	/**
	 * Finds a node given its ID.
	 * @param id	the ID to look for
	 * @return Node
	 */
	public Node getNode(int id) {
		for(Node n : node) {
			if(n.getId() == id)
				return n;
		}
		return null;
	}
	/**
	 * Finds a node given its label.
	 * @param lebel		the label to look for
	 * @return Node
	 */
	public Node getNode(String s) {
		for(Node n : node) {
			if(n.getLabel().equals(s))
				return n;
		}
		return null;
	}
	public boolean contains(Node n) {
		for(Node m : node) {
			if(m.getId() == n.getId())
				return true;
		}
		return false;
	}
	/**
	 * Removes node n from the AC
	 * @param n		node to be removed
	 */
	public void delete(Node n) {
		for(Node m : node) {
			if(m.getChild() != null)
				m.getChild().remove(n);
			if(m.getParent() != null)
				m.getParent().remove(n);
		}
		node.remove(n);
	}
	private boolean contains(int id) {
		for(Node n : node) 
			if(n.getId() == id)
				return true;
		return false;

	}
	/**
	 * The string representation of the DAG.
	 * Good for debugging but can get really verbose...
	 */
	public String toString() {
		String result = "DAG :\n";
		for(Node n : node)
			result += n + "\n---------------------\n";
		return result;
	}
	/**
	 * Returns the mutual ancestors of two input nodes
	 */
	public Set<Node> getMutualAncestors(Node d, Node v) {
		Set<Node> result = new HashSet<Node>();
		for(Node n : node) {
			if( isAncestor(n, d) && isAncestor(n, v) )
				if( ! n.equals(v) && !n.equals(d) )
					result.add(n);
		}
		return result;
	}
	/**
	 * Checks if a node is an ancestor of the other in this DAG.
	 * @param ancestor		the ancestor (or allegedly ancestor node)
	 * @param child			the child node
	 */
	public boolean isAncestor(Node ancestor,Node child) {
		if(ancestor.getId()==child.getId())
			return true;
		if(ancestor.getChild()!=null && ancestor.getChild().contains(child))
			return true;
		if(ancestor.getChild()==null || ancestor.getChild().isEmpty())
			return false;
		boolean result = false;
		for(Node ancestors_child : ancestor.getChild())
			result = result || isAncestor(ancestors_child,child);
		return result;
	}
	/**
	 * Makes a clone of this, none of the nodes or the ArrayList of the output share a reference with this.
	 * @return DAG 	a copy of this; no shared references
	 */
	@SuppressWarnings("deprecation")
	public DAG clone() {
		ArrayList<Node> newNode = new ArrayList<Node>(node.size());
		for(int i=0; i<node.size(); i++) {
			newNode.add(new Node(node.get(i).getId(),node.get(i).getLabel()));
		}
		DAG result = new DAG(newNode);
		for(Node n: node) {
			if( n.getChild()!= null)
				for(Node m : n.getChild()) {
					result.getNode(n.getId()).addChild(result.getNode(m.getId()));
				}
			if( n.getParent()!= null)
				for(Node m : n.getParent()) {
					result.getNode(n.getId()).addParent(result.getNode(m.getId()));
				}
		}
		
		return result;
	}
	/**
	 * Returns all the variable names modeled by the AC and this DAG. 
	 */
	public String getAllVariableNames() {
		if( node != null ) {
			String result = new String();
			for(Node n : node) {
				result += n.getLabel() + " ";
			}
			result += "\n";
			return result;
		}
		return "";
	}
	/**
	 * Returns the ancestral subgraph given a set of nodes.
	 * Not used in this program...
	 */
	public DAG getAncestralSubgraph(Set<Node> W) {

		DAG temp = this.clone();
		boolean done = false;
		while(done == false) {
			done = true;
			for(Node n: node) {
				//System.out.println("Checking if the node with ID " + n.getId() + " and label " + n.getVar_label() + ".");
				if(temp.contains(n.getId()))
					if( temp.getNode(n.getId()).isLeaf()) {
						//System.out.println("... is a leaf.");
						if(!W.contains(n)) {
							//System.out.println("... is not in W. Removing " + n.getVar_label() + " ...\n" );
							temp.delete(temp.getNode(n.getId()));
							done = false;
						}
					}
			}
		}

		return temp;
	}
	/**
	 * Returns the moral graph of this DAG.
	 * Not used in this program...
	 */
	public DAG getMoralGraph() {

		DAG temp = this.clone();
		for(Node n : node) {
			if(n.getParent()!=null)
				for(Node m : n.getParent()) {

					for(Node o : n.getParent()) {
						if(temp.getNode(m.getId()).getParent()!=null)
							if(!o.equals(n) && !o.equals(m) && !temp.getNode(m.getId()).getParent().contains(o))
								temp.getNode(m.getId()).addParent(o);

					}
				}
		}

		return temp;

	}
	/**
	 * Tests if given sets X and Y in this DAG are uSeparated given sets Z.
	 * Not used in this program...
	 */
	public boolean uSeparated(Set<String> XStr, Set<String> YStr, Set<String> ZStr) {

		DAG temp = this.clone();

		Set<Node> X = new HashSet<Node>();
		Set<Node> Y = new HashSet<Node>();
		Set<Node> Z = new HashSet<Node>();
		
		for(String s : XStr) 
			X.add(temp.getNode(s));
		for(String s : YStr)
			Y.add(temp.getNode(s));
		for(String s : ZStr)
			Z.add(temp.getNode(s));
		
		for(Node n : Z)
			temp.delete(n);

		Set<Node> XPlus = new HashSet<Node>();
		for(Node n: X)
			XPlus.add(n);
		
		System.out.println("Testing for u-Separation... \n ... \n ...");
		System.out.println("At first, X is :");
		for(Node x : XPlus)
			System.out.println(x);
		System.out.println("---------------------");
		
		boolean done = false;
		int num;
		while(done == false) {
			
			done = true;
			num = XPlus.size();
			for(Node n : temp.node) {
				System.out.print("Checking the node with ID " + n.getId() + " and label " + n.getLabel() + "...");
				if(XPlus.contains(n)) {
					if(temp.getNode(n.getId()).getParent()!=null) {
						System.out.println("getPatent() for " + n.getLabel() + " is not null.");
						for(Node p : temp.getNode(n.getId()).getParent()) {
							// parents
							System.out.println("Adding " + p.getLabel() + " to the links...");
							XPlus.add(p);
						}
					
					}					
					if(temp.getNode(n.getId()).getChild()!=null) {
						System.out.println("getChild() for " + n.getLabel() + " is not null.");
						for(Node c : temp.getNode(n.getId()).getChild()) {
							// children
							System.out.println("Adding " + c.getLabel() + " to the links...");
							XPlus.add(c);
						}
						
					}
					
				}
				System.out.println("done.");
			}
			if(num != XPlus.size())
				done = false;
			
		}

		System.out.println("---------------------");

		System.out.println("Now, X+ is :");
		for(Node x : XPlus)
			System.out.println(x);
		System.out.println("---------------------");
		System.out.println("Y was :");
		for(Node y : Y)
			System.out.println(y);
		System.out.println("---------------------");
		System.out.println("Z was :");
		for(Node z : Z)
			System.out.println(z);
		if(Z.isEmpty())
			System.out.println("{}");

		System.out.println();
		System.out.println("=====================");
		
		for(Node x : XPlus)
			if(Y.contains(x))
				return false;
		return true;
	}


	/*
	 * Used for testing and debugging.
	public static void main(String[] args) {

		ArrayList<Node> node = new ArrayList<Node>(6);
		node.add(new Node("e"));
		node.add(new Node("v"));
		node.add(new Node("w"));
		node.add(new Node("h"));
		node.add(new Node("c"));
		node.add(new Node("p"));
		node.add(new Node("k"));
		
		// e
		node.get(0).addChild(node.get(2));

		// v
		node.get(1).addChild(node.get(2));

		// w
		node.get(2).addChild(node.get(4));
		node.get(2).addChild(node.get(5));

		node.get(2).addParent(node.get(0));
		node.get(2).addParent(node.get(1));

		// h
		node.get(3).addChild(node.get(5));

		// c
		node.get(4).addParent(node.get(2));

		// p
		node.get(5).addParent(node.get(2));
		node.get(5).addParent(node.get(3));

		// k
		
		
		DAG dag = new DAG((node));

		// 1)
		HashSet<Node> W = new HashSet<Node>();
		W.add(node.get(0)); // e
		W.add(node.get(1)); // v

		// 2)
		//		W.add(node.get(0)); // e
		//		W.add(node.get(5)); // p
		//		W.add(node.get(3)); // h

		// 3)
		//		W.add(node.get(4)); // c
		//		W.add(node.get(0)); // e
		//		W.add(node.get(2)); // w
		//		W.add(node.get(5)); // p

		// 4)
		//		W.add(node.get(4)); // c
		//		W.add(node.get(0)); // e
		//		W.add(node.get(3)); // h
		//		W.add(node.get(5)); // p


		System.out.println(dag);
		System.out.println("\n====================\n");

		System.out.println("isAncestor(e,v): " + dag.isAncestor(dag.getNode("e"),dag.getNode("v")) );
		System.out.println("isAncestor(v,e): " + dag.isAncestor(dag.getNode("v"),dag.getNode("e")) );
		
		System.out.println("isAncestor(e,w): " + dag.isAncestor(dag.getNode("e"),dag.getNode("w")) );
		System.out.println("isAncestor(w,e): " + dag.isAncestor(dag.getNode("w"),dag.getNode("e")) );

		System.out.println("isAncestor(e,p): " + dag.isAncestor(dag.getNode("e"),dag.getNode("p")) );
		System.out.println("isAncestor(p,e): " + dag.isAncestor(dag.getNode("p"),dag.getNode("e")) );

		System.out.println("isAncestor(v,c): " + dag.isAncestor(dag.getNode("v"),dag.getNode("c")) );

		System.out.println("isAncestor(h,p): " + dag.isAncestor(dag.getNode("h"),dag.getNode("p")) );

		System.out.println("isAncestor(h,k): " + dag.isAncestor(dag.getNode("h"),dag.getNode("k")) );
		System.out.println("isAncestor(k,e): " + dag.isAncestor(dag.getNode("k"),dag.getNode("e")) );

		for(Node n : node) {
			System.out.println("k is an ancestor for " + n.getLabel() + " ? " + dag.isAncestor(dag.getNode("k"), dag.getNode(n.getId())));
			System.out.println( n.getLabel() + " is an ancestor for k ? " + dag.isAncestor(dag.getNode("k"), dag.getNode(n.getId())));
		}
		
		System.out.println("Mutual Ancestors of p and c :");
		for(Node n : dag.getMutualAncestors(dag.getNode("c"),dag.getNode("p")))
			System.out.println(n);
		
		System.out.println("Deleting w: ");
		dag.delete(dag.getNode("e"));
		System.out.println(dag);
		
	}
	*/
	
	
}
