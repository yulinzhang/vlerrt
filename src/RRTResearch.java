import java.awt.geom.Point2D;
import java.util.List;






public class RRTResearch extends RRTsearch {

	public static final double thresholdFactor = 0.05;
	
	private Tree prevSearch;
	public Tree getPrevSearch() {
		return prevSearch;
	}

	public void setPrevSearch(Tree prevSearch) {
		this.prevSearch = prevSearch;
	}

	private int nClosest = 5;
	private double dThreshold; 
	public enum averageStrat {
		Weighted, //Weighted average based on distance
		Simple; //Simple average
	}
	
	private averageStrat strat;

	public static RRTsearch basicRRT(World w, int p, int baseLength) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.RRT);
	}
	
	public static RRTsearch ERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.ERRT);
	}

	public static RRTsearch VLRRT(World w, int p, int baseLength) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.VLRRT);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.DVLRRT);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT);
	}

	public static RRTsearch VLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.VLRRT, inc, incFactor, dec, decFactor);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, inc, incFactor, dec, decFactor);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.DVLRRT, inc, incFactor, dec, decFactor);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT, inc, incFactor, dec, decFactor);
	}
	
	//Optimization parameters
	public static RRTsearch basicRRT(World w, int p, int baseLength, boolean optimize) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.RRT, null, 0 ,null, 0, optimize);
	}
	
	public static RRTsearch ERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, boolean optimize) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.ERRT, null, 0, null, 0, optimize);
	}
	
	public static RRTsearch VLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.VLRRT, inc, incFactor, dec, decFactor, optimize);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, inc, incFactor, dec, decFactor, optimize);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.DVLRRT, inc, incFactor, dec, decFactor, optimize);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT, inc, incFactor, dec, decFactor, optimize);
	}
	
	//Replanning VLERRT Strategies

	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize, RRTResearch.averageStrat strat) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, inc, incFactor, dec, decFactor, optimize, strat);
	}
	

	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type);
		this.strat = averageStrat.Simple;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
	
	}
	
	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type , RRTResearch.averageStrat strat) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type);
		this.strat = strat;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
	
	}

	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type,
			VLRRTnode.changeEpsilonScheme inc, double incFactor, VLRRTnode.changeEpsilonScheme dec,
			double decFactor) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor,
				dec, decFactor);
		this.strat = averageStrat.Simple;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
	}



	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type,
			VLRRTnode.changeEpsilonScheme inc, double incFactor, VLRRTnode.changeEpsilonScheme dec,
			double decFactor, RRTResearch.averageStrat strat) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor,
				dec, decFactor);
		this.strat = strat;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
	}

	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type,
			VLRRTnode.changeEpsilonScheme inc, double incFactor, VLRRTnode.changeEpsilonScheme dec,
			double decFactor, boolean optimize) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor,
				dec, decFactor, optimize);
		this.strat = averageStrat.Simple;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
	
	}

	
	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type,
			VLRRTnode.changeEpsilonScheme inc, double incFactor, VLRRTnode.changeEpsilonScheme dec,
			double decFactor, boolean optimize, RRTResearch.averageStrat strat) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor,
				dec, decFactor, optimize);
		this.strat = strat;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
	
	}
	


	private Node[] potentialNeighbours(Point2D point, int nClosest) {
		return prevSearch.nClosestTo(point, nClosest);
	}
	
	//SimpleAvg of all epsilons of neighbors
	private double calculateAvgEpsilon(Point2D point, Node[] potentialNeighbs) {
		
		
		if (potentialNeighbs.length == 0)
			return 0.0;
		
		double res = 0.0;
		double distance = 0.0;
		
		for (int i=0; i<potentialNeighbs.length;i++) {
			Node current = potentialNeighbs[i];
			if (current instanceof VLRRTnode) {
				res += ((VLRRTnode)current).epsilon;
				distance += point.distance(current.getPoint());
			}
		}
		
		if ((distance/potentialNeighbs.length) < dThreshold)
			return 0;
		else
			return (res/potentialNeighbs.length);
	}
	
	//Weighted Avg.
	private double calculateWAvgEpsilon(Point2D point, Node[] potentialNeighbs) {
		
		if (potentialNeighbs.length == 0)
			return 0.0;
		
		double res = 0.0;
		double distance = 0.0;
		
		for (int i=0; i<potentialNeighbs.length;i++) {
			Node current = potentialNeighbs[i];
			if (current instanceof VLRRTnode) {
				res += (((VLRRTnode)current).epsilon * (distance += point.distance(current.getPoint())));
			}
		}
		if ((distance/potentialNeighbs.length) < dThreshold)
			return 0;
		else
			return (res/(potentialNeighbs.length * distance));
	}
	
	protected Node step(Stats stats) {
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
	
	private Node getNewNode(Point2D point, Node parent) {
		switch(type) {
		case RRT: 
		case ERRT:
			return new RRTnode(point, parent, baseLength);
		case VLRRT: {
			
		}
		case VLERRT: {
			double potentialEpsilon = 0.0;
			Node[] neighbs;
			if (prevSearch != null) {
				neighbs = potentialNeighbours(point,nClosest);
				if (neighbs != null)
					switch (strat) {
					case Weighted:
						{potentialEpsilon = calculateWAvgEpsilon(point, neighbs);
						break;}
					case Simple:
						{potentialEpsilon = calculateAvgEpsilon(point,neighbs); break;}
					}
			}
			
			return new VLRRTnode(point, (VLRRTnode) parent, baseLength, inc, incFactor, dec, decFactor, potentialEpsilon);
		}
		case DVLRRT:
		case DVLERRT:
			return new DVLRRTnode(point, (DVLRRTnode) parent, baseLength, inc, incFactor, dec, decFactor);
		}
		return null;  //exception
	}
	
}
