package nodeTypes;
import java.awt.geom.Point2D;

/*
 * Directional Variable Length RRT node
 * keep track not only of how far to be extending based on past successes/failures
 * Also keep track of the direction of successes/failures
 */
public class DVLRRTnode extends EpsilonNode {
	
	/*
	 *  data structure for associating learned Epsilon values with directions
	 */
	DirectionalEpsilonMap dirEpsilon;
	
	public DVLRRTnode(Point2D point, DVLRRTnode parent, int baseLength,
			UpdateScheme inc, double incFactor,UpdateScheme dec, double decFactor, DirectionalEpsilonMap map) {
		super(point, parent, baseLength, inc, incFactor, dec, decFactor);
		this.dec = dec;
		this.decFactor = decFactor;
		this.inc = inc;
		this.incFactor = incFactor;
		if (map == null) {
			dirEpsilon = new DirectionalEpsilonMap();
			if (parent != null) {
				double angleToParent = computeAngle(pt, parent.getPoint());
				double angleFromParent = (angleToParent + Math.PI) % (2*Math.PI);
				double initialEpsilon = parent.dirEpsilon.getEpsilon(angleFromParent);
				dirEpsilon.setEpsilon(angleFromParent, initialEpsilon);
				dirEpsilon.setEpsilon(angleToParent, initialEpsilon);
			}
		} else dirEpsilon = map;
	}
	
	/*
	 * compute the angle using arcTan and converting to the 0 <= angle < 2*pi range
	 */
	public static double computeAngle(Point2D origin, Point2D dest) {
		double xDelta = dest.getX() - origin.getX();
		double yDelta = -(dest.getY() - origin.getY());

		if (xDelta == 0) {
			if (yDelta > 0) return Math.PI/2;
			else return 3*Math.PI/2;
		} else if (yDelta == 0) {
			if (xDelta > 0) return 0;
			else return Math.PI;
		} else {
			double angle = Math.atan(yDelta/xDelta);  //-pi/2 <= angle pi/2
			if (xDelta < 0) return angle + Math.PI;  //q2/q3 add pi
			else if (yDelta < 0) return angle + 2*Math.PI; //q4 add 2*pi
			else return angle;  //q1 add nothing
		}
		
	}
	
	@Override
	public double getExtensionLength(Point2D to) {
		return getExtensionLength(computeAngle(pt, to));  //use angle to get the epsilon
	}
	
	@Override
	public double getExtensionLength(double direction) {
		return extLength*dirEpsilon.getEpsilon(direction);  
	}

	@Override
	public void reportExtensionStatus(Point2D to, boolean succeeded) {
		double direction = computeAngle(pt, to);
		double oldEpsilon = dirEpsilon.getEpsilon(direction);
		double newEpsilon;
		
		if (succeeded) newEpsilon = increaseEpsilon(oldEpsilon);
		else newEpsilon = decreaseEpsilon(oldEpsilon);	
		
		dirEpsilon.setEpsilon(direction, newEpsilon); //update the map
	}
	
	public static void main(String[] args) {
		
		//Test compute angle
		Point2D origin = new Point2D.Double(0,0);
		Point2D right = new Point2D.Double(1,0);
		Point2D q1 = new Point2D.Double(1,1);
		Point2D up = new Point2D.Double(0,1);
		Point2D q2 = new Point2D.Double(-1,1);
		Point2D left = new Point2D.Double(-1,0);
		Point2D q3 = new Point2D.Double(-1,-1);
		Point2D down = new Point2D.Double(0,-1);
		Point2D q4 = new Point2D.Double(1,-1);
		
		//Note that y axis is inverted from normal
		System.out.println(computeAngle(origin,right)); // 0 
		System.out.println(computeAngle(origin,q1));
		System.out.println(computeAngle(origin,up));
		System.out.println(computeAngle(origin,q2));
		System.out.println(computeAngle(origin,left));
		System.out.println(computeAngle(origin,q3));
		System.out.println(computeAngle(origin,down));
		System.out.println(computeAngle(origin,q4));	

	}
	
}
