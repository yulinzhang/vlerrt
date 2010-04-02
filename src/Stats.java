
public class Stats {

	private int nIterations;
	private int nNodes;
	private boolean goalFound;
	private double gDistance;
	private double treeCoverage;
	private int wHeight;
	private int wWidth;
	private int pGoal;
	private int baseLength;
	private int pWayPoint;
	private double baseEpsilon;
	




	RRTsearch.Algorithm alg;
	long runtime;

	public Stats() {}


	public Stats(int nIterations, int nNodes, boolean goalFound,
			double gDistance, double treeCoverage, int wHeight, int wWidth, RRTsearch.Algorithm alg, long runtime) {
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



	public double getBaseEpsilon() {
		return baseEpsilon;
	}


	public void setBaseEpsilon(double baseEpsilon) {
		this.baseEpsilon = baseEpsilon;
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



	public void setAlg(RRTsearch.Algorithm alg) {
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


	public RRTsearch.Algorithm getAlg() {
		return alg;
	}


	public long getRuntime() {
		return runtime;
	}	
	
	
}
