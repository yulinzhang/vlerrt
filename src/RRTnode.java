import java.awt.geom.Point2D;


public class RRTnode implements Node {

	private Node parent;
	private Point2D pt;
	private double epsilon;
	
	public RRTnode(Point2D pt, Node parent, double epsilon) {
		this.pt = pt;
		this.parent = parent;
		this.epsilon = epsilon;
	}
	
	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
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

}
