package search;
import gui.GUI;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import rrt.Node;
import rrt.Tree;
import rrt.World;
import rrtImpl.DVLRRTnode;
import rrtImpl.RRTWorld;
import rrtImpl.RRTnode;
import rrtImpl.RRTtree;
import rrtImpl.VLRRTnode;
import testing.Stats;

public class RRTsearch {
	//TEST
	public static void main(String[] args){
		World testWorld;
		try {
			testWorld = new RRTWorld("worlds/proposal-world");
		} catch (Exception e) {
			testWorld = new RRTWorld(400,400);
		}

		
		RRTsearch search = basicRRT(testWorld,20,10);
		search.runSearch(1200);
		search.show("RRT", true);
		
		RRTsearch searchV = VLRRT(testWorld,20,10,VLRRTnode.changeEpsilonScheme.Mult, 2, 
									VLRRTnode.changeEpsilonScheme.Restart,1);
		searchV.runSearch(1200);
		searchV.show("VLRRT", true);
		
		try {
			testWorld = new RRTWorld("worlds/proposal-world");
		} catch (Exception e) {
			testWorld = new RRTWorld(400,400);
		}
	
		RRTsearch searchD = DVLRRT(testWorld,20,10,VLRRTnode.changeEpsilonScheme.Mult, 2, 
									VLRRTnode.changeEpsilonScheme.Restart,1);
		searchD.runSearch(1200);
		searchD.show("DVLRRT",true);
		
	}
	
	public enum Algorithm {
		RRT, //standard RRT algorithm
		ERRT, //standard RRT algorithm with replanning using waypoints
		VLRRT,  //variable length RRT algorithm.  Nodes keep track of a single epsilon value 
		VLERRT, //variable length RRT algorithm with replanning using waypoints.
		DVLRRT, //Directional variable length RRT algorithm.  Nodes keep track of epsilons for tested directions and infer epsilons for new directions
		DVLERRT  //Directional variable length RRT algorithm with replanning  using waypoints.
	}
	
	public static RRTsearch basicRRT(World w, int p, int baseLength) {
		return new RRTsearch(w, p, baseLength, null, 0, Algorithm.RRT);
	}
	
