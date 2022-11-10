package redscare;

import java.util.ArrayList;
import java.util.HashMap;

/* Directed Graph */
public class Graph {

    // map that maps a vertex name, to an vertex
    HashMap<String, Vertex> vertices;
    String start; // start node
    String end; // end node

    public Graph() {
        vertices = new HashMap<String, Vertex>();
    }

    public class Vertex {
        String name;
        boolean red;
        int lengthTo;
        boolean visited;

        // list of adjacent vertecies (represents edges)
        ArrayList<Vertex> adj = new ArrayList<>();

        public Vertex(String name, Boolean red) {
            this.name = name;
            this.red = red;
        }

        public Boolean isRed() {
            return red;
        }
    }

    /**
     * Add vertex to graph
     * name - Name of vertex
     * red - Whether the vertex is 'red' or not
     */
    public void addVertex(String name, boolean red) {
        vertices.put(name.trim(), new Vertex(name, red));
    }

    /**
     * Add edge between vertices
     * from - The name of the vertex the edge should start from
     * to - The name of the vertex the edge should go to
     */
    public void addEdge(String from, String to) {
        Vertex fromVertex = vertices.get(from.trim());
        Vertex toVertex = vertices.get(to.trim());
        fromVertex.adj.add(toVertex);
    }

    /** Set start node of graph */
    public void setStart(String start) {
        this.start = start.trim();
    }

    /** Set end node of graph */
    public void setEnd(String end) {
        this.end = end.trim();
    }

    /** Get start node of graph */
    public Vertex getStart() {
        return vertices.get(this.start);
    }

    /** Get end node of graph */
    public Vertex getEnd() {
        return vertices.get(this.end);
    }
}
