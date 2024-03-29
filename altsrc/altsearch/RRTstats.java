package altsearch;
import altrrt.Node;
import altrrt.Stats;


public class RRTstats implements Stats {

	private int nIterations;
	private int nNodes;
	private boolean goalFound;
	private double gDistance;
	private double treeCoverage;
	private Node goal;
	private int wHeight;
	private int wWidth;
	private int pGoal;
	private int baseLength;
	private int pWayPoint;
	private long initTime;
	private long goalFTime;
	String alg;
	long runtime;

	@Override
	public String toString() {
		return alg + " \t " + baseLength + " \t "
				+ gDistance + " \t " + goalFTime + " \t " + goalFound
				+ " \t " + initTime + " \t " + nIterations + " \t " + nNodes
				+ " \t " + pGoal + " \t " + pWayPoint + " \t " + runtime
				+ " \t " + treeCoverage + " \t " + wHeight + " \t " + wWidth + " \t " + 
				(gDistance/treeCoverage) + " \t " + (goalFTime-initTime);
	}
	
	public RRTstats() {}

	public RRTstats(int nIterations, int nNodes, boolean goalFound,
			double gDistance, double treeCoverage, int wHeight, int wWidth, String alg, long runtime) {
		this.nIterations = nIterations;
		this.nNodes = nNodes;
		this.goalFound = goalFound;
		this.gDistance = gDistance;
		this.treeCoverage = treeCoverage;
		this.wHeight = wHeight;
		this.wWidth = wWidth;
		this.alg = alg;
		this.runtime = runtime;
	}

	public long getInitTime() {
		return initTime;
	}

	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}

	public long getGoalFTime() {
		return goalFTime;
	}
	
	public long getElapsedTime() {
		return goalFTime-initTime;
	}

	public void setGoalFTime(long goalFTime) {
		this.goalFTime = goalFTime;
	}

	public void setnIterations(int nIterations) {
		this.nIterations = nIterations;
	}

	public void setnNodes(int nNodes) {
		this.nNodes = nNodes;
	}

	public void setGoalFound(boolean goalFound) {
		this.goalFound = goalFound;
	}

	public void setgDistance(double gDistance) {
		this.gDistance = gDistance;
	}

	public void incgDistance(double step) {
		this.gDistance += step;
	}


	public void setTreeCoverage(double treeCoverage) {
		this.treeCoverage = treeCoverage;
	}
	
	public void incTreeCoverage(double treeCoverage) {
		this.treeCoverage += treeCoverage;
	}

	public void setwHeight(int wHeight) {
		this.wHeight = wHeight;
	}

	public void setwWidth(int wWidth) {
		this.wWidth = wWidth;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}

	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}

	public int getnIterations() {
		return nIterations;
	}

	public int getnNodes() {
		return nNodes;
	}

	public boolean isGoalFound() {
		return goalFound;
	}

	public double getgDistance() {
		return gDistance;
	}

	public double getTreeCoverage() {
		return treeCoverage;
	}

	public int getwHeight() {
		return wHeight;
	}

	public int getwWidth() {
		return wWidth;
	}
	
	public int getpGoal() {
		return pGoal;
	}

	public void setpGoal(int pGoal) {
		this.pGoal = pGoal;
	}

	public int getBaseLength() {
		return baseLength;
	}

	public void setBaseLength(int baseLength) {
		this.baseLength = baseLength;
	}

	public int getpWayPoint() {
		return pWayPoint;
	}

	public void setpWayPoint(int pWayPoint) {
		this.pWayPoint = pWayPoint;
	}

	public String getAlg() {
		return alg;
	}

	public long getRuntime() {
		return runtime;
	}

	public void setGoal(Node goal) {
		this.goal = goal;
	}

	public Node getGoal() {
		return goal;
	}	
}
