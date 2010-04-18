package nodeTypes;
import gui.GUI;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import rrt.World;

/*
 *  intersects with the INTERIOR of the rectangle (*NOT* its border)
 */

public class RRTWorld implements World {
	
	public static void main(String[] args) throws Exception{
		RRTWorld w = new RRTWorld("asd");
//		w.write("asd");
		GUI.display(w, null, "ok");
	}
	
	protected Random r;
	protected List<Rectangle2D> obstacles;
	
	protected int w, h;
	protected Point2D start, goal;
	
	public RRTWorld(RRTWorld world) {
		r = world.r;
		start = world.start;
		goal = world.goal;
		w = world.w;
		h = world.h;
		
		obstacles = new LinkedList<Rectangle2D>();
		Iterator<Rectangle2D> itr = world.obstacles.iterator();
		while (itr.hasNext()) {
			obstacles.add(itr.next());
		}
	}
	
	public RRTWorld(String file) throws Exception{
		r = new Random(System.currentTimeMillis());
		
		Scanner sc = new Scanner(new File(file));
		
		w = sc.nextInt();
		h = sc.nextInt();
		
		start = new Point2D.Double(sc.nextInt(), sc.nextInt());
		goal = new Point2D.Double(sc.nextInt(), sc.nextInt());
		
		obstacles = new LinkedList<Rectangle2D>();
		
		//note that his assumes the format is always correct and that if
		//there is a next, then there will always exist the 4 next ints.
		while(sc.hasNext()){
			obstacles.add( new Rectangle(
					sc.nextInt(), //x
					sc.nextInt(), //y
					sc.nextInt(), //w
					sc.nextInt()  //h
					));
		}
		sc.close();
	}
	

	public void write(String file) throws Exception{
		PrintWriter o = new PrintWriter(new File(file));
		o.printf("%d\t%d\n", w,h);
		o.printf("%d\t%d\n", (int)start.getX(), (int)start.getY());
		o.printf("%d\t%d\n", (int)goal.getX(), (int)goal.getY());
		for(Rectangle2D r : obstacles)
			o.printf("%d\t%d\t%d\t%d\n", 
					(int)r.getX(), 
					(int)r.getY(), 
					(int)r.getWidth(), 
					(int)r.getHeight());
		o.close();
	}
	
	public RRTWorld(int w, int h){ //TODO: more initial parameters?
		this.r = new Random(System.currentTimeMillis());
		this.w = w;
		this.h = h;

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

	public List<Rectangle2D> obstacles() {
		return obstacles;
	}

	public int width() {
		return w;
	}


	public void setGoal(Point2D goal) {
		this.goal = goal;
	}



	public void setStart(Point2D start) {
		this.start = start;
	}
}
