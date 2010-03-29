import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class RRTtree implements Tree {

	private KDTree<Node> theTree;
	private Node root;
	
	public RRTtree(Node root) {
		this.root = root;
		theTree = new KDTree<Node>(2);
		add(root);
	}
	
	
	public void add(Node n) {
		Point2D.Double pt = n.getPoint();
		double[] coord = { pt.getX(), pt.getY() };
		try {
			theTree.insert(coord, n);
		} catch (KeySizeException e) {
			// Will never occurr
			e.printStackTrace();
		} catch (KeyDuplicateException e) {
			// TODO might happen if we are already at the goal - code needed to catch this case
			e.printStackTrace();
		}
	}

	public Node closestTo(Point2D.Double pt) {
		double[] coord = { pt.getX(), pt.getY() };
		try {
			return theTree.nearest(coord);
		} catch (KeySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Node root() {
		return root;
	}

	public Iterator<Node> iterator() {
		List<Node> nodes = null;
		try {
			double[] origin = { 0, 0 };  //TODO : this implementation does not have an easy way to get the values, have to get size() nearest neighbors
			nodes = theTree.nearest(origin, theTree.size());
		} catch (KeySizeException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		if (nodes != null) {
			return nodes.iterator();
		} else
			return null;
	}

}
