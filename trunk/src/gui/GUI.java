package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import rrt.Node;
import rrt.Tree;
import rrt.World;
import rrtImpl.RRTWorld;
import rrtImpl.VLRRTnode;
import search.RRTsearch;

/*
 *  - left mouse button + right mouse button = line
 *  TODO: problems of accuracy when drawing stuff, double->int conversion
 */

@SuppressWarnings("serial")
public class GUI extends JPanel implements KeyListener{
	
	protected static final int STEPS = 1;
	
	public static void main(String[] args) throws Exception {
		RRTsearch search = RRTsearch.DVLRRT(
				new RRTWorld("worlds/proposal-world"),20,10,
				VLRRTnode.changeEpsilonScheme.Mult, 2, 
				VLRRTnode.changeEpsilonScheme.Restart,1);
		final JFrame frame = new JFrame("press D for step");
		final GUI gui = new GUI(search,true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(gui);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);	
		gui.grabFocus();
	}

	public static void display(RRTsearch s, String title) {
		display(s, title, false);
	}
	
	public static void display(RRTsearch s, String title, boolean halos){
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new GUI(s,halos));
		frame.pack();
		frame.setVisible(true);		
	}
	
	protected BufferedImage createImage() {
	    BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bi.createGraphics();
	    paint(g);
	    return bi;
	}
	
	public static void screenshot(RRTsearch s, String filename){
		screenshot(s, filename, false);
	}

	public static void screenshot(RRTsearch s, String filename, boolean halos){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GUI gui = new GUI(s, halos);
		frame.add(gui);
		frame.pack();
		try {
			javax.imageio.ImageIO.write( gui.createImage()
					, "png", new java.io.File(filename+".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.dispose();
	}

	protected final World world;
	protected final Tree tree;
	protected final RRTsearch search;
	protected final boolean drawHalos;

	static final protected Color BACKGROUND = Color.WHITE;
	static final protected Color OBSTACLE = Color.BLACK;
	static final protected Color BOUNDS = Color.LIGHT_GRAY;
	static final protected Color GOAL = Color.GREEN;
	static final protected Color START = Color.RED;
	static final protected Color PATH = Color.BLUE;
	static final protected Color ALL_PATH = Color.PINK;
	static final protected Color HALO = Color.MAGENTA;
	static final protected Color WAYPOINT = Color.DARK_GRAY;
	
	public GUI(World w){
		super();
		this.drawHalos = false;
		this.search = null;
		this.world = w;
		this.tree = null;
		init();
	}
	
	public GUI(RRTsearch s, boolean halo){
		super();
		this.drawHalos = halo;
		this.search = s;
		this.world = s.getWorld();
		this.tree = s.getSearchTree();
		init();
	}
	
	public void init(){
		//component contains the world
		JPanel p = new JPanel(){
			public void paint(Graphics g) {
				super.paint(g);
				draw((Graphics2D) g);
			}
		};
		Dimension d = new Dimension(world.width(),world.height());
		p.setPreferredSize(d);
		p.setBackground(BACKGROUND);
		add(p);
		addKeyListener(this);
	}
	
	public void draw(Graphics2D g){
		// this does not improve the drawing at all...		
		//		g.setRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON );

		//world bounds
		g.setColor(BOUNDS);
		g.drawRect(0, 0, world.width()-1, world.height()-1);

		//obstacles
		g.setColor(OBSTACLE);
		for(Rectangle2D r : world.obstacles())
			g.fill(r);

		g.setColor(BOUNDS);
		for(Rectangle2D r : world.obstacles())
			g.draw(r);

		if( tree != null ){
			//all rrt paths
			g.setColor(ALL_PATH);
			for( Node n : tree){
//				if (halo) {
//					g.setColor(HALO);
//					drawHalo(g, n);
//					g.setColor(ALL_PATH);
//				}
				
				if( n.isRoot() )
					continue;
				
				Point2D leaf = n.getPoint();
				Point2D parent = n.getParent().getPoint();
				g.draw(new Line2D.Double(leaf,parent));
			}

			//the best path
			g.setColor(PATH);
			Node goal = tree.closestTo(world.goal());
			g.setColor(HALO);
			drawHalo(g, goal);
			g.setColor(PATH);
			while( !goal.isRoot() ){
				Node parent_node = goal.getParent();
				Point2D leaf = goal.getPoint();
				Point2D parent = parent_node.getPoint();
				g.draw(new Line2D.Double(leaf,parent));
				goal = parent_node;
			}
		}
		
		//waypoints
		if( search != null ){
			g.setColor(WAYPOINT);
			List<Node> wp = search.getWaypoints();
			if( wp!=null ){
				for( Node n : wp ){
					g.drawOval( ((int)n.getPoint().getX()), 
							((int)n.getPoint().getY()-1), 2, 2);
				}
			}
		}

		//goal and start points
		g.setColor(GOAL);
		g.drawOval((int)world.goal().getX()-1, (int)world.goal().getY()-1, 2, 2); 

		g.setColor(START);
		g.drawOval((int)world.start().getX()-1, (int)world.start().getY()-1, 2, 2);
	}

	public void drawHalo(Graphics2D g, Node n) {
		Point2D nodePoint = n.getPoint();
		double x = nodePoint.getX();
		double y = nodePoint.getY();
		double extLength, deltaX, deltaY;
		for(int angle = 0; angle < 360; angle += 1) {
			double angleRad = angle*2*Math.PI/360;
			extLength = n.getExtensionLength(angleRad);
			deltaX = extLength*Math.cos(angleRad);
			deltaY = extLength*Math.sin(angleRad);
			
			g.draw(new Ellipse2D.Double(x + deltaX-2,  //x coord adjusted to make upper left corner
					y - deltaY-2, 2, 2));  //y directions reversed, adjust to make upper left corner
		}
	}

	public void keyTyped(KeyEvent e) { }
	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { 
		if( e.getKeyCode() == KeyEvent.VK_D && search != null ){
			search.runSearch(STEPS);
			repaint();
		}
	}

	
}
