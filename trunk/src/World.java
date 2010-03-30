import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public interface World {

	public boolean collides(Point2D o);
	public boolean collides(Point2D o, Point2D e);
	
	public Point2D randomPoint();
	public Point2D start();
	public Point2D goal();
	
	public Iterable<Rectangle2D> obstacles();

	public int height();
	public int width();
	
}
