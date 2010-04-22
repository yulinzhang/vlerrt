package altsearch;

import java.awt.geom.Point2D;

import altNodeTypes.VLRRTnode;
import altNodeTypes.EpsilonNode.UpdateScheme;
import altrrt.Node;
import altrrt.Search;
import altrrt.Tree;
import altrrt.World;

public class VLRRTreSearch extends VLRRTsearch {

	public static final double thresholdFactor = 0.05;
	
	protected Tree prevSearch;
	public Tree getPrevSearch() {
		return prevSearch;
	}

	public void setPrevSearch(Tree prevSearch) {
		this.prevSearch = prevSearch;
	}

	protected int nClosest = 5;
	protected double dThreshold; 
	public enum averageStrat {
		Weighted, //Weighted average based on distance
		Simple; //Simple average
	}
	
	protected averageStrat strat;
	
	public VLRRTreSearch(World w, int pGoal, int baseLength,
			UpdateScheme incScheme, double incFactor, UpdateScheme decScheme, double decFactor,
			Tree prevSearch, int nClosest, averageStrat strategy) {
		super(w, pGoal, baseLength, incScheme, incFactor, decScheme, decFactor);
		
		this.prevSearch = prevSearch;
		this.nClosest = nClosest;
		this.strat = strategy;
		this.dThreshold = (w.height()*w.width())*(thresholdFactor);
	}
	
	protected VLRRTnode getNewNode(Point2D point, Node parent) {
		double potentialEpsilon = 0.0;
		Node[] neighbs;
		if (prevSearch != null) {
			neighbs = prevSearch.nClosestTo(point, nClosest);
			if (neighbs != null)
				switch (strat) {
				case Weighted:
					potentialEpsilon = calculateWAvgEpsilon(point, neighbs);
					break;
				case Simple:
					potentialEpsilon = calculateAvgEpsilon(point,neighbs); 
					break;
				}
		}
		
		return new VLRRTnode(point, (VLRRTnode) parent, baseLength, 
							incScheme, incFactor, decScheme, decFactor, 
							potentialEpsilon);
	}
	
	@Override
	public Search reset() {
		return new VLRRTreSearch(world, pGoal, baseLength, 
				incScheme, incFactor, decScheme, decFactor,
				prevSearch, nClosest, strat);
	}
	
	@Override 
	public String toString() {
		return "VLRRTrePlan<" + incScheme + ":" + incFactor + "," + decScheme + ":" + decFactor + ">(" + strat + ":" + nClosest + ")";
	}
	
	//SimpleAvg of all epsilons of neighbors
	private double calculateAvgEpsilon(Point2D point, Node[] potentialNeighbs) {
		
		
		if (potentialNeighbs.length == 0)
			return 0.0;
		
		double res = 0.0;
		double distance = 0.0;
		
		for (int i=0; i<potentialNeighbs.length;i++) {
			Node current = potentialNeighbs[i];
			if (current instanceof VLRRTnode) {
				res += ((VLRRTnode)current).getEpsilon();
				distance += point.distance(current.getPoint());
			}
		}
		
		if ((distance/potentialNeighbs.length) > dThreshold)
			return 0;
		else
			return (res/potentialNeighbs.length);
	}
	
	//Weighted Avg.
	private double calculateWAvgEpsilon(Point2D point, Node[] potentialNeighbs) {
		
		if (potentialNeighbs.length == 0)
			return 0.0;
		
		double res = 0.0;
		double distance = 0.0;
		
		for (int i=0; i<potentialNeighbs.length;i++) {
			Node current = potentialNeighbs[i];
			if (current instanceof VLRRTnode) {
				res += (((VLRRTnode)current).getEpsilon() * (distance += point.distance(current.getPoint())));
			}
		}
		if ((distance/potentialNeighbs.length) > dThreshold)
			return 0;
		else
			return (res/(potentialNeighbs.length * distance));
	}
}
