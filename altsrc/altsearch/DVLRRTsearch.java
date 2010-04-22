package altsearch;

import java.awt.geom.Point2D;

import altNodeTypes.DVLRRTnode;
import altNodeTypes.RRTWorld;
import altNodeTypes.EpsilonNode.UpdateScheme;
import altrrt.Node;
import altrrt.Search;
import altrrt.World;

public class DVLRRTsearch extends RRTepsilonSearch {
	
	public DVLRRTsearch(World world, int pGoal, int baseLength,
			UpdateScheme incScheme, double incFactor, UpdateScheme decScheme, double decFactor) {
		super(world, pGoal, baseLength, incScheme, incFactor, decScheme, decFactor);
	}
	
	@Override
	protected Node getNewNode(Point2D point, Node parent) {
		
		return new DVLRRTnode(point, (DVLRRTnode) parent, baseLength,
					incScheme, incFactor, decScheme, decFactor, null);
	}
	
	@Override
	public Search reset() {
		return new DVLRRTsearch(world, pGoal, baseLength,
				incScheme, incFactor, decScheme, decFactor);
	}
	
	public static void main(String[] args) {
		World testWorld = new RRTWorld(400,400);
		DVLRRTsearch search = new DVLRRTsearch(testWorld, 20, 10,
									UpdateScheme.Mult, 2, UpdateScheme.Restart, 1);
	}
	
	@Override
	public String toString() {
		return "DVLRRT<" + incScheme + ":" + incFactor + "," + decScheme + ":" + decFactor + ">"; 
	}
	
}
