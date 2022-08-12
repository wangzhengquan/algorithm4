import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.Iterator;
import java.util.TreeSet;

public class PointSET {
  // construct an empty set of points
  private TreeSet<Point2D> myset;


  public PointSET() {
    myset = new TreeSet<>();
  }

  // is the set empty?
  public boolean isEmpty() {
    return myset.isEmpty();
  }

  // number of points in the set
  public int size() {
    return myset.size();
  }

  // add the point to the set (if it is not already in the set)
  public void insert(Point2D p) {
    if (p == null) throw new IllegalArgumentException("argument to insert() is null");
    myset.add(p);
  }

  // does the set contain point p?
  public boolean contains(Point2D p) {
    if (p == null) throw new IllegalArgumentException("argument to contains() is null");
    return myset.contains(p);
  }

  // draw all points to standard draw
  public void draw() {
    for (Point2D p : myset) {
      p.draw();
    }
  }

  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(RectHV rect) {
    if (rect == null) throw new IllegalArgumentException("argument to range() is null");
    TreeSet<Point2D> set = new TreeSet<Point2D>();
    for (Point2D p : myset) {
      if (rect.contains(p)) {
        set.add(p);
      }
    }
    return set;
  }

  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(Point2D p) {
    if (p == null) throw new IllegalArgumentException("argument to nearest() is null");
    Iterator<Point2D> itr = myset.iterator();
    double minDis = 0;
    Point2D minPoint = null;

    if (itr.hasNext()) {
      minPoint = itr.next();
      minDis = p.distanceSquaredTo(minPoint);
    }

    while (itr.hasNext()) {
      Point2D point = itr.next();
      double dis = p.distanceSquaredTo(point);
      if (dis < minDis) {
        minDis = dis;
        minPoint = point;
      }
    }
    return minPoint;
  }

  // unit testing of the methods (optional)
  public static void main(String[] args) {

  }
}
