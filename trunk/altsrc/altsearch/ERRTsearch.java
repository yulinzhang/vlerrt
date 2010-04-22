package altsearch;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import altrrt.Node;
import altrrt.Search;
import altrrt.Stats;
import altrrt.World;

public class ERRTsearch extends RRTsearch {
	protected int pWayPoint;
	protected List<Node> wayPoints;
	
	public ERRTsearch(World world, int pGoal, int baseLength,
						int pWayPoint, List<Node> wayPoints) {
		super(world, pGoal, baseLength);
		this.pWayPoint = pWayPoint;
		
		if (wayPoints == null) this.wayPoints = new ArrayList<Node>();
		else this.wayPoints = wayPoints;
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
		return new ERRTsearch(world, pGoal, baseLength,
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
		return "ERRT";
	}
}
