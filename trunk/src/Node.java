import java.awt.geom.Point2D;


public interface Node {
	Point2D.Double getPoint();
	Node getParent();
	boolean isRoot();
	
}
