import edu.princeton.cs.algs4.In;
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
public class OrthogonalLineIntersection {

  private static class Point {
    /**
     * Compares two points by x-coordinate.
     */
    public static final Comparator<Point> X_ORDER = new XOrder();

    private final double x;     // x-coordinate of this point
    private final double y;     // y-coordinate of this point
    LineSegment line = null;

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

    public Point(double x, double y, LineSegment line) {
      this.x = x;
      this.y = y;
      this.line = line;
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


    // compare points according to their x-coordinate
    private static class XOrder implements Comparator<Point> {
      public int compare(Point p, Point q) {
        if (p.x < q.x) return -1;
        if (p.x > q.x) return +1;
        return 0;
      }
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

    
  }

  private static class LineSegment {
    //    double x1, y1, x2, y2;
    Point p1, p2;
    Color color = StdDraw.BLACK;
    List<LineSegment> intersectLines;

    public LineSegment(double x1, double y1, double x2, double y2) {
      p1 = new Point(x1, y1, this);
      p2 = new Point(x2, y2, this);
      intersectLines = new ArrayList<>();
    }

    boolean vertical() {
      return this.p1.x == this.p2.x;
    }

    boolean horizontal() {
      return this.p1.y == this.p2.y;
    }

    /**
     * Draws this line segment to standard draw.
     */
    public void draw() {
      StdDraw.setPenColor(color);
//      StdDraw.textLeft(p1.x, p1.y, this.toString());
      p1.drawTo(p2);
    }

    /**
     * Returns a string representation of this line segment
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this line segment
     */
    public String toString() {
      return p1 + " -> " + p2;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (o == null) return false;
      if (o.getClass() != this.getClass())
        return false;

      LineSegment line = (LineSegment) o;
      return line.p1.equals(p1) && line.p2.equals(p2);
    }
  }


  /**
   * @param lines
   * @return intersection lines
   */
  public static Collection<LineSegment> getIntersectLines(Collection<LineSegment> lines) {
    List<Point> points = new ArrayList<>();
    for (LineSegment line : lines) {
      points.add(line.p1);
      //vertical line just add  one time
      if (line.p2.x != line.p1.x)
        points.add(line.p2);
    }
    points.sort(Point.X_ORDER);

    List<LineSegment> intersectLines = new ArrayList<>();

    IterativeRBTree<Double, LineSegment> tree = new IterativeRBTree<>();
    LineSegment line;
    for (Point p : points) {
      line = p.line;
      if (line.horizontal()) {
        if (p == line.p1) {
          tree.put(p.y, line);
        } else if (p == line.p2) {
          tree.delete(p.y);
        }
      } else {
        Iterable<Double> itr = tree.keys(line.p1.y, line.p2.y);
        int count = 0;
        for (double y : itr) {
          LineSegment intersectHLine = tree.get(y);
          intersectHLine.color = StdDraw.RED;
          line.intersectLines.add(intersectHLine);
          count++;
        }
        if (count > 0) {
          line.color = StdDraw.RED;
          intersectLines.add(line);
        }
      }
    }
    return intersectLines;
  }

  public static void printLine(Collection<LineSegment> lines) {
    for (LineSegment l : lines) {
      StdOut.println(l);
    }
  }

  public static void draw(Collection<LineSegment> lines) {
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    for (LineSegment l : lines) {
      l.draw();
    }
    StdDraw.show();
  }

  public static void main(String[] args) {
    //intersect-lines.txt
    String filename = args[0];
    In in = new In(filename);
    List<LineSegment> lines = new ArrayList<>();
    while (!in.isEmpty()) {
      double x1 = in.readDouble();
      double y1 = in.readDouble();
      double x2 = in.readDouble();
      double y2 = in.readDouble();
      LineSegment l = new LineSegment(x1, y1, x2, y2);
      lines.add(l);
    }
//    printLine(lines);

    Collection<LineSegment> intersectLines = OrthogonalLineIntersection.getIntersectLines(lines);
    for (LineSegment vl : intersectLines) {
      StdOut.println(vl + " intersect with:");
      for (LineSegment hl : vl.intersectLines) {
        StdOut.println("  " + hl);
      }
    }
    draw(lines);
  }
}
