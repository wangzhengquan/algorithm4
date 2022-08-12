import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

interface ICombinationHandler {
  public void handle(Object[] a);

}

public class PermutationCombination {

  public PermutationCombination() {

  }

  /**
   * fully permutation, no repetition allowed
   *
   * @param a
   * @return
   */
  public static int permutation(Comparable[] a) {

    a = Arrays.copyOf(a, a.length);
    Arrays.sort(a);
    int count = 1;
    show(a);
    while (next_permutation(a)) {
      show(a);
      count++;
    }
    return count;

  }

  /**
   * ref: C6.6 in Discrete Mathematics and Its Applications by Kenneth H. Rosen
   *
   * @param a
   * @return
   */
  private static boolean next_permutation(Comparable[] a) {
    int n = a.length;

    int j = n - 2;
    while (a[j].compareTo(a[j + 1]) > 0) {
      j--; // j is the largest subscript with aj < aj+1
      if (j < 0) return false;
    }

    int k = n - 1;
    while (a[j].compareTo(a[k]) > 0)
      k--; //ak is the smallest integer greater than aj to the right of aj
    exch(a, j, k);
    int r = n - 1, s = j + 1;
    while (r > s) {
      // this puts the tail end of the permutation after the jth position in increasing order
      exch(a, r, s);
      r--;
      s++;
    }
    return true;

  }


  /**
   * r-combination, no repetition allowed
   *
   * @param a
   * @param r
   * @param ch
   * @return
   */
  public static int combination(Object[] a, int r, ICombinationHandler ch) {
    int n = a.length, count = 1;
    a = Arrays.copyOf(a, a.length);
    Arrays.sort(a);
    int[] idxarr = new int[r];
    for (int i = 0; i < r; i++) {
      idxarr[i] = i;
    }
    Object[] cbarr = new Object[r];
    for (int i = 0; i < r; i++) {
      cbarr[i] = a[idxarr[i]];
    }
    ch.handle(cbarr);
    while (next_combination(idxarr, r, n)) {
      for (int i = 0; i < r; i++) {
        cbarr[i] = a[idxarr[i]];
      }
      ch.handle(cbarr);
      count++;
    }
    return count;
  }

  /**
   * ref: C6.6 in Discrete Mathematics and Its Applications by Kenneth H. Rosen
   *
   * @param idxarr
   * @param r
   * @param n
   * @return
   */
  private static boolean next_combination(int[] idxarr, int r, int n) {
    int i = r - 1;

    while (idxarr[i] == n - r + i) {
      i--;
      if (i < 0) return false;
    }

    idxarr[i]++;
    for (int j = i + 1; j < r; j++) {
      idxarr[j] = idxarr[i] + j - i;
    }
    return true;
  }

  /**
   * r-combination, allowed repetition
   *
   * @param a
   * @param r
   * @param ch
   * @return
   */
  public static int combination_allow_repeat(Object[] a, int r, ICombinationHandler ch) {
    int n = a.length, count = 1;
    a = Arrays.copyOf(a, a.length);
    Arrays.sort(a);
    int[] idxarr = new int[r];
    for (int i = 0; i < r; i++) {
      idxarr[i] = 0;
    }
    Object[] cbarr = new Object[r];
    for (int i = 0; i < r; i++) {
      cbarr[i] = a[idxarr[i]];
    }
    ch.handle(cbarr);
    while (next_combination_allow_repeat(idxarr, r, n)) {
      for (int i = 0; i < r; i++) {
        cbarr[i] = a[idxarr[i]];
      }
      ch.handle(cbarr);
      count++;
    }
    return count;
  }


  private static boolean next_combination_allow_repeat(int[] idxarr, int r, int n) {
    int i = r - 1;

    while (idxarr[i] == n - 1) {
      i--;
      if (i < 0) return false;
    }

    idxarr[i]++;
    for (int j = i + 1; j < r; j++) {
      idxarr[j] = idxarr[i];
    }
    return true;
  }

  // exchange a[i] and a[j]
  private static void exch(Object[] a, int i, int j) {
    Object swap = a[i];
    a[i] = a[j];
    a[j] = swap;
  }

  // print array to standard output
  private static void show(Object[] a) {
    for (int i = 0; i < a.length; i++) {
      StdOut.print(a[i]);
    }
    StdOut.println();
  }

  private static void show(Object[] a, int idx[]) {
    for (int i = 0; i < idx.length; i++) {
      StdOut.print(a[idx[i]]);
    }
    StdOut.println();
  }

  private static int fix_r_combination(Object[] a) {
    int count = 0;
    int i, j, k, l;
    int n = a.length;
    for (i = 0; i < n; i++)
      for (j = i + 1; j < n; j++)
        for (k = j + 1; k < n; k++)
          for (l = k + 1; l < n; l++) {
            StdOut.print(a[i] + "" + a[j] + "" + a[k] + "" + a[l] + "\n");
            count++;
          }
    return count;
  }

  private static int fix_r_combination_with_repet(Object[] a) {
    int count = 0;
    int i, j, k, l;
    int n = a.length;
    for (i = 0; i < n; i++)
      for (j = 0; j <= i; j++)
        for (k = 0; k <= j; k++)
          for (l = 0; l <= k; l++) {
            StdOut.print(a[l] + "," + a[k] + "," + a[j] + "," + a[i] + "\n");
            count++;
          }
    return count;
  }

  public static void main(String[] args) {
//    testPermutation();
    testCombination();

//    StdOut.println("-------fix_r_combination------");
//    c = fix_r_combination(a);
//    StdOut.println(c);

//    StdOut.println("-------combination_with_repet------");
//    c = combination_with_repet(a, 4, new CbHandler());
//    StdOut.println(c);
//    StdOut.println("-------fix_r_combination_with_repet------");
//    c = fix_r_combination_with_repet(a);
//    StdOut.println(c);
  }

  private static void testPermutation() {
    Integer[] a = new Integer[]{1, 2, 3, 4, 5, 6};
    int c = permutation(a);
    StdOut.println("total:" + c);
  }

  private static void testCombination() {
    StdOut.println("-------Test combination------");
    Integer[] a = new Integer[]{0, 1, 2, 3, 4};
//    Character[] a = new Character[]{'a', 'b', 'c', 'd'};
    class CbHandler implements ICombinationHandler {
      public void handle(Object[] a) {
        for (int i = 0; i < a.length; i++) {
          StdOut.print(a[i] + " ");
        }
        StdOut.println();
      }
    }

    int c = combination(a, 2, new CbHandler());
    StdOut.println("total combination :" + c);
  }

}


