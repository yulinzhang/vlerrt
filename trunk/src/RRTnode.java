import java.awt.geom.Point2D;


public class RRTnode implements Node {

	private Node parent;
	private Point2D.Double pt;
	
	public RRTnode(Point2D.Double pt, Node parent) {
		this.pt = pt;
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}

	public Point2D.Double getPoint() {
		return pt;
	}

	public boolean isRoot() {
		return parent == null;
	}

}
