import java.awt.geom.Point2D;


public interface Node {
	Point2D getPoint();
	Node getParent();
	boolean isRoot();
	/**
	 * 
	 * @param to - point we are extending towards
	 * @return the distance to extend towards the given point
	 */
	double getExtensionLength(Point2D to);
	
	/**
	 * 
	 * @param to - point extended toward
	 * @param succeeded - whether or not the extension succeeded
	 */
	void reportExtensionStatus(Point2D to, boolean succeeded);
}
