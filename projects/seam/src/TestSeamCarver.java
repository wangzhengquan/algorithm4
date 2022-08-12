import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class TestSeamCarver {

  //HJoceanSmall.png 150,  chameleon.png
  public static void main(String[] args) {
    //    testCarveHorizontalPicture(args);
    Stopwatch watch = new Stopwatch();
    //1 time elapsed 3.319000
    //2 time elapsed 3.561000
    //2.449000
    testCarveVerticalPicture(args);

    //time elapsed 4.497000
//    testCarveHorizontalPicture(args);
    System.out.printf("time elapsed %f\n", watch.elapsedTime());
//    testRemoveVerticalSeam(args);
  }


  private static void testEnergy() {
    Picture picture = new Picture("3x4.png");
    picture.show();
    SeamCarver seam = new SeamCarver(picture);
    System.out.printf("%d,%d\n", seam.energy(1, 2), seam.energy(1, 1));
  }

  //
  private static void testRemoveVerticalSeam(String[] args) {
    Picture picture = new Picture("6x5.png");
//    picture.show();
    SeamCarver sc = new SeamCarver(picture);
    int[] vseam = {5, 5, 5, 5, 5};
    sc.removeVerticalSeam(vseam);
    StdOut.println();

  }

  //"6x5.png"
  private static void testFindVerticalSeam(String[] args) {
    Picture picture = new Picture(args[0]);
//    picture.show();
    SeamCarver seam = new SeamCarver(picture);
    int[] vseam = seam.findVerticalSeam();
    for (int y = 0; y < vseam.length; y++) {
      StdOut.printf("(%d, %d) ", vseam[y], y);
    }
    StdOut.println();

  }

  private static void testFindHorizontal(String[] args) {
    Picture picture = new Picture(args[0]);
//    picture.show();
    SeamCarver seam = new SeamCarver(picture);
    int[] hseam = seam.findHorizontalSeam();
    for (int x = 0; x < hseam.length; x++) {
      StdOut.printf("(%d, %d) ", x, hseam[x]);
    }
    StdOut.println();
  }

  private static void testCarveVerticalPicture(String[] args) {
    if (args.length < 2)
      throw new IllegalArgumentException("Usage: java SeamCarver file carvenum");
    Picture picture = new Picture(args[0]);
    picture.show();
    SeamCarver seam = new SeamCarver(picture);
    System.out.printf("%d-by-%d\n", seam.width(), seam.height());
    int n = Integer.parseInt(args[1]);
    while (n-- > 0) {
      int[] vseam = seam.findVerticalSeam();
      seam.removeVerticalSeam(vseam);
    }
    System.out.printf("%d-by-%d\n", seam.width(), seam.height());
    seam.picture().show();
  }

  private static void testCarveHorizontalPicture(String[] args) {
    if (args.length < 2)
      throw new IllegalArgumentException("Usage: java SeamCarver file carvenum");
    Picture picture = new Picture(args[0]);
    picture.show();
    SeamCarver sc = new SeamCarver(picture);
    System.out.printf("%d-by-%d%n", sc.width(), sc.height());
    int n = Integer.parseInt(args[1]);
    while (n-- > 0) {
      int[] seam = sc.findHorizontalSeam();
      sc.removeHorizontalSeam(seam);
    }
    System.out.printf("%d-by-%d\n", sc.width(), sc.height());
    sc.picture().show();
  }


}
