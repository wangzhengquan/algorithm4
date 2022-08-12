import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Set;
import java.util.TreeSet;

public class BoggleSolver {


  private static final int R = 26;        // extended ASCII

  private Node root;      // root of trie
  private int n;          // number of keys in trie

  // R-way trie node
  private static class Node {
    private Node[] next = new Node[R];
    private boolean isString;
  }


  /**
   * Does the set contain the given key?
   *
   * @param key the key
   * @return {@code true} if the set contains {@code key} and
   * {@code false} otherwise
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  private boolean contains(String key) {
    if (key == null) throw new IllegalArgumentException("argument to contains() is null");
    Node x = get(root, key, 0);
    if (x == null) return false;
    return x.isString;
  }

  private Node get(Node x, String key, int d) {
    if (x == null) return null;
    if (d == key.length()) return x;
    int c = key.charAt(d) - 'A';
    return get(x.next[c], key, d + 1);
  }

  /**
   * Adds the key to the set if it is not already present.
   *
   * @param key the key to add
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  private void add(String key) {
    if (key == null) throw new IllegalArgumentException("argument to add() is null");
    root = add(root, key, 0);
  }

  private Node add(Node x, String key, int d) {
    if (x == null) x = new Node();
    if (d == key.length()) {
      if (!x.isString) n++;
      x.isString = true;
    } else {
      int c = key.charAt(d) - 'A';
      x.next[c] = add(x.next[c], key, d + 1);
    }
    return x;
  }

  private static class Neighbors {
    private int size = 0;
    private int[][] items = new int[8][2];
  }

  // Initializes the data structure using the given array of strings as the dictionary.
  // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
  public BoggleSolver(String[] dictionary) {

    for (String word : dictionary) {
      add(word);
    }
//    StdOut.println("QUNWET:" + contains("QUNWET"));
  }


  private BoggleBoard board;
  private int rows, cols;
  private Set<String> validWords;

  private boolean[][] onStack;
  private Neighbors[][] adjs;

  // Returns the set of all valid words in the given Boggle board, as an Iterable.
  public Iterable<String> getAllValidWords(BoggleBoard board) {
    this.board = board;
    validWords = new TreeSet<>();
    rows = board.rows();
    cols = board.cols();
    onStack = new boolean[rows][cols];
    adjs = new Neighbors[rows][cols];
    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++)
        adjs[row][col] = neighbors(row, col);

    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++)
        dfs(root, new StringBuilder(), row, col);

    return validWords;
  }

  //深度优先遍历
  private void dfs(Node x, StringBuilder sb, int row, int col) {
    char c = board.getLetter(row, col);
    Node next = x.next[c - 'A'];
//    StdOut.printf("(%d, %d)=%c, level=%d, %s\n", row, col, c, level, next);
//    StdOut.println(sb.toString());
    if (c == 'Q' && next != null) {
      next = next.next['U' - 'A'];
    }
    if (next == null) {
      return;
    }
    sb.append(c);
    if (c == 'Q') {
      sb.append('U');
    }
    if (next.isString && sb.length() >= 3)
      validWords.add(sb.toString());

    onStack[row][col] = true;//函数栈进入
    Neighbors nbs = adjs[row][col];
    for (int i = 0; i < nbs.size; i++) {
      int newRow = nbs.items[i][0], newCol = nbs.items[i][1];
      if (!onStack[newRow][newCol]) {//进入回路(cycle)
        dfs(next, new StringBuilder(sb), newRow, newCol);
      }
    }
    onStack[row][col] = false;//函数栈退出
  }

  private Neighbors neighbors(int row, int col) {
    Neighbors nbs = new Neighbors();
    int i = 0;
    for (int dr = -1; dr <= 1; dr++)
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0)
          continue;
        int newRow = row + dr, newCol = col + dc;
        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols)
          nbs.items[i++] = new int[]{newRow, newCol};
      }
    nbs.size = i;
    return nbs;
  }

//  private Neighbors neighbors2(int row, int col) {
//    Neighbors nbs = new Neighbors();
//    int i = 0;
//    int newRow = row, newCol;
//
//    newCol = col - 1;
//    if (newCol >= 0)
//      nbs.items[i++] = new int[]{newRow, newCol};
//
//    newCol = col + 1;
//    if (newCol < cols)
//      nbs.items[i++] = new int[]{newRow, newCol};
//
//    newRow = row - 1;
//    if (newRow >= 0) {
//      newCol = col;
//      nbs.items[i++] = new int[]{newRow, newCol};
//
//      newCol = col - 1;
//      if (newCol >= 0)
//        nbs.items[i++] = new int[]{newRow, newCol};
//
//      newCol = col + 1;
//      if (newCol < cols)
//        nbs.items[i++] = new int[]{newRow, newCol};
//    }
//
//
//    newRow = row + 1;
//    if (newRow < rows) {
//      newCol = col;
//      nbs.items[i++] = new int[]{newRow, newCol};
//
//      newCol = col - 1;
//      if (newCol >= 0)
//        nbs.items[i++] = new int[]{newRow, newCol};
//
//      newCol = col + 1;
//      if (newCol < cols)
//        nbs.items[i++] = new int[]{newRow, newCol};
//    }
//    nbs.size = i;
//    return nbs;
//  }

  // Returns the score of the given word if it is in the dictionary, zero otherwise.
// (You can assume the word contains only the uppercase letters A through Z.)
  public int scoreOf(String word) {
    if (word == null)
      throw new IllegalArgumentException("the argument to scoreOf() is null\n");

    if (!contains(word)) return 0;
    else if (word.length() < 3) return 0;
    else if (word.length() < 5) return 1;
    else if (word.length() == 5) return 2;
    else if (word.length() == 6) return 3;
    else if (word.length() == 7) return 5;
    else return 11;
  }

  //dictionary-yawl.txt board4x4.txt
  public static void main(String[] args) {
    In in = new In(args[0]);
    String[] dictionary = in.readAllStrings();
    BoggleSolver solver = new BoggleSolver(dictionary);
    BoggleBoard board = new BoggleBoard(args[1]);
    int score = 0;
    int count = 0;
    for (String word : solver.getAllValidWords(board)) {
      StdOut.println(word);
      score += solver.scoreOf(word);
      count++;
    }
    StdOut.println("count=" + count + " ," + "Score = " + score);
  }
}
