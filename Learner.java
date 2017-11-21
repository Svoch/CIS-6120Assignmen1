import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


/**
 * Main class to learn an arithmetic circuit based on given data.
 * Implements crucial algorithms for learning such as SplitAC and the basic learning schema.
 * 
 * @author siavashnazari
 *
 */
public class Learner {

	public Circuit circuit = new Circuit(null);
	public DAG dag = new DAG(null);
	public int[][] data;
	public int[] schema;

	
	private static float edge_penalty = 0.1f;
	private static float parameters_penalty = 100.0f;

	private Node[][] vnode;
	
	/*
	 * Not used anymore.
	public void calculateScore() {
		for(Distribution d : distributions) {
			float[] logp = ml_estimate(d.counts());
			node_dl[d.getVar()] = 0;
			for(int i=0; i<logp.length; i++)
				node_dl[d.getVar()] += logp[i];
		}
	}
	*/
	/**
	 * Computes the score for a given AC.
	 * @param circuit		circuit to calculate the score for
	 */
	public double getScore(Circuit circuit) {
		double result = 0;
		
		result = circuit.computeDataLikelihood(data,schema);
		
		int e = circuit.getNumberOfEdges();
		
		int p = circuit.getNumberOfParamerets();
		
		return result - e * edge_penalty - p * parameters_penalty;
	}
	
	/**
	 * Generates the set N; set of nodes between V-ancestors and D-ancestors of given two variable IDs (v and d)
	 */
	public Set<Node> N(int d, int v,Circuit circuit) {
		HashSet<Node> result = new HashSet<Node>();

		Set<Node> v_ancestors = getV_Ancestors(v,circuit);
		Set<Node> d_ancestors = getD_Ancestors(d,circuit);
		Set<Node> mutual_ancestors = getMutualAncestors(d, v,circuit);

		for(Node n : circuit.node) {
			boolean add = false;
			if(v_ancestors.contains(n) || d_ancestors.contains(n)) {
				for(Node m : mutual_ancestors)
					if(circuit.isAncestor(m, n)) {
						add = true;
						break;
					}
			}
			if(add)
				result.add(n);
		}


		return result;
	}

	/**
	 * Gets all mutual ancestors in an AC given two variable IDs
	 */
	public Set<Node> getMutualAncestors(int d,int v,Circuit circuit) {
		HashSet<Node> d_ancestors = (HashSet<Node>) getD_Ancestors(d,circuit);
		HashSet<Node> v_ancestors= (HashSet<Node>) getV_Ancestors(v,circuit);

		HashSet<Node> temp =  new HashSet<Node>();
		for(Node n : d_ancestors)
			if(v_ancestors.contains(n))
				temp.add(n);

		HashSet<Node> result = new HashSet<Node>();

		for(Node n : temp) {
			boolean add = true;
			for(Node m : n.getChild()) {
				if( temp.contains(m) ) {
					add = false;
					break;
				}
				if(add)
					result.add(n);
			}
		}
		return result;
	}

	/**
	 * Gets all D-ancestors in an AC given a variable ID
	 */
	public Set<Node> getD_Ancestors(int d,Circuit circuit) {
		HashSet<Node> temp_d = (HashSet<Node>) circuit.getConstNodes();
		HashSet<Node> temp_v = (HashSet<Node>) circuit.getVarNodes();
		HashSet<Node> d_leaves= new HashSet<Node>();

		HashSet<Node> result = new HashSet<Node>();
		
		for(Node n : temp_d)
			if( ((ConstNode) n).getVar() == d )
				d_leaves.add(n);
		for(Node n : temp_v)
			if( ((VarNode) n).getVar() == d )
				d_leaves.add(n);

		for(Node n : circuit.node)
			for(Node l : d_leaves)
				if( circuit.isAncestor(n, l) )
					result.add(n);

		return result;
	}

