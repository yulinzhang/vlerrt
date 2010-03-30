import java.awt.geom.Point2D;

public interface Tree extends Iterable<Node> {
	Node closestTo(Point2D p);
	Node root();
	void add(Node n);
}
