import java.awt.geom.Point2D;
import java.util.Iterator;

import com.savarese.spatial.GenericPoint;
import com.savarese.spatial.KDTree;
import com.savarese.spatial.NearestNeighbors;
import com.savarese.spatial.NearestNeighbors.Entry;

public class RRTtree implements Tree {

	private KDTree<Double,GenericPoint<Double>,Node> theTree;
	private Node root;
	private NearestNeighbors<Double,GenericPoint<Double>,Node> neighborQuery;
	
	
	
	
	public RRTtree(Node root) {
		this.root = root;
		theTree = new KDTree<Double,GenericPoint<Double>,Node>(2);
		add(root);
		neighborQuery = new NearestNeighbors<Double,GenericPoint<Double>,Node>();  //Euclidean distance neighbor search
	}
	
	
	public void add(Node n) {
		Point2D pt = n.getPoint();
		theTree.put(new GenericPoint<Double>(pt.getX(),pt.getY()), n);
	}

	public Node closestTo(Point2D pt) {
		Entry<Double,GenericPoint<Double>,Node>[] neighbors = 
			neighborQuery.get(theTree, new GenericPoint<Double>(pt.getX(),pt.getY()), 1);
		if (neighbors.length > 0) {
			return neighbors[0].getNeighbor().getValue();
		} else return null; 

	}

	public Node root() {
		return root;
	}

	public Iterator<Node> iterator() {
		return theTree.values().iterator();
	}

}
