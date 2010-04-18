package rrt;

import java.awt.geom.Point2D;
import java.util.List;

public interface Search {

	public enum Algorithm {
		RRT, //standard RRT algorithm
		ERRT, //standard RRT algorithm with replanning using waypoints
		VLRRT,  //variable length RRT algorithm.  Nodes keep track of a single epsilon value 
		VLERRT, //variable length RRT algorithm with replanning using waypoints.
		DVLRRT, //Directional variable length RRT algorithm.  Nodes keep track of epsilons for tested directions and infer epsilons for new directions
		DVLERRT  //Directional variable length RRT algorithm with replanning  using waypoints.
	}
	
	public Tree getSearchTree();

	public World getWorld();

	public void runSearch(int iterations, Stats stats);

	public int runSearchHalt(Stats stats);

	public void halt();

	public boolean foundGoal();
	
	List<Node> bestPlan();

}