import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;


public class IntervalRBTree<Key extends Comparable<Key>, Value> {

  private static final boolean RED = true;
  private static final boolean BLACK = false;

  private Node root;     // root of the BST

  // BST helper node data type
  private class Node {
    private Key min;
    private Key max;
    Interval interval;
    private Value val;         // associated data
    private Node left, right;  // links to left and right subtrees
    private boolean color;     // color of parent link
    private int size;          // subtree count

    public Node(Key min, Key max, Value val, boolean color, int size) {
      this(new Interval(min, max), val, color, size);
    }

    public Node(Interval interval, Value val, boolean color, int size) {
      this.interval = interval;
      this.min = interval.min;
      this.max = interval.max;
      this.val = val;
      this.color = color;
      this.size = size;
    }
  }

  private class Interval implements Comparable<Interval> {
    private final Key min;
    private final Key max;

    /**
     * Initializes a closed interval [min, max].
     *
     * @param min the smaller endpoint
     * @param max the larger endpoint
     * @throws IllegalArgumentException if the min endpoint is greater than the max endpoint
     * @throws IllegalArgumentException if either {@code min} or {@code max}
     *                                  is {@code Double.NaN}, {@code Double.POSITIVE_INFINITY} or
     *                                  {@code Double.NEGATIVE_INFINITY}
     */
    public Interval(Key min, Key max) {
      if (min.compareTo(max) <= 0) {
        this.min = min;
        this.max = max;
      } else throw new IllegalArgumentException("Illegal interval");
    }

    public Key min() {
      return min;
    }

    public Key max() {
      return max;
    }

    /**
     * Returns true if this interval intersects the specified interval.
     *
     * @param that the other interval
     * @return {@code true} if this interval intersects the argument interval;
     * {@code false} otherwise
     */
    public boolean intersects(Interval that) {
      if (this.max.compareTo(that.min) < 0) return false;
      if (that.max.compareTo(this.min) < 0) return false;
      return true;
    }

    public boolean intersects(Key lo, Key hi) {
      return this.intersects(new Interval(lo, hi));
    }

    /**
     * Returns true if this interval contains the specified value.
     *
     * @param x the value
     * @return {@code true} if this interval contains the value {@code x};
     * {@code false} otherwise
     */
    public boolean contains(Key x) {
      return (min.compareTo(x) <= 0) && (x.compareTo(max) <= 0);
    }

    public String toString() {
      return "[" + min + ", " + max + "]";
    }

