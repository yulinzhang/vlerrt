package altrrt;

public interface Stats {

	public abstract String toString();

	public abstract long getInitTime();

	public abstract void setInitTime(long initTime);

	public abstract long getGoalFTime();

	public abstract long getElapsedTime();

	public abstract void setGoalFTime(long goalFTime);

	public abstract void setnIterations(int nIterations);

	public abstract void setnNodes(int nNodes);

	public abstract void setGoalFound(boolean goalFound);

	public abstract void setgDistance(double gDistance);

	public abstract void incgDistance(double step);

	public abstract void setTreeCoverage(double treeCoverage);

	public abstract void incTreeCoverage(double treeCoverage);

	public abstract void setwHeight(int wHeight);

	public abstract void setwWidth(int wWidth);

	public abstract void setAlg(String alg);

	public abstract void setRuntime(long runtime);

	public abstract int getnIterations();

	public abstract int getnNodes();

	public abstract boolean isGoalFound();

	public abstract double getgDistance();

	public abstract double getTreeCoverage();

	public abstract int getwHeight();

	public abstract int getwWidth();

	public abstract int getpGoal();

	public abstract void setpGoal(int pGoal);

	public abstract int getBaseLength();

	public abstract void setBaseLength(int baseLength);

	public abstract int getpWayPoint();

	public abstract void setpWayPoint(int pWayPoint);

	public abstract String getAlg();

	public abstract long getRuntime();

	public abstract void setGoal(Node goal);

	public abstract Node getGoal();

}