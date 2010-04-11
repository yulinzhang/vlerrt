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

public class Testing { 
	
/* Class for testing and evaluating the RRT algorithms */
	
	private final double PROB_CHANGE_OBSTACLE = 0.2;
	
	private World world;
	private RRTsearch searcher;
	
	private int pGoal;
	private int baseLength;
	private int pWayPoint;
	private List<Node> wayPoints;
	protected VLRRTnode.changeEpsilonScheme inc = VLRRTnode.changeEpsilonScheme.Linear;
	protected double incFactor = .1;
	protected VLRRTnode.changeEpsilonScheme dec = VLRRTnode.changeEpsilonScheme.Linear;
	protected double decFactor = .1;
	
	private int runtime;
	
	private List<Stats> stats;
	
	/* TODO: recording start time and goal find time  */
	/* TODO: pretty print of information for graphing  */
	
	
	
	
	class TestTask extends Thread {

		RRTsearch search;
		int delay;
		Stats stats;
		
		public TestTask(RRTsearch search, int delay, Stats stats) { //pre: totally init'd search.
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
	
	

	public Testing(int runtime, int p, int baseLength, int pWayPoint, List<Node> wayPoints, double baseEpsilon, World world)  {


		this.pGoal = p;
		this.baseLength = baseLength;
		this.pWayPoint = pWayPoint;
		this.wayPoints = wayPoints;

		this.stats = new LinkedList<Stats>();
		this.world = world;
		this.runtime = runtime;

	}

	public Testing(int runtime, int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world)  {


		this.pGoal = p;
		this.baseLength = baseLength;
		this.pWayPoint = pWayPoint;
		this.wayPoints = wayPoints;

		this.stats = new LinkedList<Stats>();
		this.world = world;
		this.runtime = runtime;

	}

	public Testing( int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world)  {


		this.pGoal = p;
		this.baseLength = baseLength;
		this.pWayPoint = pWayPoint;
		this.wayPoints = wayPoints;

		this.stats = new LinkedList<Stats>();
		this.world = world;
		this.runtime = 50;

	}

	
	
	public Testing(int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world,
	VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
		this(50,p, baseLength, pWayPoint, wayPoints, world);
		this.inc = inc;
		this.dec = dec;
		
		
	}
	
	public Testing(int runtime, int p, int baseLength, int pWayPoint, List<Node> wayPoints, World world,
			VLRRTnode.changeEpsilonScheme inc, double incFactor,VLRRTnode.changeEpsilonScheme dec, double decFactor) {
				this(runtime,p, baseLength, pWayPoint, wayPoints, world);
				this.inc = inc;
				this.dec = dec;
				
				
			}
	
	private void execSearch(RRTsearch.Algorithm alg, boolean printToScreen) {
		
		
		Stats stat = new Stats();
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
			case VLERRT : setupVLERRT(); break;
			case DVLRRT : setupDVLRRT(); break;
			case DVLERRT : setupDVLERRT(); break;
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
				stat.incgDistance(RRTsearch.euclidianDistance(parent, leaf));
				goal = parent_node;
			}
			
			stat.setGoal(null);
			
		}
		if (printToScreen) {
			System.out.println("World Coverage: "+stat.getTreeCoverage());
			System.out.println("Goal Distance: "+stat.getgDistance());
		}
		stat.setnNodes(searcher.getsearchTree().nNodes());
		stats.add(stat); //Store for future processing.
				
		//searcher.show();
		
		
		//searcher.screenshot("Exec_"+alg.toString()+"_"+System.currentTimeMillis());
			
		
	}
	
	private void setupBasicRRT() {
		this.searcher = RRTsearch.basicRRT(world, pGoal, baseLength);
	}
	
	private void setupERRT() {
		this.searcher = RRTsearch.ERRT(world, pGoal, baseLength, pWayPoint, wayPoints);
	}
	
	private void setupVLRRT() {
		this.searcher = RRTsearch.VLRRT(world, pGoal, baseLength, inc, incFactor, dec, decFactor);
	}
	
	private void setupVLERRT() {
		this.searcher = RRTsearch.VLERRT(world, pGoal, baseLength, pWayPoint, wayPoints, inc, incFactor, dec, decFactor);
	}
	
	private void setupDVLRRT() {
		this.searcher = RRTsearch.VLRRT(world, pGoal, baseLength, inc, incFactor, dec, decFactor);
	}
	
	private void setupDVLERRT() {
		this.searcher = RRTsearch.VLERRT(world, pGoal, baseLength, pWayPoint, wayPoints, inc, incFactor, dec, decFactor);
	}

	
	private double getRandomShiftX(Random rng,int width) {
		return rng.nextBoolean()? rng.nextInt((int)(width*0.1)) : -(rng.nextInt((int)(width*0.1)));
	}
	
	private double getRandomShiftY(Random rng, int height) {
		return rng.nextBoolean()? rng.nextInt((int)(height*0.1)) : -(rng.nextInt((int)(height*0.1)));
	}
	
	private void changeWorld() {
		
		Random rng = new Random(System.nanoTime());
		Iterator<Rectangle2D> obsItr = world.obstacles().iterator();
		int width = world.width();
		int height = world.height();
		
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
		
		
		
	}
	

	
	//output to the stats directory
	public void printStats(String outputFile, boolean includeTime) {
		String fileName = "runStats/" + outputFile;
		if (includeTime) fileName = fileName+System.currentTimeMillis();
		
		File f = new File(fileName);
		try {
			FileWriter fwr = new FileWriter(f);
			
			Iterator<Stats> itr = stats.iterator();
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
	
	public List<Stats> getStats() {
		return stats;
	}
	
	public void execNRuns(int n, RRTsearch.Algorithm alg, boolean printToScreen) {
		
		for (int i=0;i<n;i++) {
			execSearch(alg,printToScreen);
			//changeWorld();
		}
		if (printToScreen) 
			searcher.screenshot("Exec_"+alg.toString()+"_"+System.currentTimeMillis());

		
		
	}
	
	public void execNRuns(int n, RRTsearch.Algorithm alg) {
		execNRuns(n, alg, false);
	}
	
	public RRTsearch getSearcher(){
		return this.searcher;
	}
	
	//Testing(int runtimelimit, int p, int baseLength, int pWayPoint, 
	//	List<Node> wayPoints, double baseEpsilon, String testFile)  
	//

	public static void main(String[] args) throws Exception{

		Testing test = new Testing(50,20, 10, 0, null, 1, new RRTWorld("worlds/big_barrier")); //

		
		test.execNRuns(50,RRTsearch.Algorithm.RRT,true);
		test.execNRuns(50,RRTsearch.Algorithm.VLRRT,true);
		test.execNRuns(50,RRTsearch.Algorithm.DVLRRT,true);
		test.printStats("stats_bb_50_20_10_", true);
		
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
