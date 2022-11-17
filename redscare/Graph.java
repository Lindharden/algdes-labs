package redscare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* Directed Graph */
public class Graph {

    // map that maps a vertex name, to an vertex
    HashMap<String, Vertex> vertices;
    String start; // start node
    String end; // end node

    private boolean isDirected;
    private boolean isCyclic;

    public Graph() {
        vertices = new HashMap<String, Vertex>();
    }

    public class Vertex {
        private int id;
        private String name;
        private boolean red;
        private int lengthTo;
        private boolean visited;

        // list of adjacent vertecies (represents edges)
        ArrayList<Vertex> adj = new ArrayList<>();

        public Vertex(String name, Boolean red, int id) {
            this.name = name;
            this.red = red;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Boolean isRed() {
            return red;
        }

        public String getName() {
            return name;
        }

        public int getLengthTo() {
            return lengthTo;
        }

        public void setLengthTo(int lengthTo) {
            this.lengthTo = lengthTo;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

    }

    /**
     * Add vertex to graph
     * name - Name of vertex
     * red - Whether the vertex is 'red' or not
     */
    public void addVertex(String name, boolean red, int id) {
        vertices.put(name.trim(), new Vertex(name, red, id));
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
    
    /** Set all vertices to not visited */
    public void resetVisited() {
        vertices.values().forEach(v -> v.visited = false);
    }

    /** Get whether graph is cyclic or not */
    public boolean isCyclic() {
        return isCyclic;
    }

    /** Set whether graph is cyclic or not */
    public void setCyclic() {
        this.isCyclic = isCyclicGraph();
        resetVisited();
    }

    /** Get whether graph is directed or not */
    public boolean isDirected() {
        return isDirected;
    }

    /** Set whether graph is directed or not */
    public void setDirected(boolean isDirected) {
        this.isDirected = isDirected;
    }

    /** Get whether graph is DAG or not */
    public boolean isDAG() {
        return isDirected && !isCyclic;
    }

    /** Find whether graph is cyclic or not */
    private boolean isCyclicGraph() {
         
        // Mark all the vertices as not visited and
        // not part of recursion stack
        HashMap<String, Boolean> vertRec = new HashMap<>();
        for (String vertName : vertices.keySet()) {
            vertRec.put(vertName, false);
        }

        // Call the recursive helper function to
        // detect cycle in different DFS trees
        for (Vertex v : vertices.values()) {
            if (isCyclicUtil(v, vertRec)) return true;
        }
 
        return false;
    }

    private boolean isCyclicUtil(Vertex v, HashMap<String, Boolean> vertRec) {
        // Mark the current node as visited and
        // part of recursion stack
        if (vertRec.get(v.name))
            return true;
 
        if (v.visited)
            return false;
             
        v.visited = true;
 
        vertRec.put(v.name, true);
        List<Vertex> children = v.adj;
         
        for (Vertex c: children)
            if (isCyclicUtil(c, vertRec))
                return true;
                 
        vertRec.put(v.name, false);
 
        return false;
    }

    /*
     * Returns list of all red vertices
     */
    public ArrayList<Vertex> getRedVertices() {
        ArrayList<Vertex> values = new ArrayList<>(this.vertices.values());
        values.removeIf(v -> !v.isRed());
        return values;
    }
}
