package redscare;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import redscare.Graph.Vertex;

public class Some extends BaseProblem {

	// store all red vertices
	// List<Vertex> reds;
	// boolean targetFound;
	public int numVertices;
	public int newSource;
	public int newSink;
	public boolean result;
	public boolean isPath;

	// solution inspired by UsagiBo on GitHub:
	// 	https://github.com/UsagiBo/Red-Scare/blob/master/src/Some.java

	public Some(Graph g) {
		super(g);
	}

	@Override
	public void solve() {
		// isPath(this.g.getStart());
		// if ((this.g.getEnd().isRed() || this.g.getStart().isRed()) && isPath) {
		// 	result = true;
		// 	return;
		// }
		
		numVertices = this.g.vertices.size() + 2;
		// make room for new source and sink
		newSource = numVertices - 2;
		newSink = numVertices - 1;
		int[][] g = convertGraph();

		for (Vertex v : this.g.getRedVertices()) {
			// add new edge between new source and a red
			g[newSource][v.getId()] = 1;
			// add new edge between red and new sink
			g[v.getId()][newSink] = 2;
			// check whether there is a max flow of 2, between new source and new sink
			if (fordFulkerson(g, newSource, newSink) == 2) {
				result = true;
				return;
			}
			// reset the edges made for the specific red vertex
			g[newSource][v.getId()] = 0; // 0 means the edge doesn't exist
			g[v.getId()][newSink] = 0;
		}
	}

	// private void isPath(Vertex start) {
	// 	for (Vertex v : start.adj) {
	// 		if (v == this.g.getEnd()) isPath = true;
	// 		isPath(v);
	// 	}
	// }

	@Override
	public void print() {
		System.out.println("Some result = " + result);

	}

	public int[][] convertGraph() {
		int[][] newG = new int[numVertices][numVertices];

		for (Vertex f : this.g.vertices.values()) {
			for (Vertex t : f.adj) {
				newG[f.getId()][t.getId()] = 1;
			}
		}

		newG[newSource][this.g.getStart().getId()] = 1;
		newG[this.g.getEnd().getId()][newSource] = 1;

		return newG;
	}

	public boolean bfs(int rGraph[][], int s, int t, int parent[]) {
		// Create a visited array and mark all vertices as
		// not visited
		boolean visited[] = new boolean[numVertices];
		for (int i = 0; i < numVertices; ++i)
			visited[i] = false;

		// Create a queue, enqueue source vertex and mark
		// source vertex as visited
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(s);
		visited[s] = true;
		parent[s] = -1;

		// Standard BFS Loop
		while (queue.size() != 0) {
			int u = queue.poll();

			for (int v = 0; v < numVertices; v++) {
				if (visited[v] == false
						&& rGraph[u][v] > 0) {
					// If we find a connection to the sink
					// node, then there is no point in BFS
					// anymore We just have to set its parent
					// and can return true
					if (v == t) {
						parent[v] = u;
						return true;
					}
					queue.add(v);
					parent[v] = u;
					visited[v] = true;
				}
			}
		}

		// We didn't reach sink in BFS starting from source,
		// so return false
		return false;
	}

	// Returns the maximum flow from s to t in the given
	// graph
	public int fordFulkerson(int graph[][], int s, int t) {
		int u, v;

		// Create a residual graph and fill the residual
		// graph with given capacities in the original graph
		// as residual capacities in residual graph

		// Residual graph where rGraph[i][j] indicates
		// residual capacity of edge from i to j (if there
		// is an edge. If rGraph[i][j] is 0, then there is
		// not)
		int rGraph[][] = new int[numVertices][numVertices];

		for (u = 0; u < numVertices; u++)
			for (v = 0; v < numVertices; v++)
				rGraph[u][v] = graph[u][v];

		// This array is filled by BFS and to store path
		int parent[] = new int[numVertices];

		int max_flow = 0; // There is no flow initially

		// Augment the flow while there is path from source
		// to sink
		while (bfs(rGraph, s, t, parent)) {
			// Find minimum residual capacity of the edhes
			// along the path filled by BFS. Or we can say
			// find the maximum flow through the path found.
			int path_flow = Integer.MAX_VALUE;
			for (v = t; v != s; v = parent[v]) {
				u = parent[v];
				path_flow = Math.min(path_flow, rGraph[u][v]);
			}

			// update residual capacities of the edges and
			// reverse edges along the path
			for (v = t; v != s; v = parent[v]) {
				u = parent[v];
				rGraph[u][v] -= path_flow;
				rGraph[v][u] += path_flow;
			}

			// Add path flow to overall flow
			max_flow += path_flow;
		}

		// Return the overall flow
		return max_flow;
	}

	// @Override
	// public void solve() {
	// for (Vertex v : reds) {
	// // for each red vertex, test whether there is a path
	// // from start to the red vertex, and whether there
	// // is a path from the red vertex to the end.
	// if (BFS(this.g.getStart(), v) && BFS(v, this.g.getEnd())) {
	// // if there is such path mark that we have found a solution and quit
	// targetFound = true;
	// return;
	// }
	// this.g.resetVisited();
	// }
	// }

	// @Override
	// public void print() {
	// // print 'false' if there was no path with at least one red vertex, 'true'
	// // otherwise
	// System.out.println("Some result = " + targetFound);
	// }

	// private boolean BFS(Vertex start, Vertex target) {
	// // Create a queue for BFS
	// LinkedList<Vertex> queue = new LinkedList<Vertex>();

	// // Mark the current node as visited and enqueue it
	// start.visited = true;
	// queue.add(start);

	// while (queue.size() != 0) {
	// // Dequeue a vertex from queue and print it
	// Vertex vertex = queue.poll();

	// // if the current vertex is the target return true
	// if (vertex.name == target.name) {
	// return true;
	// }

	// // Get all adjacent vertices of the dequeued vertex s
	// // If an adjacent vertex has not been visited, then mark it as visited and
	// // enqueue it
	// for (Vertex v : vertex.adj) {
	// if (!v.visited) {
	// v.visited = true;
	// queue.add(v);
	// }
	// }
	// }
	// return false;
	// }
}
