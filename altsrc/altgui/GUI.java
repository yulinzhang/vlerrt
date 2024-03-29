package altgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import altrrt.Node;
import altrrt.Tree;
import altrrt.World;

/*
 *  - left mouse button + right mouse button = line
 *  TODO: problems of accuracy when drawing stuff, double->int conversion
 */

@SuppressWarnings("serial")
public class GUI extends JPanel {

	public static void display(World world, Tree tree, String title) {
		display(world, tree, title, false);
	}
	
	public static void display(World world, Tree tree, String title, boolean halos){
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new GUI(world,tree,halos));
		frame.pack();
		frame.setVisible(true);		
	}
	
	protected BufferedImage createImage() {
	    BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bi.createGraphics();
	    paint(g);
	    return bi;
	}
	
	public static void screenshot(World world, Tree tree, String filename){
		screenshot(world, tree, filename, false);
	}

	public static void screenshot(World world, Tree tree, String filename, boolean halos){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GUI gui = new GUI(world,tree, halos);
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
	protected final boolean drawHalos;

	static final protected Color BACKGROUND = Color.WHITE;
	static final protected Color OBSTACLE = Color.BLACK;
	static final protected Color BOUNDS = Color.LIGHT_GRAY;
	static final protected Color GOAL = Color.GREEN;
	static final protected Color START = Color.RED;
	static final protected Color PATH = Color.BLUE;
	static final protected Color ALL_PATH = Color.PINK;
	static final protected Color HALO = Color.MAGENTA;

	public GUI(World w, Tree t){
		this(w, t, false);
	}
		
	public GUI(World w, Tree t, boolean halo){
		super();
		this.world = w;
		this.tree = t;
		this.drawHalos = halo;

		//component contains the world
		JPanel p = new JPanel(){
			public void paint(Graphics g) {
				super.paint(g);
				draw((Graphics2D) g, world, tree, drawHalos);
			}
		};
		Dimension d = new Dimension(world.width(),world.height());
		p.setPreferredSize(d);
		p.setBackground(BACKGROUND);
		add(p);
	}

	public void draw(Graphics2D g, World world, Tree tree){
		draw(g, world, tree, false);
	}
	
	public void draw(Graphics2D g, World world, Tree tree, boolean halo){
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
	
}
