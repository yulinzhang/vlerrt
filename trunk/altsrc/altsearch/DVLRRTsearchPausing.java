package altsearch;

import altgui.PausingGUI;

import javax.swing.JFrame;

import altrrt.Search;
import altrrt.World;
import altNodeTypes.RRTWorld;
import altNodeTypes.RRTtree;
import altNodeTypes.EpsilonNode.UpdateScheme;
import altgui.PausingSearch;
import altrrt.Stats;

public class DVLRRTsearchPausing extends DVLRRTsearch implements PausingSearch {
	private boolean nextStep = true;
	private boolean exit = false;

	private PausingGUI gui;
	
	public boolean isNextStep() {
		return nextStep;
	}

	public void setNextStep(boolean nextStep) {
		this.nextStep = nextStep;
	}
	
	public boolean exit() {
		return exit;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}
	
	public DVLRRTsearchPausing(World w, int pGoal, int baseLength,
			UpdateScheme incScheme, double incFactor, UpdateScheme decScheme, double decFactor) {	
		super(w, pGoal, baseLength, incScheme, incFactor, decScheme, decFactor);
		
	}
	
	public void runSearchPausing(int step) {
		searchTree = new RRTtree(this.getNewNode(world.start(), null));
		gui = new PausingGUI(this);
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(gui);
		frame.pack();
		frame.setVisible(true);	
		Stats stats = new RRTstats();
		while (!exit) {
			
			while (!nextStep && !exit)  { }
			nextStep = false;
			
			runSearch(step, stats);
			frame.repaint();
			
		}
		frame.dispose();
	}
	
	@Override
	public Search reset() {
		return new DVLRRTsearchPausing(world, pGoal, baseLength,
				incScheme, incFactor, decScheme, decFactor);
	}
	
	public static void main(String[] args){
		World testWorld;
		try {
			testWorld = new RRTWorld("worlds/proposal-world");
		} catch (Exception e) {
			testWorld = new RRTWorld(400,400);
		}

		DVLRRTsearchPausing search = new DVLRRTsearchPausing(testWorld, 20, 10,
				UpdateScheme.Mult,2, UpdateScheme.Restart, 1);
		search.runSearchPausing(1);
		
	}
}
