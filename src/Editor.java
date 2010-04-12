import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;


@SuppressWarnings("serial")
public class Editor extends GUI implements MouseListener, MouseMotionListener {

	static final Color REALLY_LIGHT_GRAY = new Color(0.95f,0.95f,0.95f);
	static final int SPACE = 40;

	static final int WTF_W = 7;
	static final int WTF_H = 7;
	
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
	Point2D point,old;
	String file;

	public Editor(String f) throws Exception {
		super(new RRTWorld(f),null);
		file = f;
		
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

		menu.add(new AbstractAction("save") {
			public void actionPerformed(ActionEvent e) {
				try {
					world.write(file);
				} catch (Exception e1) {
					e1.printStackTrace();
				}  
			}
		});
		
		menu.add(new AbstractAction("save as") {
			public void actionPerformed(ActionEvent e) {
				try {
					final JFileChooser fc = new JFileChooser(new File("."));
					fc.showSaveDialog(null);
					file = fc.getSelectedFile().getAbsolutePath();
					world.write(file);
				} catch (Exception e1) {
					e1.printStackTrace();
				}  
				repaint();	
			}
		});
		
		menu.addSeparator();

		menu.add(new AbstractAction("do search") {
			public void actionPerformed(ActionEvent e) {

					Testing search = new Testing(50,20, 10, 0, null, world);

					search.execNRuns(1,RRTsearch.Algorithm.RRT);

					search.printStats("editorOut",false);//FIXME: halting bug.
					
					JDialog dialog = new JDialog();
					dialog.add(new GUI(world,search.getSearcher().getsearchTree()));
					dialog.pack();
					dialog.setVisible(true);
			}
		});
	}

	public void draw(Graphics2D g, World world, Tree tree){
		super.draw(g, world, tree);
		
		if( m == Mode.NEW_OBSTACLE_PT2 ){
			g.drawRect(						
					(int) Math.min( point.getX(), old.getX() ), //x
					(int) Math.min( point.getY(), old.getY() ), //y
					(int) Math.abs( old.getX() - point.getX() ), //w
					(int) Math.abs( old.getY() - point.getY() )  //h 
					);
		}
		
		if( m == Mode.DELETE_OBSTACLE ){
			List<Rectangle2D> list = ((RRTWorld)world).obstacles;
			Iterator<Rectangle2D> it = list.iterator();
			while( it.hasNext() ){
				Rectangle2D o = it.next();
				if( o.contains(point) ){
					g.setColor(Color.RED);
					g.fill(o);
					break;
				}
			}
		}

		print("\'"+file+"\' "+m+" @ "+world);
	}

	protected void print(String txt){ }
	
	protected void mark(Graphics2D g, int x, int y, Color c){
		g.setColor(c);
		g.drawLine(x-2,y-2,x+2,y+2);
		g.drawLine(x-2,y+2,x+2,y-2);
	}

	protected Point2D wtfMouse(MouseEvent e){
//		System.out.println( this.getBounds() +" "+ e.getPoint());

		return new Point2D.Double(e.getX()-WTF_W, e.getY()-WTF_H);	
	}

	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) {
		point = wtfMouse(e);
		
		if( e.getButton() == MouseEvent.BUTTON1 ){
			switch(m){
			case SET_GOAL:
				((RRTWorld)world).goal = point;
				break;
			case SET_START:
				((RRTWorld)world).start = point;
				break;
			case DELETE_OBSTACLE:
				List<Rectangle2D> list = ((RRTWorld)world).obstacles;
				Iterator<Rectangle2D> it = list.iterator();
				while( it.hasNext() ){
					Rectangle2D o = it.next();
					if( o.contains(point) ){
						it.remove();
						break;
					}
				}
				break;
			case NEW_OBSTACLE_PT1:
				old = point;
				m = Mode.NEW_OBSTACLE_PT2;
				break;
			case NEW_OBSTACLE_PT2:
				Rectangle2D rect = new Rectangle(
						(int) Math.min( point.getX(), old.getX() ), //x
						(int) Math.min( point.getY(), old.getY() ), //y
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
			menu.show(this, e.getX()-WTF_W, e.getY()-WTF_H);
	}

	public void mouseDragged(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { 
		if( m == Mode.NEW_OBSTACLE_PT2 || m == Mode.DELETE_OBSTACLE ){
			point = wtfMouse(e);
			repaint();
		}
	}

	public static void main(String[] args) throws Exception{
		String file = null;
		if( args.length < 1 ){
			final JFileChooser fc = new JFileChooser(new File("."));
			fc.showOpenDialog(null);
			file = fc.getSelectedFile().getAbsolutePath();
		}else
			file = args[0];
		
		if( file == null )
			return;
		
		final JFrame frame = new JFrame();
		final Editor editor = new Editor(file){
			protected void print(String txt){
				frame.setTitle(txt);
			}
		};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(editor);
		frame.pack();
		frame.setResizable(false); //avoids problems with mouse clicks.
		frame.setVisible(true);		
	}

}


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