	public static RRTsearch ERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints) {
		return new RRTsearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.ERRT);
	}

	public static RRTsearch VLRRT(World w, int p, int baseLength) {
		return new RRTsearch(w, p, baseLength, null, 0, Algorithm.VLRRT);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints) {
		return new RRTsearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength) {
		return new RRTsearch(w, p, baseLength, null, 0, Algorithm.DVLRRT);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints) {
		return new RRTsearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT);
	}

	public static RRTsearch VLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTsearch(w, p, baseLength, null, 0, Algorithm.VLRRT, inc, incFactor, dec, decFactor);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTsearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, inc, incFactor, dec, decFactor);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTsearch(w, p, baseLength, null, 0, Algorithm.DVLRRT, inc, incFactor, dec, decFactor);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTsearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT, inc, incFactor, dec, decFactor);
	}
	
	//Optimization parameters
	public static RRTsearch basicRRT(World w, int p, int baseLength, boolean optimize) {
		return new RRTsearch(w, p, baseLength, null, 0, Algorithm.RRT, null, 0 ,null, 0, optimize);
	}
	
	public static RRTsearch ERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, boolean optimize) {
		return new RRTsearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.ERRT, null, 0, null, 0, optimize);
	}
	
	public static RRTsearch VLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTsearch(w, p, baseLength, null, 0, Algorithm.VLRRT, inc, incFactor, dec, decFactor, optimize);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTsearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, inc, incFactor, dec, decFactor, optimize);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTsearch(w, p, baseLength, null, 0, Algorithm.DVLRRT, inc, incFactor, dec, decFactor, optimize);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTsearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT, inc, incFactor, dec, decFactor, optimize);
	}
	
	
	//parameters
	protected World w;  //world to search in (includes start and goal points)
	protected int pGoal = 20; //0 <= pGoal <= 100-pWayPoint - probability to extend towards the goal
	protected int pWayPoint = 0; // 0 <= pWayPoint <= 100-pGoal - probability to extend towards a waypoint
	protected int baseLength = 10;  //base extension distance
	protected Algorithm type;
	protected VLRRTnode.changeEpsilonScheme inc = VLRRTnode.changeEpsilonScheme.Linear;
	protected double incFactor = .1;
	protected VLRRTnode.changeEpsilonScheme dec= VLRRTnode.changeEpsilonScheme.Linear;
	protected double decFactor = .1;
	
	protected Random r;  
	protected List<Node> wayPoints;
	protected Tree searchTree;
	protected boolean done = false;
	
	protected boolean optimize = false;
	
	public RRTsearch() {
		w = new RRTWorld(400,400);	
		init();
		type = Algorithm.RRT;
	}
	
	public RRTsearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type) {
		this.pGoal = pGoal;
		this.w = w;
		this.baseLength = baseLength;
		this.wayPoints = wayPoints;
		this.pWayPoint = pWayPoint;
		this.type = type;
		
		init();

	}
	
	public RRTsearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,
			VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		this (w, pGoal, baseLength, wayPoints, pWayPoint, 
				type, inc, incFactor, dec, decFactor, false);
		
	}
	
	public RRTsearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,
			VLRRTnode.changeEpsilonScheme dec, double decFactor,
			boolean optimize) {
		this.pGoal = pGoal;
		this.w = w;
		this.baseLength = baseLength;
		this.wayPoints = wayPoints;
		this.pWayPoint = pWayPoint;
		this.type = type;
		this.dec = dec;
		this.decFactor = decFactor;
		this.inc = inc;
		this.incFactor = incFactor;
		this.optimize = optimize;
		init();
		
	}
	
	
	static public double euclidianDistance(Point2D p1, Point2D p2) {
		
		return Math.sqrt(Math.pow(p1.getX()-p2.getX(), 2)+Math.pow(p1.getY()-p2.getY(),2));
	}
	
	
	private void init() {
		Node initNode = null;
		switch(type) {
		case RRT:
		case ERRT:
			initNode = new RRTnode(w.start(), null, baseLength); break;
		case VLRRT:
		case VLERRT:
			initNode = new VLRRTnode(w.start(), null, baseLength, inc, incFactor, dec, decFactor); break;
		case DVLRRT:
		case DVLERRT:
			initNode = new DVLRRTnode(w.start(), null, baseLength, inc, incFactor, dec, decFactor); break;
		}
		
		searchTree = new RRTtree(initNode);
		r = new Random(System.currentTimeMillis());
	}
	
	public Tree getSearchTree() {
		return searchTree;
	}
	
	public World getWorld() {
		return w;
	}
	
	public List<Node> getWaypoints(){
		return wayPoints;
	}
	
	public void show(){
		GUI.display(this, "RRTWorld");
//		GUI.screenshot(w, searchTree, "asd");	
	}
	
	public void show(String title){
		GUI.display(this, title);
//		GUI.screenshot(w, searchTree, "asd");	
	}
	
	public void show(String title, boolean halos){
		GUI.display(this, title, halos);
//		GUI.screenshot(w, searchTree, "asd");	
	}
	
	public void screenshot(String filename) {
		GUI.screenshot(this,filename);
	}
	
	public void runSearch(int iterations) {
		Node next = null;
		for (int count = 0; count < iterations && !done; count++){
			next = step(new Stats()); //Dummy
			if (next != null) searchTree.add(next);
		}
	}
	
	public int runSearchHalt(Stats stats, int timer) {
		int nItrs = 0;
		Node next = null;
		stats.setInitTime(System.nanoTime());
		
		long init = System.currentTimeMillis();
		while (!done ) {
			next = step(stats);
			if (next != null) searchTree.add(next);
			nItrs++;
			
			if( ( System.currentTimeMillis() - init ) > timer )
				break;
		}
		return nItrs;
		
	}
	
	public boolean foundGoal() {
		return done;
	}
	
	private Node step(Stats stats) {
		Point2D toward, destination;
		Node from;
		boolean toGoal = false, reachGoal = false;
		
		//find the point to extend towards
		int p = r.nextInt(100);
		if ( p < pGoal) {
			toward = w.goal();
			toGoal = true; //
		} else if (wayPoints != null && p < pGoal+pWayPoint)
			toward = wayPoints.get(r.nextInt(wayPoints.size())).getPoint();
		else {
			toward = w.randomPoint();
		}
		
		//find the Node to extend from
		from = searchTree.closestTo(toward);
		double extensionLength = from.getExtensionLength(toward); //getEpsilon()*baseLength;
		if(from.getPoint().distance(toward) < extensionLength) {  //We can get to the point we are extending towards
			destination = toward; //dest = the point
			if (toGoal) reachGoal = true;  //in fact, it is the goal and we might be done!
		}
		else destination = nextPoint(from.getPoint(), toward, extensionLength);
		
		if(w.collides(from.getPoint(), destination)) { //collision, decrease the
			from.reportExtensionStatus(toward, false);
			return null;
		}
		else {
			from.reportExtensionStatus(toward, true);
			Node ret = getNewNode(destination, from);
			
			if (reachGoal) {
				done = true;  //didn't collide, will reach goal
				stats.setGoalFTime(System.nanoTime());
				stats.setGoal(ret);
				if (optimize) optimizePath(ret);
			}
			
			stats.incTreeCoverage(RRTsearch.euclidianDistance(from.getPoint(), destination));
			return ret;
		}	
	}
	
	protected Point2D.Double nextPoint(Point2D origin, Point2D towards, double length) {
	
		double xDelta,yDelta,hypotenuse;
		double originX, originY, destinationX, destinationY;
		double newXDelta, newYDelta;
		double newXCoord, newYCoord;
		
		//coordinates of the two points
		originX = origin.getX();
		originY = origin.getY();
		destinationX = towards.getX();
		destinationY = towards.getY();
		
		//Right Triangle side lengths
		xDelta = Math.abs(originX - destinationX);
		yDelta = Math.abs(originY - destinationY);	
		hypotenuse = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta,2));
		
		//side lengths of the similar right triangle with hypotenuse length length
		newXDelta = length*(xDelta/hypotenuse);
		newYDelta = length*(yDelta/hypotenuse);
		
		if (originX < destinationX) newXCoord = originX + newXDelta;
		else newXCoord = originX - newXDelta;
		
		if (originY < destinationY) newYCoord = originY + newYDelta;
		else newYCoord = originY - newYDelta;
		
		return new Point2D.Double(newXCoord, newYCoord);	
	}
	
	private Node getNewNode(Point2D point, Node parent) {
		switch(type) {
		case RRT: 
		case ERRT:
			return new RRTnode(point, parent, baseLength);
		case VLRRT:
		case VLERRT:
			return new VLRRTnode(point, (VLRRTnode) parent, baseLength, inc, incFactor, dec, decFactor);
		case DVLRRT:
		case DVLERRT:
			return new DVLRRTnode(point, (DVLRRTnode) parent, baseLength, inc, incFactor, dec, decFactor);
		}
		return null;  //exception
	}
	
	protected void optimizePath(Node n) {
		Node start = n;
		Node optimized, current;
		
		do {
			current = start.getParent();
			optimized = current;
			
			
			while (optimized.getParent() != null &&  //loop until reached root
					!w.collides(start.getPoint(), optimized.getParent().getPoint())) {  //or path between start and candidate not clear
				optimized = optimized.getParent();
			}
			if (optimized != current) start.setParent(optimized);
			
			start = start.getParent();  //next node to try to optimize
		} while (start.getParent() != null);	
	}
	
	public LinkedList<Node> collectBestPlan() {
		LinkedList<Node> l = new LinkedList<Node>();
		Node g = searchTree.closestTo(w.goal());
		while (!g.isRoot()) {
			l.addFirst(g);
			g = g.getParent();	
		}
		l.addFirst(g);
		return l;
	}
	
}
