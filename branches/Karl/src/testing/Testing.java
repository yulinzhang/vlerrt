package testing;

import gui.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

import nodeTypes.RRTWorld;
import nodeTypes.EpsilonNode.UpdateScheme;
import rrt.Node;
import rrt.Search;
import rrt.Stats;
import rrt.Tree;
import rrt.World;
import rrt.Search.Algorithm;
import search.DVLERRTreSearch;
import search.DVLRRTsearch;
import search.ERRTsearch;
import search.RRTsearch;
import search.RRTstats;
import search.VLERRTreSearch;
import search.VLRRTreSearch;
import search.VLRRTsearch;

public class Testing { 
	
/* Class for testing and evaluating the RRT algorithms */
	
	private final double PROB_CHANGE_OBSTACLE = 0.05;
	
	private Search searcher;
	
	private World world;
	private int pGoal;
	private int baseLength;
	private int pWayPoint;
	private List<Node> wayPoints = null;
	protected UpdateScheme inc; //= VLRRTnode.changeEpsilonScheme.Linear;
	protected double incFactor;// = .1;
	protected UpdateScheme dec;// = VLRRTnode.changeEpsilonScheme.Linear;
	protected double decFactor;// = .1;
	private boolean optimize;// = false;
	private int runtime;

	private List<RRTstats> stats;
	
	/* TODO: recording start time and goal find time  */
	/* TODO: pretty print of information for graphing  */
	
	class TestTask extends Thread {

		Search search;
		int delay;
		Stats stats;
		
		public TestTask(Search search, int delay, Stats stats) { //pre: totally init'd search.
			super();
			this.search = search;
			this.delay = delay;
			this.stats = stats;
		}

		public void run() {
			ActionListener stopper = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					search.halt();
				}
			};
			javax.swing.Timer timer = new Timer(delay,stopper);
			timer.setRepeats(false); //stop once...
			long t0 = System.currentTimeMillis();
			timer.start();
			int nItrs = search.runSearchHalt(stats);
			long rtime = System.currentTimeMillis() - t0;
			stats.setRuntime(rtime);
			stats.setnIterations(nItrs);
			//System.out.println("Search has halted in "+rtime);
		
		}
		
	}

	public Testing(int runtime, int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world, boolean optimize)  {
		this(runtime, p, baseLength, pWayPoint, wayPoints, world, optimize, 
				UpdateScheme.Linear, .1, UpdateScheme.Linear, .1);
	}
	
	public Testing(int runtime, int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world)  {
		this(runtime, p, baseLength, pWayPoint, wayPoints, world, false, 
				UpdateScheme.Linear, .1, UpdateScheme.Linear, .1);

	}

	public Testing( int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world)  {
		this(50, p, baseLength, pWayPoint, wayPoints, world, false, 
				UpdateScheme.Linear, .1, UpdateScheme.Linear, .1);
	}

	public Testing( int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world, boolean optimize)  {
		this(50, p, baseLength, pWayPoint, wayPoints, world, optimize, 
				UpdateScheme.Linear, .1, UpdateScheme.Linear, .1);
	}
	
	public Testing(int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world,
			UpdateScheme inc, double incFactor,UpdateScheme dec, double decFactor) {
		this(50, p, baseLength, pWayPoint, wayPoints, world, false, 
				inc, incFactor, dec, decFactor);		
	}
	
	public Testing(int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world, boolean optimize,
			UpdateScheme inc, double incFactor,UpdateScheme dec, double decFactor) {
				this(50, p, baseLength, pWayPoint, wayPoints, world, optimize, 
						inc, incFactor, dec, decFactor);		
			}
	
	public Testing(int runtime, int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world,
			UpdateScheme inc, double incFactor,UpdateScheme dec, double decFactor) {
				this(runtime, p, baseLength, pWayPoint, wayPoints, world, false, 
				inc, incFactor, dec, decFactor);

			}
	
	public Testing(int runtime, int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world, boolean optimize,
			UpdateScheme inc, double incFactor, UpdateScheme dec, double decFactor) {
				this.inc = inc;
				this.dec = dec;
				this.incFactor = incFactor;
				this.decFactor = decFactor;
				this.pGoal = p;
				this.baseLength = baseLength;
				this.pWayPoint = pWayPoint;
				this.wayPoints = wayPoints;

				this.stats = new LinkedList<RRTstats>();
				this.world = world;
				this.runtime = runtime;
				this.optimize = optimize;
			}
	
	private void execSearch(Search.Algorithm alg, boolean printToScreen, VLRRTreSearch.averageStrat strat, int nClosest) {
			
		RRTstats stat = new RRTstats();
		stat.setwHeight(world.height());
		stat.setwWidth(world.width());
		stat.setAlg(alg);
		stat.setBaseLength(baseLength);
		stat.setpGoal(pGoal);
		stat.setpWayPoint(pWayPoint);

		switch (alg) {
			case ERRT: setupERRT(); break;
			case RRT : setupBasicRRT(); break;
			case VLRRT : setupVLRRT(); break;
			case VLERRT : setupVLERRT(strat,nClosest); break;
			case DVLRRT : setupDVLRRT(); break;
			case DVLERRT : setupDVLERRT(nClosest); break;
		}
		
		TestTask task = new TestTask(searcher,runtime,stat);
		task.start();
		try {
			task.join();
		} catch (InterruptedException e) {
			e.printStackTrace(); //Should not happen. Probably... :)
		}
		
		boolean gFound = searcher.foundGoal();
		stat.setGoalFound(gFound);
		
		if (gFound) { //Goal to root distance in the tree
		
			Node goal = stat.getGoal();
		
			while( !goal.isRoot() ){
				Node parent_node = goal.getParent();
				Point2D leaf = goal.getPoint();
				Point2D parent = parent_node.getPoint();
				stat.incgDistance(parent.distance(leaf));
				goal = parent_node;
			}
			
			stat.setGoal(null);
			
		}
		if (printToScreen) {
			System.out.println("World Coverage: "+stat.getTreeCoverage());
			System.out.println("Goal Distance: "+stat.getgDistance());
			//searcher.show();
		}
		stat.setnNodes(searcher.getSearchTree().nNodes());
		stats.add(stat); //Store for future processing.
				
		//searcher.show();
		if (printToScreen)
			GUI.screenshot(searcher.getWorld(),
					   searcher.getSearchTree(),
					   "Exec_"+alg.toString()+"_"+System.currentTimeMillis());	
		
	}

	private void setupBasicRRT() {
		
		this.searcher = new RRTsearch(world, pGoal, baseLength);
	
	}
	
	private void setupERRT() {
		this.searcher = new ERRTsearch(world, pGoal, baseLength, pWayPoint, wayPoints);	
	}
	
	private void setupVLRRT() {

		this.searcher = new VLRRTsearch(world, pGoal, baseLength, inc, incFactor, dec, decFactor);

	}
	
	private void setupVLERRT(VLRRTreSearch.averageStrat strat, int nClosest) {

		Tree t = null;
		if (searcher != null) t = searcher.getSearchTree();
		this.searcher =  new VLERRTreSearch(world, pGoal, baseLength, 
				inc, incFactor, dec, decFactor, 
				t, nClosest ,strat,
				pWayPoint, wayPoints);
	}
	
	private void setupDVLRRT() {
		this.searcher = new DVLRRTsearch(world, pGoal, baseLength, inc, incFactor, dec, decFactor);	
	}
	
	private void setupDVLERRT(int nClosest) {
		Tree t = null;
		
		if (searcher != null)
			t = searcher.getSearchTree();
		
		this.searcher = new DVLERRTreSearch(world, pGoal, baseLength, 
				inc, incFactor, dec, decFactor,
				t, nClosest,
				pWayPoint, wayPoints);
	}
	
