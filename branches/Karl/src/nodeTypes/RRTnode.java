package nodeTypes;
import java.awt.geom.Point2D;

import rrt.Node;

/*
 * RRT always extend the same amount
 */
public class RRTnode implements Node {

	protected Node parent;
	protected Point2D pt;
	protected double extLength;
	
	public RRTnode(Point2D pt, Node parent, double baseLength) {
		this.pt = pt;
		this.parent = parent;
		this.extLength = baseLength;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Point2D getPoint() {
		return pt;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public double getExtensionLength(Point2D to) {
		return extLength;
	}
	
	public double getExtensionLength(double direction) {
		return extLength;
	}

	public void reportExtensionStatus(Point2D to, boolean succeeded) {
		//DO nothing
	}
	
}
