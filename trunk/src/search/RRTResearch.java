package search;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.SortedMap;


import rrt.Node;
import rrt.Tree;
import rrt.World;
import rrtImpl.DVLRRTnode;
import rrtImpl.DirectionalEpsilonMap;
import rrtImpl.RRTnode;
import rrtImpl.VLRRTnode;
import rrtImpl.VLRRTnode.changeEpsilonScheme;
import testing.Stats;






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
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.RRT, 5);
	}
	
	public static RRTsearch ERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.ERRT, 5);
	}

	public static RRTsearch VLRRT(World w, int p, int baseLength) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.VLRRT, 5);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, int nClosest) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, nClosest);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.DVLRRT, 5);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, int nClosest) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT, nClosest);
	}

	public static RRTsearch VLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.VLRRT, inc, incFactor, dec, decFactor, 5);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, int nClosest) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, inc, incFactor, dec, decFactor, nClosest);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.DVLRRT, inc, incFactor, dec, decFactor, 5);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, int nClosest) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT, inc, incFactor, dec, decFactor, nClosest);
	}
	
	//Optimization parameters
	public static RRTsearch basicRRT(World w, int p, int baseLength, boolean optimize) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.RRT, null, 0 ,null, 0, optimize, 5);
	}
	
	public static RRTsearch ERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, boolean optimize) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.ERRT, null, 0, null, 0, optimize, 5);
	}
	
	public static RRTsearch VLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.VLRRT, inc, incFactor, dec, decFactor, optimize, 5);
	}
	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize, int nClosest) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, inc, incFactor, dec, decFactor, optimize, nClosest);
	}
	
	public static RRTsearch DVLRRT(World w, int p, int baseLength, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize) {
		return new RRTResearch(w, p, baseLength, null, 0, Algorithm.DVLRRT, inc, incFactor, dec, decFactor, optimize,5);
	}
	
	public static RRTsearch DVLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize, int nClosest) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.DVLERRT, inc, incFactor, dec, decFactor, optimize, nClosest);
	}
	
	//Replanning VLERRT Strategies

	
	public static RRTsearch VLERRT(World w, int pGoal, int baseLength, int pWayPoint, List<Node> wayPoints, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor, boolean optimize, RRTResearch.averageStrat strat, int nClosest) {
		return new RRTResearch(w, pGoal, baseLength, wayPoints, pWayPoint, Algorithm.VLERRT, inc, incFactor, dec, decFactor, optimize, strat, nClosest);
	}
	

	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type, int nClosest) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type);
		this.strat = averageStrat.Simple;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
		this.nClosest = nClosest;

	
	}
	
	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type , RRTResearch.averageStrat strat, int nClosest) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type);
		this.strat = strat;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
		this.nClosest = nClosest;
	
	}

	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type,
			VLRRTnode.changeEpsilonScheme inc, double incFactor, VLRRTnode.changeEpsilonScheme dec,
			double decFactor, int nClosest) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor,
				dec, decFactor);
		this.strat = averageStrat.Simple;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
		this.nClosest = nClosest;
	}



	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type,
			VLRRTnode.changeEpsilonScheme inc, double incFactor, VLRRTnode.changeEpsilonScheme dec,
			double decFactor, RRTResearch.averageStrat strat, int nClosest) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor,
				dec, decFactor);
		this.strat = strat;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
		this.nClosest = nClosest;
	}

	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type,
			VLRRTnode.changeEpsilonScheme inc, double incFactor, VLRRTnode.changeEpsilonScheme dec,
			double decFactor, boolean optimize, int nClosest) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor,
				dec, decFactor, optimize);
		this.strat = averageStrat.Simple;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
		this.nClosest = nClosest;
	
	}

	
	public RRTResearch(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type,
			VLRRTnode.changeEpsilonScheme inc, double incFactor, VLRRTnode.changeEpsilonScheme dec,
			double decFactor, boolean optimize, RRTResearch.averageStrat strat, int nClosest) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor,
				dec, decFactor, optimize);
		this.strat = strat;
		this.dThreshold = (w.height()*w.width())*(RRTResearch.thresholdFactor);
		this.nClosest = nClosest;
	
	}
	


	private Node[] potentialNeighbours(Point2D point, int nClosest) {
		return prevSearch.nClosestTo(point, nClosest);
	}
	
	
	private DirectionalEpsilonMap calculateMap(Point2D point, Node[] potentialNeighbs) { //For DVLRRT
		if (potentialNeighbs.length == 0)
			return null;
		
		DirectionalEpsilonMap res = new DirectionalEpsilonMap();
		double distance = 0.0;
		
		for (int i=0;i<potentialNeighbs.length;i++) {
			Node current = potentialNeighbs[i];
			if (current instanceof DVLRRTnode) {
				distance += point.distance(current.getPoint());
				double dirNP = DVLRRTnode.computeAngle(current.getPoint(), point);
				double epsCurrent = current.getExtensionLength(dirNP);
				res.setEpsilon(DVLRRTnode.computeAngle(point, current.getPoint()), epsCurrent);
			}
		}
		
		if ((distance/potentialNeighbs.length) > dThreshold) {
			return null;
		}
		else {
			return res;
		}
		
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
		
		if ((distance/potentialNeighbs.length) > dThreshold) {
			return 0;
		}
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
		if ((distance/potentialNeighbs.length) > dThreshold) {
			return 0;
		}
		else
			return (res/(potentialNeighbs.length * distance));
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
	
	@Override
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
		case DVLERRT: {
			DirectionalEpsilonMap m = null;
			Node[] neighbs;
			if (prevSearch != null) {
				neighbs = potentialNeighbours(point,nClosest);
				if (neighbs != null)
					m = calculateMap(point, neighbs);
			}
			return new DVLRRTnode(point, (DVLRRTnode) parent, baseLength, inc, incFactor, dec, decFactor, m);
		}
		}
		return null;  //exception
	}
	
}
