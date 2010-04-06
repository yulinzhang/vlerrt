import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

public class RRTsearch {
	//TEST
	public static void main(String[] args){
		World testWorld;
		try {
			testWorld = new RRTWorld("asd");
		} catch (Exception e) {
			testWorld = new RRTWorld(400,400);
		}

		
		RRTsearch search = basicRRT(testWorld,20,10);
		search.runSearch();
		search.show();

		try {
			testWorld = new RRTWorld("asd");
		} catch (Exception e) {
			testWorld = new RRTWorld(400,400);
		}
		
		RRTsearch searchVLRRT = VLRRT(testWorld,20,10,1);
		searchVLRRT.runSearch();
		searchVLRRT.show();
		
		try {
			testWorld = new RRTWorld("asd");
		} catch (Exception e) {
			testWorld = new RRTWorld(400,400);
		}
		
		RRTsearch searchDVLRRT = DVLRRT(testWorld,20,10,1);
		searchDVLRRT.runSearch();
		searchDVLRRT.show();
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
		return new RRTsearch(w, p, baseLength, 1, null, 0, Algorithm.RRT);
	}
	
	public static RRTsearch ERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints) {
		return new RRTsearch(w, pGoal, baseLength, 1, wayPoints, pWayPoint, Algorithm.ERRT);
	}

	public static RRTsearch VLRRT(World w, int p, int baseLength, double baseEpsilon) {
		return new RRTsearch(w, p, baseLength, baseEpsilon, null, 0, Algorithm.VLRRT);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, double baseEpsilon, int pWayPoint, List<Node> wayPoints) {
		return new RRTsearch(w, pGoal, baseLength, baseEpsilon, wayPoints, pWayPoint, Algorithm.VLERRT);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength, double baseEpsilon) {
		return new RRTsearch(w, p, baseLength, baseEpsilon, null, 0, Algorithm.DVLRRT);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, double baseEpsilon, int pWayPoint, List<Node> wayPoints) {
		return new RRTsearch(w, pGoal, baseLength, baseEpsilon, wayPoints, pWayPoint, Algorithm.DVLERRT);
	}
	
	//parameters
	private World w;  //world to search in (includes start and goal points)
	private int pGoal = 20; //0 <= pGoal <= 100-pWayPoint - probability to extend towards the goal
	private int pWayPoint = 0; // 0 <= pWayPoint <= 100-pGoal - probability to extend towards a waypoint
	private int baseLength = 10;  //base extension distance
	private double baseEpsilon = 1;  //base multiplier to the extension distance
	private Algorithm type;
	
	private Random r;  
	private List<Node> wayPoints;
	private Tree searchTree;
	private boolean done = false;
	private boolean halt = false;
	
	public RRTsearch() {
		w = new RRTWorld(400,400);	
		init();
		type = Algorithm.RRT;
	}
	
	public RRTsearch(World w, int pGoal, int baseLength, double baseEpsilon,
			List<Node> wayPoints, int pWayPoint, Algorithm type) {
		this.pGoal = pGoal;
		this.w = w;
		this.baseEpsilon = baseEpsilon;
		this.baseLength = baseLength;
		this.wayPoints = wayPoints;
		this.pWayPoint = pWayPoint;
		this.type = type;
		
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
			initNode = new RRTnode(w.start(), null, baseLength);
		case VLRRT:
		case VLERRT:
			initNode = new VLRRTnode(w.start(), null, baseLength);
		case DVLRRT:
		case DVLERRT:
			initNode = new DVLRRTnode(w.start(), null, baseLength);
		}
		
		searchTree = new RRTtree(initNode);
		r = new Random(System.currentTimeMillis());
	}
	
	public Tree getsearchTree() {
		return searchTree;
	}
	
	public void show(){
		GUI.display(w, searchTree, "RRTWorld");
//		GUI.screenshot(w, searchTree, "asd");	
	}
	
	public void screenshot(String filename) {
		GUI.screenshot(w,searchTree,filename);
	}
	
	
	
	public void runSearch() {
		Node next = null;
		for (int count = 1; count < 100 && !done; count++){
			next = step(new Stats()); //Dummy
			if (next != null) searchTree.add(next);
		}
	}
	
	public int runSearchHalt(Stats stats) {
		int nItrs = 0;
		Node next = null;
		stats.setInitTime(System.nanoTime());
		while (!done && !halt) {
			next = step(stats);
			if (next != null) searchTree.add(next);
			nItrs++;
		}
		return nItrs;
		
	}
	
	public void halt() {
		halt = true;
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
		if(toGoal && from.getPoint().distance(toward) < extensionLength) {  //We can get to the goal this time...
			destination = toward; //dest = the goal
			reachGoal = true;
		}
		else destination = nextPoint(from.getPoint(), toward, extensionLength);
		
		if(w.collides(from.getPoint(), destination)) { //collision, decrease the
			from.reportExtensionStatus(toward, false);
			return null;
		}
		else {
			if (reachGoal) {
				done = true;  //didn't collide, will reach goal
				stats.setGoalFTime(System.nanoTime());
			}
			from.reportExtensionStatus(toward, true);
			stats.incTreeCoverage(RRTsearch.euclidianDistance(from.getPoint(), destination));
			return getNewNode(destination, from);	
		}	
	}
	
	private Point2D.Double nextPoint(Point2D origin, Point2D towards, double length) {
	
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
			return new VLRRTnode(point, (VLRRTnode) parent, baseLength);
		case DVLRRT:
		case DVLERRT:
			return new DVLRRTnode(point, (DVLRRTnode) parent, baseLength);
		}
		return null;  //exception
	}
	
}
