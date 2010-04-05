import java.awt.geom.Point2D;
/*
 * Variable Length RRT
 * change how far to extend from a given node based on past successes and failures
 */
public class VLRRTnode extends RRTnode {

	//Enum to keep track of how to change Epsilon
	protected enum changeEpsilonScheme {
		Linear,  //increase/decrease epislon by a fixed amount 
		Double,  //double/halve epsilon
		Restart  //reset to 1
	}
	
	private double epsilon;
	protected changeEpsilonScheme inc = changeEpsilonScheme.Linear;
	protected changeEpsilonScheme dec = changeEpsilonScheme.Linear;
	
	
	public VLRRTnode(Point2D pt, VLRRTnode parent, double extLength) {
		super(pt, parent, extLength);
		if (parent != null) this.epsilon = parent.epsilon;  //copy Epsilon from parent
		else epsilon = 1;  //default 1
	}
	
	public Node getParent() {
		return parent;
	}

	public Point2D getPoint() {
		return pt;
	}

	public boolean isRoot() {
		return parent == null;
	}

	@Override
	public double getExtensionLength(Point2D to) {
		return extLength*epsilon;
	}

	@Override
	public void reportExtensionStatus(Point2D to, boolean succeeded) {
		if (succeeded) epsilon = increaseEpsilon(epsilon);
		else epsilon = decreaseEpsilon(epsilon);	
	}

	protected double decreaseEpsilon(double epsilon) {
		switch (dec) {
		case Linear:
			if (epsilon > .1 ) return epsilon - .1;  //don't decrease past .1
			return epsilon;
		case Double:
			if (epsilon > .1 ) return epsilon/2; //don't decrease past .1
			return epsilon;
		case Restart:
			return 1; //TODO : did we have a different starting epsilon?
		}
		return epsilon;
	}
	
	protected double increaseEpsilon(double epsilon) {
		switch (inc) {
		case Linear:
			return epsilon + .1;
		case Double:
			return epsilon*2; 
		case Restart:
			return 1; 
		}
		return epsilon;
	}	
}