	/**
	 * Gets all V-ancestors in an AC given a variable ID
	 */
	public Set<Node> getV_Ancestors(int d,Circuit circuit) {
		HashSet<Node> temp = (HashSet<Node>) circuit.getVarNodes();
		HashSet<Node> v_leaves= new HashSet<Node>();

		HashSet<Node> result = new HashSet<Node>();

		for(Node n : temp)
			if( ((VarNode) n).getVar() == d ) {
				v_leaves.add(n);
			}
		
		for(Node n : circuit.node)
			for(Node l : v_leaves)
				if( circuit.isAncestor(n, l) )
					result.add(n);

		return result;
	}

	/**
	 * It is essentially SplitAC
	 */
	public Circuit applySplit(Split s) {

		int d = s.getD();
		int v = s.getVar();
		Set<Node> M = getMutualAncestors(d, v,circuit);
		Set<Node> setN = N(d,v,circuit);
		Set<Node> vancestors = getV_Ancestors(v,circuit);
		Set<Node> dancestors = getD_Ancestors(d,circuit);

		ArrayList<Node> N = new ArrayList<Node>(setN);
		
		/*
		for(Node n : vancestors)
			System.err.print(n.getId() + " ");
		System.err.println();
		for(Node n : dancestors)
			System.err.print(n.getId() + " ");
		System.err.println();
		*/
		
		Circuit[] Ni = new Circuit[schema[v]];
		
		for(int i=0; i<schema[v]; i++) {
//			WHY YOU COPY circuit?! YOU NEED TO COPY ONLY N!!!!!!!
//			because cloning is better, besides the method below helps
			Ni[i] = circuit.clone();
			
			Ni[i].removerOtherRelationships(N);
//			it's not needed now that we have the method above.
//			Ni[i].removeRelationships();

			for(Node n : N) {
				Node ni = Ni[i].getNode(n.getId());
				for(Node c : n.getChild()) {
					if(isInconsistent(c, v, i) || c.equals(circuit.getVarNode(v, i)))
						continue;
					else if( c.is_of_type(NodeType.ConstNode) ) {
						// we shouldn't condition parameters of any variable but d, on v, right?!
						if( ((ConstNode) c).getVar()==d ) {
							Node ci = Ni[i].getNode(c.getId());
							((ConstNode) ci).setVar(d);
							((ConstNode) ci).setValue(-1);
							((ConstNode) ci).addDag_parent(Ni[i].getVarNode(v, i));
							((ConstNode) ci).initializeValue(data, schema);
							ci.setLabel(ci.getLabel().substring(0, ci.getLabel().length()-1) + "| var_"+v+"="+i+")"  );
							ni.addChild(ci);
						}
					}
					if( N.contains(c) ){
						// I think ni already has ci as a child...
						// I was wrong! :P
						Node ci = Ni[i].getNode(c.getId());
						ni.addChild(ci);
					}
					else {
						ni.addChild(Ni[i].getNode(c.getId()));
					}
				}
			}
			/*
			 * debug
			System.err.println("================");
			System.err.println("================");
			System.err.println("================");
			System.err.println("================");
			System.err.println("Nodes in N" + i + ":" );
			for(Node n : Ni[i].node) {
				if(n.getId()==41)
					System.err.println(n);
			}
			*/
		}
		HashSet<Node> Mi = new HashSet<Node>();
		for(Node m : M) {
			PlusNode pn = new PlusNode();
			Node nv = null, nd = null;
			for(Node mc : m.getChild()) {
				if(dancestors.contains(mc))
					nd = mc;
				if(vancestors.contains(mc))
					nv = mc;
			}
			/*
			 * debug
			if(nd==null)
				System.err.println("nd is null!\n"+nd);
			if(nv==null)
				System.err.println("nv is null!\n"+nv);
			*/
			for(int i=0; i<schema[v]; i++) {
				Node nvi = Ni[i].getNode(nv.getId());
				Node ndi = Ni[i].getNode(nd.getId());
				/*
				 * debug
				for(Node nnv : Ni[i].node)
					if(nnv.getId()==nv.getId())
						System.err.println("fskjdfjksdbfjksdf");
				for(Node nnd : Ni[i].node)
					if(nnd.getId()==nd.getId())
						System.err.println("sdbfkjsdbfjksdbfkjdsb");
				*/
				// debug
				if(ndi==null)
					System.err.println("nd"+i+" is null!");
				if(nvi==null)
					System.err.println("nv"+i+" is null!");
				Node nti = new TimesNode();
				Node vi = circuit.getVarNode(v, i);
				nti.addChild(ndi);
				nti.addChild(nvi);
				
				if(vi==null)
					System.err.println("vi"+i+" is null!");
				
				// be careful!
				nti.addChild( Ni[i].getNode(vi.getId()) );
				pn.addChild(nti);
				// be careful!
				Ni[i].node.add(nti);
				Ni[i].root = nti;

//				m.removeChild(nv);
//				m.removeChild(nd);

				pn.addChild(nti);
				
				/*
				 * debug
				System.err.println("The root in Ni["+i+"]: \n" + Ni[i].root);
				System.err.println("The v"+i+" in Ni["+i+"]: \n" + Ni[i].getNode(vi.getId()) );
				System.err.println("The v"+i+" in circuit: \n" + circuit.getNode(vi.getId()) );
				Ni[i].removeNonDecendantsOfTheRoot();
				Ni[i].pruneNodes();
				System.err.println("Ni["+i+"] is: \n" + Ni[i]);
				System.err.println("getVarNode " + circuit.getVarNode(v, i));
				System.err.println("getNode with id " + circuit.getNode(vi.getId()));
				System.err.println("are they equal? " + ((Object)circuit.getVarNode(v, i)).equals((Object)circuit.getNode(vi.getId())));
				Ni[i].node.add(vi);
				*/
			}
			m.addChild(pn);
			Mi.add(pn);
		}
		for(Node m : M) {
			Node nv = null, nd = null;
			for(Node mc : m.getChild()) {
				if(dancestors.contains(mc))
					nd = mc;
				if(vancestors.contains(mc))
					nv = mc;
			}
			circuit.remove(nd);
			circuit.remove(nv);
			m.removeChild(nd);
			m.removeChild(nv);
		}
		/*
		 * debug
		for(int j=0; j<schema[v]; j++)
			System.err.println(Ni[j].hashCode());
		*/
		for(int i=0; i<schema[v]; i++) {
			Ni[i].pruneNodes();
	
			circuit.addNodes(new HashSet<Node>(Ni[i].node));
			/*
			 * debug
			System.out.println("Nodes in N" + i + ":" );
			for(Node n : Ni[i].node) {
				System.out.println(n);
			}
			*/
		}
		circuit.addNodes(Mi);
		
		circuit.reID();
	
		circuit.removeNonDecendantsOfTheRoot();
		circuit.deleteOrphans();
		
		circuit.reID();

		circuit.pruneEdges();
		
		dag.getNode(d).addParent(dag.getNode(v));
		
		return circuit;
	}

