package nodeTypes;
import java.awt.geom.Point2D;
import java.util.Iterator;

import rrt.Node;
import rrt.Tree;

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
		//see if the actual point is there
		GenericPoint<Double> ptRep = new GenericPoint<Double>(pt.getX(), pt.getY());
		if (theTree.containsKey(ptRep)) return theTree.get(ptRep);
		Entry<Double,GenericPoint<Double>,Node>[] neighbors = 
			neighborQuery.get(theTree, ptRep, 1);
		if (neighbors.length > 0) {
			return neighbors[0].getNeighbor().getValue();
		} else return null; 

	}
	
	private Node[] toNodeArray(Entry<Double,GenericPoint<Double>,Node>[] neighbors) {
		Node[] res = new Node[neighbors.length];
		
		for (int i = 0; i<neighbors.length; i++) {
			res[i] = neighbors[i].getNeighbor().getValue();			
		}
		return res;
	}
	
	public Node[] nClosestTo(Point2D pt, int n) {
		if (theTree.size() < n) {
			return null;
		}
		GenericPoint<Double> ptRep = new GenericPoint<Double>(pt.getX(),pt.getY());
			try {
				Entry<Double,GenericPoint<Double>,Node>[] neighbors = 
				neighborQuery.get(theTree, ptRep, n);
				if (neighbors.length > 0) {
					return toNodeArray(neighbors);
				} else return null;
			} catch (Exception e) {
				return null;
			}
		
	}

	public Node root() {
		return root;
	}

	public int nNodes() {
		return theTree.size();
	}
	
	public Iterator<Node> iterator() {
		return theTree.values().iterator();
	}
	


}
