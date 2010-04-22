package altrrt;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public interface World {

	public boolean collides(Point2D o);
	public boolean collides(Point2D o, Point2D e);
	
	public Point2D randomPoint();
	public Point2D start();
	public Point2D goal();
	
	
	public void setStart(Point2D start);
	public void setGoal(Point2D goal);
	
	public List<Rectangle2D> obstacles();

	public int height();
	public int width();
	
	void write(String file) throws Exception;
	
}
