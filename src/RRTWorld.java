import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/*
 *  intersects with the INTERIOR of the rectangle (*NOT* its border)
 *  TODO: world in a text file
 */

public class RRTWorld implements World {
	
	protected Random r;
	protected List<Rectangle2D> obstacles;
	
	protected int w, h;
	protected Point2D start, goal;
	protected Tree searchTree = null;
	
	public RRTWorld(int w, int h){ //TODO: more initial parameters?
		super();
		this.w = w;
		this.h = h;
		this.r = new Random(System.currentTimeMillis());

		obstacles = new LinkedList<Rectangle2D>();
		
		//TODO: better obstacle generation
		int n = 10;
		while( n-- > 0 )
			obstacles.add( new Rectangle(r.nextInt(w),r.nextInt(h),20,20));

		//TODO: distance to other points?
		do{
			start = new Point2D.Double(r.nextInt(w), r.nextInt(h));
		}while(collides(start));
		do{
			goal = new Point2D.Double(r.nextInt(w), r.nextInt(h));
		}while(collides(goal));

	}

	public boolean collides(Point2D point){
		if( point.getX() < 0 || point.getY() < 0 || point.getX() >= w || point.getY() >= h )
			return true;
		
		for(Rectangle2D rect : obstacles){
			if( rect.contains(point) )
				return true;
		}
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

	public Point2D randomPoint() {
		return new Point2D.Double(r.nextInt(w),r.nextInt(h));
	}

	public Point2D goal() {
		return goal;
	}

	public Point2D start() {
		return start;
	}

	public int height() {
		return h;
	}

	public Iterable<Rectangle2D> obstacles() {
		return obstacles;
	}

	public int width() {
		return w;
	}
}
