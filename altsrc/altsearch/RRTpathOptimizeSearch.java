package altsearch;

import altrrt.Node;
import altrrt.Search;
import altrrt.World;

public class RRTpathOptimizeSearch extends RRTsearch {
	public RRTpathOptimizeSearch(World world, int pGoal, int baseLength) {
		super(world, pGoal, baseLength);
	}
	
	@Override
	protected void finishSearch(Node goal) {
		Node start = goal;
		Node optimized, current;
		
		do {
			current = start.getParent();
			optimized = current;
			
			
			while (optimized.getParent() != null &&  //loop until reached root
					!world.collides(start.getPoint(), optimized.getParent().getPoint())) {  //or path between start and candidate not clear
				optimized = optimized.getParent();
			}
			if (optimized != current) start.setParent(optimized);
			
			start = start.getParent();  //next node to try to optimize
		} while (start.getParent() != null);	
	}
	
	@Override
	public Search reset() {
		return new RRTpathOptimizeSearch(world, pGoal, baseLength);
	}
	
	@Override
	public String toString() {
		return "RRTpathOpt";
	}
}
