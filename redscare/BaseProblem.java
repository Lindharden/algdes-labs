package redscare;

public abstract class BaseProblem implements IProblem {
    final Graph g;

    public BaseProblem(Graph g) {
        this.g = g;
    }
}
