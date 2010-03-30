import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * NOTES:
 *  - left mouse button + right mouse button = line
 *  - intersects with the INTERIOR of the rectangle (*NOT* its border)
 */

//TODO: split this into a special GUI class
//TODO: world in a text file

@SuppressWarnings("serial")
public class RRTWorld extends JPanel implements World, MouseListener {

	//TEST
	public static void main(String[] args){
		JFrame frame = new JFrame("RRTWorld");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new RRTWorld(400,400) );
		frame.pack();
		frame.setVisible(true);
	}

	public void display() {
		JFrame frame = new JFrame("RRTWorld");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}
	
	public boolean collides(Point2D point){
		return false;
	}
	
	/**
	 * checks if o-e line collides with any of the world's obstacles
	 * and if 'e' falls out of bounds.
	 */
	public boolean collides(Point2D o, Point2D e) {
		if( e.getX() < 0 || e.getY() < 0 || e.getX() >= w || e.getY() >= h )
			return true;
		
		Line2D line = new Line2D.Double(o,e);
		
		for(Rectangle2D rect : obstacles){
			if( line.intersects(rect) )
				return true;
		}
		return false;
	}

	public Point2D.Double randomPoint() {
		return new Point2D.Double(r.nextInt(w),r.nextInt(h));
	}
	
	///
	//
	//
	protected Random r;
	protected List<Rectangle2D> obstacles;
	
	protected int w, h;
	protected Point2D.Double start, goal;
	protected Tree searchTree = null;
	
	public void setSearchTree(Tree searchTree) {
		this.searchTree = searchTree;
	}

	static final protected Color BACKGROUND = Color.WHITE;
	static final protected Color OBSTACLE = Color.BLACK;
	static final protected Color BOUNDS = Color.LIGHT_GRAY;
	static final protected Color GOAL = Color.RED;
	static final protected Color START = Color.BLUE;
	
	public RRTWorld(int w, int h){ //TODO: more initial parameters...
		super();
		this.w = w;
		this.h = h;
		this.r = new Random(System.currentTimeMillis());
		
		//component contains the world
		JPanel world = new JPanel(){
			public void paint(Graphics g) {
				super.paint(g);
				paintWorld((Graphics2D) g);
			}
		};
		Dimension d = new Dimension(w,h);
		world.setMinimumSize(d);
		world.setPreferredSize(d);
		world.setBackground(BACKGROUND);
		add(world);
		
		obstacles = new LinkedList<Rectangle2D>();
		
		//TODO: better obstacle generation
		int n = 10;
		while( n-- > 0 )
			obstacles.add( new Rectangle(r.nextInt(w),r.nextInt(h),20,20));

		//FIXME: not inside a BLOCK!!
		start = new Point2D.Double(r.nextInt(w), r.nextInt(h));
		goal = new Point2D.Double(r.nextInt(w), r.nextInt(h));
		
		addMouseListener(this);
	}
	
	protected void paintWorld(Graphics2D g){
//		g.setRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON );
		
		g.setColor(OBSTACLE);
		for(Rectangle2D r : obstacles)
			g.fill(r);
		
		g.setColor(BOUNDS);
		for(Rectangle2D r : obstacles)
			g.draw(r);
		
		g.drawRect(0, 0, w-1, h-1);
		
		g.setColor(GOAL);
		g.drawOval((int)goal.x, (int)goal.y, 1, 1); //TODO: draw double oval?
		
		g.setColor(START);
		g.drawOval((int)start.x, (int)start.y, 1, 1);  //TODO: draw double oval?
		
		if( x!= null && y != null ){
			g.setColor( collides(x,y) ? Color.RED : Color.GREEN );
			g.draw(new Line2D.Double(x,y));
		}
		
		//TODO: no appropriate way to get an RTT tree here??
		Tree rtt = searchTree;
		//draw full tree
		if( rtt != null ){
			g.setColor(Color.BLACK);//TODO: iterator retusn null?
			for( Node n : rtt ){
				if( n.isRoot() )
					continue;
				Point2D leaf = n.getPoint();
				Point2D parent = n.getParent().getPoint();
				g.draw(new Line2D.Double(leaf,parent));
			}
		}
		
		//draw path
		if( rtt != null ){
			g.setColor(Color.BLUE);
			Node goal = rtt.closestTo(this.goal);
			while( !goal.isRoot() ){
				Node parent_node = goal.getParent();
				Point2D leaf = goal.getPoint();
				Point2D parent = parent_node.getPoint();
				g.draw(new Line2D.Double(leaf,parent));
				goal = parent_node;
			}
		}
	}


	protected Point2D.Double x,y;
	
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

	public Point2D.Double goal() {
		return goal;
	}

	public Point2D.Double start() {
		return start;
	}
}
