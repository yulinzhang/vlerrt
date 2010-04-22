package altsearch;

import java.awt.geom.Point2D;

import altrrt.Node;
import altrrt.Search;
import altrrt.Stats;
import altrrt.Tree;
import altrrt.World;
import altNodeTypes.DVLRRTnode;
import altNodeTypes.DirectionalEpsilonMap;
import altNodeTypes.EpsilonNode.UpdateScheme;

public class DVLRRTreSearch extends DVLRRTsearch {
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
	
	public DVLRRTreSearch(World w, int pGoal, int baseLength,
			UpdateScheme incScheme, double incFactor, UpdateScheme decScheme, double decFactor,
			Tree prevSearch, int nClosest) {
		super(w, pGoal, baseLength, incScheme, incFactor, decScheme, decFactor);
		
		this.prevSearch = prevSearch;
		this.nClosest = nClosest;
		this.dThreshold = (w.height()*w.width())*(thresholdFactor);
	}
	
	@Override
	protected DVLRRTnode getNewNode(Point2D point, Node parent) {

	DirectionalEpsilonMap m = null;
	Node[] neighbs;
	if (prevSearch != null) {
		neighbs = prevSearch.nClosestTo(point, nClosest);
		if (neighbs != null) {
			m = calculateMap(point, neighbs);
		}
	}
	return new DVLRRTnode(point, (DVLRRTnode) parent, baseLength, 
			incScheme, incFactor, decScheme, decFactor, m);	
	}	

	@Override
	public Search reset() {
		return new DVLRRTreSearch(world, pGoal, baseLength,
				incScheme, incFactor, decScheme, decFactor,
				prevSearch, nClosest);
	}
	
	private DirectionalEpsilonMap calculateMap(Point2D point, Node[] potentialNeighbs) { //For DVLRRT
		
		if (potentialNeighbs.length == 0)
			return null;
		
		DirectionalEpsilonMap res = new DirectionalEpsilonMap();
		double distance = 0.0;
		
		for (int i=0;i<potentialNeighbs.length;i++) {
			Node current = potentialNeighbs[i];
			if (current instanceof DVLRRTnode) {
				distance += point.distance(current.getPoint());
				double dirNP = DVLRRTnode.computeAngle(current.getPoint(), point);
				double epsCurrent = current.getExtensionLength(dirNP);
				res.setEpsilon(DVLRRTnode.computeAngle(point, current.getPoint()), epsCurrent);
			}
		}
		
		if ((distance/potentialNeighbs.length) > dThreshold)
			return null;
		else
			return res;	
	}
	
	@Override
	public String toString() {
		return "DVLRRTrePlan<" + incScheme + ":" + incFactor + "," + decScheme + ":" + decFactor + ">(" + nClosest + ")";
	}
	
	
}
