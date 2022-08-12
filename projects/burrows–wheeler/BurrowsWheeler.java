import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * java-algs4 BurrowsWheeler - < abra.txt
 * java-algs4 BurrowsWheeler - < abra.txt | java-algs4 BurrowsWheeler +
 */
public class BurrowsWheeler {
  private final static int R = 256;

  // apply Burrows-Wheeler transform,
  // reading from standard input and writing to standard output
  public static void transform() {
    String s = BinaryStdIn.readString();
    int n = s.length();
    CircularSuffixArray suffixArray = new CircularSuffixArray(s);
    for (int i = 0; i < suffixArray.length(); i++) {
      if (suffixArray.index(i) == 0) {
//        StdOut.println("write " + i);
        BinaryStdOut.write(i);
        break;
      }
    }
    for (int i = 0; i < n; i++) {
      BinaryStdOut.write(s.charAt((suffixArray.index(i) + n - 1) % n));
    }

    BinaryStdOut.close();
  }


  // apply Burrows-Wheeler inverse transform,
  // reading from standard input and writing to standard output
  public static void inverseTransform() {
    int first = BinaryStdIn.readInt();
    String input = BinaryStdIn.readString();
    char[] t = input.toCharArray();
    int n = t.length;
    int R = 256;
    int[] next = new int[n];
    char[] s = new char[n];
    int[] count = new int[R + 1];
    for (int i = 0; i < n; i++) {
      count[t[i] + 1]++;
    }
    for (int i = 0; i < R; i++) {
      count[i + 1] += count[i];
    }
    for (int i = 0; i < n; i++) {
      s[count[t[i]]] = t[i];
      next[count[t[i]]++] = i;
    }

    for (int c = first, i = 0; i < n; i++, c = next[c]) {
      BinaryStdOut.write(s[c]);
    }
    BinaryStdOut.close();

  }

  // if args[0] is "-", apply Burrows-Wheeler transform
  // if args[0] is "+", apply Burrows-Wheeler inverse transform
  public static void main(String[] args) {
    if (args[0].equals("-")) transform();
    else if (args[0].equals("+")) inverseTransform();
    else throw new IllegalArgumentException("Illegal command line argument");
  }

}
