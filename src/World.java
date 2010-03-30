import java.awt.geom.Point2D;


public interface World {

	boolean collides(Point2D o, Point2D e);
	
	Point2D randomPoint();
	
	Point2D start();
	
	Point2D goal();
	
	public void setSearchTree(Tree searchTree);
	public void display();
	
}
