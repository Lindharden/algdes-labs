package redscare;

import java.util.LinkedList;

import redscare.Graph.Vertex;

public class None extends BaseProblem {

    public None(Graph g) {
		super(g);
	}

	@Override
	public void solve() {
		BFS();
	}

	@Override
	public void print() {
		int lengthToEnd = this.g.getEnd().lengthTo;
		// if length to end is 0, then we didn't find a path to the end
		// print -1 if there was no path
		System.out.println("None result = " + (lengthToEnd == 0 ? -1 : lengthToEnd));
	}

	private void BFS() {
		// start vertex
		Vertex start = this.g.getStart();
 
        // Create a queue for BFS
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
 
        // Mark the current node as visited and enqueue it
        start.visited = true;
        queue.add(start);
 
        while (queue.size() != 0) {
            // Dequeue a vertex from queue and print it
            Vertex vertex = queue.poll();
 
            // Get all adjacent vertices of the dequeued vertex s
            // If an adjacent has not been visited, and is not red, then mark it as visited and enqueue it
			for (Vertex v : vertex.adj) {
				if (!v.visited && !v.red) {
					v.visited = true;
					// length to adjacent vertex is length to current vertex + 1
					v.lengthTo = vertex.lengthTo + 1;
					queue.add(v);
				}
			}
        }
    }
}
