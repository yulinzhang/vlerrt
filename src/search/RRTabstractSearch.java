package search;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import rrt.Node;
import rrt.Search;
import rrt.Stats;
import rrt.Tree;
import rrt.World;

public abstract class RRTabstractSearch implements Search {

	public enum Algorithm {
		RRT, //standard RRT algorithm
		ERRT, //standard RRT algorithm with replanning using waypoints
		VLRRT,  //variable length RRT algorithm.  Nodes keep track of a single epsilon value 
		VLERRT, //variable length RRT algorithm with replanning using waypoints.
		DVLRRT, //Directional variable length RRT algorithm.  Nodes keep track of epsilons for tested directions and infer epsilons for new directions
		DVLERRT  //Directional variable length RRT algorithm with replanning  using waypoints.
	}
	
	protected World world;  //world to search in (includes start and goal points)
	protected int pGoal; //0 <= pGoal <= 100 - probability to extend towards the goal
	protected int baseLength;  //base extension distance
	protected Algorithm type;
	
	protected Random r;
	protected Tree searchTree;
	protected boolean foundGoal = false;
	protected boolean halt = false;
	
	public RRTabstractSearch(World world, int pGoal, int baseLength) {
		this.world = world;
		this.pGoal = pGoal;
		this.baseLength = baseLength;
		r = new Random(System.currentTimeMillis());
	}
	
	public boolean foundGoal() {
		return foundGoal;
	}

	public Tree getSearchTree() {
		return searchTree;
	}

	public World getWorld() {
		return world;
	}

	public void halt() {
		halt = true;

	}
	
	public List<Node> bestPlan() {
		LinkedList<Node> l = new LinkedList<Node>();
		Node g = searchTree.closestTo(world.goal());
		while (!g.isRoot()) {
			l.addFirst(g);
			g = g.getParent();	
		}
		l.addFirst(g);
		return l;
	}

	public abstract void runSearch(int iterations, Stats stats);

	public abstract int runSearchHalt(Stats stats);
	
	protected abstract Node step(Stats stats);
	
	protected abstract Point2D towardPoint();
	
	protected abstract Point2D getDestination(Node from, Point2D toward);
	
	protected abstract void finishSearch(Node goal);

}
