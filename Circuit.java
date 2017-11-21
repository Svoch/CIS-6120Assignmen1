
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The class to represent an Arithmetic Circuit.
 * Technically the DAG class added new methods.
 * 
 * @author siavashnazari
 *
 * TODO: Make this class an implementation of a more general Interface (Graph?).
 */
public class Circuit {

	/**
	 * List of nodes in the AC.
	 */
	public ArrayList<Node> node;
	/**
	 * The root; used for calculations of probabilities with AC and drawing. (only these!)
	 */
	public Node root;
	
	
	public Circuit(ArrayList<Node> node) {
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
	 * @return Node
	 * @Deprecated: Since there maybe several nodes of same title in an AC.
	 */
	@Deprecated
	public Node getNode(String s) {
		for(Node n : node) {
			if(n.getLabel().equals(s))
				return n;
		}
		return null;
	}

	/**
	 * Checks if a node equal to input is present in the AC or not.
	 * @param n		the node to check for its presence
	 * @return Node
	 */
	public boolean contains(Node n) {
		for(Node m : node) {
			if(m.getId() == n.getId())
				return true;
		}
		return false;
	}

	/**
	 * Makes a clone of this, none of the nodes or the ArrayList of the output share a reference with this.
	 * @return Circuit 	a copy of this; no shared references
	 * 
	 * Writes in System.err if an AC with a node of type NullNode is being cloned.
	 */
	public Circuit clone() {

		ArrayList<Node> newNode = new ArrayList<Node>(node.size());
		for(int i=0; i<node.size(); i++) {
			switch (node.get(i).getType()) {
			case VarNode:
				VarNode v = (VarNode) node.get(i);
				newNode.add( v.clone() );
				break;
			case ConstNode:
				ConstNode c = (ConstNode) node.get(i);
				newNode.add( c.clone() );
				break;
			case TimesNode:
				TimesNode t = (TimesNode) node.get(i);
				newNode.add( t.clone() );
				break;
			case PlusNode:
				PlusNode p = (PlusNode) node.get(i);
				newNode.add( p.clone() );
				break;
			default:
				Node n = node.get(i);
				System.err.println("Error in Circuit.clone(). Node " + n.getLabel() + " (ID=" + n.getId() + ") is of NullType." );
				break;
			}
			
		}
		
		Circuit result = new Circuit(newNode);
		
		for(Node n: node) {
			if( n.getChild()!= null)
				for(Node m : n.getChild()) {
					result.getNode(n.getId()).addChild(result.getNode(m.getId()));
				}
			if( n.getParent()!= null)
				for(Node m : n.getParent()) {
					result.getNode(m.getId()).addChild(result.getNode(n.getId())); 
				}
		}
		
		result.root = result.getNode(root.getId());
		return result;

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
	/**
	 * The string representation of the AC.
	 * Good for debugging but can get really verbose...
	 */
	public String toString() {
		String result = "Circuit :\n";
		for(Node n : node)
			result += n + "\n---------------------\n";
		return result;
	}
	/**
	 * Copies the given ArrayList of nodes and adds all the links they may have in this AC.
	 * @param N
	 * @deprecated since clone does the same.
	 */
	@Deprecated
	public ArrayList<Node> copy(ArrayList<Node> N) {
		
		ArrayList<Node> newNode = new ArrayList<Node>();
		for(int i=0; i<node.size(); i++) {
			switch (node.get(i).getType()) {
			case VarNode:
				VarNode v = (VarNode) node.get(i);
				if( N.contains(v) )
					newNode.add( v.clone() );
				break;
			case ConstNode:
				ConstNode c = (ConstNode) node.get(i);
				if( N.contains(c) )
					newNode.add( c.clone() );
				break;
			case TimesNode:
				TimesNode t = (TimesNode) node.get(i);
				if( N.contains(t) )
					newNode.add( t.clone() );
				break;
			case PlusNode:
				PlusNode p = (PlusNode) node.get(i);
				if( N.contains(p) )
					newNode.add( p.clone() );
				break;
			default:
				Node n = node.get(i);
				System.err.println("Error in Circuit.clone(). Node " + n.getLabel() + " (ID=" + n.getId() + ") is of NullType." );
				break;
			}
			
		}
		
		Circuit result = new Circuit(newNode);
		
		for(Node n: N) {
			if( n.getChild()!= null)
				for(Node m : n.getChild()) {
					if(result.getNode(m.getId())!= null && (result.getNode(n.getId())!= null) )
							result.getNode(n.getId()).addChild(result.getNode(m.getId()));
				}
			if( n.getParent()!= null)
				for(Node m : n.getParent()) {
					if(result.getNode(m.getId())!= null && (result.getNode(n.getId())!= null) )
							result.getNode(n.getId()).addParent(result.getNode(m.getId()));
				}
		}
		
		return (result.node);
		
	}
	
	/**
	 * Returns nodes that are corresponding to a distribution.
	 * i.e. They are sums of products of only variable and indicator nodes.
	 */
	/*
	public ArrayList<Node> all_var_dists() {
		ArrayList<Node> result = new ArrayList<Node>();
		for( Node n : node ) 
			if( n.is_sot_dist() )
				result.add(n);
		return result;
	}
	 */
	/**
	 * Returns all indicator nodes; redundant...
	 */
	public ArrayList<Node> flat_vnodes() {
		
		ArrayList<Node> result = new ArrayList<Node>();
		for(Node n : node) {
			if(n.is_of_type(NodeType.VarNode))
				result.add(n);
		}
	
		return result;
	}
	

	// define a class for probability,
	// define prior probabilities based on counts in data
	// infer schema from data... or input it
	// build 'empty' circuit with these
	
	// Splits; how do you do them?
	
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
	 * Returns all indicator nodes in this AC.
	 */
	public Set<Node> getVarNodes() {
		HashSet<Node> result = new HashSet<Node>();
		for(Node n : node)
			if( n.is_of_type(NodeType.VarNode) )
				result.add(n);
		return result;
	}
	/**
	 * Returns all indicator nodes in this AC for input variable number.
	 */
	public Set<Node> getVarNodes(int v) {
		HashSet<Node> result = new HashSet<Node>();
		for(Node n : node)
			if( n.is_of_type(NodeType.VarNode) )
				if( ((VarNode) n).getVar()==v )
					result.add(n);
		return result;
	}
	/**
	 * Returns all parameter nodes in this AC.
	 */
	public Set<Node> getConstNodes() {
		HashSet<Node> result = new HashSet<Node>();
		for(Node n : node)
			if( n.is_of_type(NodeType.ConstNode) )
				result.add(n);
		return result;
	}
	
	/**
	 * Checks if a node is an ancestor of the other in this AC.
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
	 * Returns an indicator node based on its variable and state.
	 * WARNING: There may be more than one matching node in the AC but this method returns only one of them.
	 * @param v		the variable number of the parameter node to retrieve
	 * @param i		the state of the corresponding variable
	 */
	public VarNode getVarNode(int v, int i) {
		for(Node n : node) {
			if(n.is_of_type(NodeType.VarNode)) {
				VarNode vn = (VarNode) n ;
				if( vn.getVar()==v && vn.getValue()==i  )
					return vn;
			}
		}
		return null;
	}
	/**
	 * Returns the parameter node based on its variable and state.
	 * @deprecated and is only used in debugging 
	 * @param d		the variable number of the parameter node to retrieve
	 * @param j		the state of the corresponding variable
	 */
	@Deprecated
	public ConstNode getConstNode(int d, int j) {
		for(Node n : node) {
			if(n.is_of_type(NodeType.ConstNode)) {
				ConstNode cn = (ConstNode) n ;
				if( cn.getVar()==d && cn.getState()==j  )
					return cn;
			}
		}
		return null;
	}
	/**
	 * Returns number of edges in this AC.
	 */
	public int getNumberOfEdges() {
		int result = 0;
		for(Node n : node)
			result += n.getChild().size() + n.getParent().size();
		return result / 2;
	}
	/**
	 * Checks if two ACs are equal or not.
	 * Only used in debugging.
	 * @param copy		Circuit to check its equality with this AC
	 * @override 
	 */
	public boolean equals(Circuit copy) {
		for(Node n : node) {
			Node n_copy = copy.getNode(n.getId());
			if(n_copy==null || !n_copy.equals(n)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Removes some violations in a smooth, decomposable and deterministic AC.
	 * e.g.
	 * If a leaf is not a parameter or indicator node, it should be removed.
	 * If the root is a parameter or indicator node, it should be removed.
	 */
	public void pruneNodes()	{
		HashSet<Node> goners = new HashSet<Node>();
		for(Node n : node) 
			if(n.is_of_type(NodeType.PlusNode) || n.is_of_type(NodeType.TimesNode)) {
				if(n.isLeaf()) {
					goners.add(n);
				}
			} else if(n.is_of_type(NodeType.ConstNode) || n.is_of_type(NodeType.VarNode)) {
				if(n.isRoot()) {
					goners.add(n);
				}
			}
		node.removeAll(goners);
		}
	/**
	 * Removes all links from nodes in this AC to any node not present in it.
	 */
	public void pruneEdges() {
		for(Node n : node) {
			HashSet<Node> goner_children = new HashSet<Node>();
			for(Node nc : n.getChild())
				if(!node.contains(nc))
					goner_children.add(nc);
			n.getChild().removeAll(goner_children);
			HashSet<Node> goner_parents = new HashSet<Node>();
			for(Node np : n.getParent())
				if(!node.contains(np))
					goner_parents.add(np);
			n.getParent().removeAll(goner_parents);
		}
	}
	/**
	 * Removes all links from this AC. 
	 * After this there will be no edge left.
	 */
	public void removeRelationships() {
		for(Node n : node) {
			n.setChild(new ArrayList<Node>());
			n.setParent(new ArrayList<Node>());
		}
	}
	/**
	 * Removes from all edges in nodes in input. Any other link (i.e. between nodes not in N) remains. 
	 * @param N		ArrayList of nodes
	 */
	public void removerOtherRelationships(ArrayList<Node> N) {
		for(Node n : node)
			for(Node n_copy : N) {
				if(n_copy.equals(n)) {
					n.setChild(new ArrayList<Node>());
					n.setParent(new ArrayList<Node>());
				}
			}	
	}
	/**
	 * Adds a set of nodes to this AC.
	 * Never used, aside from debugging... Can be removed I suppose.
	 * @param nodes		set of nodes to add to this AC
	 */
	public void addNodes(Set<Node> nodes) {
		node.addAll(nodes);
		//reID();
	}

	/**
	 * Reassigns a unique ID to every node in the AC.
	 * Used to maintain meaningful IDs after addition of nodes with potential duplicate IDs to AC.
	 */
	public void reID() {
		for(int i=0; i<node.size(); i++)
			node.get(i).setID(i);
		Node.id_max = node.size();
	}
	/**
	 * Adds to this all nodes that are children or parents (not descendants or ancestors) of the nodes in AC.
	 * Not effectively used... But to use it, it may be needed to put it in some loop. 
	 */
	public void absorbFamily() {
		Set<Node> family = new HashSet<Node>();
		for(Node n : node) {
			for(Node nc : n.getChild())
				if(!node.contains(nc))
					family.add(nc);
			for(Node np : n.getParent())
				if(!node.contains(np))
					family.add(np);
		}
		addNodes(family);
	}
	/**
	 * Removes all nodes from the ArrayList node in the AC that have no link to any other node.
	 */
	public void deleteOrphans() {
		Set<Node> goners = new HashSet<Node>();
		for(Node n : node)
			if(n.isOrphan())
				goners.add(n);
		for(Node n : goners)
			node.remove(n);
	}
	/**
	 * Checks if the AC has more than one root or not.
	 * Only used for debugging.
	 */
	public boolean isSingleRooted()	{
		boolean isRootSeen = false;
		for(Node n : node)
			if(n.isRoot()) {
				if(!isRootSeen)
					isRootSeen = true;
				else
					return false;
			}
		return true;
	}
	/**
	 * Removes all nodes that are not a descendant of the root of this AC from it.
	 */
	public void removeNonDecendantsOfTheRoot() {
		HashSet<Node> goners = new HashSet<Node>();
		for(Node goner : node) 
			if(!isAncestor(root, goner))
				goners.add(goner);
		node.removeAll(goners);
		
	}
	/**
	 * Removes a node from AC and all its links to any node.
	 */
	public void remove(Node n) {
		n.setChild(new ArrayList<Node>());
		n.setParent(new ArrayList<Node>());
		
		removeNonDecendantsOfTheRoot();
	}
	/**
	 * Computes the value being hold in the root given input data. 
	 * @param dataline		array of an instantiation of variables.
	 * @return float		the calculated amount in root node, based on input variables
	 */
	public float compute(int[] dataline) {
		return root.compute(dataline);
	}
	/**
	 * Computes the likelihood of input data. For each line in data, corresponding to an instantiation of variables, computes the root.
	 * For each line, the computed amount by AC is multiplied by the number of occurrences of that instantiation.
	 * Summing up the results generates the likelihood of the input data.
	 * @param data			input data
	 * @param schema		the schema for given data - domains of variables
	 * @return double		likelihood of the input data given current AC
	 */
	public double computeDataLikelihood(int[][] data,int[] schema) {
		
		double result = 0;
		
		double[] log = new double[data.length];
		for(int i=0; i<data.length; i++)
			log[i] +=  Math.log( (double) root.compute(data[i]) );
		
		int[] pattern;
		int count = 0;
		boolean add = false;
		for(int k=0; k<data.length; k++) {
			pattern = data[k];
			for(int i=0; i<data.length; i++) {
				add = true;
				for(int j=0; j<data[i].length; j++) {
					if(pattern[j] != -1 && data[i][j] != pattern[j])
						add = false;
				}
				if(add)
					count++;
			}
			result += ( count * log[k] ); 
		}

		return result;
		
	}
	/**
	 * Returns number of parameter nodes in AC.
	 */
	public int getNumberOfParamerets() {
		int result = 0;
		for(Node n : node)
			if(n.is_of_type(NodeType.ConstNode))
				result++;
		return result;
	}
	/**
	 * Returns number of variables modeled by this AC.
	 */
	public int getNumberOfVariables() {
		int result = 0;
		for(Node n : node)
			if(n.is_of_type(NodeType.VarNode))
				result++;
		return result;
	}
	/**
	 * Returns all indicator nodes that are descendants of input in this AC.
	 * @param c		input node
	 */
	public Set<VarNode> getVarNodeDescendants(Node c) {
		HashSet<VarNode> result = new HashSet<VarNode>();
		for(Node n : node)
			if(isAncestor(c, n))
				if(n.is_of_type(NodeType.VarNode))
					result.add((VarNode)n);
		return result;
	}
	
	
	/*
	public static void main(String[] args) {
		
		ConstNode cold_0 = new ConstNode("P_cold_0");
		cold_0.setValue(0.4f);
		ConstNode cold_1 = new ConstNode("P_cold_1");
		cold_1.setValue(0.6f);
		ConstNode light_0 = new ConstNode("P_light_0");
		light_0.setValue(0.3f);
		ConstNode light_1 = new ConstNode("P_light_1");
		light_1.setValue(0.7f);
		VarNode var_cold_0 = new VarNode(0, 0);
		var_cold_0.setLabel("I_cold_0");
		VarNode var_cold_1 = new VarNode(0, 1);
		var_cold_1.setLabel("I_cold_1");
		VarNode var_light_0 = new VarNode(1, 0);
		var_light_0.setLabel("I_light_0");
		VarNode var_light_1 = new VarNode(1, 0);
		var_light_1.setLabel("I_light_1");
		
		PlusNode p1 = new PlusNode();
		PlusNode p2 = new PlusNode();
		TimesNode t1 = new TimesNode(); 
		TimesNode t2 = new TimesNode(); 
		TimesNode t3 = new TimesNode(); 
		TimesNode t4 = new TimesNode(); 
		TimesNode t5 = new TimesNode(); 
		
		
		ArrayList<Node> node = new ArrayList<Node>();
		node.add(cold_0);
		node.add(cold_1);
		node.add(light_0);
		node.add(light_1);
		node.add(var_cold_0);
		node.add(var_cold_1);
		node.add(var_light_0);
		node.add(var_light_1);
		node.add(t1);
		node.add(t2);
		node.add(t3);
		node.add(t4);
		node.add(t5);
		node.add(p1);
		node.add(p2);
		
		Circuit ac = new Circuit(node);
		
		t1.addChild(cold_0);
		t1.addChild(var_cold_0);
		
		t2.addChild(cold_1);
		t2.addChild(var_cold_1);
		
		t3.addChild(light_0);
		t3.addChild(var_light_0);
		
		t4.addChild(light_1);
		t4.addChild(var_light_1);
		
		t1.addParent(p1);
		t1.addParent(p1);
		t2.addParent(p1);
		
		p2.addChild(t3);
		p2.addChild(t3);
		p2.addChild(t4);
		
		t5.addChild(p1);
		p2.addParent(t5);
		
		System.out.println(ac.toString());
		
		Circuit ac_clone = ac.clone();
		
		System.out.println("--------------------- \n--------------------- \nCloning... \n  ");

		System.out.println(ac_clone);

		File dataFile = new File("/Users/siavashnazari/Desktop/cold.data");
		File schemaFile = new File("/Users/siavashnazari/Desktop/cold.schema");

//		Distribution.readSchema(schemaFile);
//		Distribution.readData(dataFile);
		
		
		Node n = new Node(1,"s");
		Node m = new Node(1,"s");
		n.addChild(t5);
		m.addChild(t5);
		m.removeChild(t5);
		n.addParent(m);
		System.out.println("n.equals(m) = " + n.equals(m));
		
		System.out.println("ac_clone.equals(ac) = " + ac_clone.equals(ac));
		
		System.out.println("--------------------- \n--------------------- \n");
		
		ArrayList<Node> test_nodes = new ArrayList<Node>();
		test_nodes.add(t1);
		test_nodes.add(n.clone());
		test_nodes.add(n.clone());
		Circuit test = new Circuit(test_nodes);
		
		test.reID();
		
		test.pruneNodes();
		
		test.absorbFamily();
		System.out.println(test);
		
		System.out.println("is first node in test a root? " + test.node.get(0).isRoot());
		
		System.out.println("is first node in test an orphan? " + test.node.get(0).isOrphan());
		
		System.out.println("is test singleRooted? " + test.isSingleRooted() );
		
	}
	*/
	
}
