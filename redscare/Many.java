package redscare;

import java.util.HashMap;
import java.util.Map;

import redscare.Graph.Vertex;

public class Many extends BaseProblem {

	private Map<String, Integer> dist; // could probably use Vertex.lengthTo instead
	private Map<String, Boolean> longestPathSet;

	public Many(Graph g) {
		super(g);

		// initiate distance and longest path to default values
		dist = new HashMap<>();
		longestPathSet = new HashMap<>();
		for (String vertexName : this.g.vertices.keySet()) {
			dist.put(vertexName, Integer.MIN_VALUE);
			longestPathSet.put(vertexName, false);
		}

		dist.put(this.g.getStart().getName(), vertexWeight(this.g.getStart()));
	}

	@Override
	public void solve() {
		// if the graph is not a DAG, we do not solve it
		if (!this.g.isDAG())
			return;

		// Find longest path (most amounts of reds visited) for all vertices
		for (int count = 0; count < this.g.vertices.size() - 1; count++) {
			// Pick the maximum distance vertex from the set
			// of vertices not yet processed. u is always
			// equal to src in first iteration.
			Vertex from = maxDistance();
			String fName = from.getName();

			// Mark the selected vertex as processed
			longestPathSet.put(fName, true);

			// Update dist value of the adjacent vertices of
			// the selected vertex.
			for (Vertex to : from.adj) {
				String tName = to.getName();

				// Update dist only if is not in longestPathSet,
				// there is an edge from 'from' to 'to', and total
				// weight of path from src to 'to' through 'from' is
				// larger than current value of dist
				if (!longestPathSet.get(tName) && from.adj.contains(to)
						&& dist.get(fName) != Integer.MIN_VALUE
						&& dist.get(fName) + vertexWeight(to) > dist.get(tName)) {
					dist.put(tName, dist.get(fName) + vertexWeight(to));
				}
			}
		}
	}

	// if the vertex is red the weight is 1, else its 0
	private int vertexWeight(Vertex v) {
		return v.isRed() ? 1 : 0;
	}

	// A utility function to find the vertex with maximum
	// distance value, from the set of vertices not yet
	// included in longest path tree
	private Vertex maxDistance() {

		Vertex maxVertex = null;
		int maxDist = Integer.MIN_VALUE;

		for (String vertexName : this.g.vertices.keySet()) {
			// for each vertex test whether the vertex is included in the longest path
			// also test whether the distance to that vertex is longer than the largest
			// dist we have found so far
			if (longestPathSet.get(vertexName) == false && dist.get(vertexName) >= maxDist) {
				maxDist = dist.get(vertexName);
				maxVertex = this.g.vertices.get(vertexName);
			}
		}

		// return vertex with maximum distance value
		return maxVertex;
	}

	@Override
	public void print() {
		if (!this.g.isDAG()) {
			System.out.println("Many result = Can only solve for DAG");
			return;
		}
		int maxReds = dist.get(this.g.getEnd().getName());
		System.out.println("Many result = " + (maxReds == Integer.MIN_VALUE ? -1 : maxReds));
	}

}
