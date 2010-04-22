package altNodeTypes;
import java.awt.geom.Point2D;
/*
 * Variable Length RRT
 * change how far to extend from a given node based on past successes and failures
 */
public class VLRRTnode extends EpsilonNode {
	
	protected double epsilon;
	
	public VLRRTnode(Point2D point, VLRRTnode parent, int baseLength,
			UpdateScheme inc, double incFactor, UpdateScheme dec, double decFactor, 
			double epsilon) {
		super(point, parent, baseLength, inc, incFactor, dec, decFactor);
		this.dec = dec;
		this.decFactor = decFactor;
		this.inc = inc;
		this.incFactor = incFactor;
		this.epsilon = epsilon;
	}

	public double getEpsilon() {
		return epsilon;
	}

	@Override
	public double getExtensionLength(Point2D to) {
		return extLength*epsilon;
	}
	
	@Override
	public double getExtensionLength(double direction) {
		return extLength*epsilon;
	}

	@Override
	public void reportExtensionStatus(Point2D to, boolean succeeded) {
		if (succeeded) epsilon = increaseEpsilon(epsilon);
		else epsilon = decreaseEpsilon(epsilon);	
	}
}
