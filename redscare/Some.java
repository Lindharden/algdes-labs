package redscare;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import redscare.Graph.Vertex;

public class Some extends BaseProblem {

	// store all reachable red vertices we find via BFS
	List<Vertex> reds = new ArrayList<>();
	boolean targetFound;

    public Some(Graph g) {
		super(g);
	}

	@Override
	public void solve() {
		BFS(null, true); // first phase (find reds)
		for (Vertex v : reds) { // second phase (find end from reds)
			// if we have found a way to a red, and then to the end, 
			// then we don't have to continue
			if (targetFound) break; 
			BFS(v, false);
		}
	}

	@Override
	public void print() {
		// print 'false' if there was no path with at least one red vertex, 'true' otherwise
		System.out.println("Some result = " + targetFound);
	}

	private void BFS(Vertex s, boolean findRed) {
		// start vertex
		// if we want to find an end from a red (second phase), 
		// we set the start to a vertex in reds
		Vertex start = findRed ? this.g.getStart() : s;
 
        // Create a queue for BFS
        LinkedList<Vertex> queue = new LinkedList<Vertex>();
 
        // Mark the current node as visited and enqueue it
        start.visited = true;
        queue.add(start);
 
        while (queue.size() != 0) {
            // Dequeue a vertex from queue and print it
            Vertex vertex = queue.poll();

			// if we have found a path to the end, from a red (second phase), mark that we are finished
			if (!findRed && vertex.name == this.g.end) { 
				targetFound = true; 
				break; 
			}
			
			// we want to find red vertices (first phase)
			if (findRed && vertex.red) {
				// if the current vertex is red, add it to the list of red vertices
				reds.add(vertex);
				// don't continue the search from the red node
				continue;
			}
 
            // Get all adjacent vertices of the dequeued vertex s
            // If an adjacent vertex has not been visited, then mark it as visited and enqueue it
			for (Vertex v : vertex.adj) {
				if (!v.visited) {
					v.visited = true;
					queue.add(v);
				}
			}
        }
    }
}
