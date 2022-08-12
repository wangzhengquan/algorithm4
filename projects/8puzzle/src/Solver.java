import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;


public class Solver {

  private boolean solvable;

  private SearchNode goalNode;

  private static class SearchNode implements Comparable<SearchNode> {
    Board board;
    SearchNode pre;
    int moves;
    int priority;

    public SearchNode(Board board, int moves, SearchNode pre) {
      this.board = board;
      this.pre = pre;
      this.moves = moves;
      this.priority = this.moves + this.board.manhattan();
    }

    public int compareTo(SearchNode that) {
      return Integer.compare(this.priority, that.priority);
    }
  }


  public Solver(Board initial) {
    if (initial == null) {
      throw new IllegalArgumentException();
    }
    solvable = false;
    MinPQ<SearchNode> pq = new MinPQ<>();
    MinPQ<SearchNode> contrastPq = new MinPQ<>();

    SearchNode node = new SearchNode(initial, 0, null);
    SearchNode contrastNode = new SearchNode(initial.twin(), 0, null);
    while (true) {
      if (node.board.isGoal()) {
        solvable = true;
        break;
      } else if (contrastNode.board.isGoal()) {
        solvable = false;
        break;
      }
      node = nextMinNode(node, pq);
      contrastNode = nextMinNode(contrastNode, contrastPq);
    }
//    StdOut.println(node.board);
    if (solvable)
      goalNode = node;
  }

  private SearchNode nextMinNode(SearchNode node, MinPQ<SearchNode> pq) {
    for (Board b : node.board.neighbors()) {
      if (node.pre == null || !b.equals(node.pre.board)) {
        pq.insert(new SearchNode(b, node.moves + 1, node));
      }
    }
    return pq.delMin();
  }

  // is the initial board solvable? (see below)
  public boolean isSolvable() {
    return solvable;
  }

  // min number of moves to solve initial board; -1 if unsolvable
  public int moves() {
    if (!isSolvable())
      return -1;
    return goalNode.moves;
  }

  // sequence of boards in a shortest solution; null if unsolvable
  public Iterable<Board> solution() {
    if (!isSolvable()) return null;

    Stack<Board> sk = new Stack<>();
    SearchNode node = goalNode;
    while (node != null) {
      sk.push(node.board);
      node = node.pre;
    }
    return sk;

  }

  // test client (see below)
  public static void main(String[] args) {

    // create initial board from file
    In in = new In(args[0]);
    int n = in.readInt();
    int[][] tiles = new int[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        tiles[i][j] = in.readInt();
    Board initial = new Board(tiles);

    // solve the puzzle
    Solver solver = new Solver(initial);

    // print solution to standard output
    if (!solver.isSolvable())
      StdOut.println("No solution possible");
    else {
      StdOut.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution())
        StdOut.println(board);
    }
  }

}