	/*
	private Node generateConstNode(ConstNode cdj, int i) {
		Distribution dij = new Distribution(cdj.getVar());
		boolean[] condition = dij.getCondition();
		condition[i] = true;
		dij.setCondition(condition);
		return cdj;
	}
 	*/

	/**
	 * Checks if node c is inconsistent with indicator node v having state of i
	 */
	public boolean isInconsistent(Node c, int v, int i) {
		/*
		 * Not a good way to do this...
		if(circuit.isAncestor(c, circuit.getVarNode(v,i)))
			return false;
		for(int j=0; j<schema[v]; j++)
			if( j!=i && circuit.isAncestor(c, circuit.getVarNode(v,j)))
				return true;
		return false;
		*/
		Set<VarNode> varNodeDescendants = circuit.getVarNodeDescendants(c);
		for(VarNode n : varNodeDescendants)
			if(n.getVar() == v &&  n.getValue() == i)
				return false;
		for(VarNode n : varNodeDescendants)
			if(n.getVar() == v &&  n.getValue() != i)
				return true;
		return false;
		
	}

	/**
	 * Generates all possible splits based on the DAG.
	 */
	public Set<Split> getPossibleSplits() {

		HashSet<Split> result = new HashSet<Split>();

		Split.dag = dag;

		for(int d=0; d<schema.length; d++)
			for(int v=0; v<schema.length; v++) {
				if(Split.isValid(d, v)) {
					result.add(new Split(d,v));
				}
			}
		return result;
	}

