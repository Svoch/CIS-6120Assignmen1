import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class ArithmeticCircuitDrawingPanel extends JPanel {

	public Circuit circuit;
	public Queue<Node> queue;
	
	static int rootLevel ;//= (int) Math.pow(2,  circuit.getNumberOfVariables()/2   );
	static Point rootPoint ;// =  new Point(500,100);
	
    public ArithmeticCircuitDrawingPanel(Circuit circuit) {
    	this.circuit = circuit;
    	this.setLayout(null);
    	queue = new LinkedList<Node>();
    	queue.add(circuit.root);
    	
    
    	
    }

    public void paintComponent(Graphics g) {
    	
    	g.drawString("Circuit", 12, 12);
    	/*
    	JLabel titleLabel = new JLabel("Circuit");
    	titleLabel.setBounds(10,5,100,20);
    	titleLabel.setVisible(true);
    	this.add(titleLabel);
    	*/
    	g.drawString("Parameter", 10, 30);
    	/*
    	JLabel parameterLabel = new JLabel("Parameter");
    	parameterLabel.setBounds(10,30,80,20);
    	parameterLabel.setVisible(true);
    	this.add(parameterLabel);
    	*/
    	g.setColor(Color.BLUE);
    	g.fillOval(110, 20, 10, 10);
    	g.setColor(Color.BLACK);
    	
    	g.drawString("Indicator", 10, 50);
    	/*
    	JLabel indicatorLabel = new JLabel("Indicator");
    	indicatorLabel.setBounds(10,50,80,20);
    	indicatorLabel.setVisible(true);
    	this.add(indicatorLabel);
    	*/
    	g.setColor(Color.GREEN);
    	g.fillOval(110, 40, 10, 10);
    	g.setColor(Color.BLACK);
    	
    	g.drawString("Times Node", 10, 70);
    	/*
    	JLabel timesNodeLabel = new JLabel("Times Node");
    	timesNodeLabel.setBounds(10,70,80,20);
    	timesNodeLabel.setVisible(true);
    	this.add(timesNodeLabel);
    	*/
    	g.setColor(Color.ORANGE);
    	g.fillOval(110, 60, 10, 10);
    	g.setColor(Color.BLACK);
    	g.drawLine(115 + 3, 75 + 3 - 10, 115 - 3, 75 - 3 - 10);
    	g.drawLine(115 + 3, 75 - 3 - 10, 115 - 3, 75 + 3 - 10);
    	g.setColor(Color.BLACK);
    	
    	g.drawString("Plus Node", 10, 90);
    	/*
    	JLabel plusNodeLabel = new JLabel("Plus Node");
    	plusNodeLabel.setBounds(10,90,100,20);
    	plusNodeLabel.setVisible(true);
    	this.add(plusNodeLabel);
    	*/
    	g.setColor(Color.CYAN);
    	g.fillOval(110, 80, 10, 10);
    	g.setColor(Color.BLACK);
    	g.drawLine(115 + 4, 95 - 10 , 115 - 4, 95 - 10 );
    	g.drawLine(115 , 95 - 4 - 10, 115 , 95 + 4 - 10);
    	
    	g.setColor(Color.BLACK);
    	
    	drawCircuit(g, circuit);
	}

    private void drawNode(Graphics g,int x,int y, Node n) {
    	
    	
    	Graphics2D g2d = (Graphics2D) g;
    	AffineTransform orig = g2d.getTransform();
        AffineTransform at = new AffineTransform();
        at.setToRotation(Math.PI / 2.0);
        g2d.setTransform(at);
        g2d.setTransform(orig);

    	switch (n.getType()) {
		case ConstNode:
			g.setColor(Color.BLUE);
	    	g.fillOval(x-5, y-5, 10, 10);
	    	g.setColor(Color.BLACK);

//	    	g.drawString(n.getLabel()/*"P" + ((ConstNode)n).getVar() + "," + ((ConstNode)n).getState()*/, x+5, y+15);// + ":" + ((ConstNode)n).getValue() + " ID " + n.getId(), x+5, y+15);

	    	orig = g2d.getTransform();
	        at = new AffineTransform();
	        at.setToRotation(Math.PI / 2.0);
	        g2d.setTransform(at);
	        g2d.drawString(n.getLabel() + " (ID " + n.getId() + ")", y+15,-(x+5));
	        g2d.setTransform(orig);
	    	
	    	/*
	    	JLabel parameterLabel = new JLabel("P" + ((ConstNode)n).getVar() + "," + ((ConstNode)n).getState() + " ID " + n.getId());
	    	parameterLabel.setBounds(n.getCoordinate().x,n.getCoordinate().y,120,20);
	    	parameterLabel.setVisible(true);
			this.add(parameterLabel);
			*/
			break;
		case VarNode:
			g.setColor(Color.GREEN);
	    	g.fillOval(x-5, y-5, 10, 10);
	    	g.setColor(Color.BLACK);
	    	
//	    	g.drawString("I" + ((VarNode)n).getVar() + "," + ((VarNode)n).getValue(), x+5, y+15 );//+ " ID " + n.getId(), x+5, y+15);

	    	orig = g2d.getTransform();
	        at = new AffineTransform();
	        at.setToRotation(Math.PI / 2.0);
	        g2d.setTransform(at);
	        g2d.drawString("I" + ((VarNode)n).getVar() + "," + ((VarNode)n).getValue() + " (ID " + n.getId() + ")", y+15,-(x+5));
	        g2d.setTransform(orig);
	    	
	    	/*
	    	JLabel indicatorLabel = new JLabel("I" + ((VarNode)n).getVar() + "," + ((VarNode)n).getValue() + " ID " + n.getId());
	    	indicatorLabel.setBounds(n.getCoordinate().x,n.getCoordinate().y,120,20);
	    	indicatorLabel.setVisible(true);
			this.add(indicatorLabel);
	    	*/
	    	break;
		case TimesNode:
			g.setColor(Color.ORANGE);
	    	g.fillOval(x-5, y-5, 10, 10);
	    	g.setColor(Color.BLACK);
	    	g.drawLine(x + 3, y + 3, x - 3, y - 3);
	    	g.drawLine(x + 3, y - 3, x - 3, y + 3);
	    	g.setColor(Color.BLACK);
	    	
	    	g.drawString(/*"ID " + */Integer.toString( n.getId() ), x+5, y+15);

	    	/*
	    	JLabel timesNodeLabel = new JLabel("ID " + n.getId());
	    	timesNodeLabel.setBounds(n.getCoordinate().x,n.getCoordinate().y,120,20);
	    	timesNodeLabel.setVisible(true);
			this.add(timesNodeLabel);
	    	*/
	    	break;
		case PlusNode:
			g.setColor(Color.CYAN);
	    	g.fillOval(x-5, y-5, 10, 10);
	    	g.setColor(Color.BLACK);
	    	g.drawLine(x + 3, y , x - 3, y );
	    	g.drawLine(x , y - 3, x , y + 3);
	    	g.setColor(Color.BLACK);
	    	
	    	g.drawString(/*"ID " + */Integer.toString( n.getId() ), x+5, y+15);

	    	/*
	    	JLabel plusNodeLabel = new JLabel("ID " + n.getId());
	    	plusNodeLabel.setBounds(n.getCoordinate().x,n.getCoordinate().y,120,20);
	    	plusNodeLabel.setVisible(true);
			this.add(plusNodeLabel);
	    	*/
			break;
		default:
	    	g.fillOval(x-5, y-5, 10, 10);
			break;
		}
    }
    
    @SuppressWarnings("deprecation")
	private void drawCircuit(Graphics g,Circuit c) {
    	Node n;
    	while(!queue.isEmpty()) {
    		n = queue.poll();
    		if( n != null ) {
        		if( n.getId() != -1) {
        			drawNode(g,n.getCoordinate().x,n.getCoordinate().y,n);
        		}
        		if( n.getId() == -1 ) {
        			continue;
        		}
        	}
    		if( n == null )
    			break;
			queue.add(new Node(-1,"Null"));
			for(Node m : n.getChild()) {
				queue.add(m);
				g.drawLine(n.getCoordinate().x, n.getCoordinate().y, m.getCoordinate().x, m.getCoordinate().y);
			}
    	}
    }
	protected void rePaintComponent() {
		if(getGraphics()!=null)
			this.paintComponent(getGraphics());
		paintComponent(this.getGraphics());
	}

	public static void reCoordinateCircuitBFS(Circuit circuit) {
	   	Node n;
	   	ArrayList<Node> node = new ArrayList<Node>();
	   	Queue<Node> queue = new LinkedList<Node>();
    	queue.add(circuit.root);
	   	circuit.root.setCoordinate(rootPoint);
    	circuit.root.setLevel( rootLevel );
	   	while(!queue.isEmpty()) {
    		n = queue.poll();
    		if( n != null ) {
        		if( n.getId() != -1) {
        			node.add(n);
        			n.reCoordinate();
        		}
        		if( n.getId() == -1 ) {
        			continue;
        		}
        	}
    		if( n == null )
    			break;
			for(Node m : n.getChild()) {
				queue.add(m);
			}
    	}
	}

	
	/*
	 * For debugging purposes...
    public static void main(String[] args) {
    	
    	ArrayList<Node> node = new ArrayList<Node>();
    	node.add(new Node("ehem"));
    	node.add(new Node("behem"));
    	node.add(new Node("cehem"));
    	node.add(new Node("dehem"));
    	node.add(new Node("pehem"));

    	
    	Circuit circuit = new Circuit(node);

    	circuit.root = circuit.getNode(0);
    	circuit.getNode(0).addChild(circuit.getNode(1));
    	circuit.getNode(1).addChild(circuit.getNode(2));
    	circuit.getNode(0).addChild(circuit.getNode(3));
    	circuit.getNode(1).addChild(circuit.getNode(4));
        
    	Learner learn = new Learner();
		File input = new File("/Users/siavashnazari/Desktop/cold.data");
		learn.readData(input,5,10);
		learn.createInitialCircuit();
		learn.createInitialDAG();

//		Circuit circuit = learn.circuit;

		
		Circuit circuit = learn.applySplit(new Split(0,1));
//		circuit = learn.applySplit(new Split(0, 2));
		
//		Circuit circuit = learn.learn();

		
		rootLevel = (int) Math.pow(2,  circuit.getNumberOfVariables()/2  );
		rootPoint =  new Point(600,100);
		reCoordinateCircuitBFS(circuit);

		System.err.println(circuit);
    	System.err.println(circuit.getNumberOfParamerets());
    	System.err.println(circuit.getNumberOfVariables());
    	System.err.println(learn.dag);
    	for(Node n : learn.circuit.node) 
    		if(n.is_of_type(NodeType.ConstNode))
    			System.out.println(((ConstNode)n));
    	System.out.println("---------------------");
    	System.out.println("---------------------");

        ArithmeticCircuitDrawingPanel acPane = new ArithmeticCircuitDrawingPanel(circuit);
        JButton jButton = new JButton("Repaint");
        jButton.setLocation(150, 10);
//        jButton.setBounds(150, 10, 50, 50);
        jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				jFrame.repaint();
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
        
        
        jFrame.add(addButton);
        
    	jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        
//        jFrame.setBounds(0, 0, 10000, 10000);
        jFrame.setSize(1200, 600);
        
        
        jFrame.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				jFrame.getContentPane().removeAll();
				jFrame.getContentPane().add(new ArithmeticCircuitDrawingPanel(circuit));
				jFrame.setVisible(true);
				System.err.println("componentResized");
			}
			@Override
			public void componentMoved(ComponentEvent e) {
				jFrame.getContentPane().removeAll();
				jFrame.getContentPane().add(new ArithmeticCircuitDrawingPanel(circuit));
				jFrame.setVisible(true);
				System.err.println("componentMoved");
			}
			@Override
			public void componentShown(ComponentEvent e) {
				jFrame.getContentPane().removeAll();
				jFrame.getContentPane().add(new ArithmeticCircuitDrawingPanel(circuit));
				jFrame.setVisible(true);
				System.err.println("componentShown");
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				
			}
        });
        jFrame.setVisible(true);
        
        
    }
    */
	
	
}