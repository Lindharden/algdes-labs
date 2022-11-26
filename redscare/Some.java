package redscare;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import redscare.Graph.Vertex;

public class Some extends BaseProblem {
	public int numVertices;
	public int originalNumVertices;
	public int newSource;
	public int newSink;
	public boolean result;

	public Some(Graph g) {
		super(g);
	}

	@Override
	public void solve() {

		// if it's a DAG, use Many to solve
		if (this.g.isDAG()) {
			Many m = new Many(g);
			m.solve();
			int numReds = m.getRedsOnPath();
			// if many finds a path using 1 or more reds, Some is true
			result = numReds > 0;
			return;
		}

		// don't solve if the graph is not undirected
		// directed cyclic more specifically
		if (this.g.isDirected())
			return;

		originalNumVertices = this.g.vertices.size();
		numVertices = originalNumVertices * 2 + 2;
		// make room for new source and sink
		newSource = numVertices - 2;
		newSink = numVertices - 1;

		Map<Integer, Map<Integer, Integer>> g;

		for (Vertex v : this.g.getRedVertices()) {
			g = convertGraph();
			// set the internal capacity of the given red vertex to 2
			g.get(v.getId()).put(v.getId() + originalNumVertices, 2);

			// add new edge between red and new sink
			g.get(v.getId() + originalNumVertices).put(newSink, 2);

			// check whether there is a max flow of 2, between new source and new sink
			if (fordFulkerson(g, newSource, newSink) == 2) {
				result = true;
				return;
			}
			// reset the edge from the red vertex to the new sink
			// and reset internal capacity in the red node.
			g.get(v.getId() + originalNumVertices).remove(newSink);
			g.get(v.getId()).put(v.getId() + originalNumVertices, 1);
		}
	}

	@Override
	public void print() {
		if (this.g.isDirected() && this.g.isCyclic()) {
			System.out.println("Some result = Can only solve for undirected graphs (or DAG's)");
		} else {
			System.out.println("Some result = " + result);
		}
	}

	public Map<Integer, Map<Integer, Integer>> convertGraph() {
		Map<Integer, Map<Integer, Integer>> newG = new HashMap<>();

		// for each vertex, split that vertex into two, and connect
		// then with a capacity of 1. This ensures that each vertex
		// (from the original graph) can only be visited once.
		for (Vertex f : this.g.vertices.values()) {
			// the edge between the original vertex and its new mate has capacity 1
			newG.put(f.getId(), new HashMap<Integer, Integer>() {
				{
					put(f.getId() + originalNumVertices, 1);
				}
			});
			newG.put(f.getId() + originalNumVertices, new HashMap<>());
			Map<Integer, Integer> m = newG.get(f.getId() + originalNumVertices);
			for (Vertex t : f.adj) {
				// move the edges that where previously connected with t, to the new mate of f.
				m.put(t.getId(), 1);
			}
		}

		// Connect the new source with the original source and the original sink.
		// Each will have a capacity of 1, to ensure that the flow will go both ways.

		// Since the edge between the red and new sink has capacity 2, we ensure that
		// we find two disjoint paths from the original source, to a red, and to the
		// original sink.
		int startId = this.g.getStart().getId();
		int endId = this.g.getEnd().getId();
		newG.put(newSource, new HashMap<Integer, Integer>() {
			{
				put(startId, 1);
			}
		});

		newG.put(newSource, new HashMap<Integer, Integer>() {
			{
				put(endId, 1);
			}
		});

		return newG;
	}

	// from: https://www.geeksforgeeks.org/breadth-first-search-or-bfs-for-a-graph/
	public boolean bfs(Map<Integer, Map<Integer, Integer>> rGraph, int s, int t, int parent[]) {
		// Create a visited array and mark all vertices as not visited
		boolean visited[] = new boolean[numVertices];
		for (int i = 0; i < numVertices; ++i)
			visited[i] = false;

		// Create a queue, enqueue source vertex and mark source vertex as visited
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(s);
		visited[s] = true;
		parent[s] = -1;

		// Standard BFS Loop
		while (queue.size() != 0) {
			int u = queue.poll();

			for (int v = 0; v < numVertices; v++) {
				if (visited[v] == false
						&& isEdge(u, v, rGraph)) {
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

		// We didn't reach sink in BFS starting from source, so return false
		return false;
	}

	public boolean isEdge(int from, int to, Map<Integer, Map<Integer, Integer>> graph) {
		// if none of the edges exist, return false
		if (!graph.containsKey(from)) return false;
		if (!graph.get(from).containsKey(to)) return false;
		return graph.get(from).get(to) > 0; // return whether the edge is used
	}

	// Returns the maximum flow from s to t in the given graph
	// from:
	// https://www.geeksforgeeks.org/ford-fulkerson-algorithm-for-maximum-flow-problem/
	public int fordFulkerson(Map<Integer, Map<Integer, Integer>> rGraph, int s, int t) {
		int u, v;

		// Create a residual graph and fill the residual
		// graph with given capacities in the original graph
		// as residual capacities in residual graph

		// Residual graph where rGraph[i][j] indicates
		// residual capacity of edge from i to j (if there
		// is an edge. If rGraph[i][j] is 0, then there is not)

		// This array is filled by BFS and to store path
		int parent[] = new int[numVertices];

		int max_flow = 0; // There is no flow initially

		// Augment the flow while there is path from source to sink
		while (bfs(rGraph, s, t, parent)) {
			// Find minimum residual capacity of the edhes
			// along the path filled by BFS. Or we can say
			// find the maximum flow through the path found.
			int path_flow = Integer.MAX_VALUE;
			for (v = t; v != s; v = parent[v]) {
				u = parent[v];
				path_flow = Math.min(path_flow, rGraph.get(u).get(v));
			}

			// update residual capacities of the edges and
			// reverse edges along the path
			for (v = t; v != s; v = parent[v]) {
				u = parent[v];
				rGraph.get(u).put(v, rGraph.get(u).get(v) - path_flow);
				rGraph.get(v).put(u, rGraph.get(v).get(u) + path_flow);
			}

			// Add path flow to overall flow
			max_flow += path_flow;
		}

		// Return the overall flow
		return max_flow;
	}
}