	/*
	public Set<Distribution> createInitialDistributions() {
		ArrayList<Node> nodes = circuit.all_var_dists();
		HashSet<Distribution> result = new HashSet<Distribution>();
		for(Node n: nodes) {
			result.add(makeDistribution(n));
		}
		Distribution.data = data;
		Distribution.schema = schema;
		Distribution.dag = dag;
		distributions = result;
		return result;
	}
	*/
	/*
	public Distribution makeDistribution (Node n) {
		int var = n.dist_var();
		ArrayList<Node> nodes = n.get_consts();
		Distribution result = new Distribution(var,null);
		result.computeLikelihood(nodes);
		return result;
	}
	*/

	/**
	 * Generates maximum likelihood estimates based on counts
	 * 
	 * Not quite a useful method...
	 */
	public float[] ml_estimate(int[] counts) {
		int numvalues = counts.length;
		float prior = 1 / (float) numvalues ;

		int total = 0;
		for(int i=0; i<counts.length; i++)
			total += counts[i];

		float[] result = new float[counts.length];
		double factor;
		for(int i=0; i<result.length; i++) {
			factor = (double) (prior+counts[i]) / (double) total;
			result[i] = (float) Math.log( factor );
		}
		return result;
	}
	
	/**
	 * Generates indicator nodes for given variables based on data.
	 * @return		two dimensional array of nodes
	 */
	public Node[][] generateVarNodes() {
		int number_of_variables = schema.length; 
		Node[][] result = new Node[number_of_variables][];
		for(int i=0; i<number_of_variables; i++) {
			result[i] = new Node[schema[i]];
			for(int j=0; j<result[i].length; j++)
				result[i][j] = new VarNode(i,j);

		}
		vnode = result;
		return result;
	}

	/**
	 * Creates the initial AC equivalent to a BN with no edge.
	 * @return		
	 */
	public Circuit createInitialCircuit() {

		ArrayList<Node> node = new ArrayList<Node>();
		Node[][] vnodes = generateVarNodes();

		Node rn = new TimesNode();

		for(int var=0; var<vnodes.length; var++) {
			int numvals = schema[var];

			Node pn = new PlusNode();

			for(int i=0; i<numvals; i++) {
				ConstNode cn = new ConstNode(-1,var,i);
				cn.setLabel("P(var_"+var+"="+i+")");
				cn.setValue( (float)(1.0 / schema[var]) );
				
				Node vn = vnodes[var][i];
				vn.setLabel("I(var_"+var+"="+i+")");

				// be careful
				cn.addDag_parent((VarNode) vn); 
				
				Node tn = new TimesNode();

				tn.addChild(vn);
				tn.addChild(cn);
				pn.addChild(tn);
				node.add(tn);
				node.add(vn);
				node.add(cn);
			}
			
			rn.addChild(pn);
			node.add(pn);
		}

		node.add(rn);
		
		Circuit c = new Circuit(node);
		c.root = rn;

		Split.circuit = this.circuit;
		
		circuit = c;
		return c;
	}

