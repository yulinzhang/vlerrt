import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
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

	// ---
	public boolean collides(Point o, Point e, int d) {
		//TODO: not tested or linked... UGLY CODE
//		double angle = Math.atan2(o.y-e.y, o.x-e.x);
//		Point t = new Point( (int)(o.x+Math.cos(angle)*d) , (int)(o.y+Math.sin(angle)*d) );
		
		Line2D line = new Line2D.Double(o,e);
		
		for(Rectangle2D rect : obstacles){
			if( line.intersects(rect) )
				return true;
		}
		return false;
	}

	public Point randomPoint() {
		return new Point(r.nextInt(w),r.nextInt(h));
	}
	
	///
	//
	//
	protected Random r;
	protected List<Rectangle2D> obstacles;
	
	protected int w, h;
	protected Point start, goal;
	
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

		start = new Point(r.nextInt(w), r.nextInt(h));
		goal = new Point(r.nextInt(w), r.nextInt(h));
		
		addMouseListener(this);
	}
	
	protected void paintWorld(Graphics2D g){
		g.setColor(OBSTACLE);
		for(Rectangle2D r : obstacles)
			g.fill(r);
		
		g.setColor(BOUNDS);
		for(Rectangle2D r : obstacles)
			g.draw(r);
		
		g.drawRect(0, 0, w-1, h-1);
		
		g.setColor(GOAL);
		g.drawOval(goal.x, goal.y, 1, 1);
		
		g.setColor(START);
		g.drawOval(start.x, start.y, 1, 1);
		
		if( x!= null && y != null ){
			g.setColor( collides(x,y,0) ? Color.RED : Color.GREEN );
			g.drawLine(x.x, x.y, y.x, y.y);
		}
		
		//TODO: no appropriate way to get an RTT tree here??
		Tree rtt = null;
		//draw full tree
		if( rtt != null ){
			g.setColor(Color.BLACK);
			for( Node n : rtt ){
				if( n.isRoot() )
					continue;
				Point leaf = n.getPoint();
				Point parent = n.getParent().getPoint();
				g.drawLine(leaf.x, leaf.y, parent.x, parent.y);
			}
		}
		
		//draw path
		if( rtt != null ){
			g.setColor(Color.BLUE);
			Node goal = rtt.closestToGoal();
			while( !goal.isRoot() ){
				Node parent_node = goal.getParent();
				Point leaf = goal.getPoint();
				Point parent = parent_node.getPoint();
				g.drawLine(leaf.x, leaf.y, parent.x, parent.y);
				goal = parent_node;
			}
		}
	}


	protected Point x,y;
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited (MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		if( e.getButton() == MouseEvent.BUTTON1 )
			x = new Point(e.getX(),e.getY());
		if( e.getButton()== MouseEvent.BUTTON3 )
			y = new Point(e.getX(),e.getY());
		this.repaint();
	}
}
