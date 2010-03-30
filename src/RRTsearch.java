import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

public class RRTsearch {
	//TEST
	public static void main(String[] args){
		RRTsearch search = basicRRT(new RRTWorld(400,400),20,10);
		search.runSearch();
		RRTsearch searchVLRRT = VLRRT(new RRTWorld(400,400),20,10,1);
		searchVLRRT.runSearch();
	}
	
	private enum Algorithm {
		RRT, ERRT, VLRRT, VLERRT
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
	
	private void init() {
		searchTree = new RRTtree(new RRTnode(w.start(),null,baseEpsilon));
		r = new Random(System.currentTimeMillis());
		GUI.display(w, searchTree, "RRTWorld");
	}
	
	public void runSearch() {
		Node next = null;
		for (int count = 1; count < 100 && !done; count++){
			next = step();
			if (next != null) searchTree.add(next);
		}
	}
	
	private Node step() {
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
		double extensionLength = from.getEpsilon()*baseLength;
		if(toGoal && from.getPoint().distance(toward) < extensionLength) {  //We can get to the goal this time...
			destination = toward; //dest = the goal
			reachGoal = true;
		}
		else destination = nextPoint(from.getPoint(), toward, extensionLength);
		
		if(w.collides(from.getPoint(), destination)) { //collision, decrease the
			switch (type) { //extra functionality for VLRRT
			case VLRRT: 
			case VLERRT:
				decreaseEpsilon(from);
			}
			return null;
		}
		else {
			if (reachGoal) done = true;  //didn't collide, will reach goal
			double newEpsilon = baseEpsilon; //normal is the baseEpsilon (1 when not used)
			switch (type) {
			case VLRRT: 
			case VLERRT:
				newEpsilon = increaseEpsilon(from);
			}
			return new RRTnode(destination,from, newEpsilon);	
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
	
	private void decreaseEpsilon(Node n) {
		n.setEpsilon(n.getEpsilon() - .1);
	}
	
	private double increaseEpsilon(Node n) {
		double e = n.getEpsilon();
		double newEpsilon = e + .1;
		n.setEpsilon(newEpsilon);
		return newEpsilon;
	}
	
}
