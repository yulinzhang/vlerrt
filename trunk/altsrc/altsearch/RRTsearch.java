package altsearch;

import java.awt.geom.Point2D;

import altNodeTypes.RRTnode;
import altNodeTypes.RRTtree;
import altrrt.Node;
import altrrt.Search;
import altrrt.Stats;
import altrrt.World;

public class RRTsearch extends RRTabstractSearch {

	public RRTsearch(World world, int pGoal, int baseLength) {
		super(world, pGoal, baseLength);
	}
	
	@Override
	//search tree must have been initialized
	public void runSearch(int iterations, Stats stats) {
		if (searchTree == null) searchTree = new RRTtree(this.getNewNode(world.start(), null));
		Node next = null;
		for (int count = 0; count < iterations && !foundGoal; count++){
			next = step(stats); //Dummy
			if (next != null) searchTree.add(next);
		}
	}

	@Override
	public int runSearchHalt(Stats stats) {
		searchTree = new RRTtree(this.getNewNode(world.start(), null));
		int nItrs = 0;
		Node next = null;
		stats.setInitTime(System.nanoTime());
		while (!foundGoal && !halt) {
			next = step(stats);
			if (next != null) searchTree.add(next);
			nItrs++;
		}
		return nItrs;
		
	}
	
	@Override
	protected Node step(Stats stats) {
		Point2D toward, destination;
		Node from;
		
		//find the point to extend towards
		toward = this.towardPoint();
		
		//find the closest node in the tree
		from = this.fromNode(toward);
		
		//determine the destination point
		destination = this.getDestination(from, toward);
		
		if(world.collides(from.getPoint(), destination)) { //collision, decrease the
			from.reportExtensionStatus(toward, false);
			return null;
		}
		else {
			from.reportExtensionStatus(destination, true);
			Node ret = this.getNewNode(destination, from);
			
			if (destination.equals(world.goal())) {
				this.finishSearch(ret);  //do any final things (like optimization for instance)
				foundGoal = true;  //didn't collide, will reach goal
				stats.setGoalFTime(System.nanoTime());
				stats.setGoal(ret);
			}
			
			stats.incTreeCoverage(from.getPoint().distance(destination));
			return ret;
		}
	}

	protected Point2D.Double nextPoint(Point2D origin, Point2D towards, double length) {
		
		double xDelta,yDelta,hypotenuse;
		double originX, originY, destinationX, destinationY;
		double newXDelta, newYDelta;
		double newXCoord, newYCoord;
		
		//coordinates of the two points
		originX = origin.getX();
		originY = origin.getY();
		destinationX = towards.getX();
		destinationY = towards.getY();
		
		//Right Triangle side lengths
		xDelta = Math.abs(originX - destinationX);
		yDelta = Math.abs(originY - destinationY);	
		hypotenuse = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta,2));
		
		//side lengths of the similar right triangle with hypotenuse length length
		newXDelta = length*(xDelta/hypotenuse);
		newYDelta = length*(yDelta/hypotenuse);
		
		if (originX < destinationX) newXCoord = originX + newXDelta;
		else newXCoord = originX - newXDelta;
		
		if (originY < destinationY) newYCoord = originY + newYDelta;
		else newYCoord = originY - newYDelta;
		
		return new Point2D.Double(newXCoord, newYCoord);	
	}
	
	protected Point2D towardPoint() {
		int p = r.nextInt(100);
		if ( p < pGoal) {
			return world.goal();
		} else {
			return world.randomPoint();
		}
	}
	
	protected Node getNewNode(Point2D point, Node parent) {
		return new RRTnode(point, parent, baseLength);
	}
	
	protected Node fromNode(Point2D point) {
		return searchTree.closestTo(point);
	}
	
	protected Point2D getDestination(Node from, Point2D toward) {
		double extensionLength = from.getExtensionLength(toward);
		
		if(from.getPoint().distance(toward) < extensionLength) {  //We can get to the point we are extending towards
			return toward;
		} else return nextPoint(from.getPoint(), toward, extensionLength);
		
	}
	
	protected void finishSearch(Node goal) {
		//Do nothing for basic search
	}
	
	@Override
	public Search reset() {
		return new RRTsearch(world, pGoal, baseLength);
	}

	public Stats initStats() {
		Stats stat = new RRTstats();
		stat.setwHeight(world.height());
		stat.setwWidth(world.width());
		stat.setAlg(this.toString());
		stat.setBaseLength(baseLength);
		stat.setpGoal(pGoal);
		stat.setpWayPoint(0);
		
		return stat;
	}
	
	@Override
	public String toString() {
		return "RRT";
	}
	
}
