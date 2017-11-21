import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The class to represent nodes in an AC or a DAG.
 * Each node stores its neighbors as parents and children, making it suitable for directed graphs.
 * All nodes store a unique ID, a label and also, the type of the node is being stored for use in ACs.
 * 
 * In order to use nodes for graphical representation of graphs, their coordinates and levels in a DAG are stored,
 * as well as the edge_lenght. 
 * Please note that although DAGs are not necessarily trees, level is defined as a distance from root node to help with graphical representation.
 * 
 * @author siavashnazari
 *
 */
public class Node {

	private NodeType type;
	
	private String label;

	private ArrayList<Node> child;
	private ArrayList<Node> parent;

	private Point coordinate;
	public int level;
	private static int edge_length = 20;
	
	/**
	 * Return the coordinates of the node for use in graphical representation.
	 */
	public Point getCoordinate() {
		return coordinate;
	}
	/**
	 * Assigns new coordinates to the node.
	 * @param coordinate	the point for new coordinates
	 */
	public void setCoordinate(Point coordinate) {
		this.coordinate = coordinate;
	}

	private int id;
	static int id_max = -1;

	/**
	 * Basic constructor, assigns a unique ID to the resulting node.
	 */
	public Node() {
		id = ++id_max;
		this.type = NodeType.NullNode;
		this.parent = new ArrayList<Node>();
		this.child = new ArrayList<>();
		this.coordinate = new Point(500,100);
	}

	/**
	 * Basic constructor, assigns a unique ID and the input label to the resulting node.
	 * @param label		the label for new node
	 */
	public Node(String label) {
		this.label = new String(label);
		id = ++id_max;
		this.type = NodeType.NullNode;
		this.parent = new ArrayList<Node>();
		this.child = new ArrayList<>();
		this.coordinate = new Point(500,100);
	}

	/**
	 * Basic constructor, does NOT assign a unique ID to the resulting node.
	 * It is only used for DAG and cloning.
	 * @deprecated as the new node is not guaranteed to have a unique ID.
	 * 
	 * @param id		the id to be assigned to the new node
	 * @param label		the label for new node
	 */
	@Deprecated
	public Node(int id, String label) {
		this.label = new String(label);
		this.id = id;
		this.type = NodeType.NullNode;
		this.parent = new ArrayList<Node>();
		this.child = new ArrayList<>();
		this.coordinate = new Point(500,100);
	}

	/**
	 * Adds a new child to this node.
	 * TODO: optimize options - set length of ArrayLists first
	 * @param node		the child node to be added
	 */
	public void addChild(Node node) {
		if(child == null)
			child = new ArrayList<Node>();
		
		if(!child.contains(node)) {
			child.add(node);
			// this shouldn't be in the parent list of node yet...
			//if(node.parent != null && node.parent.contains(this))
			//	System.err.println("something is wrong with addChild(). Node " + node.label + " with ID " + node.id + " already has Node " + label + " (ID=" + id + ") as a parent." );
			node.parent.add(this); 
 			/*
 			 * Moved to reCoordinate.			
			node.level = (this.level / 2);
			switch (this.child.size()) {
			case 1:
				node.coordinate = new Point(this.coordinate.x - edge_length * level , this.coordinate.y + edge_length);
				break;
			case 2:
				node.coordinate = new Point(this.coordinate.x + edge_length * level , this.coordinate.y + edge_length);
				break;
			case 3:
				node.coordinate = new Point(this.coordinate.x + edge_length * level , this.coordinate.y - edge_length);
				break;
			default:
				node.coordinate = new Point(this.coordinate.x - edge_length * level , this.coordinate.y - edge_length);
				System.err.println("Node.coordinate: how is it possible?!");
				break;
			}
			*/
		}
	}
	/**
	 * Adds a new child to this node.
	 * TODO: optimize options - set length of ArrayLists first
	 * @param node		the parent node to be added
	 */
	public void addParent(Node node) {
		if(parent == null)
			parent = new ArrayList<Node>();
		
		if(!parent.contains(node)) {
			parent.add(node);
			// this shouldn't be in the parent list of node yet...
			if(node.child != null && node.child.contains(this))
				System.err.println("something is wrong with addParent(). Node " + node.label + " with ID " + node.id + " already has Node " + label + " (ID=" + id + ") as a child." );
			node.child.add(this); 
		}
	}
	/**
	 * Checks if a node is a leaf or not.
	 * TODO: exception handling
	 */
	public boolean isLeaf() {
		if(child != null)
			return child.isEmpty();
		return true;
	}
	/**
	 * Checks if a node is a root node or not.
	 * TODO: exception handling
	 */
	public boolean isRoot() {
		if(parent != null)
			return parent.isEmpty();
		return true;
	}
	/**
	 * Gets all the linked nodes to this node.
	 * @return		the nodes linked to this in a set
	 */
	public Set<Node> getLinks() {
		HashSet<Node> result = new HashSet<Node>();
		if(parent != null)
			result.addAll(parent);
		if(child != null)
			result.addAll(child);
		return result;
	}
	/**
	 * Basic string representation for nodes.
	 * Has type, children, parents and coordinates and its level.
	 */
	public String toString() {
		String result =  label + " / ID: " + id;
		result += "\n Type: " + type.toString();
		result += "\n Children: ";
		if(child != null)
			for(Node n : child)
				result += n.label + " ID: " + n.id + " / " ;
		result += "\n Parents: ";
		if(parent != null)
			for(Node n : parent)
				result += n.label + " ID: " + n.id + " / ";
		result += "\n Coordinate: (" + coordinate.x + "," + coordinate.y + ")" ; 
		result += "\n Level: " + level ;
		return result;
	}
		
