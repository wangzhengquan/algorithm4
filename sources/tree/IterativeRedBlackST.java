import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public class IterativeRedBlackST<Key extends Comparable<Key>, Value> {
  private static final boolean RED = true;
  private static final boolean BLACK = false;

  private int size;
  private Node root;     // root of the BST
  private Node nil;

  // BST helper node data type
  private class Node {
    Key key;
    Value val;
    int level;
    private Node p, left, right;  // links to left and right subtrees
    private boolean color;     // color of parent link
    private int size;          // subtree count

    public Node() {
      this.color = BLACK;
      this.size = 0;
    }

    public Node(Key key, Value val, boolean color, int size) {
      this.key = key;
      this.val = val;
      this.color = color;
//      this.size = size;
//      this.level = level;
    }
  }

  public IterativeRedBlackST() {
    size = 0;
    nil = new Node((Key) "nil", null, BLACK, 0);
    root = nil;
  }

  /***************************************************************************
   *  Ordered symbol table methods.
   ***************************************************************************/

  /**
   * Returns the value associated with the given key.
   *
   * @param key the key
   * @return the value associated with the given key if the key is in the symbol table
   * and {@code null} if the key is not in the symbol table
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public Value get(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to get() is null");
    return getNode(root, key).val;
  }

  private Node getNode(Key key) {
    return getNode(root, key);
  }

  // value associated with the given key in subtree rooted at x; null if no such key
  private Node getNode(Node x, Key key) {
    while (x != nil) {
      int cmp = key.compareTo(x.key);
      if (cmp < 0) x = x.left;
      else if (cmp > 0) x = x.right;
      else return x;
    }
    return null;
  }

  private int size() {
    return size;
  }


  /**
   * Is this symbol table empty?
   *
   * @return {@code true} if this symbol table is empty and {@code false} otherwise
   */
  public boolean isEmpty() {
    return root == nil;
  }

  /**
   * Does this symbol table contain the given key?
   *
   * @param key the key
   * @return {@code true} if this symbol table contains {@code key} and
   * {@code false} otherwise
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public boolean contains(Key key) {
    return get(key) != null;
  }


  public Iterable<Key> keys() {
    List<Key> list = new ArrayList<>();
    keys(root, list);
    return list;
  }

  private void keys(Node x, List<Key> list) {
    if (x == nil) return;
    keys(x.left, list);
    list.add(x.key);
    keys(x.right, list);
  }


  /**
   * Returns all keys in the symbol table in the given range in ascending order,
   * as an {@code Iterable}.
   *
   * @param lo minimum endpoint
   * @param hi maximum endpoint
   * @return all keys in the symbol table between {@code lo}
   * (inclusive) and {@code hi} (inclusive) in ascending order
   * @throws IllegalArgumentException if either {@code lo} or {@code hi}
   *                                  is {@code null}
   */
  public Collection<Key> keys(Key lo, Key hi) {
    if (lo == null) throw new IllegalArgumentException("first argument to keys() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to keys() is null");

    List<Key> queue = new ArrayList<Key>();
    // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
    keys(root, queue, lo, hi);
    return queue;
  }

  // add the keys between lo and hi in the subtree rooted at x
  // to the queue
  private void keys(Node x, List<Key> queue, Key lo, Key hi) {
    if (x == nil) return;
    int cmplo = lo.compareTo(x.key);
    int cmphi = hi.compareTo(x.key);
    if (cmplo < 0) keys(x.left, queue, lo, hi);
    if (cmplo <= 0 && cmphi >= 0) queue.add(x.key);
    if (cmphi > 0) keys(x.right, queue, lo, hi);
  }


  /**
   * Returns the height of the BST (for debugging).
   *
   * @return the height of the BST (a 1-node tree has height 0)
   */
  public int height() {
    return height(root);
  }

  private int height(Node x) {
    if (x == nil) return -1;
    return 1 + Math.max(height(x.left), height(x.right));
  }

  /**
   * Returns the smallest key in the symbol table.
   *
   * @return the smallest key in the symbol table
   * @throws NoSuchElementException if the symbol table is empty
   */
  public Key min() {
    if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
    return min(root).key;
  }

  // the smallest key in subtree rooted at x; null if no such key
  private Node min(Node x) {
    // assert x != null;
    while (x.left != nil)
      x = x.left;
    return x;
  }

  /**
   * Returns the largest key in the symbol table.
   *
   * @return the largest key in the symbol table
   * @throws NoSuchElementException if the symbol table is empty
   */
  public Key max() {
    if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
    return max(root).key;
  }

  // the largest key in the subtree rooted at x; null if no such key
  private Node max(Node x) {
    // assert x != null;
    while (x.right != nil)
      x = x.right;
    return x;
  }

  public void put(Key key, Value val) {
    Node y = nil;
    Node x = root;
    int cmp = 0;
//    int level = 0;
    while (x != nil) {
      y = x;
      cmp = key.compareTo(x.key);
      if (cmp < 0)
        x = x.left;
      else if (cmp > 0)
        x = x.right;
      else {
        x.val = val;
        return;
      }
    }

    Node z = new Node(key, val, RED, 1);
    z.left = nil;
    z.right = nil;
    z.p = y;
    if (y == nil)
      root = z;
    else if (cmp < 0) {
      y.left = z;
    } else {
      y.right = z;
    }
    putFixUp(z);
    size++;
  }

  private void putFixUp(Node z) {
    while (z.p.color == RED)
      if (z.p == z.p.p.left) {
        Node y = z.p.p.right;
        if (y.color == RED) {
          // case 1
          flipColors(z.p.p);
          z = z.p.p;
          continue;
        } else if (z == z.p.right) {
          // case 2
          z = z.p;
          rotateLeft(z);
        }
        // case 3
        rotateRight(z.p.p);
      } else {
        Node y = z.p.p.left;
        if (y.color == RED) {
          // case 1
          flipColors(z.p.p);
          z = z.p.p;
          continue;
        } else if (z == z.p.left) {
          // case 2
          z = z.p;
          rotateRight(z);
        }
        // case 3
        rotateLeft(z.p.p);
      }

    root.color = BLACK;
  }

  /**
   * Removes the specified key and its associated value from this symbol table
   * (if the key is in this symbol table).
   *
   * @param key the key
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public void delete(Key key) {
    Node z = getNode(key);
    if (z == null)
      return;
    Node y = z, x;

    boolean yOrigColor = y.color;
    if (z.left == nil) {
      x = z.right;
      transplant(z, z.right);
    } else if (z.right == nil) {
      x = z.left;
      transplant(z, z.left);
    } else {
      y = min(z.right);
      yOrigColor = y.color;
      x = y.right;
      if (y.p == z)
        //  x.p本来就等于y？ 针对x==nil的情况，deleteFixUp方法需要用到,
        //  前面left或right为nil的情况, x.p是在transplant里设置的。
        x.p = y;
      else {
        transplant(y, y.right);
        y.right = z.right;
        y.right.p = y;
      }
      transplant(z, y);
      y.left = z.left;
      y.left.p = y;
      y.color = z.color;
    }
    if (yOrigColor == BLACK)
      deleteFixUp(x);
    this.size--;
  }

  private void transplant(Node u, Node v) {
    if (u.p == nil)
      root = v;
    else if (u == u.p.left)
      u.p.left = v;
    else
      u.p.right = v;
    v.p = u.p;
  }

  /**
   * x is a double black node,the goal is to remove one black from it.
   * To achieve that goal,we should transmit the black to a red node,
   * then turn the red node to be black
   *
   * @param x
   */
  private void deleteFixUp(Node x) {
    Node w;
    while (x != root && x.color == BLACK)
      if (x == x.p.left) {
        w = x.p.right;
        if (w.color == RED) {
          //case 1: x’s sibling w is red
          rotateLeft(x.p);
          w = x.p.right;
        }
        if (w.left.color == BLACK && w.right.color == BLACK) {
          // case 2: x’s sibling w is black, and both of w’s children are black
          w.color = RED;
          x = x.p;
          continue;
        } else if (w.right.color == BLACK) {
          // case 3: x’s sibling w is black, w’s left child is red, and w’s right child is black
          rotateRight(w);
          w = x.p.right;
        }
        // case 4: x’s sibling w is black, and w’s right child is red
        // next 2 line is to exchange the color of w and w.right
        w.right.color = BLACK;
        w.color = RED;
        rotateLeft(x.p);
        x.p.color = BLACK;
        x = root;

      } else {
        w = x.p.left;
        if (w.color == RED) {
          //case 1
          rotateRight(x.p);
          w = x.p.left;
        }
        if (w.left.color == BLACK && w.right.color == BLACK) {
          // case 2
          w.color = RED;
          x = x.p;
          continue;
        } else if (w.left.color == BLACK) {
          // case 3
          rotateLeft(w);
          w = x.p.left;
        }
        // case 4
        w.left.color = BLACK;
        w.color = RED;

        rotateRight(x.p);
        x.p.color = BLACK;
        x = root;
      }

    x.color = BLACK;
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

  // make a right-leaning link lean to the left
  private Node rotateLeft(Node x) {
//    assert (x != null) && isRed(x.right);
    Node y = x.right;
    x.right = y.left;
    if (y.left != nil)
      y.left.p = x;
    y.p = x.p;
    if (x.p == nil)
      root = y;
    else if (x == x.p.left)
      x.p.left = y;
    else
      x.p.right = y;

    y.left = x;
    x.p = y;

    boolean tmpcolor = y.color;
    y.color = x.color;
    x.color = RED;
    return y;
  }


  private Node rotateRight(Node x) {
//    assert (x != null) && isRed(x.left);
    // assert (h != null) && isRed(h.right) && !isRed(h.left);  // for insertion only
    Node y = x.left;

    x.left = y.right;
    if (y.right != nil)
      y.right.p = x;

    y.p = x.p;
    if (x.p == nil)
      root = y;
    else if (x == x.p.left)
      x.p.left = y;
    else
      x.p.right = y;

    y.right = x;
    x.p = y;

    boolean tmpcolor = y.color;
    y.color = x.color;
    x.color = RED;
    return y;
  }


  /***************************************************************************
   *  Check integrity of red-black tree data structure.
   ***************************************************************************/
  private boolean check() {
    boolean isBST = isBST(),
        isBalanced = isBalanced();
    if (!isBST) StdOut.println("Not in symmetric order");
//    if (!isSizeConsistent()) StdOut.println("Subtree counts not consistent");
//    if (!isRankConsistent()) StdOut.println("Ranks not consistent");
//    if (!is23())             StdOut.println("Not a 2-3 tree");
    if (!isBalanced()) StdOut.println("Not balanced");
    boolean pass = isBST && isBalanced;
    if (!pass)
      throw new RuntimeException("check failed");
    return pass;
  }

  // does this binary tree satisfy symmetric order?
  // Note: this test also ensures that data structure is a binary tree since order is strict
  private boolean isBST() {
    return isBST(root, null, null);
  }

  // is the tree rooted at x a BST with all keys strictly between min and max
  // (if min or max is null, treat as empty constraint)
  // Credit: Bob Dondero's elegant solution
  private boolean isBST(Node x, Key min, Key max) {
    if (x == nil) return true;
    if (min != null && x.key.compareTo(min) <= 0) return false;
    if (max != null && x.key.compareTo(max) >= 0) return false;
    return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
  }


  // do all paths from root to leaf have same number of black edges?
  private boolean isBalanced() {
    int black = 0;     // number of black links on path from root to min
    Node x = root;
    while (x != nil) {
      if (x.color == BLACK) black++;
      x = x.left;
    }
    return isBalanced(root, black);
  }

  // does every path from the root to a leaf have the given number of black links?
  private boolean isBalanced(Node x, int black) {
    if (x == nil) return black == 0;
    if (x.color == BLACK) black--;
    return isBalanced(x.left, black) && isBalanced(x.right, black);
  }


  // Draw in a canvas
  double r = 0.02, rx, ry; // radius
  double u = 0.06; // unit length
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
    setSize();
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
    StdDraw.circle(x, y, r);
    StdDraw.text(x, y, node.key.toString());

    StdDraw.setPenColor(StdDraw.BLACK);
    if (node.left != nil) {
      dx = (size(node.left.right) + 1) * ux;
      targetX = x - dx;
      targetY = y - dy;
      d = Math.sqrt(dx * dx + dy * dy);
      rx = r * dx / d;
      ry = r * dy / d;
      StdDraw.line(x - rx, y - ry, targetX + rx, targetY + ry);
      drawNode(node.left, targetX, targetY);
    }
    if (node.right != nil) {
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


  // return number of key-value pairs in BST rooted at x
  private int size(Node x) {
    if (x == null) return 0;
    else return x.size;
  }

  private void setSize() {
    setSize(root);
  }

  private void setSize(Node h) {
    if (h == nil) return;
    setSize(h.left);
    setSize(h.right);
    h.size = h.left.size + h.right.size + 1;
  }

  private void printTree() {
    StdOut.println("----------Tree Info----------------------");
    if (this.isEmpty()) {
      StdOut.println("blank tree");
      return;
    }
    StdOut.println("size = " + this.size());
    StdOut.println("min  = " + this.min());
    StdOut.println("max  = " + this.max());
    StdOut.println("Detail:");
    for (Key s : this.keys())
      StdOut.println(s + "=" + this.get(s));
  }


  public static void main(String[] args) {

//    String test = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
//    String test = "K N W Q R V T M E A D Z B L F Y G H U C O J P S X I";
//    String[] keys = test.split(" ");
////    shuffle(keys);
//    IterativeRedBlackST<String, Integer> st = testPut(keys);
////    testRandomDel(st);
//    testDesignateDel(st, "A B C D E F G".split(" "));

    int n = 10;
    while (n-- > 0)
      test();
  }

  private static void test() {
    //    String test = "S E A R C H X M P L";
//    String test = "A C E H L M P R S X";
//    String test = "X S R P M L H E C A";
    String test = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
    String[] keys = test.split(" ");
    shuffle(keys);

    IterativeRedBlackST<String, Integer> st = testPut(keys);
    testRandomDel(st);

//    StdDraw.pause(1000);
    st = testPut(keys);
    testOrderedDel(st);

//    StdDraw.pause(1000);
//    st = testPut(keys);
//    testDesignateDel(st, delkeys);
  }

  public static void shuffle(Object[] a) {
    int N = a.length;
    for (int i = 0; i < N; i++) {
      int r = StdRandom.uniform(i + 1);
      exch(a, i, r);
    }
  }

  // exchange a[i] and a[j]
  private static void exch(Object[] a, int i, int j) {
    Object swap = a[i];
    a[i] = a[j];
    a[j] = swap;
  }

  private static void testRandomDel(IterativeRedBlackST<String, Integer> st) {
    List<String> list = new ArrayList<>();
    for (String key : st.keys()) {
      list.add(key);
    }
    int i;
    String key;
    StdOut.println("---testRandomDel---");
    while (!st.isEmpty()) {
//      StdDraw.pause(2000);
      i = StdRandom.uniform(list.size());
      key = list.get(i);
      StdOut.print(" " + key);
      st.delete(key);
      list.remove(i);
      st.drawTree();
      st.check();
//      st.printTree();
    }
    StdOut.println();
  }

  //A X T L Z
  private static void testDesignateDel(IterativeRedBlackST<String, Integer> st, String[] keys) {
    StdOut.println("---testDesignateDel---");
    for (String key : keys) {
//      StdDraw.pause(2000);
      StdOut.println("To delete " + key);
      st.delete(key);
      st.drawTree();
//      st.printTree();
      st.check();
    }
    StdOut.println();
  }

  private static void testOrderedDel(IterativeRedBlackST<String, Integer> st) {
    StdOut.println("---testOrderedDel---");
    String key;
    while (!st.isEmpty()) {
//      StdDraw.pause(2000);
      key = st.min();
      StdOut.print(" " + key);
      st.delete(key);
      st.drawTree();
//      st.printTree();
      st.check();
    }
    StdOut.println();
  }

  private static IterativeRedBlackST<String, Integer> testPut(String[] keys) {
    StdOut.println("---testPut---");
    IterativeRedBlackST<String, Integer> st = new IterativeRedBlackST<String, Integer>();
    for (int i = 0; i < keys.length; i++) {
      StdOut.print(" " + keys[i]);
      st.put(keys[i], i);
      st.check();
    }
    st.drawTree();
    StdOut.println();
//    st.printTree();
    return st;
  }


}
