import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
  private List<LineSegment> seglist = new ArrayList<LineSegment>();

  /**
   * finds all line segments containing 4 points
   *
   * @param points
   */
  public BruteCollinearPoints(Point[] points) {
    if (points == null) {
      throw new IllegalArgumentException("argument to constructor is null");
    }
    for (Point p : points) {
      if (p == null) {
        throw new IllegalArgumentException("one point is null");
      }
    }

    int len = points.length;
    points = Arrays.copyOf(points, len);
    Arrays.sort(points);
    if (haveRepeatPoints(points)) {
      throw new IllegalArgumentException("repeated point");
    }
    if (len < 4) return;
//    printPoints(points);
    combinationPoints(points);

  }

  private void combinationPoints(Point[] points) {
    int i, j, k, l;
    int len = points.length;
    for (i = 0; i < len; i++)
      for (j = i + 1; j < len; j++)
        for (k = j + 1; k < len; k++)
          for (l = k + 1; l < len; l++) {
            double s1 = points[i].slopeTo(points[j]);
            double s2 = points[i].slopeTo(points[k]);
            double s3 = points[i].slopeTo(points[l]);
            if (s1 == s2 && s1 == s3) {
              seglist.add(new LineSegment(points[i], points[l]));
            }
          }
  }

  private boolean haveRepeatPoints(Point[] points) {
    Point prePoint, curPoint;
    prePoint = points[0];
    for (int i = 1; i < points.length; i++) {
      curPoint = points[i];
      if (curPoint.compareTo(prePoint) == 0)
        return true;
      prePoint = curPoint;
    }
    return false;
  }

  /**
   * @return the number of line segments
   */
  public int numberOfSegments() {
    return seglist.size();
  }

  /**
   * @return the line segments
   */
  public LineSegment[] segments() {
    LineSegment[] arr = new LineSegment[seglist.size()];
    return seglist.toArray(arr);
  }

  public static void main(String[] args) {

    // read the n points from a file
    if (args.length == 0) {
      StdOut.println("Client inputfile");
      return;
    }
    In in = new In(args[0]);
    int n = in.readInt();
    Point[] points = new Point[n];
    for (int i = 0; i < n; i++) {
      int x = in.readInt();
      int y = in.readInt();
      points[i] = new Point(x, y);
    }

    // draw the points
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    for (Point p : points) {
      p.draw();
    }
    StdDraw.show();

    // print and draw the line segments
    BruteCollinearPoints collinear = new BruteCollinearPoints(points);
    StdOut.printf("numberOfSegments:%d\n", collinear.numberOfSegments());
    for (LineSegment segment : collinear.segments()) {
      StdOut.println(segment);
      segment.draw();
    }
    StdDraw.show();
  }
}