	/**
	 * Creates the initial DAG to represent parent/child relationships and their changes based on splits.
	 * @return		initial DAG
	 */
	@SuppressWarnings("deprecation")
	public DAG createInitialDAG() {
		ArrayList<Node> node = new ArrayList<Node>();
		for(int i=0; i<vnode.length; i++)
			node.add( new Node(i , "Var_" + Integer.toString(((VarNode) vnode[i][0]).getVar()) ) );
		dag = new DAG(node);

		Split.dag = this.dag;
		
		return dag;
	}

	
	/**
	 * Searches through all currently possible splits and picks the one yielding the best score for the current AC.
	 * @return		the split granting the most score update
	 */
	public Split getBestPossibleSplit(){
		Circuit c = circuit.clone();
		@SuppressWarnings("unused")
		DAG d = dag.clone() , d_;
		Circuit c_;
		Set<Split> validSplits = getPossibleSplits();
		Split bestSplit = null;
		double bestScore = getScore(c), score;
		for(Split s : validSplits) {
			// applying split s; note circuit is changed, and c_ is the new circuit
			c_ = applySplit(s);
			// saving current DAG
			d_ = dag.clone();
			// reverting circuit back to its normal...
			circuit = c.clone();
			// also the DAG
			dag = d.clone();
//			c = circuit.clone();
//			d = dag.clone();
			score = getScore(c_);
			if( score > bestScore ) {
				// score improves...
				// we desire to keep this change...
				bestScore = getScore(c_);
				bestSplit = s;
			}
		}
		
		circuit = c.clone();
		dag = d.clone();
		if(bestSplit!=null) {
			System.out.println("Best Split is Split("+bestSplit.getD()+","+bestSplit.getVar()+"), providing score " + bestScore);
		}
		return bestSplit;
	}
	
	
	/**
	 * The learn algorithm is implemented in this method.
	 * It changes the circuit field in this class as well as returning it.
	 * @return		the circuit that has been learned
	 */
	public Circuit learn() {
		Circuit c = circuit.clone();
		Split bestSplit;
		while(true) {
			bestSplit = getBestPossibleSplit();
			if(bestSplit==null)
				break;
			applySplit(bestSplit);
		}
		return c;
		
	}

	
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		
		Learner l = new Learner();

		File dataFile = new File(args[0]);

		l.readData(dataFile,Integer.parseInt( args[1] ), Integer.parseInt( args[2] ));
		
		// Initializing the circuit
		System.out.println("initial circuit: ");
		l.circuit = l.createInitialCircuit();
		System.out.println(l.circuit);
		System.out.println("=====================");

		sc.next();
		
		System.out.println("initial DAG: ");
		l.dag = l.createInitialDAG();
		System.out.println(l.dag);
		System.out.println("=====================");
		
		sc.next();

		System.out.println("initial possible splits: ");
		Set<Split> splits = new HashSet<Split>();
		splits = l.getPossibleSplits();
		for(Split s : splits) 
			System.out.println("Split(" + s.getD() +"," +s.getVar() +") is valid.");
		System.out.println("=====================");

		Set<Node> d_ancestors = l.getD_Ancestors(0,l.circuit);
		System.out.println("D_Ancestors for nodes with d = " + 0);
		for(Node n : d_ancestors)
			System.out.print(n.getId() + " ");
		System.out.println();
		System.out.println("=====================");
		System.out.println("V_Ancestors for nodes with v = " + 1);
		Set<Node> v_ancestors = l.getV_Ancestors(1,l.circuit);
		for(Node n : v_ancestors)
			System.out.print(n.getId() + " ");
		System.out.println();
		System.out.println("=====================");
		System.out.println("MA nodes for d = " + 0 + " and v = " + 1);
		Set<Node> ma_ancestors = l.getMutualAncestors(0, 1,l.circuit);
		for(Node n : ma_ancestors)
			System.out.println(n);
		System.out.println("=====================");

		System.out.println("N nodes for d = " + 0 + " and v = " + 1);
		Set<Node> n_nodes = l.N(0, 1,l.circuit);
		for(Node n : n_nodes)
			System.out.print(n.getId() + " ");
		System.out.println();
		System.out.println("=====================");
		

		sc.next();
		
		System.out.println("---------------------");
		System.out.println("---------------------");
		System.out.println("---------------------");

//		System.out.println("is 11 inconsistent with 2? " + l.isInconsistent(l.circuit.getNode(11), 1, 0));
	
		System.out.println("*********************");
		System.out.println("=====================");
		System.out.println("*********************");
		
