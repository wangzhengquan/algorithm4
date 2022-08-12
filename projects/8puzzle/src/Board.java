import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class Board {

  private int[][] tiles;
  //  private int[][] goal;
  private int n;
  private int blRow, blCol;
  private boolean changed;
  private int manhattanDist;
  private int hammingDist;

  // create a board from an n-by-n array of tiles,
  // where tiles[row][col] = tile at (row, col)
  public Board(int[][] tiles) {
    if (tiles == null) {
      throw new IllegalArgumentException();
    }
    n = tiles.length;
    changed = true;
    this.tiles = new int[n][n];
//    goal = new int[n][n];
    int i = 0;
    for (int row = 0; row < n; row++)
      for (int col = 0; col < n; col++) {
//        goal[row][col] = ++i;
        this.tiles[row][col] = tiles[row][col];
        if (tiles[row][col] == 0) {
          blRow = row;
          blCol = col;
        }
      }

    onChange();
  }

  // string representation of this board
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(n + "\n");

    for (int row = 0; row < n; row++) {
      for (int col = 0; col < n; col++) {
        buf.append(tiles[row][col] + " ");
      }
      buf.append("\n");
    }
    return buf.toString();
  }

  // board dimension n
  public int dimension() {
    return n;
  }

  // number of tiles out of place
  public int hamming() {
    return hammingDist;
  }


  // sum of Manhattan distances between tiles and goal
  public int manhattan() {
    return manhattanDist;
  }


  // is this board the goal board?
  public boolean isGoal() {
    int goal = 0;
    int end = n * n;
    for (int row = 0; row < n; row++)
      for (int col = 0; col < n; col++) {
        if (++goal == end) goal = 0;
        if (tiles[row][col] != goal) return false;
      }
    return true;
  }

  // does this board equal y?
  public boolean equals(Object y) {
    if (y == this) return true;
    if (y == null) return false;
    if (y.getClass() != this.getClass())
      return false;

    Board that = (Board) y;
    if (this.tiles.length != that.tiles.length)
      return false;
    if (this.tiles[0].length != that.tiles[0].length)
      return false;
    for (int row = 0; row < this.tiles.length; row++)
      for (int col = 0; col < this.tiles[0].length; col++)
        if (this.tiles[row][col] != that.tiles[row][col]) return false;
    return true;
  }

  // all neighboring boards
  public Iterable<Board> neighbors() {
    int[] dx = {-1, 0, 0, 1};
    int[] dy = {0, -1, 1, 0};
    List<Board> neighborsList = new ArrayList<Board>();

    int nbRow, nbCol;
    for (int i = 0; i < 4; i++) {
      nbRow = blRow + dx[i];
      nbCol = blCol + dy[i];
      if (isInGrid(nbRow, nbCol)) {
        Board nb = new Board(this.tiles);
        nb.exchWithBlank(nbRow, nbCol);
        neighborsList.add(nb);
      }
    }

    return neighborsList;
  }


  // a board that is obtained by exchanging any pair of tiles
  public Board twin() {
    Board b = new Board(this.tiles);
    int row = blRow - 1;
    if (row < 0)
      row = blRow + 1;

    b.exch(row, 0, row, 1);
    return b;
  }

  /***************************************************************************
   *  Helper  function.
   ***************************************************************************/
  private void onChange() {
    measure();
//    neighbors();
    changed = false;
  }

  private void measure() {
    if (!changed) {
      return;
    }
    manhattanDist = 0;
    hammingDist = 0;
    int goal = 0;
    int end = n * n;
    for (int row = 0; row < n; row++)
      for (int col = 0; col < n; col++) {
        if (++goal == end) goal = 0;
        if (tiles[row][col] != 0 && tiles[row][col] != goal) {
          hammingDist++;
          int[] rc = goalRowColOf(tiles[row][col]);
          int dr = row + 1 - rc[0];
          int dc = col + 1 - rc[1];
//          StdOut.printf("title[%d, %d]=%d : rc=[%d, %d], dr=%d, dc=%d \n",
//              row, col, tiles[row][col],
//              rc[0], rc[1],
//              dr, dc);
          manhattanDist += Math.abs(dr) + Math.abs(dc);
        }
      }
  }

  private int[] goalRowColOf(int tile) {
    int[] rc = new int[2];

    int r = (int) Math.ceil((double) tile / n);
    int c = tile - (r - 1) * n;
    rc[0] = r;
    rc[1] = c;
    return rc;
  }

  private void exch(int srcRow, int srcCol, int desRow, int desCol) {
    int tmp = tiles[desRow][desCol];
//    StdOut.printf("%d, %d, %d, %d, %d, %d\n", srcRow, srcCol, desRow, desCol, tmp, tiles[srcRow][srcCol]);
    tiles[desRow][desCol] = tiles[srcRow][srcCol];
    tiles[srcRow][srcCol] = tmp;
    changed = true;
    onChange();
  }

  private void exchWithBlank(int desRow, int desCol) {
    exch(blRow, blCol, desRow, desCol);
    blRow = desRow;
    blCol = desCol;
  }


  private boolean isInGrid(int row, int col) {
    if (row < 0 || row >= n || col < 0 || col >= n) {
      return false;
    }
    return true;
  }


  // unit testing (not graded)
  public static void main(String[] args) {
    In in = new In(args[0]);
    int n = in.readInt();
    int[][] tiles = new int[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        tiles[i][j] = in.readInt();
    Board initial = new Board(tiles);

//    Board clone = (Board) initial.clone();
//    clone.tiles[0][0] = 100;
    StdOut.println("---initial---");
    StdOut.println(initial);
    StdOut.println("---twin---");
    StdOut.println(initial.twin());
    StdOut.println("hamming:" + initial.hamming());
    StdOut.println("manhattan:" + initial.manhattan());


    StdOut.println("---neighbors---");
    for (Board b : initial.neighbors()) {
      StdOut.println(b);
    }


  }


}
