import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KdTree {
  // BST helper node data type
  private static class Node {
    RectHV rect;
    Point2D point;
    int level;
    Node p, left, right;  // links to left and right subtrees
//    private int size;          // subtree count

    public Node(Point2D point, RectHV rect, int level) {
      this.point = point;
      this.rect = rect;
//      this.size = size;
      this.level = level;
    }

    public boolean verticalSplit() {
      return level % 2 == 0;
    }

    public RectHV bottomRect() {
      return new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), point.y());
    }

    public RectHV topRect() {
      return new RectHV(rect.xmin(), point.y(), rect.xmax(), rect.ymax());
    }

    public RectHV leftRect() {
      return new RectHV(rect.xmin(), rect.ymin(), point.x(), rect.ymax());
    }

    public RectHV rightRect() {
      return new RectHV(point.x(), rect.ymin(), rect.xmax(), rect.ymax());
    }


  }

  private int size;

  private Node root;     // root of the BST

  public KdTree() {
    size = 0;
  }

  // add the point to the set (if it is not already in the set)
  public void insert(Point2D p) {
    if (p == null) throw new IllegalArgumentException("argument to insert() is null");
    this.put(p);
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return root == null;
  }

  public boolean contains(Point2D key) {
    if (key == null) throw new IllegalArgumentException("argument to contains() is null");
    return getNode(key) != null;
  }

  // draw all points to standard draw
  public void draw() {
    draw(root);
  }

  private void draw(Node node) {
    if (node == null)
      return;
    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor(StdDraw.BLACK);
    node.point.draw();

    StdDraw.setPenRadius();
    if (node.verticalSplit()) {
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.line(node.point.x(), node.rect.ymin(), node.point.x(), node.rect.ymax());
    } else {
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.line(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.point.y());
    }
    draw(node.left);
    draw(node.right);

  }

  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(RectHV rect) {
    if (rect == null) throw new IllegalArgumentException("argument to range() is null");
    List<Point2D> list = new ArrayList<>();
    range(root, rect, list);
    return list;
  }

  private void range(Node x, RectHV rect, List<Point2D> list) {
    if (x == null)
      return;
    if (!x.rect.intersects(rect))
      return;

    if (rect.contains(x.point))
      list.add(x.point);

    range(x.left, rect, list);
    range(x.right, rect, list);
  }

  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(Point2D p) {
    if (p == null) throw new IllegalArgumentException("argument to nearest() is null");
    if (isEmpty())
      return null;
    return nearest(root, p, root.point);
  }

  private Point2D nearest(Node x, Point2D p, Point2D mp) {
    if (x == null)
      return mp;
    if (x.rect.distanceSquaredTo(p) >= mp.distanceSquaredTo(p)) {
      return mp;
    }

    if (x.point.distanceSquaredTo(p) < mp.distanceSquaredTo(p)) {
      mp = x.point;
    }
    int cmp = getComparator(x.level).compare(p, x.point);
    if (cmp < 0) {
      // which branch will be most probably find the nearest point
      mp = nearest(x.left, p, mp);
      mp = nearest(x.right, p, mp);
    } else {
      mp = nearest(x.right, p, mp);
      mp = nearest(x.left, p, mp);
    }

    return mp;
  }

  private boolean verticalSplit(int level) {
    return level % 2 == 0;
  }

  private Comparator<Point2D> getComparator(int level) {
    if (verticalSplit(level)) {
      return Point2D.X_ORDER;
    } else {
      return Point2D.Y_ORDER;
    }
  }


  private void put(Point2D point) {
    Node y = null;
    Node x = root;
    int cmp = 0;
    int level = 0;
    while (x != null) {
      y = x;
      cmp = getComparator(level).compare(point, x.point);
      if (cmp == 0)
        cmp = getComparator(level + 1).compare(point, x.point);
      if (cmp < 0)
        x = x.left;
      else if (cmp > 0)
        x = x.right;
      else {
        return;
      }

      level++;
    }

    Node z;

    if (y == null) {
      z = new Node(point, new RectHV(0.0, 0.0, 1.0, 1.0), level);
      root = z;
    } else if (cmp < 0) {
      if (y.verticalSplit()) {
        z = new Node(point, y.leftRect(), level);
      } else {
        z = new Node(point, y.bottomRect(), level);
      }
      y.left = z;

    } else {
      if (y.verticalSplit()) {
        z = new Node(point, y.rightRect(), level);
      } else {
        z = new Node(point, y.topRect(), level);
      }
      y.right = z;
    }
    z.p = y;
    size++;
  }


  /***************************************************************************
   *  Ordered symbol table methods.
   ***************************************************************************/


  private Node getNode(Point2D key) {
    if (key == null) throw new IllegalArgumentException("argument to get() is null");
    return getNode(root, key);
  }

  // value associated with the given key in subtree rooted at x; null if no such key
  private Node getNode(Node x, Point2D key) {
    int level = 0;
    while (x != null) {
      int cmp = this.getComparator(level).compare(key, x.point);
      if (cmp < 0)
        x = x.left;
      else if (cmp > 0)
        x = x.right;
      else {
        cmp = this.getComparator(level + 1).compare(key, x.point);
        if (cmp < 0)
          x = x.left;
        else if (cmp > 0)
          x = x.right;
        else
          return x;
      }
      level++;
    }
    return null;
  }


  private Iterable<Node> nodes() {
    return nodes(root);
  }

  private Iterable<Node> nodes(Node x) {
    List<Node> list = new ArrayList<>();
    nodes(x, list);
    return list;
  }

  private void nodes(Node x, List<Node> list) {
    if (x == null) return;
    nodes(x.left, list);
    list.add(x);
    nodes(x.right, list);

  }


  public static void main(String[] args) {
    String filename = args[0];
    In in = new In(filename);
    KdTree kdtree = new KdTree();
    while (!in.isEmpty()) {
      double x = in.readDouble();
      double y = in.readDouble();
      Point2D p = new Point2D(x, y);
      kdtree.insert(p);
    }
    StdOut.println("size=" + kdtree.size());
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);

    kdtree.draw();
    StdDraw.show();

  }

}

