import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;

//
//this is just for debug
//


////this is collision testing code
//if( x!= null && y != null ){
//	g.setColor( world.collides(x,y) ? Color.RED : Color.GREEN );
//	g.draw(new Line2D.Double(x,y));
//}
//
//
//protected Point2D x,y;
//
//public void mouseClicked(MouseEvent e) {}
//public void mouseEntered(MouseEvent e) {}
//public void mouseExited (MouseEvent e) {}
//public void mousePressed(MouseEvent e) {}
//public void mouseReleased(MouseEvent e) {
//	if( e.getButton() == MouseEvent.BUTTON1 )
//		x = new Point2D.Double(e.getX(),e.getY());
//	if( e.getButton()== MouseEvent.BUTTON3 )
//		y = new Point2D.Double(e.getX(),e.getY());
//	this.repaint();
//}

@SuppressWarnings("serial")
public class Editor extends GUI implements MouseListener, MouseMotionListener  {

	static final Color REALLY_LIGHT_GRAY = new Color(0.95f,0.95f,0.95f);
	static final int SPACE = 40;

	enum Mode {
		NONE,
		NEW_OBSTACLE_PT1,
		NEW_OBSTACLE_PT2,
		DELETE_OBSTACLE, 
		SET_START,
		SET_GOAL
	};

	JPopupMenu menu;
	Mode m;
	Point2D point;

	public Editor(World world) {
		super(world,null);

		addMouseListener(this);
		addMouseMotionListener(this);

		m = Mode.NONE;
		menu = new JPopupMenu();

		//menu options
		menu.add(new AbstractAction("new obstacle") {
			public void actionPerformed(ActionEvent e) { m = Mode.NEW_OBSTACLE_PT1; repaint(); }
		});
		menu.add(new AbstractAction("delete obstacle"){
			public void actionPerformed(ActionEvent e) { m = Mode.DELETE_OBSTACLE; repaint(); }
		});


		menu.addSeparator();

		menu.add(new AbstractAction("set goal"){
			public void actionPerformed(ActionEvent e) { m = Mode.SET_GOAL; repaint(); }
		});

		menu.add(new AbstractAction("set start") {
			public void actionPerformed(ActionEvent e) { m = Mode.SET_START; repaint(); }
		});

		menu.addSeparator();

		menu.add(new AbstractAction("load") {
			public void actionPerformed(ActionEvent e) {   }
		});

		menu.add(new AbstractAction("save") {
			public void actionPerformed(ActionEvent e) {   }
		});
		
		menu.add(new AbstractAction("quick save") {
			public void actionPerformed(ActionEvent e) {   }
		});

		
		//TODO: resize window for new dimensions...use component width

	}

	public void draw(Graphics2D g, World world, Tree tree){
		super.draw(g, world, tree);
		
		print(g,"OPT: "+m);
	}

	protected void print(Graphics2D g, String txt){
		g.setColor(Color.BLACK);
		g.drawString(txt, 0, getHeight()-50);
	}

	protected void mark(Graphics2D g, int x, int y, Color c){
		g.setColor(c);
		g.drawLine(x-2,y-2,x+2,y+2);
		g.drawLine(x-2,y+2,x+2,y-2);
	}

	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) {
		Point2D old = point;
		point = new Point2D.Double(e.getX(), e.getY());
		
		if( e.getButton() == MouseEvent.BUTTON1 ){
			switch(m){
			case SET_GOAL:
				((RRTWorld)world).goal = point;
				break;
			case SET_START:
				((RRTWorld)world).start = point;
				break;
			case DELETE_OBSTACLE:
				//FIXME: buggy! origins is incorrect!
				Point2D target = point;
				List<Rectangle2D> list = ((RRTWorld)world).obstacles;
				Iterator<Rectangle2D> it = list.iterator();
				while( it.hasNext() ){
					Rectangle2D o = it.next();
					if( o.contains(target) ){
						it.remove();
						break;
					}
				}
				break;
			case NEW_OBSTACLE_PT1:
				m = Mode.NEW_OBSTACLE_PT2;
				break;
			case NEW_OBSTACLE_PT2:
				Rectangle2D rect = new Rectangle(
						(int) old.getX(), //x
						(int) old.getY(), //y
						(int) Math.abs( old.getX() - point.getX() ), //w
						(int) Math.abs( old.getY() - point.getY() )  //h
						);
				((RRTWorld)world).obstacles.add(rect);
				m = Mode.NEW_OBSTACLE_PT1;
				break;
			default:
				break;
			}
			repaint();
			return;
		}

		if(e.getButton() == MouseEvent.BUTTON3)
			menu.show(this, e.getX(), e.getY());
	}

	public void mouseDragged(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { 
//		if( m == Mode.TRANSLATE ){
//			x1 = e.getX();
//			y1 = e.getY();
//			repaint();
//		}
//
//		if( m == Mode.BUILD_SIDE_2ND_SIDE ){
//			x1 = e.getX(); 
//			y1 = e.getY();
//			repaint();
//		}
	}

	public static void main(String[] args) throws Exception{
		JFrame frame = new JFrame("Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Editor(new RRTWorld(800,600)));
		frame.pack();
		frame.setVisible(true);		
	}
}

