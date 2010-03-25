import java.awt.Point;


public interface Node {
	Point getPoint();
	Node getParent();
	boolean isRoot();
	
}
