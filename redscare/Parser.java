package redscare;

import redscare.Graph;
import java.util.Scanner;
import java.io.*;

public class Parser {
    Graph graph;

    /* Parse input, and construct graph */
    public Graph parse(File file) {
        /*
         * Input structure:
         * n (no. of vertices), m (no. of edges), r (no. of red edges)
         * Then, start vertex, end vertex
         * Then, n vertices, if red, name is followed by " *"
         * Then, m edges, if undirected, then "a--b", else if directed, then "a->b"
         */
        try {
            Scanner sc = new Scanner(file);
            graph = new Graph();

            String[] line = sc.nextLine().split(" ");
            int n = Integer.parseInt(line[0]);
            int m = Integer.parseInt(line[1]);
            int r = Integer.parseInt(line[2]);

            line = sc.nextLine().split(" ");
            graph.setStart(line[0]); // save the start node
            graph.setEnd(line[1]); // save the end node

            // add vertices to graph
            for (int i = 0; i < n; i++) {
                line = sc.nextLine().split(" ");
                Boolean red = line.length > 1 ? true : false;
                graph.addVertex(line[0], red);
            }

            // parse and add edges
            for (int i = 0; i < m; i++) {
                String l = sc.nextLine();
                if (l.contains("--")) {
                    // parse undirected edge
                    String[] edge = l.split(" -- ");
                    // edge in both ways
                    graph.addEdge(edge[0], edge[1]);
                    graph.addEdge(edge[1], edge[0]);
                } else if (l.contains("->")) {
                    // parse directed edge
                    String[] edge = l.split(" -> ");
                    // only one edge
                    graph.addEdge(edge[0], edge[1]);
                }
            }
            sc.close();
            return graph;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
        
    }
}