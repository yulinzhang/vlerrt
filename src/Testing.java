import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.TimerTask;
import javax.swing.Timer;

public class Testing { 
	
/* Class for testing and evaluating the RRT algorithms */
	
	
	
	private World world;
	private RRTsearch searcher;
	
	private int pGoal;
	private int baseLength;
	private int pWayPoint;
	private List<Node> wayPoints;
	private double baseEpsilon;
	
	
	class TestTask extends Thread {

		RRTsearch search;
		int delay;
		
		public TestTask(RRTsearch search, int delay) { //pre: totally init'd search.
			super();
			this.search = search;
			this.delay = delay;
		}

		public void run() {
			ActionListener stopper = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					search.halt();
				}
			};
			javax.swing.Timer timer = new Timer(delay,stopper);
			timer.setRepeats(false); //stop once...
			timer.start();
			search.runSearchHalt();
		
		}
		
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	
		
	}
	
	
	public Testing(int p, int baseLength, int pWayPoint, List<Node> wayPoints, double baseEpsilon, String testFile)  {
		
		this.pGoal = p;
		this.baseLength = baseLength;
		this.pWayPoint = pWayPoint;
		this.wayPoints = wayPoints;
		this.baseEpsilon = baseEpsilon;
		
		try {
			this.world = new RRTWorld(testFile);
		} catch (Exception e) {
			// TODO Treat the FNF better.
			e.printStackTrace();
		}
		
	}
	
	private void collectInfo() { 
		//TODO: This method will collect all the statistics
		//      at the end of a run and store it in some data structure.		
		
	}
	
	private void execSearch() {
		TestTask task = new TestTask(searcher,1000);
		task.start();
		try {
			task.join();
		} catch (InterruptedException e) {
			e.printStackTrace(); //Should not happen. Probably.
		}
		
		
	}
	
	private void setupBasicRRT() {
		this.searcher = RRTsearch.basicRRT(world, pGoal, baseLength);
	}
	
	private void setupERRT() {
		this.searcher = RRTsearch.ERRT(world, pGoal, baseLength, pWayPoint, wayPoints);
	}
	
	private void setup() {
		this.searcher = RRTsearch.VLRRT(world, pGoal, baseLength, baseEpsilon);
	}
	
	private void setupVLERRT() {
		this.searcher = RRTsearch.VLERRT(world, pGoal, baseLength, baseEpsilon, pWayPoint, wayPoints);
	}

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



}
