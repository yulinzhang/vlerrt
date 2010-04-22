package altNodeTypes;

import java.awt.geom.Point2D;

public abstract class EpsilonNode extends RRTnode {
	
	//Enum to keep track of how to change Epsilon
	public enum UpdateScheme {
		Linear,  //increase/decrease epislon by a fixed amount 
		Mult,  //multiply/divide by a factor
		Restart;  //reset to 1
	}
	
	protected UpdateScheme inc;
	protected double incFactor;
	protected UpdateScheme dec;
	protected double decFactor;
	
	public EpsilonNode(Point2D point, EpsilonNode parent, int baseLength,
			UpdateScheme inc, double incFactor, UpdateScheme dec, double decFactor) {
		super(point, parent, baseLength);
		this.dec = dec;
		this.decFactor = decFactor;
		this.inc = inc;
		this.incFactor = incFactor;
	}
	
	protected double decreaseEpsilon(double epsilon) {
		switch (dec) {
		case Linear:
			if (epsilon > .1 ) return epsilon - .1;  //don't decrease past .1
			return epsilon;
		case Mult:
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
		case Mult:
			return epsilon*2; 
		case Restart:
			return 1; 
		}
		return epsilon;
	}	
}