		System.out.println("initializing parameter nodes: ");
		for(Node n : l.circuit.node)
			if(n.is_of_type(NodeType.ConstNode)) {
				((ConstNode)n).initializeValue(l.data,l.schema);
				System.out.println(n);
			}
		
//		System.out.println("likelihood of data[0] is: " + l.circuit.compute(l.data[0]));
//		
//		System.out.println("score is: " + l.getScore());
		
		System.out.println("*********************");
		System.out.println("=====================");
		System.out.println("*********************");
		
		int[][] input = new int[l.data.length][l.schema.length];
		for(int i=0; i<l.data.length; i++)
			input[i] = l.data[i];
		System.out.println("likelihood of data is: " + l.circuit.computeDataLikelihood(input,l.schema));
		
		sc.next();
		
		System.out.println("*********************");
		System.out.println("=====================");
		System.out.println("*********************");
		
		
		// Initializing prior likelihoods for parameters
		System.out.println("Initializing prior likelihoods for parameters: ");
		for(Node n : l.circuit.node)
			if(n.is_of_type(NodeType.ConstNode)) {
			  ((ConstNode) n ).initializeValue(l.data, l.schema);
			  System.out.println(n);
			}
				
		
		
		// examples showing splits
		System.out.println("Applying Split S(1,0) on initial circuit.");
		Circuit c2 = l.circuit.clone();
		for(Split s : l.getPossibleSplits()) {
			if(s.getD()==1 && s.getVar()==0) {
			l.circuit = c2;
			Circuit afterSplit = l.applySplit(s);
			System.out.println("*********************");
			System.out.println("=====================");
			System.out.println("*********************");
			System.out.println(l.circuit);
			System.out.println("score is: " + l.getScore(afterSplit));
			System.out.println(l.dag);
			for(Node n : l.circuit.node)
				if(n.is_of_type(NodeType.ConstNode))
					System.out.println(n);
			System.out.println("likelihood of data after split is: " + afterSplit.computeDataLikelihood(input,l.schema));
			sc.next();
			break;
			}
		}
		
//		l.calculateScore();
//		System.out.println();
		
		System.out.println("*********************");
		System.out.println("=====================");
		System.out.println("*********************");
		System.out.println("*********************");
		System.out.println("=====================");
		System.out.println("*********************");
		System.out.println("*********************");
		System.out.println("=====================");
		System.out.println("*********************");
//		
//		System.out.println(l.circuit);
//		System.out.println("*********************");
//		System.out.println("=====================");
//		System.out.println("*********************");		
//		System.out.println(l.dag);
//		System.out.println("*********************");
//		System.out.println("=====================");
//		System.out.println("*********************");
//		l.learn();
//		System.out.println(l.circuit);
//		System.out.println("*********************");
//		System.out.println("=====================");
//		System.out.println("*********************");
//		System.out.println(l.dag);
		
		l.createInitialDAG();
//		System.out.println(l.circuit);
//		l.createInitialCircuit();
//		l.createInitialDistributions();
//		l.generateVarNodes();
//		for(Node n : l.circuit.node)
//			if(n.is_of_type(NodeType.ConstNode)) {
//			  ((ConstNode) n ).initializeValue(l.data, l.schema);
//				System.out.println(n+"\nvar: "+((ConstNode)n).getVar()+"\n");
//			}
		
		
//		System.err.println(l.circuit.compute(dataline));
		
//		System.out.println(l.circuit.computeDataLikelihood(l.data, l.schema));
/*
		Circuit cir1 = l.applySplit(new Split(2,0));
		System.out.println("root\n=======================\n"+cir1.root+"\n=======================\n");
		System.out.println(cir1);
		System.out.println(l.dag);
		System.out.println("isAncestor " + c1.isAncestor(c1.getNode(26), c1.getNode(6)));
		Circuit cir2 = l.applySplit(new Split(1,2));
		System.out.println(cir2);
		System.out.println(l.circuit);
		System.out.println("root\n=======================\n"+cir2.root+"\n=======================\n");
		Circuit cir3 = l.applySplit(new Split(1,0));
		System.out.println(cir3);
		System.out.println(l.dag);
		System.out.println(l.circuit);
		System.out.println(l.dag);
		System.out.println(l.circuit);
		System.out.println(l.dag);
		l.getBestPossibleSplit();
		if(l.getBestPossibleSplit()!=null)
			l.applySplit(l.getBestPossibleSplit());
		else
			System.out.println("Possible splits exhausted.");
		System.out.println(l.circuit);
		System.out.println(l.dag);
*/		
//		
//		Set<Split> splits = new HashSet<Split>();
//		splits = l.getPossibleSplits();
//		for(Split s : splits) 
//			System.out.println("Split(" + s.getDsdf().getVar() +"," +s.getVar() +") is valid.");
//		System.out.println("=====================");
//		System.out.println("=====================");
//		for(int i=0;i<l.schema.length; i++)
//			for(int j=0; j<l.schema.length; j++)
//				if(Split.isValid(i, j, l.dag))
//					System.out.println("Split(" + i +"," + j +") is valid.");
//
//		
		
//
		
