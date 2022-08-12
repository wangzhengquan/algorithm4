import edu.princeton.cs.algs4.StdOut;

/**
 * circular suffix array of s
 */

public class CircularSuffixArray {
  private static final int CUTOFF = 5;   // cutoff to insertion sort (any value between 0 and 12)

  private final char[] text;
  private final int[] index;   // index[i] = j means text.substring(j) is ith largest suffix
  private final int N;         // number of characters in text

  /**
   * Initializes a suffix array for the given {@code text} string.
   *
   * @param s the input string
   */
  public CircularSuffixArray(String s) {
    if (s == null)
      throw new IllegalArgumentException(" Argument to CircularSuffixArray is null");
    N = s.length();
    this.text = s.toCharArray();
    this.index = new int[N];
    for (int i = 0; i < N; i++)
      index[i] = i;

    sort(0, N - 1, 0);
  }

  /**
   * Returns the length of the input string.
   *
   * @return the length of the input string
   */
  public int length() {
    return N;
  }


  /**
   * Returns the index into the original string of the <em>i</em>th smallest suffix.
   * That is, {@code text.substring(sa.index(i))} is the <em>i</em> smallest suffix.
   *
   * @param i an integer between 0 and <em>n</em>-1
   * @return the index into the original string of the <em>i</em>th smallest suffix
   * @throws java.lang.IllegalArgumentException unless {@code 0 <=i < n}
   */
  public int index(int i) {
    if (i < 0 || i >= N) throw new IllegalArgumentException();
    return index[i];
  }


  private int charAt(int i, int d) {
    assert d >= 0 && d <= text.length;
    if (d == text.length) return -1;
    return text[(i + d) % N];
  }

  // 3-way string quicksort lo..hi starting at dth character
  private void sort(int lo, int hi, int d) {
//    StdOut.println(lo + "," + hi);

//    if (lo >= hi) {
//      return;
//    }
    // cutoff to insertion sort for small subarrays
    if (hi <= lo + CUTOFF) {
      insertion(lo, hi, d);
      return;
    }

    int lt = lo, gt = hi;
    int v = charAt(index[lo], d);
    int i = lo + 1;
    while (i <= gt) {
      //lo****lt***i**********gt*****hi
      //  < v   =v  unchecked   >v
      int t = charAt(index[i], d);
      if (t < v) exch(lt++, i++);
      else if (t > v) exch(i, gt--);
      else i++;
    }

    // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
    sort(lo, lt - 1, d);
    if (v >= 0) sort(lt, gt, d + 1);
    sort(gt + 1, hi, d);
  }

  // sort from a[lo] to a[hi], starting at the dth character
  private void insertion(int lo, int hi, int d) {
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && less(index[j], index[j - 1], d); j--)
        exch(j, j - 1);
  }

  // is charAt(i+d..n) < charAt(j+d..n) ?
  private boolean less(int i, int j, int d) {
    if (i == j) return false;
    while (d < N) {
      if (charAt(i, d) < charAt(j, d)) return true;
      if (charAt(i, d) > charAt(j, d)) return false;
      d++;
    }
    return false;
  }

  // exchange index[i] and index[j]
  private void exch(int i, int j) {
    int swap = index[i];
    index[i] = index[j];
    index[j] = swap;
  }

  private String select(int i) {
    if (i < 0 || i >= N) throw new IllegalArgumentException();
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < N; k++) {
      sb.append((char) charAt(index[i], k));
    }
    return sb.toString();
  }


  /**
   * Unit tests the {@code SuffixArrayx} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    String s = "ABRACADABRA!";
    CircularSuffixArray suffixArray = new CircularSuffixArray(s);
    for (int i = 0; i < suffixArray.length(); i++) {
      StdOut.println(i + ":" + suffixArray.index(i) + ":" + suffixArray.select(i));
    }
  }

}

