import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver2 {

  private Picture picture;
  private double[][] energy;

  // create a seam carver object based on the given picture
  public SeamCarver2(Picture picture) {
    if (picture == null)
      throw new IllegalArgumentException();
    this.setPicture(new Picture(picture));

    int width = width(), height = height();
    energy = new double[width][height];
    for (int col = 0; col < width; col++)
      for (int row = 0; row < height; row++) {
        energy[col][row] = energy(col, row);
      }
  }

  private void setPicture(Picture picture) {
    this.picture = picture;
  }

  // current picture
  public Picture picture() {
    return new Picture(picture);
  }

  // width of current picture
  public int width() {
    return picture.width();
  }

  // height of current picture
  public int height() {
    return picture.height();
  }

  // energy of pixel at column x and row y
  public double energy(int x, int y) {
    validatePixel(x, y);
    return Math.sqrt(energySquare(x, y));
  }

  private int energySquare(int x, int y) {
    int w = width(), h = height();

    if (x == 0 || x == w - 1 || y == 0 || y == h - 1)
      return 1000 * 1000;
    Color cx1 = picture.get(x - 1, y);
    Color cx2 = picture.get(x + 1, y);
    Color cy1 = picture.get(x, y - 1);
    Color cy2 = picture.get(x, y + 1);

    int Rx = cx2.getRed() - cx1.getRed();
    int Gx = cx2.getGreen() - cx1.getGreen();
    int Bx = cx2.getBlue() - cx1.getBlue();
    int Ry = cy2.getRed() - cy1.getRed();
    int Gy = cy2.getGreen() - cy1.getGreen();
    int By = cy2.getBlue() - cy1.getBlue();
    return (Rx * Rx + Gx * Gx + Bx * Bx + Ry * Ry + Gy * Gy + By * By);
  }

  // sequence of indices for horizontal seam
  public int[] findHorizontalSeam() {
    Picture oldPic = this.picture;
    double[][] oldEnergy = this.energy;
    transpose();
    int[] seam = findVerticalSeam();
    this.picture = oldPic;
    this.energy = oldEnergy;
    return seam;
  }

  // sequence of indices for vertical seam
  public int[] findVerticalSeam() {
    int height = height(), width = width();
    double[][] distTo = new double[width][height];
    int[][] edgeTo = new int[width][height];

    int edgeToEnd = -1;
    double distToEnd = Double.POSITIVE_INFINITY;

    for (int col = 0; col < width; col++)
      for (int row = 0; row < height; row++) {
//        energy[col][row] = energy(col, row);
        if (row == 0)
          distTo[col][row] = energy[col][row];
        else
          distTo[col][row] = Double.POSITIVE_INFINITY;
      }

    // visit vertices in topological order
    int toRow, toCol;
    for (int row = 0; row < height; row++)
      for (int col = 0; col < width; col++) {
        toRow = row + 1;
        if (toRow == height) {
          if (distToEnd > distTo[col][row]) {
            distToEnd = distTo[col][row];
            edgeToEnd = col;
          }
          continue;
        }
        toCol = col;
        if (distTo[toCol][toRow] > distTo[col][row] + energy[toCol][toRow]) {
          distTo[toCol][toRow] = distTo[col][row] + energy[toCol][toRow];
          edgeTo[toCol][toRow] = col;
        }

        toCol = col - 1;
        if (toCol >= 0)
          if (distTo[toCol][toRow] > distTo[col][row] + energy[toCol][toRow]) {
            distTo[toCol][toRow] = distTo[col][row] + energy[toCol][toRow];
            edgeTo[toCol][toRow] = col;
          }
        toCol = col + 1;
        if (toCol < width)
          if (distTo[toCol][toRow] > distTo[col][row] + energy[toCol][toRow]) {
            distTo[toCol][toRow] = distTo[col][row] + energy[toCol][toRow];
            edgeTo[toCol][toRow] = col;
          }

      }

//    if (edgeToEnd == -1)
//      throw new RuntimeException("edgeToEnd == -1");

    int[] seam = new int[height];
    int l = height;
    int e = edgeToEnd;
    while (l-- > 0) {
      seam[l] = e;
      e = edgeTo[e][l];
    }
    return seam;
  }

  // relax edge e
//  private void relax(int fromX, int fromY, int toX, int toY) {
////    StdOut.printf("form(%d, %d) to (%d, %d)\n", fromX, fromY, toX, toY);
//    if (distTo[toX][toY] > distTo[fromX][fromY] + _energy_(toX, toY)) {
//      distTo[toX][toY] = distTo[fromX][fromY] + _energy_(toX, toY);
//      edgeTo[toX][toY] = new int[]{fromX, fromY};
//    }
////    StdOut.printf("form(%d, %d) to (%d, %d) %f %f+%f\n", fromX, fromY, toX, toY, distTo[toX][toY], distTo[fromX][fromY], _energy_(toX, toY));
//  }

  // remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    transpose();
    removeVerticalSeam(seam);
    transpose();
  }

  // remove vertical seam from current picture
  public void removeVerticalSeam(int[] seam) {
    if (seam == null)
      throw new IllegalArgumentException("argument can't be null.");

    int height = height(), width = width();

    if (width < 2)
      throw new IllegalArgumentException("width of the picture is less than or equal to 1");
    if (seam.length != height)
      throw new IllegalArgumentException("wrong length");

    validateSeam(seam);

    Picture newp = new Picture(width - 1, height);
    double[][] newenergy = new double[width - 1][height];
    for (int y = 0; y < height; y++) {
      if (seam[y] < 0 || seam[y] > width - 1)
        throw new IllegalArgumentException("an entry is outside its prescribed range ");
      for (int x = 0; x < seam[y]; x++) {
        newp.set(x, y, picture.get(x, y));
        newenergy[x][y] = energy[x][y];
      }
      for (int x = seam[y] + 1; x < width; x++) {
        newp.set(x - 1, y, picture.get(x, y));
        newenergy[x - 1][y] = energy[x][y];
      }
    }

    this.setPicture(newp);
    // ??????seam???????????????energy???????????????
    for (int y = 0; y < height(); y++) {
      int x = seam[y];
      if (x < width())
        newenergy[x][y] = energy(x, y);
      if (x - 1 > 0)
        newenergy[x - 1][y] = energy(x - 1, y);
    }
    this.energy = newenergy;

  }

  private void transpose() {
    Picture newPicture = new Picture(height(), width());
    double[][] newenergy = new double[height()][width()];
    for (int col = 0; col < newPicture.width(); col++)
      for (int row = 0; row < newPicture.height(); row++) {
        newPicture.set(col, row, picture.get(row, col));
        newenergy[col][row] = energy[row][col];
      }
    this.picture = newPicture;
    this.energy = newenergy;
  }

  private double[][] transpose(double[][] energy) {
    int w = energy.length;
    int h = energy[0].length;
    double[][] newenergy = new double[h][w];
    for (int col = 0; col < h; col++)
      for (int row = 0; row < w; row++) {
        newenergy[col][row] = energy[row][col];
      }
    return newenergy;
  }

  private void validatePixel(int x, int y) {
    int w = width();
    int h = height();
    if (x < 0 || x > w - 1 || y < 0 || y > h - 1)
      throw new IllegalArgumentException("pixel (" + x + "," + y + ") is not between 0 and (" + w + "," + h + ")");
  }

  // make sure two adjacent entries differ within 1
  private void validateSeam(int[] seam) {
    for (int i = 0; i < seam.length - 1; i++) {
      if (Math.abs(seam[i] - seam[i + 1]) > 1) {
        throw new IllegalArgumentException("two adjacent entries differ by more than 1 in seam\n");
      }
    }
  }

  //HJoceanSmall.png
  //  unit testing (optional)
  public static void main(String[] args) {
  }
}