	/**
	 * Returns true if the type of the node in an AC or DAG is the same as input type.
	 * @param type		options are: VarNode, ConstNode, TimesNode, PlusNode and NullNode
	 */
	public boolean is_of_type(NodeType type) {
		if(this.type==type)
			return true;
		return false;
	}
	
	/*
	 * Not used anymore.
	public int dist_var() {
		if( ! this.is_sot_dist() ) {
			System.err.println("Error in dist_var()... Node " + label + " (ID=" + id + ") is_not_sot_dist!");
			return -1;
		}
		Node child = this.getChild().get(0);
		for( Node n: child.getChild() ) {
			if( n.is_of_type(NodeType.VarNode) )
				return ((VarNode) n).getVar();
		}
		return -1;
	}
	*/
	/* 
	 * Not used anymore.
	public ArrayList<Node> get_consts() {
		ArrayList<Node> result = new ArrayList<Node>();
		for(Node n : this.getChild()) {
			// TODO: change to exception
			if( ! n.is_of_type(NodeType.TimesNode) ) {
				System.err.println("Error: " + n + " is not a TimesNode!");
			}
			
			for(Node m : n.getChild()) {
				if( m.is_of_type(NodeType.ConstNode) )
					result.add(m);
			}
			
		}
		return result;
	}
	*/
	/*
	 * Not used anymore.
	public boolean is_var_product() {
		boolean result = false;
		if( is_of_type(NodeType.TimesNode) ) {
			boolean var_flag = false;
			boolean const_flag = false;
			for(Node n : this.getChild() ) {
				if(n.is_of_type(NodeType.VarNode) )
					var_flag = true;
				if(n.is_of_type(NodeType.ConstNode) )
					const_flag = true;
			}
			result = var_flag && const_flag;
		}
		return result;
	}
	*/
	/*
	 * Not used anymore.
	public boolean is_sot_dist() {
		boolean result = false;
		if( is_of_type(NodeType.PlusNode) ) {
			boolean times_flag = true;
			for( Node n : this.getChild() )
				if( ! n.is_of_type(NodeType.TimesNode) ) {
					times_flag = false;
					break;
				}
		result = times_flag;
		}
		return result;
	}
	*/
	/**
	 * Returns the label of this node.
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * Sets the label of this node.
	 */
	public void setLabel(String var_label) {
		this.label = var_label;
	}
	/**
	 * Returns the child of this node with given ID.
	 * @param id		the ID for the child node
	 * @return			a node with specified ID or null
	 */
	public Node getChild(int id) {
		for(Node n : child)
			if(n.id == id)
				return n;
		return null;
	}
	/**
	 * Returns the parent of this node with given ID.
	 * @param id		the ID for the parent node
	 * @return			a node with specified ID or null
	 */
	public Node getParent(int id) {
		for(Node n : parent)
			if(n.id == id)
				return n;
		return null;
	}
	/**
	 * Returns all of the children of this node.
	 * @return			an ArrayList of child nodes 
	 */
	public ArrayList<Node> getChild() {
		return child;
	}
	/**
	 * Sets the ArrayList of the children for this node.
	 */
	public void setChild(ArrayList<Node> child) {
		this.child = child;
	}
	/**
	 * Returns all of the parents of this node.
	 * @return			an ArrayList of child nodes 
	 */
	public ArrayList<Node> getParent() {
		return parent;
	}
	/**
	 * Sets the ArrayList of the parents for this node.
	 */
	public void setParent(ArrayList<Node> parent) {
		this.parent = parent;
	}
	/**
	 * Returns the ID for this node.
	 */
	public int getId() {
		return id;
	}
	/**
	 * Returns the type for this node.
	 */
	public NodeType getType() {
		return type;
	}
	/**
	 * Sets the type for this node.
	 */
	public void setType(NodeType type) {
		this.type = type;
	}
	/**
	 * Removes the input node from the list of this node's children.
	 * Also removes this node from the list of parents of the input node.
	 * @param n		node to be removed from the children list
	 */
	public void removeChild(Node n) {
		this.getChild().remove(n);
		n.getParent().remove(this);
	}
	
