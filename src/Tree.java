import java.awt.geom.Point2D;

public interface Tree extends Iterable<Node> {
	Node closestTo(Point2D p);
	Node root();
	int nNodes();
	void add(Node n);
}
