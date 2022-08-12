import edu.princeton.cs.algs4.Queue;

public class GraphVisit {
  private boolean[] marked;     // marked[v] = true iff s->v path in residual graph
  private FlowEdge[] edgeTo;    // edgeTo[v] = last edge on shortest residual s->v path
  private int count;         // number of vertices reachable from source(s)
  private final int V;          // number of vertices

  public GraphVisit(FlowNetwork G, int s) {
    V = G.V();
    marked = new boolean[G.V()];
    edgeTo = new FlowEdge[G.V()];
    validateVertex(s);
    dfs(G, s);
  }

  public GraphVisit(FlowNetwork G, Iterable<Integer> sources) {
    V = G.V();
    marked = new boolean[G.V()];
    edgeTo = new FlowEdge[G.V()];
    validateVertices(sources);
    for (int v : sources) {
      if (!marked[v]) dfs(G, v);
    }
  }

  private void dfs(FlowNetwork G, int v) {
    count++;
    marked[v] = true;
    if (edgeTo[v] != null)
      edgeTo[v].addResidualFlowTo(v, 1);
    for (FlowEdge e : G.adj(v)) {
      int w = e.to();
      if (e.residualCapacityTo(w) > 0 && !marked[w]) {
        edgeTo[w] = e;
        dfs(G, w);
      }
    }
  }

  private void bfs(FlowNetwork G, int s) {

    edgeTo = new FlowEdge[G.V()];
    marked = new boolean[G.V()];

    // breadth-first search
    Queue<Integer> queue = new Queue<Integer>();
    queue.enqueue(s);
    marked[s] = true;
    while (!queue.isEmpty()) {
      int v = queue.dequeue();

      for (FlowEdge e : G.adj(v)) {
        int w = e.other(v);
        // if residual capacity from v to w
        if (e.residualCapacityTo(w) > 0) {
          if (!marked[w]) {
            edgeTo[w] = e;
            marked[w] = true;
            queue.enqueue(w);
          }
        }
      }
    }

    // is there an augmenting path?
//    return marked[t];
  }

  /**
   * Is there a directed path from the source vertex (or any
   * of the source vertices) and vertex {@code v}?
   *
   * @param v the vertex
   * @return {@code true} if there is a directed path, {@code false} otherwise
   * @throws IllegalArgumentException unless {@code 0 <= v < V}
   */
  public boolean marked(int v) {
    validateVertex(v);
    return marked[v];
  }

  // throw an IllegalArgumentException unless {@code 0 <= v < V}
  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
  }

  // throw an IllegalArgumentException if vertices is null, has zero vertices,
  // or has a vertex not between 0 and V-1
  private void validateVertices(Iterable<Integer> vertices) {
    if (vertices == null) {
      throw new IllegalArgumentException("argument is null");
    }
    int V = marked.length;
    int count = 0;
    for (Integer v : vertices) {
      count++;
      if (v == null) {
        throw new IllegalArgumentException("vertex is null");
      }
      validateVertex(v);
    }
    if (count == 0) {
      throw new IllegalArgumentException("zero vertices");
    }
  }

}