		// Learning
		System.out.println("Learning...");
		System.out.println("*********************");
		System.out.println("=====================");
		System.out.println("*********************");
		System.out.println("*********************");
		System.out.println("=====================");
		System.out.println("*********************");
		System.out.println("*********************");
		System.out.println("=====================");
		
		l.createInitialCircuit();
		l.createInitialDAG();
		
		l.learn();
		System.out.println( l.learn() );
		System.out.println( l.dag );
		
		sc.close();

		System.out.println(l.circuit.computeDataLikelihood(l.data, l.schema));
//		l.applySplit(new Split(2,1));
//		l.createInitialDistributions();
		for(Node n : l.circuit.node)
		if(n.is_of_type(NodeType.ConstNode)) {
//			System.out.println("Prior:\n"+n);
			((ConstNode)n).initializeValue(l.data, l.schema);
//			System.out.println("After Split:\n"+n);
			System.out.println("*********************");
		}
//		
	}
	
	/**
	 * Obtains counts for a given configuration in the data.
	 * @param dataline		the configuration to count
	 */
	public int computeCounts(int[] dataline) {
		int result = 0;

		int[] pattern = dataline;
		int count = 0;
		boolean add = false;
		for(int i=0; i<data.length; i++) {
			add = true;
			for(int j=0; j<data[i].length; j++) {
				if(data[i][j] != pattern[j])
					add = false;
			}
			if(add)
				count++;
		}
		result = ( count ); 

		return result;

	}
	
	/**
	 * Computes the likelihood of a single configuration (dataline) based on counts and the current AC in this class.
	 * @param dataline		the configuration to compute likelihood for
	 */
	public double computeLikelihood(int[] dataline) {
		double result = 0;
		double prob = circuit.root.compute(dataline);
		double count = computeCounts(dataline);
		double n = 1;
		for(int i=0; i<schema.length; i++)
			n *= schema[i];
		result = (1/n + count)/(1 + data.length) * prob ;
		return result;
	}
	/**
	 * Computes the likelihood of the data based on counts and the current AC in this class.
	 * @param data		the data to compute likelihood for
	 */
	public double computeDataLikelihood(int[][] data) {
		double result = 1;
		for(int i=0; i<data.length; i++) {
			result *= computeLikelihood(data[i]);
		}
		return result;
	}
	/**
	 * Reads the given file to obtain the schema for the data. 
	 * param data		path to file to read 
	 * @deprecated since the correct data format has the schema in the second line.
	 * 
	 * throws exceptions if the data format is not correct!
	 */
	public void readSchema(File schemaFile) {

		Scanner sc;		
		try {
			sc = new Scanner( schemaFile );

			String line = new String();
			String[] token;

			line = sc.nextLine();
			token = line.split(",");

			int[] schemaLine = new int[token.length];
			for(int i=0; i<schemaLine.length; i++)
				schemaLine[i] = Integer.parseInt(token[i]);

			schema = schemaLine;

			sc.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Reads the given data defined and assigns schema and data fields. 
	 * @param dataFile		path to file to read 
	 * 
	 * throws exceptions if the data format is not correct!
	 */
	public void readData(File dataFile) {

		Scanner sc;
		try {
			sc = new Scanner( dataFile );

			String line = new String();
			String[] token;
			
			line = sc.nextLine();
			int number_of_lines = Integer.parseInt(line);

			line = sc.nextLine();
			token = line.split(",");

			int[] schemaLine = new int[token.length];
			for(int i=0; i<schemaLine.length; i++)
				schemaLine[i] = Integer.parseInt(token[i]);

			schema = schemaLine;
			
			data = new int[number_of_lines][schema.length];

			int[] dataLine = new int[schema.length];
			int n = 0;
			while(sc.hasNext()) {
				line = sc.nextLine();
				token = line.split(",");

				for(int i=0; i<dataLine.length; i++) {
					dataLine[i] = Integer.parseInt(token[i]);
					data[n][i] = dataLine[i];
				}
				n++;
			}
			
			Split.data = this.data;
			Split.schema = this.schema;
			
			sc.close();

		} catch (Exception e) {
			System.err.println("Data format is not correct.");
			e.printStackTrace();
		}
	}
	/**
	 * Reads a subset of given data defined by the number of variables to read. 
	 * @param dataFile		path to file to read 
	 * 
	 * exceptions if the data format is not correct!
	 */
	public void readData(File dataFile, int number_of_variables) {
		Scanner sc;
		try {
			sc = new Scanner( dataFile );

			String line = new String();
			String[] token;
			
			line = sc.nextLine();
			int number_of_lines = Integer.parseInt(line);
			
			line = sc.nextLine();
			token = line.split(",");
			
			if(number_of_variables==0)
				number_of_variables = token.length;
			

			int[] schemaLine = new int[number_of_variables];
			for(int i=0; i<schemaLine.length; i++)
				schemaLine[i] = Integer.parseInt(token[i]);

			schema = schemaLine;
			
			
			data = new int[number_of_lines][number_of_variables];

			int[] dataLine = new int[number_of_variables];
			int n = 0;
			while(sc.hasNext()) {
				line = sc.nextLine();
				token = line.split(",");

				if(n==number_of_lines)
					break;
				
				for(int i=0; i<dataLine.length; i++) {
					dataLine[i] = Integer.parseInt(token[i]);
					data[n][i] = dataLine[i];
				}
				n++;
			}

			Split.data = this.data;
			Split.schema = this.schema;
			
			sc.close();

		} catch (Exception e) {
			System.err.println("Data format is not correct.");
			e.printStackTrace();
		}

	}
	
	/**
	 * Reads a subset of given data defined by the number of variables to read and number of input lines. 
	 * @param dataFile		path to file to read 
	 * 
	 * exceptions if the data format is not correct!
	 */
	public void readData(File dataFile, int number_of_variables, int number_of_lines) {

		Scanner sc;
		try {
			sc = new Scanner( dataFile );

			String line = new String();
			String[] token;

			line = sc.nextLine();
			if(number_of_lines==0)
				number_of_lines = Integer.parseInt(line);

			line = sc.nextLine();
			token = line.split(",");
			
			if(number_of_variables==0)
				number_of_variables = token.length;
			

			int[] schemaLine = new int[number_of_variables];
			for(int i=0; i<schemaLine.length; i++)
				schemaLine[i] = Integer.parseInt(token[i]);

			schema = schemaLine;
			
			data = new int[number_of_lines][number_of_variables];

			int[] dataLine = new int[number_of_variables];
			int n = 0;
			while(sc.hasNext()) {
				line = sc.nextLine();
				token = line.split(",");

				if(n==number_of_lines)
					break;
				
				for(int i=0; i<dataLine.length; i++) {
					dataLine[i] = Integer.parseInt(token[i]);
					data[n][i] = dataLine[i];
				}
				n++;
			}

			Split.data = this.data;
			Split.schema = this.schema;
			
			sc.close();

		} catch (Exception e) {
			System.err.println("Data format is not correct.");
			e.printStackTrace();
		}

	}

	
}
