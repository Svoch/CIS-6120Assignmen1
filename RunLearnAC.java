import java.awt.Point;
import java.io.File;

import javax.swing.JFrame;


public class RunLearnAC {

	public static Learner learn;


	public static void main(String[] args) {

		learn = new Learner();
		File input = new File(args[0]);
		learn.readData(input,Integer.parseInt(args[1]));
//		learn.readData(input);
		learn.createInitialCircuit();
		learn.createInitialDAG();

		/*
		for(Node n : learn.circuit.node) 
			if(n.is_of_type(NodeType.ConstNode)) {
				System.out.println((ConstNode) n);
				System.out.println("-------------------");
			}
		 */

//		learn.applySplit(new Split(2,1));
//		System.out.println(learn.circuit);
//		System.out.println(learn.circuit);
//		learn.applySplit(new Split(1,0));
		Split.dag = learn.dag;
		Split.data = learn.data;
		Split.schema = learn.schema;
		Split.circuit = learn.circuit;
//		System.out.println( Split.isValid(1, 2) );
//		learn.applySplit(new Split(2,1));
//		System.out.println(learn.circuit.root);
//		System.out.println("------------");
//		System.out.println("------------");
//		System.out.println("------------");
//		System.out.println("------------");

//		System.out.println(learn.circuit);
		
		int[] dataline = new int[10];
		dataline[0] = 1; dataline[1] = 1 ; dataline[2] = 1; dataline[3] = 1; dataline[4] = 1;
		dataline[5] = 1; dataline[6] = 1 ; dataline[7] = 1; dataline[8] = 1; dataline[9] = 1;
				
//		System.out.println("log-likelihood for dataline is " + ( learn.computeLikelihood(dataline)) );
		
//		System.out.println("log-likelihood for data is " + ( learn.computeDataLikelihood(learn.data)) );
		/*
		System.out.println("Circuit: ");
		System.out.println( learn.circuit.computeDataLikelihood(learn.data, learn.schema) );
		System.out.println("Learner: ");
		System.out.println( Math.log( learn.computeDataLikelihood(learn.data) ) );
		System.out.println("Score: ");
		System.out.println( learn.getScore(learn.circuit));
		for(Node n : learn.circuit.node) 
			if(n.is_of_type(NodeType.ConstNode)) {
				System.out.println((ConstNode) n);
				System.out.println("-------------------");
			}
		
		for(Node n : learn.circuit.node) 
			if(n.is_of_type(NodeType.ConstNode)) {
				((ConstNode) n).initializeValue(learn.data, learn.schema);
			} 
		System.out.println("Splitting...");
		learn.applySplit(new Split(0,2));
		learn.applySplit(new Split(0,1));

		System.out.println("Circuit: ");
		System.out.println( learn.circuit.computeDataLikelihood(learn.data, learn.schema) );
		System.out.println("Learner: ");
		System.out.println( Math.log( learn.computeDataLikelihood(learn.data) ) );
		System.out.println("Score: ");
		System.out.println( learn.getScore(learn.circuit));
		for(Node n : learn.circuit.node) 
			if(n.is_of_type(NodeType.ConstNode)) {
				System.out.println((ConstNode) n);
				System.out.println("-------------------");
			} 
		*/
		
		learn.learn();
		
		
		System.out.println("---------------------");
		System.out.println("---------------------");
		System.out.println("=====================");
		System.out.println("---------------------");
		System.out.println("---------------------");
		
		System.out.println("Resulting DAG based on LearnAC: ");
		System.out.println("This DAG shows parent/child relationships in equivalent Bayesian netwrok as well as the consequences of the splits applied");

		
		System.out.println(learn.dag);
        ArithmeticCircuitDrawingPanel acPane = new ArithmeticCircuitDrawingPanel(learn.circuit);
        ArithmeticCircuitDrawingPanel.rootLevel = (int) Math.pow(2,  learn.circuit.getNumberOfVariables()/2 + 1 );
        ArithmeticCircuitDrawingPanel.rootPoint =  new Point(500,100);  
		ArithmeticCircuitDrawingPanel.reCoordinateCircuitBFS(learn.circuit);
		JFrame jFrame = new JFrame();
        jFrame.setSize(1200, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.getContentPane().add(acPane);
		jFrame.setVisible(true);
		System.out.println("---------------------");
		System.out.println("---------------------");
		System.out.println("=====================");
		System.out.println("---------------------");
		System.out.println("---------------------");
		//Querry
		
		
		System.out.println();
		
//		System.out.println(learn.circuit.root);
//		System.out.println("-------------------");
//		System.out.println("-------------------");
//		System.out.println(learn.circuit);
		
//		for(Node n : learn.circuit.node) 
//			if(n.is_of_type(NodeType.ConstNode)) {
//				System.out.println((ConstNode) n);
//				System.out.println("-------------------");
//			}
		
		/*
		Scanner sc = new Scanner(System.in);
		String line;
		String[] token;
		int[] dataLine = new int[learn.schema.length];

		
		
		while(true) {
			System.out.println("input the dataline: ");
			line = sc.nextLine();
			token = line.split(",");

			if(line.equals("exit"))
				break;
			
			line = sc.nextLine();
			token = line.split(",");
			
			try {
				for(int i=0; i<dataLine.length; i++) {
					dataLine[i] = Integer.parseInt(token[i]);
				}
			} catch(NumberFormatException e) {
				continue;
			}

			System.out.println("likelihood for dataline is " + ( learn.computeLikelihood(dataLine)) );
			System.out.println("Network Polynomial for dataline is " + ( learn.circuit.root.compute(dataLine)) );
			System.out.println("Counts for dataline is " + ( learn.computeCounts(dataLine)) );
			System.out.println("Ratio of counts for dataline is " + (float)( (float)learn.computeCounts(dataLine) / 1000.0) );
			
			
		}
		*/
		
		MixedBasedInteger mbi = new MixedBasedInteger(learn.schema);
		int decimal = 1;
		for(int i=0; i<learn.schema.length; i++)
			decimal *= learn.schema[i];
		
		int[][] patterned = new int[decimal][learn.schema.length];
		for(int i=0; i<decimal; i++) {
			patterned[i] = mbi.computeDigitsFor(i);
		}
		
		
		double [] logp = new double [decimal];
		for(int i=0; i<decimal; i++) {
			logp[i] = learn.circuit.root.compute(patterned[i]);
			mbi.setDigit(patterned[i]);
		}
		
//		for(int i=0; i<decimal; i++) {
//			for(int j=0; j<learn.schema.length; j++) 
//				System.out.print(patterned[i][j] + " ");
//			System.out.println();
//		}
		
		
		System.out.println("Resulting JPD based on LearnAC: ");
		System.out.println("First column is the index of MixedBasedInteger matched to an instantiation of variables."
				+ "\n For example, if schema is (2,2,3), index of 15 is matching the configuration of (1,0,1) in variables. ");
		System.out.println("Second column is the probability of the corresponding configuration suggested by the learned AC.");
		System.out.println("Third column is the ratio of the corresponding configutation to all seen (not possible) configurations in the data."
				+ "\n Note that this column is not a part of LearnAC; it is present to compare results of LearnAC with the actual data.");
		for(int i=0; i<decimal; i++) {
//			for(int j=0; j<learn.schema.length; j++) {
				System.out.print( i + "\t");
//				System.out.print( ( learn.computeLikelihood(dataLine)) );
				System.out.print( ( learn.circuit.root.compute(patterned[i])) + "\t" );
//				System.out.print(" " + ( learn.computeCounts(dataLine)) );
				System.out.print(" " + (float)( (float)learn.computeCounts(patterned[i]) / (float) learn.data.length ) );
				System.out.println();
//			}
		}
		
		
		
//		int[] count = new int[decimal];
//		for(int i=0; i<data.length; i++) {
//			mbi.setDigit(data[i]);
//			count[mbi.getDecimalValue()]++;
//		}
//		
//		for(int i=0; i<decimal; i++) {
//			System.err.println(logp[i]);
//			result +=  Math.log(  logp[i]  ) ;
//		}
		
//		System.out.println( (float)( (float)learn.computeCounts(patterned[i]) / 1000.0) );
		
		
	}

}
