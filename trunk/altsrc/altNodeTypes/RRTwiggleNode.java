package altNodeTypes;

import java.awt.geom.Point2D;

import altrrt.Node;

public class RRTwiggleNode extends RRTnode {

	boolean extendedToGoal = false;
	boolean wiggled = false;
	
	public RRTwiggleNode(Point2D pt, Node parent, double baseLength) {
		super(pt, parent, baseLength);
	}

	public boolean hasExtendedToGoal() {
		return extendedToGoal;
	}

	public void setExtendedToGoal(boolean extendedToGoal) {
		this.extendedToGoal = extendedToGoal;
	}
	
	public void setWiggled(boolean wiggled) {
		this.wiggled = wiggled;
	}
	
	public boolean wiggled() {
		return wiggled;
	}
}
