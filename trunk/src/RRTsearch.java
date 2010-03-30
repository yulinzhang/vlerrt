import java.awt.geom.Point2D;
import java.util.Random;

public class RRTsearch {
	//TEST
	public static void main(String[] args){
		RRTsearch search = new RRTsearch(20);
		search.runSearch();
	}
	
	private World w;
	private Tree searchTree;
	private int p = 0;
	private Random r;  //0 <= r <= 100
	private int length = 10;
	
	public RRTsearch() {
		w = new RRTWorld(400,400);	
		searchTree = new RRTtree(new RRTnode(w.start(),null));
		r = new Random(System.currentTimeMillis());
		w.setSearchTree(searchTree);
		w.display();
	}
	
	
	
	public RRTsearch(int p) {
		this();
		this.p = p;
	}
	
	public void runSearch() {
		Node next = null;
		for (int count = 1; count < 100; count++){
			while(next == null) {
				next = step();
			}
			searchTree.add(next);
			next = null;

		}
	}
	
	private Node step() {
		Point2D toward;
		Node from;
		
		//find the point to extend towards
		if (r.nextInt(100) < p) {
			toward = w.goal();
		} else {
			toward = w.randomPoint();
		}
		
		//find the Node to extend from
		from = searchTree.closestTo(toward);
		Point2D.Double destination = nextPoint(from.getPoint(),toward,length);
		
		if(w.collides(from.getPoint(),destination)) return null;
		else {
			return new RRTnode(destination,from);	
		}
		
		
	}
	
	private Point2D.Double nextPoint(Point2D origin, Point2D towards, int length) {
		double xDelta,yDelta,hypotenuse;
		double originX, originY, destinationX, destinationY;
		double newXDelta, newYDelta;
		double newXCoord, newYCoord;
		
		//coordinates of the two points
		originX = origin.getX();
		originY = origin.getY();
		destinationX = towards.getX();
		destinationY = towards.getY();
		
		//Right Triangle side lengths
		xDelta = Math.abs(originX - destinationX);
		yDelta = Math.abs(originY - destinationY);	
		hypotenuse = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta,2));
		
		//side lengths of the similar right triangle with hypotenuse length length
		newXDelta = length*(xDelta/hypotenuse);
		newYDelta = length*(yDelta/hypotenuse);
		
		if (originX < destinationX) newXCoord = originX + newXDelta;
		else newXCoord = originX - newXDelta;
		
		if (originY < destinationY) newYCoord = originY + newYDelta;
		else newYCoord = originY - newYDelta;
		
		return new Point2D.Double(newXCoord, newYCoord);
		
		
	}
	
}
