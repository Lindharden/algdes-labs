package redscare;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import redscare.Graph.Vertex;

public class Some extends BaseProblem {

	// store all red vertices
	List<Vertex> reds;
	boolean targetFound;

	public Some(Graph g) {
		super(g);

		// fetch all red vertices
		reds = this.g.vertices.values().stream()
				.filter(v -> v.isRed())
				.collect(Collectors.toList());
	}

	@Override
	public void solve() {
		// solve only for undirected, using network flow
		if (this.g.isDirected()) return;

		// TODO Auto-generated method stub
		
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
		
	}

	// @Override
	// public void solve() {
	// 	for (Vertex v : reds) {
	// 		// for each red vertex, test whether there is a path
	// 		// from start to the red vertex, and whether there
	// 		// is a path from the red vertex to the end.
	// 		if (BFS(this.g.getStart(), v) && BFS(v, this.g.getEnd())) {
	// 			// if there is such path mark that we have found a solution and quit
	// 			targetFound = true;
	// 			return;
	// 		}
	// 		this.g.resetVisited();
	// 	}
	// }

	// @Override
	// public void print() {
	// 	// print 'false' if there was no path with at least one red vertex, 'true'
	// 	// otherwise
	// 	System.out.println("Some result = " + targetFound);
	// }

	// private boolean BFS(Vertex start, Vertex target) {
	// 	// Create a queue for BFS
	// 	LinkedList<Vertex> queue = new LinkedList<Vertex>();

	// 	// Mark the current node as visited and enqueue it
	// 	start.visited = true;
	// 	queue.add(start);

	// 	while (queue.size() != 0) {
	// 		// Dequeue a vertex from queue and print it
	// 		Vertex vertex = queue.poll();

	// 		// if the current vertex is the target return true
	// 		if (vertex.name == target.name) {
	// 			return true;
	// 		}

	// 		// Get all adjacent vertices of the dequeued vertex s
	// 		// If an adjacent vertex has not been visited, then mark it as visited and
	// 		// enqueue it
	// 		for (Vertex v : vertex.adj) {
	// 			if (!v.visited) {
	// 				v.visited = true;
	// 				queue.add(v);
	// 			}
	// 		}
	// 	}
	// 	return false;
	// }
}
