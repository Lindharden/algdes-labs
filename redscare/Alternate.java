package redscare;

import java.util.LinkedList;

import redscare.Graph.Vertex;

public class Alternate extends BaseProblem {

    public Alternate(Graph g) {
		super(g);
	}

	@Override
	public void solve() {
		BFS();
	}

	@Override
	public void print() {
		int lengthToEnd = this.g.getEnd().getLengthTo();
		// if length to end is 0, then we didn't find a path to the end with alternating colors
		// print 'false' if there was no path
		System.out.println("Alternate result = " + (lengthToEnd > 0 ? true : false));
	}

	private void BFS() {
		// start vertex
		Vertex start = this.g.getStart();
 
        // Create a queue for BFS
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
 
        // Mark the current node as visited and enqueue it
        start.setVisited(true);
        queue.add(start);
 
        while (queue.size() != 0) {
            // Dequeue a vertex from queue and print it
            Vertex vertex = queue.poll();
 
            // Get all adjacent vertices of the dequeued vertex s
            // If an adjacent vertex has not been visited, and is the opposite color of the current one, 
			// then mark it as visited and enqueue it
			for (Vertex v : vertex.adj) {
				if (!v.isVisited() && vertex.isRed() != v.isRed()) {
					v.setVisited(true);
					// length to adjacent vertex is length to current vertex + 1
					v.setLengthTo(vertex.getLengthTo() + 1);
					queue.add(v);
				}
			}
        }
    }
}
