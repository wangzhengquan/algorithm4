import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Test if a horizontal line and a vertical line orthogonal intersect.
 */
public class OrthogonalRectIntersection {

  private static class Point {
    /**
     * Compares two points by x-coordinate.
     */
    public static final Comparator<Point> X_ORDER = new XOrder();

    private final double x;     // x-coordinate of this point
    private final double y;     // y-coordinate of this point
    Rect rect = null;

    /**
     * Initializes a new point.
     *
     * @param x the <em>x</em>-coordinate of the point
     * @param y the <em>y</em>-coordinate of the point
     */
    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }

    public Point(double x, double y, Rect rect) {
      this.x = x;
      this.y = y;
      this.rect = rect;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
      StdDraw.setPenRadius(0.01);
      StdDraw.point(x, y);
      StdDraw.setPenRadius();
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
      StdDraw.line(this.x, this.y, that.x, that.y);
    }

    public double x() {
      return x;
    }

    /**
     * Returns the y-coordinate.
     *
     * @return the y-coordinate
     */
    public double y() {
      return y;
    }

    public String toString() {
      return "(" + x + ", " + y + ")";
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() != this.getClass())
        return false;

      Point point = (Point) o;
      return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    // compare points according to their x-coordinate
    private static class XOrder implements Comparator<Point> {
      public int compare(Point p, Point q) {
        if (p.x < q.x) return -1;
        if (p.x > q.x) return +1;
        return 0;
      }
    }
  }


  public static final class Rect {
    private final double xmin, ymin;   // minimum x- and y-coordinates
    private final double xmax, ymax;   // maximum x- and y-coordinates
    Point min;
    Point max;
    Color color = StdDraw.BLACK;
    Collection<Rect> intersectRects = new ArrayList<>();


    public Rect(double xmin, double ymin, double xmax, double ymax) {
      this.xmin = xmin;
      this.ymin = ymin;
      this.xmax = xmax;
      this.ymax = ymax;
      this.min = new Point(xmin, ymin, this);
      this.max = new Point(xmax, ymax, this);
      if (Double.isNaN(xmin) || Double.isNaN(xmax)) {
        throw new IllegalArgumentException("x-coordinate is NaN: " + toString());
      }
      if (Double.isNaN(ymin) || Double.isNaN(ymax)) {
        throw new IllegalArgumentException("y-coordinate is NaN: " + toString());
      }
      if (xmax < xmin) {
        throw new IllegalArgumentException("xmax < xmin: " + toString());
      }
      if (ymax < ymin) {
        throw new IllegalArgumentException("ymax < ymin: " + toString());
      }
    }

    public double xmin() {
      return xmin;
    }


    public double xmax() {
      return xmax;
    }


    public double ymin() {
      return ymin;
    }

    public double ymax() {
      return ymax;
    }

    public double width() {
      return xmax - xmin;
    }

    public double height() {
      return ymax - ymin;
    }

    public boolean intersects(Rect that) {
      return this.xmax >= that.xmin && this.ymax >= that.ymin
          && that.xmax >= this.xmin && that.ymax >= this.ymin;
    }

    public boolean contains(Point p) {
      return (p.x() >= xmin) && (p.x() <= xmax)
          && (p.y() >= ymin) && (p.y() <= ymax);
    }


    public double distanceTo(Point2D p) {
      return Math.sqrt(this.distanceSquaredTo(p));
    }


    public double distanceSquaredTo(Point2D p) {
      double dx = 0.0, dy = 0.0;
      if (p.x() < xmin) dx = p.x() - xmin;
      else if (p.x() > xmax) dx = p.x() - xmax;
      if (p.y() < ymin) dy = p.y() - ymin;
      else if (p.y() > ymax) dy = p.y() - ymax;
      return dx * dx + dy * dy;
    }


    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null) return false;
      if (other.getClass() != this.getClass()) return false;
      Rect that = (Rect) other;
      if (this.xmin != that.xmin) return false;
      if (this.ymin != that.ymin) return false;
      if (this.xmax != that.xmax) return false;
      if (this.ymax != that.ymax) return false;
      return true;
    }


    @Override
    public int hashCode() {
      int hash1 = ((Double) xmin).hashCode();
      int hash2 = ((Double) ymin).hashCode();
      int hash3 = ((Double) xmax).hashCode();
      int hash4 = ((Double) ymax).hashCode();
      return 31 * (31 * (31 * hash1 + hash2) + hash3) + hash4;
    }


    @Override
    public String toString() {
      return "[" + xmin + ", " + ymin + "] x [" + xmax + ", " + ymax + "]";
    }

    /**
     * Draws this rectangle to standard draw.
     */
    public void draw() {
      StdDraw.setPenColor(color);
      StdDraw.line(xmin, ymin, xmax, ymin);
      StdDraw.line(xmax, ymin, xmax, ymax);
      StdDraw.line(xmax, ymax, xmin, ymax);
      StdDraw.line(xmin, ymax, xmin, ymin);
//      StdDraw.text(xmin, ymin, this.toString());
    }


  }


  /**
   * @return intersection lines
   */
  public static Collection<Rect> getIntersectRects(Collection<Rect> rects) {
    List<Point> points = new ArrayList<>();
    for (Rect rect : rects) {
      points.add(rect.min);
//      if (line.p2.x != line.p1.x)
      points.add(rect.max);
    }
    points.sort(Point.X_ORDER);

    List<Rect> intersectRects = new ArrayList<>();

    IntervalRBTree<Double, Rect> tree = new IntervalRBTree<>();
    Rect the;
    for (Point p : points) {
      the = p.rect;
      if (p == the.min) {
        Collection<Rect> theIntersects = tree.intersects(the.min.y, the.max.y);
        if (!theIntersects.isEmpty()) {
          for (Rect r : theIntersects) {
            r.color = StdDraw.RED;
            the.intersectRects.add(r);
          }
          the.color = StdDraw.RED;
          intersectRects.add(the);
        }
        tree.put(the.min.y, the.max.y, the);
      } else if (p == the.max) {
        tree.delete(the.min.y, the.max.y);
      }
    }
    return intersectRects;
  }

  public static void printRects(Collection<Rect> rects) {
    for (Rect r : rects) {
      StdOut.println(r);
    }
  }

  public static void draw(Collection<Rect> lines) {
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    for (Rect l : lines) {
      l.draw();
    }
    StdDraw.show();
  }

  public static void main(String[] args) {
    //intersect-lines.txt
    String filename = args[0];
    In in = new In(filename);
    List<Rect> rects = new ArrayList<>();
    while (!in.isEmpty()) {
      double minx = in.readDouble();
      double miny = in.readDouble();
      double maxx = in.readDouble();
      double maxy = in.readDouble();
      Rect l = new Rect(minx, miny, maxx, maxy);
      rects.add(l);
    }
//    printLine(lines);

    Collection<Rect> intersections = OrthogonalRectIntersection.getIntersectRects(rects);
    for (Rect vl : intersections) {
      StdOut.println(vl + " intersect with:");
      for (Rect hl : vl.intersectRects) {
        StdOut.println("  " + hl);
      }
    }
    draw(rects);
  }
}
