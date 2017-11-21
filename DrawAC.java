import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;


public class DrawAC {

	
    public static void main(String[] args) {
        
    	Learner learn = new Learner();
		File input = new File(args[0]);
		
		learn.readData(input,3,10);
		learn.createInitialCircuit();
		learn.createInitialDAG();

//		Circuit circuit = learn.circuit;
//		learn.applySplit(new Split(0, 2));
		Circuit circuit = learn.applySplit(new Split(0,1));
		
		
		ArithmeticCircuitDrawingPanel.rootLevel = (int) Math.pow(2,  circuit.getNumberOfVariables()/2 );
		ArithmeticCircuitDrawingPanel.rootPoint =  new Point(600,100);
		ArithmeticCircuitDrawingPanel.reCoordinateCircuitBFS(circuit);

		System.err.println(circuit);
    	System.err.println(circuit.getNumberOfParamerets());
    	System.err.println(circuit.getNumberOfVariables());
    	System.err.println(learn.dag);
    	for(Node n : learn.circuit.node) 
    		if(n.is_of_type(NodeType.ConstNode))
    			System.out.println(((ConstNode)n));
    	System.out.println("---------------------");
    	System.out.println("---------------------");
    	System.out.println(learn.dag);
    	System.out.println("---------------------");
    	System.out.println("---------------------");

    	
        ArithmeticCircuitDrawingPanel acPane = new ArithmeticCircuitDrawingPanel(circuit);
        JButton jButton = new JButton("Repaint");
        jButton.setLocation(150, 10);
        jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				acPane.repaint();
				acPane.rePaintComponent();
			}
		});
        
        
    	JFrame jFrame = new JFrame();

        
        JButton addButton = new JButton("Draw");
        addButton.setBounds(15, 15, 100, 30);
        addButton.setSize(100,30);
        jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
		        jFrame.remove(addButton);
				jFrame.getContentPane().add(acPane);
		        jFrame.repaint();
		        
			}
		});
        
    	jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1200, 600);
        
        jFrame.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				jFrame.getContentPane().removeAll();
				jFrame.getContentPane().add(new ArithmeticCircuitDrawingPanel(circuit));
				jFrame.setVisible(true);
			}
			@Override
			public void componentMoved(ComponentEvent e) {
				jFrame.getContentPane().removeAll();
				jFrame.getContentPane().add(new ArithmeticCircuitDrawingPanel(circuit));
				jFrame.setVisible(true);
			}
			@Override
			public void componentShown(ComponentEvent e) {
				jFrame.getContentPane().removeAll();
				jFrame.getContentPane().add(new ArithmeticCircuitDrawingPanel(circuit));
				jFrame.setVisible(true);
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				
			}
        });
        jFrame.setVisible(true);
        
        
        System.out.println("Note: Reading 3 variables from data. To read more variables please change the second argument in readData."
				+ "\nAdding more variables can make the graphical represenation of the AC messy since there will be so many parameter and indicator nodes to draw");
		
        
    }
}
