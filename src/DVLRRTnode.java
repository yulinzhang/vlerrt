import java.awt.geom.Point2D;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Directional Variable Length RRT node
 * keep track not only of how far to be extending based on past successes/failures
 * Also keep track of the direction of successes/failures
 */
public class DVLRRTnode extends VLRRTnode {
	
	/*
	 *  data structure for associating learned Epsilon values with directions
	 */
	private class DirectionalEpsilonMap {
		private SortedMap<Double,Double> epsilonMap;
		
		public double getEpsilon(double direction) {
			if (epsilonMap.size() == 0) return 1;
			if (epsilonMap.containsKey(direction)) return epsilonMap.get(direction);
			//return 1;// how to compute when we don't have the exact value

			SortedMap<Double,Double> below = epsilonMap.headMap(direction);
			SortedMap<Double,Double> above = epsilonMap.tailMap(direction);
			
			double prev, next;
			
			if (below.size() == 0) {
				prev = above.lastKey();
				next = above.firstKey();
			} else if (above.size() ==0) {
				prev = below.lastKey();
				next = below.firstKey();
			} else {
				prev = below.lastKey();
				next = above.firstKey();
			}
			
			double newEpsilon = computeEpsilon(direction, prev, epsilonMap.get(prev), next, epsilonMap.get(next));
			//if (update) epsilonMap.put(direction, newEpsilon);  //cache - can't do this - impacts the values  TODO : a different/better way to cache to improve performanc?
			return newEpsilon;

		}

		public void setEpsilon(double direction, double epsilon) {
			epsilonMap.put(direction, epsilon);
		}

		public DirectionalEpsilonMap() {
			epsilonMap = new TreeMap<Double,Double>();
		}	
		
		//Compute the epsilon for targetAngle based off of what information we have clockwise and counterclockwise and their proximity
		private double computeEpsilon(double targetAngle, double CWangle, double CWepsilon, double CCWangle, double CCWepsilon) {
			double distCW = (targetAngle - CWangle);
			if (distCW < 0) distCW = distCW + 2*Math.PI;
			else if (distCW > Math.PI*2) distCW = distCW - 2*Math.PI;
			double distCCW = (CCWangle - targetAngle);
			if (distCCW < 0) distCCW = distCCW + 2*Math.PI;
			else if (distCCW > Math.PI*2) distCCW = distCCW - 2*Math.PI;
			
			double impactCW, impactCCW;
			
			if (distCW >= Math.PI/2) impactCW = 0; //only impacts if with pi/2
			else impactCW = Math.cos(distCW);
			
			if (distCCW >= Math.PI/2) impactCCW = 0;  //only impacts if with pi/2
			else impactCCW = Math.cos(distCCW);
			
			if (impactCW == 0) {
				if (impactCCW == 0) return 1; //No information to use
				else return computeContrib(impactCCW,CCWepsilon);  //only use CCW
			} else {
				if (impactCCW == 0) return computeContrib(impactCW,CWepsilon);  //only use CW
				else {
					double ratioDenom = distCW + distCCW;  //use both weigh depending on relative distances
					double ratioCW = distCCW/ratioDenom;  //CW higher weight as dist from CCW gets greater
					double ratioCCW = distCW/ratioDenom;  //CCW higher weight as dist from CW gets greater
					
					return computeContrib(impactCW,CWepsilon)*ratioCW + computeContrib(impactCCW,CCWepsilon)*ratioCCW;
				}
				
			}

		}
		
		//normalize towards 1, stay between 1 and epsilon
		//requires 0 <= factor <= 1
		private double computeContrib(double factor, double epsilon) {
			if (epsilon < 1) {
				return 1 - factor*(1-epsilon);  //if epsilon is less than 1, always return something between epsilon and 1
			}
			else return (1-factor) + factor*epsilon;  //if epsilon is greater than 1, always return something between 1 and epsilon
		}
		
		public void testDirEps() {
			System.out.println(getEpsilon(0)); //returns 1 - ok
			setEpsilon(Math.PI,1.3);
			System.out.println(getEpsilon(0)); //returns 1 - ok
			System.out.println(getEpsilon(Math.PI)); //returns 1.3 - ok
			System.out.println(getEpsilon(Math.PI*7/8)); // ok
			System.out.println(getEpsilon(Math.PI*3/4)); // ok
			System.out.println(getEpsilon(Math.PI*5/8)); // ok
			System.out.println(getEpsilon(Math.PI/2)); // ok
			
			setEpsilon(Math.PI*3/2,1.2);
			System.out.println(getEpsilon(Math.PI*3/2)); //returns 1.2
			System.out.println(getEpsilon(Math.PI*5/4)); // 
			System.out.println(getEpsilon(Math.PI*9/8)); // 
			System.out.println(getEpsilon(Math.PI)); // 
			
			
			setEpsilon(0,.5);
			System.out.println(getEpsilon(0)); //returns .5
			System.out.println(getEpsilon(Math.PI/8)); // ok
			System.out.println(getEpsilon(Math.PI/4)); // ok
			System.out.println(getEpsilon(Math.PI*3/8)); // ok
			System.out.println(getEpsilon(Math.PI/2)); // 1
			
			setEpsilon(Math.PI/2,2);
			System.out.println(getEpsilon(0)); //returns .5
			System.out.println(getEpsilon(Math.PI/8)); // ok
			System.out.println(getEpsilon(Math.PI/4)); // ok
			System.out.println(getEpsilon(Math.PI*3/8)); // ok
			System.out.println(getEpsilon(Math.PI/2)); // returns .9
			
		}

	}
	
	DirectionalEpsilonMap dirEpsilon;
	
	public DVLRRTnode(Point2D pt, DVLRRTnode parent, double extLength) {
		super(pt, parent, extLength);
		//dec = changeEpsilonScheme.Restart;
		//initially add each direction at Epsilon for this successful extension
		dirEpsilon = new DirectionalEpsilonMap();
		if (parent != null) {
			double angleToParent = computeAngle(pt, parent.getPoint());
			double angleFromParent = (angleToParent + Math.PI) % (2*Math.PI);
			double initialEpsilon = parent.dirEpsilon.getEpsilon(angleFromParent);
			dirEpsilon.setEpsilon(angleFromParent, initialEpsilon);
			dirEpsilon.setEpsilon(angleToParent, initialEpsilon);
		}
	}
	public DVLRRTnode(Point2D pt, DVLRRTnode parent, double extLength,
			changeEpsilonScheme inc, double incFactor,changeEpsilonScheme dec, double decFactor) {
		this(pt, parent, extLength);
		this.dec = dec;
		this.decFactor = decFactor;
		this.inc = inc;
		this.incFactor = incFactor;
	}
	
	/*
	 * compute the angle using arcTan and converting to the 0 <= angle < 2*pi range
	 */
	private static double computeAngle(Point2D origin, Point2D dest) {
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
		
		System.out.println(computeAngle(origin,right)); // 0 
		System.out.println(computeAngle(origin,q1));
		System.out.println(computeAngle(origin,up));
		System.out.println(computeAngle(origin,q2));
		System.out.println(computeAngle(origin,left));
		System.out.println(computeAngle(origin,q3));
		System.out.println(computeAngle(origin,down));
		System.out.println(computeAngle(origin,q4));	
		
		//test the node and epsilon maps
//		DVLRRTnode parent = new DVLRRTnode(origin, null, 10);
//		DirectionalEpsilonMap test = parent.new DirectionalEpsilonMap();
//		test.testDirEps();
		

		
	}
	
}