    /**
     * Compares this transaction to the specified object.
     *
     * @param other the other interval
     * @return {@code true} if this interval equals the other interval;
     * {@code false} otherwise
     */
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null) return false;
      if (other.getClass() != this.getClass()) return false;
      Interval that = (Interval) other;
      return this.min.compareTo(that.min) == 0 && this.max.compareTo(that.max) == 0;
    }

    public int compareTo(Interval that) {
      if (this.min.compareTo(that.min) == 0 && this.max.compareTo(that.max) == 0) {
        return 0;
      }
      boolean isLess = this.min.compareTo(that.min) < 0
          || (this.min.compareTo(that.min) == 0 && this.max.compareTo(that.max) < 0);
      return isLess ? -1 : 1;
    }
  }

  /**
   * Initializes an empty symbol table.
   */
  public IntervalRBTree() {
  }

  /***************************************************************************
   *  Node helper methods.
   ***************************************************************************/
  // is node x red; false if x is null ?
  private boolean isRed(Node x) {
    if (x == null) return false;
    return x.color == RED;
  }

  // number of node in subtree rooted at x; 0 if x is null
  private int size(Node x) {
    if (x == null) return 0;
    return x.size;
  }


  /**
   * Returns the number of key-value pairs in this symbol table.
   *
   * @return the number of key-value pairs in this symbol table
   */
  public int size() {
    return size(root);
  }

  /**
   * Is this symbol table empty?
   *
   * @return {@code true} if this symbol table is empty and {@code false} otherwise
   */
  public boolean isEmpty() {
    return root == null;
  }


  /***************************************************************************
   *  Standard BST search.
   ***************************************************************************/

  /**
   * Returns the value associated with the given key.
   */
  public Value get(Key lo, Key hi) {
    if (lo == null || hi == null) throw new IllegalArgumentException("argument to get() is null");
    return get(root, new Interval(lo, hi));
  }

  // value associated with the given key in subtree rooted at x; null if no such key
  private Value get(Node x, Interval key) {
    while (x != null) {
      int cmp = key.compareTo(x.interval);
//      StdOut.printf(key + "-" + x.interval + "=" + cmp);
      if (cmp < 0) x = x.left;
      else if (cmp > 0) x = x.right;
      else return x.val;
    }
    return null;
  }

  public Collection<Value> intersects(Key lo, Key hi) {
    List<Value> list = new ArrayList<>();
    int count = intersects(root, list, lo, hi, 0);
    StdOut.printf("O(%d)=%d\n", this.size(), count);
    return list;
  }

  /**
   * all intervals that intersect the given interval
   *
   * @param x
   * @param list
   * @param lo
   * @param hi
   */
  private int intersects(Node x, List<Value> list, Key lo, Key hi, int n) {
    if (x == null) return 0;
    if (x.interval.intersects(lo, hi)) {
      list.add(x.val);
    }
    if (x.left != null && lo.compareTo(x.left.max) < 0 && hi.compareTo(x.left.min) > 0)
      n = intersects(x.left, list, lo, hi, n);
    if (x.right != null && lo.compareTo(x.right.max) < 0 && hi.compareTo(x.right.min) > 0)
      n = intersects(x.right, list, lo, hi, n);

    return ++n;

  }

  /**
   * Does this symbol table contain the given key?
   */
  public boolean contains(Key lo, Key hi) {
    return get(lo, hi) != null;
  }

  /***************************************************************************
   *  Red-black tree insertion.
   ***************************************************************************/

  public void put(Key lo, Key hi, Value val) {
    if (lo == null || hi == null) throw new IllegalArgumentException("first argument to put() is null");
    if (val == null) {
      delete(lo, hi);
      return;
    }

    root = put(root, new Interval(lo, hi), val);
    root.color = BLACK;
    // assert check();
  }


  // insert the key-value pair in the subtree rooted at h
  // insert the key-value pair in the subtree rooted at h
  private Node put(Node h, Interval key, Value val) {
    if (h == null) return new Node(key, val, RED, 1);

    int cmp = key.compareTo(h.interval);
    if (cmp < 0) h.left = put(h.left, key, val);
    else if (cmp > 0) h.right = put(h.right, key, val);
    else h.val = val;

    // fix-up any right-leaning links

    if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
    if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
    if (isRed(h.left) && isRed(h.right)) flipColors(h);
    h.size = size(h.left) + size(h.right) + 1;
    h.min = minOfSubTree(h);
    h.max = maxOfSubTree(h);

    return h;
  }

  private Key minOfSubTree(Node x) {
    Key i, j, k;
    i = j = k = x.interval.min;
    if (x.left != null) {
      j = x.left.min;
    }
    if (x.right != null) {
      k = x.right.min;
    }

    return i.compareTo(j) < 0 ? (i.compareTo(k) < 0 ? i : k) : (j.compareTo(k) < 0 ? j : k);
  }

  private Key maxOfSubTree(Node x) {
    Key i, j, k;
    i = j = k = x.interval.max;
    if (x.left != null) {
      j = x.left.max;
    }
    if (x.right != null) {
      k = x.right.max;
    }

    Key max = i.compareTo(j) > 0 ? (i.compareTo(k) > 0 ? i : k) : (j.compareTo(k) > 0 ? j : k);

    return max;

  }


  /***************************************************************************
   *  Red-black tree deletion.
   ***************************************************************************/

  /**
   * Removes the smallest key and associated value from the symbol table.
   *
   * @throws NoSuchElementException if the symbol table is empty
   */
  public void deleteMin() {
    if (isEmpty()) throw new NoSuchElementException("BST underflow");

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = deleteMin(root);
    if (!isEmpty()) root.color = BLACK;
    // assert check();
  }

  // delete the key-value pair with the minimum key rooted at h
  private Node deleteMin(Node h) {
    if (h.left == null)
      return null;

    if (!isRed(h.left) && !isRed(h.left.left))
      h = moveRedLeft(h);

    h.left = deleteMin(h.left);
    return balance(h);
  }


  /**
   * Removes the largest key and associated value from the symbol table.
   *
   * @throws NoSuchElementException if the symbol table is empty
   */
  public void deleteMax() {
    if (isEmpty()) throw new NoSuchElementException("BST underflow");

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = deleteMax(root);
    if (!isEmpty()) root.color = BLACK;
    // assert check();
  }

  // delete the key-value pair with the maximum key rooted at h
  private Node deleteMax(Node h) {
    if (isRed(h.left))
      h = rotateRight(h);

    if (h.right == null)
      return null;

    if (!isRed(h.right) && !isRed(h.right.left))
      h = moveRedRight(h);

    h.right = deleteMax(h.right);

    return balance(h);
  }

  /**
   * Removes the specified key and its associated value from this symbol table
   * (if the key is in this symbol table).
   */
  public void delete(Key lo, Key hi) {
    if (lo == null || hi == null) throw new IllegalArgumentException("argument to delete() is null");
    if (!contains(lo, hi)) return;

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = delete(root, new Interval(lo, hi));
    if (!isEmpty()) root.color = BLACK;
    // assert check();
  }

  // delete the key-value pair with the given key rooted at h
  private Node delete(Node h, Interval key) {
    // assert get(h, key) != null;

    if (key.compareTo(h.interval) < 0) {
      if (!isRed(h.left) && !isRed(h.left.left))
        h = moveRedLeft(h);
      h.left = delete(h.left, key);
    } else {
      if (isRed(h.left))
        h = rotateRight(h);
      if (key.compareTo(h.interval) == 0 && (h.right == null))
        return null;
      if (!isRed(h.right) && !isRed(h.right.left))
        h = moveRedRight(h);
      if (key.compareTo(h.interval) == 0) {
        Node x = min(h.right);
        h.interval = x.interval;
        h.val = x.val;
        // h.val = get(h.right, min(h.right).key);
        // h.key = min(h.right).key;
        h.right = deleteMin(h.right);
      } else h.right = delete(h.right, key);
    }
    return balance(h);
  }

  /***************************************************************************
   *  Red-black tree helper functions.
   ***************************************************************************/

  // make a left-leaning link lean to the right
  private Node rotateRight(Node h) {
    assert (h != null) && isRed(h.left);
    // assert (h != null) && isRed(h.left) &&  !isRed(h.right);  // for insertion only
    Node x = h.left;
    h.left = x.right;
    x.right = h;
    x.color = h.color;
    h.color = RED;
    x.size = h.size;
    h.size = size(h.left) + size(h.right) + 1;
    h.min = minOfSubTree(h);
    h.max = maxOfSubTree(h);
    x.min = minOfSubTree(x);
    x.max = maxOfSubTree(x);
    return x;
  }

  // make a right-leaning link lean to the left
  private Node rotateLeft(Node h) {
    assert (h != null) && isRed(h.right);
    // assert (h != null) && isRed(h.right) && !isRed(h.left);  // for insertion only
    Node x = h.right;
    h.right = x.left;
    x.left = h;
    x.color = h.color;
    h.color = RED;
    x.size = h.size;
    h.size = size(h.left) + size(h.right) + 1;
    h.min = minOfSubTree(h);
    h.max = maxOfSubTree(h);
    x.min = minOfSubTree(x);
    x.max = maxOfSubTree(x);
    return x;
  }

  // flip the colors of a node and its two children
  private void flipColors(Node h) {
    // h must have opposite color of its two children
    // assert (h != null) && (h.left != null) && (h.right != null);
    // assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
    //    || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
    h.color = !h.color;
    h.left.color = !h.left.color;
    h.right.color = !h.right.color;
  }

  // Assuming that h is red and both h.left and h.left.left
  // are black, make h.left or one of its children red.
  private Node moveRedLeft(Node h) {
    // assert (h != null);
    // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

    flipColors(h);
    if (isRed(h.right.left)) {
      h.right = rotateRight(h.right);
      h = rotateLeft(h);
      flipColors(h);
    }
    return h;
  }

  // Assuming that h is red and both h.right and h.right.left
  // are black, make h.right or one of its children red.
  private Node moveRedRight(Node h) {
    // assert (h != null);
    // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
    flipColors(h);
    if (isRed(h.left.left)) {
      h = rotateRight(h);
      flipColors(h);
    }
    return h;
  }

  // restore red-black tree invariant
  private Node balance(Node h) {
    // assert (h != null);

    if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
    if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
    if (isRed(h.left) && isRed(h.right)) flipColors(h);

    h.size = size(h.left) + size(h.right) + 1;
    h.min = minOfSubTree(h);
    h.max = maxOfSubTree(h);
    return h;
  }


  /***************************************************************************
   *  Utility functions.
   ***************************************************************************/

  /**
   * Returns the height of the BST (for debugging).
   *
   * @return the height of the BST (a 1-node tree has height 0)
   */
  public int height() {
    return height(root);
  }

  private int height(Node x) {
    if (x == null) return -1;
    return 1 + Math.max(height(x.left), height(x.right));
  }

  /***************************************************************************
   *  Ordered symbol table methods.
   ***************************************************************************/

  /**
   * Returns the smallest key in the symbol table.
   *
   * @return the smallest key in the symbol table
   * @throws NoSuchElementException if the symbol table is empty
   */
  private Interval min() {
    if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
    return min(root).interval;
  }

  // the smallest key in subtree rooted at x; null if no such key
  private Node min(Node x) {
    // assert x != null;
    if (x.left == null) return x;
    else return min(x.left);
  }

  /**
   * Returns the largest key in the symbol table.
   *
   * @return the largest key in the symbol table
   * @throws NoSuchElementException if the symbol table is empty
   */
  private Interval max() {
    if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
    return max(root).interval;
  }

  // the largest key in the subtree rooted at x; null if no such key
  private Node max(Node x) {
    // assert x != null;
    if (x.right == null) return x;
    else return max(x.right);
  }


  /***************************************************************************
   *  Check integrity of red-black tree data structure.
   ***************************************************************************/
  private boolean check() {
    if (!isBST()) StdOut.println("Not in symmetric order");
    if (!isSizeConsistent()) StdOut.println("Subtree counts not consistent");
    if (!isMaxMinConsistent()) StdOut.println("Subtree max or min not consistent");
    if (!is23()) StdOut.println("Not a 2-3 tree");
    if (!isBalanced()) StdOut.println("Not balanced");
    return isBST() && isSizeConsistent() && is23() && isBalanced();
  }

  // does this binary tree satisfy symmetric order?
  // Note: this test also ensures that data structure is a binary tree since order is strict
  private boolean isBST() {
    return isBST(root, null, null);
  }

  // is the tree rooted at x a BST with all keys strictly between min and max
  // (if min or max is null, treat as empty constraint)
  // Credit: Bob Dondero's elegant solution
  private boolean isBST(Node x, Interval min, Interval max) {
    if (x == null) return true;
    if (min != null && x.interval.compareTo(min) <= 0) return false;
    if (max != null && x.interval.compareTo(max) >= 0) return false;
    return isBST(x.left, min, x.interval) && isBST(x.right, x.interval, max);
  }

  // are the size fields correct?
  private boolean isSizeConsistent() {
    return isSizeConsistent(root);
  }

  private boolean isSizeConsistent(Node x) {
    if (x == null) return true;
    if (x.size != size(x.left) + size(x.right) + 1) return false;
    return isSizeConsistent(x.left) && isSizeConsistent(x.right);
  }

  private boolean isMaxMinConsistent() {
    return isMaxMinConsistent(root);
  }

  private boolean isMaxMinConsistent(Node x) {
    if (x == null) return true;
    if (x.max != maxOfSubTree(x) || x.min != minOfSubTree(x)) return false;
    return isMaxMinConsistent(x.left) && isMaxMinConsistent(x.right);
  }


  // Does the tree have no red right links, and at most one (left)
  // red links in a row on any path?
  private boolean is23() {
    return is23(root);
  }

  private boolean is23(Node x) {
    if (x == null) return true;
    if (isRed(x.right)) return false;
    if (x != root && isRed(x) && isRed(x.left))
      return false;
    return is23(x.left) && is23(x.right);
  }

  // do all paths from root to leaf have same number of black edges?
  private boolean isBalanced() {
    int black = 0;     // number of black links on path from root to min
    Node x = root;
    while (x != null) {
      if (!isRed(x)) black++;
      x = x.left;
    }
    return isBalanced(root, black);
  }

  // does every path from the root to a leaf have the given number of black links?
  private boolean isBalanced(Node x, int black) {
    if (x == null) return black == 0;
    if (!isRed(x)) black--;
    return isBalanced(x.left, black) && isBalanced(x.right, black);
  }


  // Draw in a canvas
  double r = 0.03, rx, ry; // radius
  double u = 0.2; // unit length
  double ux = u * Math.cos(Math.PI / 3);
  double uy = u * Math.sin(Math.PI / 3);

  double targetX, targetY;
  double d, dx, dy = uy; // distance

  public void drawTree() {
//    StdDraw.setCanvasSize(2000, 1000);
    StdDraw.enableDoubleBuffering();
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
//    StdDraw.setPenRadius(0.01);

    if (this.isEmpty()) {
      StdDraw.show();
      return;
    }
    drawNode(root, .5 - r, 1 - r);
    StdDraw.show();
  }

  public void drawNode(Node node, double x, double y) {
    if (node == null) return;
//    StdOut.printf("key=%s, dx=%f, dy=%f\n", node.key,  dx, dy);
    if (node.color == RED) {
      StdDraw.setPenColor(StdDraw.RED);
    } else {
      StdDraw.setPenColor(StdDraw.BLACK);
    }
//    StdDraw.circle(x, y, r);
    StdDraw.text(x, y, node.interval.toString());
    StdDraw.text(x, y - r, node.min + "," + node.max);

    StdDraw.setPenColor(StdDraw.BLACK);
    if (node.left != null) {
      dx = (size(node.left.right) + 1) * ux;
      targetX = x - dx;
      targetY = y - dy;
      d = Math.sqrt(dx * dx + dy * dy);
      rx = r * dx / d;
      ry = r * dy / d;
      StdDraw.line(x - rx, y - ry, targetX + rx, targetY + ry);
      drawNode(node.left, targetX, targetY);
    }
    if (node.right != null) {
      dx = (size(node.right.left) + 1) * ux;
      targetX = x + dx;
      targetY = y - dy;
      d = Math.sqrt(dx * dx + dy * dy);
      rx = r * dx / d;
      ry = r * dy / d;
      StdDraw.line(x + rx, y - ry, targetX - rx, targetY + ry);
      drawNode(node.right, targetX, targetY);
    }
  }


  public static void main(String[] args) {
    String filename = args[0];
    In in = new In(filename);
    IntervalRBTree<Double, Double[]> tree = new IntervalRBTree<>();
    while (!in.isEmpty()) {
      double x = in.readDouble();
      double y = in.readDouble();
      tree.put(x, y, new Double[]{x, y});
    }
    Collection<Double[]> list = tree.intersects(21.0, 23.0);
    if (!list.isEmpty()) {
      StdOut.println("intersect with:");
      for (Double[] sec : list) {
        StdOut.printf("[%f %f]\n", sec[0], sec[1]);
      }
    } else {
      StdOut.println("no one intersect with  interval (21.0, 23.0)");
    }
    tree.drawTree();
    tree.check();

    Double[] sec = tree.get(17.0, 19.0);
    if (sec != null) {
      StdOut.printf("find : [%f %f]\n", sec[0], sec[1]);
    } else {
      StdOut.println("no one find");
    }

    StdDraw.pause(2000);
//    tree.delete(21.0, 24.0);
    tree.delete(4.0, 8.0);
    tree.drawTree();
    tree.check();

  }


}