//	private void advanceStart() {
//		Node n = searcher.getSearchTree().closestTo(world.goal());
//		Node prev = null;
//		while( !n.isRoot() ){
//			prev = n;
//			n = n.getParent();
//		}
//		if (prev == null) //In this case, we are already at the goal, return the goal?
//			world.setStart(n.getPoint());
//		else
//			world.setStart(prev.getPoint());
//		
//	}
	
	private void advanceStart(int n) {
		List<Node> l = searcher.bestPlan();
		if (l.size() < n)
			world.setStart(l.get(l.size()-1).getPoint());
		else
			world.setStart(l.get(n).getPoint());
	}
	
	public List<Node> genWaypoints(int nWaypoints) {
		List<Node> l = searcher.bestPlan();
		Random rng = new Random(System.nanoTime());
		List<Node> res = new LinkedList<Node>();
		for (int i=0;i<nWaypoints;i++) {
			res.add(l.get(rng.nextInt(l.size())));
		}
		return res;
	}
	
	
	private void changeWorld() { //Needs to be re-done.
		World newWorld = new RRTWorld((RRTWorld)world);
		Random rng = new Random(System.nanoTime());
		Iterator<Rectangle2D> obsItr = newWorld.obstacles().iterator();
		int width = newWorld.width();
		int height = newWorld.height();
		
		while (obsItr.hasNext()) {
			Rectangle2D obstacle = obsItr.next();
			if (rng.nextDouble() < PROB_CHANGE_OBSTACLE) {
				double shiftX = getRandomShiftX(rng,width);
				double shiftY = getRandomShiftY(rng,height);

				if (shiftX < 0) {
					if (obstacle.getX()+shiftX < 0)
						shiftX += obstacle.getX()+shiftX;
				} else
					if (shiftX >= 0)
						if (obstacle.getX()+obstacle.getWidth()+shiftX > width)
							shiftX -= (obstacle.getX()+obstacle.getWidth()+shiftX - width);
				if (shiftY < 0) {
					if (obstacle.getY()+shiftY < 0)
						shiftY += obstacle.getY()+shiftY;
				} else
					if (shiftY >= 0)
						if (obstacle.getY()+obstacle.getHeight()+shiftY > height)
							shiftY -= (obstacle.getY()+obstacle.getHeight()+shiftY - height);
				
				
				obstacle.setRect(obstacle.getX()+shiftX, obstacle.getY()+shiftY, obstacle.getWidth(), obstacle.getHeight());

				
			}
		}
		this.world = newWorld;		
	}
	
	private double getRandomShiftX(Random rng,int width) {
		return rng.nextBoolean()? rng.nextInt((int)(width*0.1)) : -(rng.nextInt((int)(width*0.1)));
	}
	
	private double getRandomShiftY(Random rng, int height) {
		return rng.nextBoolean()? rng.nextInt((int)(height*0.1)) : -(rng.nextInt((int)(height*0.1)));
	}
	

	
	public List<RRTstats> getStats() {
		return stats;
	}
	
	public void execNRuns(int n, Search.Algorithm alg, boolean printToScreen) {
		
		for (int i=0;i<n;i++) {
			execSearch(alg,printToScreen,VLRRTreSearch.averageStrat.Weighted, 5);
			//changeWorld();
		}
		if (printToScreen) 
			GUI.screenshot(searcher.getWorld(),
					   searcher.getSearchTree(),
					   "Exec_"+alg.toString()+"_"+System.currentTimeMillis());	

		
		
	}
	
	public void execNRuns(int n, Search.Algorithm alg) {
		for (int i=0;i<n;i++) {
			execSearch(alg,false,VLRRTreSearch.averageStrat.Weighted, 5);
			//changeWorld();
		}
	}
	
	public void execNReRuns(int n, Algorithm alg, boolean printToScreen, VLRRTreSearch.averageStrat strat, int nWaypoints, int nClosest) {
		for (int i=0;i<n;i++) {
			execSearch(alg,printToScreen, strat, nClosest);
			//changeWorld(); 
			advanceStart(2);
			wayPoints = genWaypoints(nWaypoints);

		}
	}

	public Search getSearcher(){
		return this.searcher;
	}
	
	//output to the stats directory
	public void printStats(String outputFile) {
		String fileName = "runStats/" + outputFile + System.currentTimeMillis();
		
		File f = new File(fileName);
		try {
			FileWriter fwr = new FileWriter(f);
			
			Iterator<RRTstats> itr = stats.iterator();
			fwr.write("Algorithm \t baseLength \t gDistance \t goalFTime \t" +
					"goalFound \t initTime \t nIterations \t nNodes \t pGoal \t pWayPoint \t " +
					"runtimeCap \t treeCoverage \t wHeight \t wWidth \t coverageRatio \t TimeDelta\n");
			while (itr.hasNext()) {
				Stats s = itr.next();
				fwr.write(s.toString()+"\n");
			}
			fwr.close();
	
		} catch (FileNotFoundException e) {
			System.err.println("Bad file.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error writing to file.");
		}
		
	}
	
	//Testing(int runtimelimit, int p, int baseLength, int pWayPoint, 
	//	List<Node> wayPoints, double baseEpsilon, String testFile)  
	//

	public static void main(String[] args) throws Exception{

		Testing test = new Testing(50,20, 15, 0, null, new RRTWorld("worlds/cluttered")); //

		
		//test.execNRuns(50,RRTsearch.Algorithm.RRT,true);
		//test.execNReRuns(50,RRTsearchAbs.Algorithm.DVLERRT,true, RRTResearch.averageStrat.Weighted,10, 10);
		//test.execNRuns(50,RRTsearch.Algorithm.DVLRRT,true);
		//test.printStats("stats_bb_50_20_10_", true);
		
		
		/*test = new Testing(50,40, 15, 0, null, 1, new RRTWorld("worlds/big_barrier_redux")); //

		
		test.execNRuns(50,RRTsearch.Algorithm.RRT);
		test.execNRuns(50,RRTsearch.Algorithm.VLRRT);
		test.execNRuns(50,RRTsearch.Algorithm.DVLRRT);
		test.printStats("stats_bbr_40_15_20_", true);
		
		test = new Testing(50,40, 15, 0, null, 1, new RRTWorld("worlds/mazzy")); //

		
		test.execNRuns(50,RRTsearch.Algorithm.RRT);
		test.execNRuns(50,RRTsearch.Algorithm.VLRRT);
		test.execNRuns(50,RRTsearch.Algorithm.DVLRRT);
		test.printStats("stats_mazzy_40_15_20_", true);
		
		test = new Testing(50,40, 15, 0, null, 1, new RRTWorld("worlds/tiny_passage")); //

		
		test.execNRuns(50,RRTsearch.Algorithm.RRT);
		test.execNRuns(50,RRTsearch.Algorithm.VLRRT);
		test.execNRuns(50,RRTsearch.Algorithm.DVLRRT);
		test.printStats("stats_tp_40_15_20_", true);*/
	}
	



}
