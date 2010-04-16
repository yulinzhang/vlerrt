import java.awt.geom.Point2D;

public interface Tree extends Iterable<Node> {
	Node closestTo(Point2D p);
	public Node[] nClosestTo(Point2D pt, int n);
	Node root();
	int nNodes();
	void add(Node n);
	
}
