import java.io.File;
import java.util.Scanner;



public class QueryData {

	public static Learner learn;

	public static void main(String[] args) {

		learn = new Learner();
		File input = new File(args[0]);
		learn.readData(input,Integer.parseInt(args[1]),1000);
//		learn.readData(input);

		learn.createInitialCircuit();
		learn.createInitialDAG();

		Split.dag = learn.dag;
		Split.data = learn.data;
		Split.schema = learn.schema;
		Split.circuit = learn.circuit;
		
		int[] dataline = new int[10];
		dataline[0] = 1; dataline[1] = 1 ; dataline[2] = 1; dataline[3] = 1; dataline[4] = 1;
		dataline[5] = 1; dataline[6] = 1 ; dataline[7] = 1; dataline[8] = 1; dataline[9] = 1;
		
		learn.learn();
		
//		System.out.println(learn.circuit);
		System.out.println("---------------------");
		System.out.println("---------------------");
		System.out.println("=====================");
		System.out.println("---------------------");
		System.out.println("---------------------");
		System.out.println("Learning finished. The DAG structure for data, showing parent/child relationships in variables is as below: ");
		System.out.println(learn.dag);
		System.out.println("---------------------");
		System.out.println("---------------------");
		System.out.println("=====================");
		System.out.println("---------------------");
		System.out.println("---------------------");
		
		//Querry
		System.out.println("To querry the data using the model, enter an instantiation of data in the format below:");
		System.out.println("1) In a single line, enter the states of each variable as an integer ranging from 0 to the variables domain. "
				+ "\n 2) Enter the values in the order as variables, seperating them by a comma. Please do not add any whitespace."
				+ "\n 3) You may type 'exit' to end querying."
				+ "\n Note: You may need to enter the input dataline again if an empty input is entered.");
			
		
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
			System.out.println("The value of the Network Polynomial for dataline is " + ( learn.circuit.root.compute(dataLine)) );
			System.out.println("Counts of the dataline is " + ( learn.computeCounts(dataLine)) );
			System.out.println("Ratio of counts for dataline to total number of data entries is " + (float)( (float)learn.computeCounts(dataLine) / ( (float)learn.data.length ) ) );
			
			
		}
		
		sc.close();
		
	}

}