	/**
	 * Equality check for nodes:
	 * For variable nodes, it suffices that their value and variable they are describing be the same.
	 * For other nodes, if their ID is the same and the list of their children and parents are identical, returns true.
	 * 
	 * TODO: Override equals() in VarNode instead of the if statement.
	 * @param copy		node to check its equality with this node
	 */
	public boolean equals(Node copy) {

		if(this.is_of_type(NodeType.VarNode) && copy.is_of_type(NodeType.VarNode)){
			VarNode v1 = (VarNode) this;
			VarNode v2 = (VarNode) copy;
			if(v1.getVar()==v2.getVar() && v1.getValue()== v2.getValue())
				return true;
			return false;
		}
		
		if(copy.id != id || !copy.label.equals(label))
			return false;
		if(copy.child.size() > child.size())
			return false;
		for(Node n: child) {
			Node c_copy = copy.getChild(n.id);
			if(c_copy==null || c_copy.id!=n.id || !c_copy.label.equals(n.label))
				return false;
		}
		if(copy.parent.size() > parent.size())
			return false;
		for(Node n: parent) {
			Node p_copy = copy.getParent(n.id);
			if(p_copy==null || p_copy.id!=n.id || !p_copy.label.equals(n.label))
				return false;
		}
			
		return true;
	}

	/**
	 * Obtains this node from a set of given nodes.
	 * @param N		the set of nodes to retrieve this node from
	 * @return		this node
	 */
	public Node getNode(Set<Node> N) {
		for(Node n : N) 
			if(this.equals(n))
				return n;
		return null;
	}
	/**
	 * Clones this node.
	 * @deprecated since this method does not creates a node matching in type, children, parents.
	 */
	@Deprecated
	public Node clone() {
		Node clone = new Node(this.getId(),this.getLabel());
		return clone;
	}
	/**
	 * Sets the ID for this node.
	 * @param id		the ID to be set
	 */
	public void setID(int id) {
		this.id = id;
	}
	/**
	 * Checks if this node has any parent or any child.
	 * @return true if this node has no parent and no child
	 */
	public boolean isOrphan() {
		if(getChild().isEmpty() && getParent().isEmpty())
			return true;
		return false;
	}
	/**
	 * Computes the amount in this node given the input instantiation of variables.
	 * Note: Should not be used for Node class. It is defined in the inheriting classes.
	 * @param dataline		an instantiation of variables
	 */
	public float compute(int[] dataline) {
		return -1;
	}
	/**
	 * Checks if this node is a parent of the input node.
	 * @param node		node to check for being a child for this node
	 */
	public boolean isParent(Node node) {
		return child.contains(node);
	}
	/**
	 * Computes the coordinates of this node.
	 * Should be used before attempting to draw the circuit.
	 */
	public void reCoordinate() {
		ArrayList<Node> children = this.getChild();
		for(int i=0; i<children.size(); i++) {
			Node m = children.get(i);
			m.level = this.level / 2;
			switch (i) {
			case 0:
				m.coordinate = new Point(this.coordinate.x - edge_length * level , this.coordinate.y + edge_length);
				break;
			case 1:
				m.coordinate = new Point(this.coordinate.x + edge_length * level , this.coordinate.y + edge_length);
				break;
			case 2:
				m.coordinate = new Point(this.coordinate.x + edge_length * 0 * level , this.coordinate.y + edge_length );
				break;
			case 4:
				m.coordinate = new Point(this.coordinate.x - edge_length * level , this.coordinate.y - edge_length);
				break;
			default:
				m.coordinate = new Point(this.coordinate.x - edge_length * level , this.coordinate.y - edge_length);
				System.err.println("The graphical representaion is going to be messy...");
				break;
			}
		}
		
	}
	/**
	 * Sets the level of this node.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

}
