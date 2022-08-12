/**
 * % java FastCollinearPoints  input8.txt
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastCollinearPoints {
  private List<LineSegment> seglist = new ArrayList<LineSegment>();

  // finds all line segments containing 4 or more points
  public FastCollinearPoints(Point[] points) {
    if (points == null) {
      throw new IllegalArgumentException("argument to constructor is null");
    }
    for (Point p : points) {
      if (p == null) {
        throw new IllegalArgumentException("one point is null");
      }
    }
    int n = points.length;
    points = Arrays.copyOf(points, n);
    Arrays.sort(points);
    if (haveRepeatPoints(points)) {
      throw new IllegalArgumentException("repeated point");
    }

    if (n < 4) return;

    Point[] auxArr = new Point[n];
    System.arraycopy(points, 0, auxArr, 0, n);
    for (int i = 0; i < n; i++) {
      Point p = points[i];
      Arrays.sort(auxArr, p.slopeOrder());
//      printArrSlope(p, auxArr);
      countCollineSeg(p, auxArr);
    }
  }

  private void countCollineSeg(Point p, Point[] arr) {
    Point prePoint, curPoint;
    double preSlope, curSlope;
    boolean isPMin = true;
    assert (p == arr[0]);
//    StdOut.println(p == arr[0]);
    prePoint = arr[1];
    preSlope = p.slopeTo(prePoint);
    int n = arr.length;
    int count = 1;
    Point segMaxPoint = prePoint;
    for (int i = 2; i < n; i++) {
      curPoint = arr[i];
      curSlope = p.slopeTo(curPoint);
      if (curSlope == preSlope) {
        //  For example, if 5 points appear on a line segment in the order p→q→r→s→t,
        //  then do not include the subsegments p→s or q→t.
        if (isPMin && (p.compareTo(prePoint) > 0 || p.compareTo(curPoint) > 0)) {
          isPMin = false;
        }
        if (isPMin && curPoint.compareTo(segMaxPoint) > 0) {
          segMaxPoint = curPoint;
        }

        count++;
      } else {
        if (count >= 3 && isPMin) {
          seglist.add(new LineSegment(p, segMaxPoint));
        }
        count = 1;
        isPMin = true;
        segMaxPoint = curPoint;
      }
      prePoint = curPoint;
      preSlope = curSlope;
    }

    if (count >= 3 && isPMin) {
      seglist.add(new LineSegment(p, segMaxPoint));
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

  private void printArrSlope(Point p, Point[] arr) {
    for (int i = 0; i < arr.length; i++) {
      StdOut.printf("%f ", p.slopeTo(arr[i]));
    }
    StdOut.println();
  }

  // the number of line segments
  public int numberOfSegments() {
    return seglist.size();
  }

  // the line segments
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
    FastCollinearPoints collinear = new FastCollinearPoints(points);
    StdOut.printf("Number Of Segments:%d\n", collinear.numberOfSegments());
    for (LineSegment segment : collinear.segments()) {
      StdOut.println(segment);
      segment.draw();
    }
    StdDraw.show();
  }
}
