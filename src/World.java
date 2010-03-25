import java.awt.Point;


public interface World {

	boolean collides(Point o, Point e, int d);
	
	Point randomPoint();
	
}
