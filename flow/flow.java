package flow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class flow {

    public static class Edge {
        final int to;
        public int capacity;
        public int originalCapacity;

        public Edge(int to, int capacity) {
            this.to = to;
            this.capacity = capacity;
            this.originalCapacity = capacity;
        }
    }

    public static void main(String[] args) {
        try {
            File file = new File("data/rail.txt");
            Scanner scanner = new Scanner(file);

            int numNodes = Integer.parseInt(scanner.nextLine());
            LinkedList<Edge>[] adj = new LinkedList[numNodes];

            for (int i = 0; i < numNodes; i++) {
                scanner.nextLine(); // skip name of node
                adj[i] = new LinkedList<>(); // use index to instantiate new adjacency list
            }
            int numArcs = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < numArcs; i++) {
                String[] uvc = scanner.nextLine().split(" ");
                int u = Integer.parseInt(uvc[0]); // parse id of 'from' node
                int v = Integer.parseInt(uvc[1]); // parse id of 'to' node
                int c = Integer.parseInt(uvc[2]); // parse capacity of edge
                if (c == -1)
                    c = Integer.MAX_VALUE; // if the capacity is -1, it's equal to infinity
                adj[u].add(new Edge(v, c)); // create new edge from 'from' node to 'to' node with capacity
                adj[v].add(new Edge(u, c)); // create edge in opposite direction, as we have an undirected graph
            }
            scanner.close();

            // find the maximum flow using Ford Fulkerson
            int maxFlow = fordFulkerson(adj, numNodes);

            // find and print the minimum cut
            List<int[]> minCutEdges = minCut(adj);

            // print min cut
            for (int[] minCutEdge : minCutEdges) {
                System.out.println(minCutEdge[0] + " " + minCutEdge[1] + " " + minCutEdge[2]);
            }

            System.out.println("Max flow: " + maxFlow);

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }

    }

    public static int fordFulkerson(LinkedList<Edge>[] adj, int numNodes) {
        int s = 0; // start (source)
        int t = numNodes - 1; // end (sink)
        int v, u;

        int[] path = bfs(adj, numNodes, s, t); // find first path

        int maxFlow = 0;

        // if path.length is 0, we didnt find any new paths and we are done
        while (path.length != 0) {
            int bottleneck = Integer.MAX_VALUE;

            // go through the path we just found
            for (v = t; v != s; v = path[v]) {
                u = path[v];

                // find the lowest capacity on the path (the bottleneck)
                bottleneck = Math.min(bottleneck, getEdgeById(adj[u], v).capacity);
            }

            // go through the path a second time to update the flows (to the bottleneck)
            for (v = t; v != s; v = path[v]) {
                u = path[v];

                // fetch the edges in each direction
                Edge fst = getEdgeById(adj[u], v); // the one we are going to
                Edge snd = getEdgeById(adj[v], u); // the one we are coming from

                fst.capacity -= bottleneck; // increase forward capacity
                snd.capacity += bottleneck; // decrease residual capacity
            }

            maxFlow += bottleneck;

            // bfs finds a new path since the flows are changed
            // this is because edges are getting saturated
            path = bfs(adj, numNodes, s, t);
        }
        return maxFlow;
    }

    private static List<int[]> minCut(LinkedList<Edge>[] adj) {

        // find all vetices reachable from source
        boolean[] isVisited = new boolean[adj.length];
        dfs(adj, 0, isVisited);

        // store the edges of the minimum cut
        List<int[]> results = new ArrayList<>();

        // go through all the vertices in the graph
        for (int i = 0; i < adj.length; i++) {
            for (Edge edge : adj[i]) {
                // for each edge check whether it leads to an unvisited vertex
                if (isVisited[i] && !isVisited[edge.to]) {
                    // if the 'to' vertex is unvisited, 'i' is in the minimum cut
                    results.add(new int[] { i, edge.to, edge.originalCapacity });
                }
            }
        }

        return results;
    }

    private static Edge getEdgeById(LinkedList<Edge> edges, int id) {
        for (Edge e : edges) {
            // iterate through all the adjacent nodes from the node
            // if we find an adjacent node that is equal our id, return the corresponding
            // edge
            if (e.to == id)
                return e;
        }
        return null;
    }

    /**
     * Breadth First Search for finding paths in graphs
     */
    public static int[] bfs(LinkedList<Edge>[] adj, int v, int src, int dst) {
        // save whether we have visited the node at the given index
        boolean[] visited = new boolean[v];
        for (int i = 0; i < v; ++i)
            visited[i] = false;

        // stores the path we find in reverse order
        // index i stores the node we reach after i on the path
        int[] path = new int[v];

        // queue of nodes to visit
        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(src); // add the source node as the first node to consider
        visited[src] = true; // mark the source node as visited

        // while there is still nodes to visit
        while (queue.size() != 0) {
            int u = queue.poll(); // fetch next node to visit

            // consider all adjacent nodes to u
            for (Edge e : adj[u]) {

                // use the current node in path if its not already visited
                // the node can only be used if its capacity is not reached (capacity > 0)
                if (!visited[e.to] && e.capacity > 0) {
                    path[e.to] = u; // set the next node on path to u

                    if (e.to == dst) {
                        // if the next node is the sink, we have found our path
                        return path;
                    }

                    // we have not reached the sink, add the to vertex of the edge to the queue
                    queue.add(e.to);
                    visited[e.to] = true; // mark the node as visited
                }
            }
        }

        // it was not possible to find a path
        return new int[0];
    }

    /**
     * Depth First Search to find all reachable vertices from source.
     */
    public static void dfs(LinkedList<Edge>[] adj, int src, boolean[] visited) {

        // recursion stops once there is no more reachable nodes

        // mark the current node as visited
        visited[src] = true;

        for (Edge e : adj[src]) {
            // go through each edge from the current node
            if (e.capacity > 0 && !visited[e.to]) {
                // if the 'to' node is "reachable", and is not visited, search from that node
                dfs(adj, e.to, visited);
            }
        }

    }
}
