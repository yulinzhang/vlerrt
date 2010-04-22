package altrrt;
import java.awt.geom.Point2D;


public interface Node {
	Point2D getPoint();
	Node getParent();
	void setParent(Node parent);
	boolean isRoot();
	/**
	 * 
	 * @param to - angle in which we're extending, 0<= direction < 2pi
	 * @return the distance to extend towards the given point
	 */
	double getExtensionLength(double direction);
	
	/**
	 * 
	 * @param to - point we're extending toward
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
