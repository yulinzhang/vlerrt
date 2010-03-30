import java.awt.geom.Point2D;


public interface Node {
	Point2D getPoint();
	Node getParent();
	boolean isRoot();
	
}
