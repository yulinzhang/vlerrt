package altNodeTypes;
import java.util.SortedMap;
import java.util.TreeMap;

public class DirectionalEpsilonMap {
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