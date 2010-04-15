import java.util.List;

import javax.swing.JFrame;

public class RRTsearchPausing extends RRTsearch {

	private boolean nextStep = true;
	private boolean done = false;
	private PausingGUI gui;
	
	public boolean isNextStep() {
		return nextStep;
	}

	public void setNextStep(boolean nextStep) {
		this.nextStep = nextStep;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
	
	public RRTsearchPausing() {
		super();

	}
	
	public RRTsearchPausing(World w, int pGoal, int baseLength,
			List<Node> wayPoints, int pWayPoint, Algorithm type, 
			VLRRTnode.changeEpsilonScheme inc, double incFactor,
			VLRRTnode.changeEpsilonScheme dec, double decFactor,
			boolean optimize) {
		super(w, pGoal, baseLength, wayPoints, pWayPoint, type, inc, incFactor, dec, decFactor, optimize);
		gui = new PausingGUI(this);
	}
	
	public void runSearchPausing(int step) {
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(gui);
		frame.pack();
		frame.setVisible(true);	
		while (!done) {
			
			while (!nextStep && ! done)  { }
			nextStep = false;
			
			runSearch(step);
			frame.repaint();
			
		}
		frame.dispose();
	}
	
	public static void main(String[] args){
		World testWorld;
		try {
			testWorld = new RRTWorld("worlds/proposal-world");
		} catch (Exception e) {
			testWorld = new RRTWorld(400,400);
		}

		
		RRTsearchPausing search = new RRTsearchPausing(testWorld, 20, 10, null, 0, Algorithm.DVLRRT, VLRRTnode.changeEpsilonScheme.Mult, 2, VLRRTnode.changeEpsilonScheme.Restart, 1, false);
		search.runSearchPausing(1);
		
	}
	
}
