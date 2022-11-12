package redscare;

import java.util.HashMap;
import java.util.Map;

import redscare.Graph.Vertex;

public class Few extends BaseProblem {

	private Map<String, Integer> dist; // could probably use Vertex.lengthTo instead
	private Map<String, Boolean> shortestPathSet;

	public Few(Graph g) {
		super(g);

		// initiate distance and shortest path to default values
		dist = new HashMap<>();
		shortestPathSet = new HashMap<>();
		for (String vertexName : this.g.vertices.keySet()) {
			dist.put(vertexName, Integer.MAX_VALUE);
			shortestPathSet.put(vertexName, false);
		}

		dist.put(this.g.getStart().getName(), vertexWeight(this.g.getStart()));
	}

	@Override
	public void solve() {
		// Find shortest path (fewest amounts of reds visited) for all vertices
		for (int count = 0; count < this.g.vertices.size() - 1; count++) {
			// Pick the minimum distance vertex from the set
			// of vertices not yet processed. u is always
			// equal to src in first iteration.
			Vertex from = minDistance();
			String fName = from.getName();

			// Mark the picked vertex as processed
			shortestPathSet.put(from.getName(), true);

			// Update dist value of the adjacent vertices of
            // the picked vertex.
			for (Vertex to : from.adj) {
				String tName = to.getName();
				
				// Update dist only if is not in shortestPathSet,
                // there is an edge from 'from' to 'to', and total
                // weight of path from src to 'to' through 'from' is
                // smaller than current value of dist
				if (!shortestPathSet.get(tName) && from.adj.contains(to)
					&& dist.get(fName) != Integer.MAX_VALUE
					&& dist.get(fName) + vertexWeight(to) < dist.get(tName)) {
						dist.put(tName, dist.get(fName) + vertexWeight(to));
					}
			}
		}
	}

	// if the vertex is red the weight is 1, else its 0
	private int vertexWeight(Vertex v) {
		return v.isRed() ? 1 : 0;
	}

	// A utility function to find the vertex with minimum
	// distance value, from the set of vertices not yet
	// included in shortest path tree
	private Vertex minDistance() {

		Vertex minVertex = null;
		int minDist = Integer.MAX_VALUE;

		for (String vertexName : this.g.vertices.keySet()) {
			// for each vertex test whether the vertex is included in the shortest path
			// also test whether the distance to that vertex is shorter than the smallest
			// dist we have found so far
			if (shortestPathSet.get(vertexName) == false && dist.get(vertexName) <= minDist) {
				minDist = dist.get(vertexName);
				minVertex = this.g.vertices.get(vertexName);
			}
		}

		// return vertex with minimum dinstance value
		return minVertex;
	}

	@Override
	public void print() {
		int minReds = dist.get(this.g.getEnd().getName());
		System.out.println("Few result = " + (minReds == Integer.MAX_VALUE ? -1 : minReds));
	}
}
