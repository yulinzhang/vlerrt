import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

public class Testing { 
	
/* Class for testing and evaluating the RRT algorithms */
	
	private final double PROB_CHANGE_OBSTACLE = 0.5;
	
	private World world;
	private RRTsearch searcher;
	
	private int pGoal;
	private int baseLength;
	private int pWayPoint;
	private List<Node> wayPoints;
	private double baseEpsilon;
	
	private List<Stats> stats;
	
	
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
			System.out.println("Search has halted in "+rtime);
		
		}
		

	
		
	}
	
	
	public Testing(int p, int baseLength, int pWayPoint, List<Node> wayPoints, double baseEpsilon, String testFile)  {
		
		this.pGoal = p;
		this.baseLength = baseLength;
		this.pWayPoint = pWayPoint;
		this.wayPoints = wayPoints;
		this.baseEpsilon = baseEpsilon;
		
		this.stats = new LinkedList<Stats>();
		
		try {
			this.world = new RRTWorld(testFile);
		} catch (Exception e) {
			// TODO Treat the FNF better.
			e.printStackTrace();
		}
		
	}
	
	
	private void execSearch(RRTsearch.Algorithm alg) {
		
		
		Stats stat = new Stats();
		stat.setwHeight(world.height());
		stat.setwWidth(world.width());
		stat.setAlg(alg);
		stat.setBaseLength(baseLength);
		stat.setpGoal(pGoal);
		stat.setpWayPoint(pWayPoint);
		stat.setBaseEpsilon(baseEpsilon);
				
		
		switch (alg) {
			case ERRT: setupERRT(); break;
			case RRT : setupBasicRRT(); break;
			case VLRRT : setupVLRRT(); break;
			case VLERRT : setupVLERRT(); break;
		}
		
		
		
		TestTask task = new TestTask(searcher,100,stat);
		task.start();
		try {
			task.join();
		} catch (InterruptedException e) {
			e.printStackTrace(); //Should not happen. Probably... :)
		}
		
		boolean gFound = searcher.foundGoal();
		stat.setGoalFound(gFound);
		
		if (gFound) { //Goal to root distance in the tree
			Tree tree = searcher.getsearchTree();
			Node goal = tree.closestTo(world.goal());
			while( !goal.isRoot() ){
				Node parent_node = goal.getParent();
				Point2D leaf = goal.getPoint();
				Point2D parent = parent_node.getPoint();
				stat.incgDistance(RRTsearch.euclidianDistance(parent, leaf));
				goal = parent_node;
			}
			
		}
		System.out.println("World Coverage: "+stat.getTreeCoverage());
		System.out.println("Goal Distance: "+stat.getgDistance());
		stat.setnNodes(searcher.getsearchTree().nNodes());
		stats.add(stat); //Store for future processing.
		
		searcher.show();
		
		
		searcher.screenshot("Exec_"+alg.toString()+"_"+System.currentTimeMillis());
		
	
		
		
		
		
		
	}
	
	private void setupBasicRRT() {
		this.searcher = RRTsearch.basicRRT(world, pGoal, baseLength);
	}
	
	private void setupERRT() {
		this.searcher = RRTsearch.ERRT(world, pGoal, baseLength, pWayPoint, wayPoints);
	}
	
	private void setupVLRRT() {
		this.searcher = RRTsearch.VLRRT(world, pGoal, baseLength, baseEpsilon);
	}
	
	private void setupVLERRT() {
		this.searcher = RRTsearch.VLERRT(world, pGoal, baseLength, baseEpsilon, pWayPoint, wayPoints);
	}

	
	private double getRandomShiftX(Random rng,int width) {
		return rng.nextBoolean()? rng.nextInt((int)(width*0.05)) : -(rng.nextInt((int)(width*0.05)));
	}
	
	private double getRandomShiftY(Random rng, int height) {
		return rng.nextBoolean()? rng.nextInt((int)(height*0.05)) : -(rng.nextInt((int)(height*0.05)));
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
						if (obstacle.getX()+shiftX > width)
							shiftX -= (obstacle.getX()+shiftX - width);
				if (shiftY < 0) {
					if (obstacle.getY()+shiftY < 0)
						shiftY += obstacle.getY()+shiftY;
				} else
					if (shiftY >= 0)
						if (obstacle.getY()+shiftY > height)
							shiftY -= (obstacle.getY()+shiftY - height);
				
				
				obstacle.setRect(obstacle.getX()+shiftX, obstacle.getY()+shiftY, obstacle.getWidth(), obstacle.getHeight());

				
			}
		}
		
		
		
	}
	
	
	
	private void execNRuns(int n, RRTsearch.Algorithm alg) {
		
		for (int i=0;i<n;i++) {
			execSearch(alg);
			changeWorld();
		}
		
		
		
	}
	

	public static void main(String[] args) {
		Testing test = new Testing(20, 10, 0, null, 0, "asd");
		
		test.execNRuns(5,RRTsearch.Algorithm.RRT);

	}



}
