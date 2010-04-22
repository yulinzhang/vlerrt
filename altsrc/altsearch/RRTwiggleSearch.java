package altsearch;

import java.awt.geom.Point2D;
import java.util.Random;

import altNodeTypes.RRTwiggleNode;
import altrrt.Node;
import altrrt.Search;
import altrrt.World;

public class RRTwiggleSearch extends RRTsearch {
	
	private static final Random r = new Random(System.currentTimeMillis());

	public RRTwiggleSearch(World world, int pGoal, int baseLength) {
		super(world, pGoal, baseLength);
	}
	
	@Override
	protected Node fromNode(Point2D toward) {
		RRTwiggleNode closest = (RRTwiggleNode) searchTree.closestTo(toward);
		
		//if we've already tried to extend from this point, check if the parent of this node can make progress
		if (toward.equals(world.goal()) && closest.hasExtendedToGoal()) {
			RRTwiggleNode parent = (RRTwiggleNode) closest.getParent();
			if (!parent.hasExtendedToGoal()) return parent;
		}
		
		return closest;
	}
	
	@Override
	protected Point2D getDestination(Node from, Point2D toward) {
		double extensionLength = from.getExtensionLength(toward);
		
		//if we've already tried extending to the goal, don't try again - find a different point that might be better
		if (toward.equals(world.goal()) && ((RRTwiggleNode) from).hasExtendedToGoal()) {
			int angle = r.nextInt(360);
			double dist = baseLength*r.nextDouble();
			
			return nextPoint(from.getPoint(), (angle*Math.PI)/360, dist);
		} else if(from.getPoint().distance(toward) < extensionLength) {  //We can get to the point we are extending towards
			return toward;
		} else return nextPoint(from.getPoint(), toward, extensionLength);
		
	}
	
	protected Node getNewNode(Point2D point, Node parent) {
		Node newNode = new RRTwiggleNode(point, parent, baseLength);
		
		//if we wiggled, attempt to change parent relationship
		if (parent != null && ((RRTwiggleNode) parent).wiggled()) {
			Node grandparent = parent.getParent();
			
			//check if path from grandparent to new node is clear 
			if (!world.collides(point,grandparent.getPoint())) {
				//setup parent relationship so that newNode will be the parent of the
				//original node.  Now it can extend to goal even if not the closest
				newNode.setParent(grandparent);
				parent.setParent(newNode);
			}
			((RRTwiggleNode) parent).setWiggled(false); //might not wiggle next time
		}
		
		
		return newNode; 
	}
	
	@Override
	public Search reset() {
		return new RRTwiggleSearch(world, pGoal, baseLength);
	}
	
	@Override
	public String toString() {
		return "RRTwiggle";
	}

	private static Point2D nextPoint(Point2D origin, double angle, double distance) {
		double oldX = origin.getX();
		double oldY = origin.getY();
		double deltaY = -Math.sin(angle)*distance;  //negative to take care of reversed y axis for Java
		double deltaX = Math.cos(angle)*distance;
		
		return new Point2D.Double(oldX+deltaX, oldY + deltaY);
	}
	
}
