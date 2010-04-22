package altrrt;

import java.util.List;

public interface Search {
	
	public Tree getSearchTree();

	public World getWorld();

	public void runSearch(int iterations, Stats stats);

	public int runSearchHalt(Stats stats);

	public void halt();

	public boolean foundGoal();
	
	List<Node> bestPlan();
	
	public Search reset();
	
	public Stats initStats();

}