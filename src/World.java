import java.awt.geom.Point2D;


public interface World {

	boolean collides(Point2D.Double o, Point2D.Double e);
	
	Point2D.Double randomPoint();
	
	Point2D.Double start();
	
	Point2D.Double goal();
	
	public void setSearchTree(Tree searchTree);
	public void display();
	
}
