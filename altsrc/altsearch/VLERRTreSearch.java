package altsearch;

import java.awt.geom.Point2D;
import java.util.List;

import altrrt.Node;
import altrrt.Search;
import altrrt.Stats;
import altrrt.Tree;
import altrrt.World;
import altNodeTypes.EpsilonNode.UpdateScheme;

public class VLERRTreSearch extends VLRRTreSearch {
	protected int pWayPoint;
	protected List<Node> wayPoints;
	
	public VLERRTreSearch(World w, int pGoal, int baseLength,
			UpdateScheme incScheme, double incFactor, UpdateScheme decScheme, double decFactor,
			Tree prevSearch, int nClosest, averageStrat strategy,
			int pWayPoint, List<Node> wayPoints) {
		super(w, pGoal, baseLength, incScheme, incFactor, decScheme, decFactor, prevSearch, nClosest, strategy);
		this.pWayPoint = pWayPoint;
		this.wayPoints = wayPoints;
	}
	
	@Override
	protected Point2D towardPoint() {
		int p = r.nextInt(100);
		if ( p < pGoal) {
			return world.goal();
		} else if (wayPoints != null && p < pGoal+pWayPoint) {
			return wayPoints.get(r.nextInt(wayPoints.size())).getPoint();
		} else {
			return world.randomPoint();
		}
	}
	
	@Override
	public Search reset() {
		return new VLERRTreSearch(world, pGoal, baseLength, 
				incScheme, incFactor, decScheme, decFactor,
				prevSearch, nClosest, strat,
				pWayPoint, wayPoints);
	}
	
	@Override
	public Stats initStats() {
		Stats stats = super.initStats();
		stats.setpWayPoint(pWayPoint);
		return stats;
	}
	
	@Override 
	public String toString() {
		return"VLERRTrePlan<" + incScheme + ":" + incFactor + "," + decScheme + ":" + decFactor + ">(" + strat + ":" + nClosest + ")";
	}
}
