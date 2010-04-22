package altsearch;

import java.awt.geom.Point2D;

import altNodeTypes.VLRRTnode;
import altNodeTypes.EpsilonNode.UpdateScheme;
import altrrt.Node;
import altrrt.Search;
import altrrt.World;

public class VLRRTsearch extends RRTepsilonSearch {
	
	public VLRRTsearch(World world, int pGoal, int baseLength,
			UpdateScheme incScheme, double incFactor, UpdateScheme decScheme, double decFactor) {
		super(world, pGoal, baseLength, incScheme, incFactor, decScheme, decFactor);
	}
	
	@Override
	protected Node getNewNode(Point2D point, Node parent) {
		double epsilon = 1;
		try {
			if (parent != null) {
				epsilon = ((VLRRTnode) parent).getEpsilon();
			}
		} catch (ClassCastException e){

		}
		
		return new VLRRTnode(point, (VLRRTnode) parent, baseLength,
					incScheme, incFactor, decScheme, decFactor, epsilon);
	}
	
	@Override
	public Search reset() {
		return new VLRRTsearch(world, pGoal, baseLength, incScheme, incFactor, decScheme, decFactor);
	}
	
	@Override 
	public String toString() {
		return "VLRRT<" + incScheme + ":" + incFactor + "," + decScheme + ":" + decFactor + ">";
	}
	
}