import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 *  - left mouse button + right mouse button = line
 *  TODO: problems of accuracy when drawing stuff, double->int conversion
 */

@SuppressWarnings("serial")
public class GUI extends JPanel implements MouseListener {
	
	public static void display(World world, Tree tree, String title){
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new GUI(world,tree));
		frame.pack();
		frame.setVisible(true);		
	}

	//TODO: incomplete
	public static void screenshot(World world, Tree tree,String filename){
//		JFrame frame = new JFrame("RRTWorld");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.add(new GUI(world,tree));
//		frame.pack();
//		frame.setVisible(true);
//		// jpg screenshot
//		javax.imageio.ImageIO.write(new java.awt.Robot().createScreenCapture(frame.getBounds()), "jpg", new java.io.File("asd.jpg"));
	}
	
	protected World world;
	protected Tree tree;
	
	static final protected Color BACKGROUND = Color.WHITE;
	static final protected Color OBSTACLE = Color.BLACK;
	static final protected Color BOUNDS = Color.LIGHT_GRAY;
	static final protected Color GOAL = Color.GREEN;
	static final protected Color START = Color.RED;
	static final protected Color PATH = Color.BLUE;
	static final protected Color ALL_PATH = Color.DARK_GRAY;
	
	public GUI(World world, Tree tree){
		super();
		this.world = world;
		this.tree = tree;
		
		//component contains the world
		JPanel p = new JPanel(){
			public void paint(Graphics g) {
				super.paint(g);
				paintWorld((Graphics2D) g);
			}
		};
		Dimension d = new Dimension(world.width(),world.height());
		p.setMinimumSize(d);
		p.setPreferredSize(d);
		p.setBackground(BACKGROUND);
		add(p);
		
		addMouseListener(this);
	}
	
	protected void paintWorld(Graphics2D g){
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
		
		//this is collision testing code
		if( x!= null && y != null ){
			g.setColor( world.collides(x,y) ? Color.RED : Color.GREEN );
			g.draw(new Line2D.Double(x,y));
		}
		
		//all rrt paths
		g.setColor(ALL_PATH);
		for( Node n : tree){
			if( n.isRoot() )
				continue;
			Point2D leaf = n.getPoint();
			Point2D parent = n.getParent().getPoint();
			g.draw(new Line2D.Double(leaf,parent));
		}
		
		//the best path
		g.setColor(PATH);
		Node goal = tree.closestTo(world.goal());
		while( !goal.isRoot() ){
			Node parent_node = goal.getParent();
			Point2D leaf = goal.getPoint();
			Point2D parent = parent_node.getPoint();
			g.draw(new Line2D.Double(leaf,parent));
			goal = parent_node;
		}
		
		//goal and start points
		g.setColor(GOAL);
		g.drawOval((int)world.goal().getX()-1, (int)world.goal().getY()-1, 2, 2); 
		
		g.setColor(START);
		g.drawOval((int)world.start().getX()-1, (int)world.start().getY()-1, 2, 2);
	}


	//
	//this is just for debug
	//
	
	protected Point2D x,y;
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited (MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		if( e.getButton() == MouseEvent.BUTTON1 )
			x = new Point2D.Double(e.getX(),e.getY());
		if( e.getButton()== MouseEvent.BUTTON3 )
			y = new Point2D.Double(e.getX(),e.getY());
		this.repaint();
	}
}
