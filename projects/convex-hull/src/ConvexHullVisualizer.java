/**
 * % java ConvexHullVisualizer < input100.txt
 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

import java.util.Iterator;

public class ConvexHullVisualizer {
  public static void show(Point2D[] points) {
    GrahamScan graham = new GrahamScan(points);

    // draw the points
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);

    // draw all vertex
    StdDraw.setPenRadius(0.01);
    for (Point2D p : points) {
      p.draw();
    }
    StdDraw.setPenRadius();

    // draw convex hull vertex
    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor(StdDraw.RED);
    for (Point2D p : graham.hull()) {
      p.draw();
      StdDraw.text(p.x(), p.y(), p.x() + "");
//      StdOut.println(p);
    }
    StdDraw.setPenColor();
    StdDraw.setPenRadius();

    // draw convex hull line
    Iterable<Point2D> hull = graham.hull();
    Iterator<Point2D> iterator = hull.iterator();
    Point2D first = null, pre = null, cur = null;
    if (iterator.hasNext()) {
      first = iterator.next();
      pre = first;
    }
    while (iterator.hasNext()) {
      cur = iterator.next();
      pre.drawTo(cur);
      pre = cur;
    }
    if (cur != null)
      cur.drawTo(first);

    StdDraw.show();
  }

  public static void main(String[] args) {
    test2();
  }

  private static void test2() {
    //500, -1000, 1500, -2000, 2500, 3000
//    int[] xArr = new int[]{50, -100, 150, -200, -250, 250, 300};
//    int[] xArr = new int[]{3500, 2000, 4500, 1000, 5500, 6000};
    int[] xArr = new int[]{5500, 4000, 6500, 3000, 7500, 8000};
    Point2D[] points = new Point2D[xArr.length];

    for (int i = 0; i < xArr.length; i++) {
      int x = xArr[i];
      int y = (x - 5000) * (x - 5000) / 1000;
      points[i] = new Point2D(x, y);
    }
    show(points);

  }

  private static void test(String[] args) {
    int n = StdIn.readInt();
    Point2D[] points = new Point2D[n];
    for (int i = 0; i < n; i++) {
      int x = StdIn.readInt();
      int y = StdIn.readInt();
      points[i] = new Point2D(x, y);
    }
    show(points);

  }
}
