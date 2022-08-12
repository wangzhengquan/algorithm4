/******************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java-algs4 LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   https://algs4.cs.princeton.edu/55compression/abraLZW.txt
 *                https://algs4.cs.princeton.edu/55compression/ababLZW.txt
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  % java-algs4 LZW - < LZW.txt  | java-algs4 LZW +
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;
import edu.princeton.cs.algs4.TST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * The {@code LZW} class provides static methods for compressing
 * and expanding a binary input using LZW compression over the 8-bit extended
 * ASCII alphabet with 12-bit codewords.
 * <p>
 * WARNING: Starting with Oracle Java 7u6, the substring method takes time and
 * space linear in the length of the extracted substring (instead of constant
 * time an space as in earlier versions). As a result, compression takes
 * quadratic time. TODO: fix.
 * See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 * for more details.
 * <p>
 * For additional documentation,
 * see <a href="https://algs4.cs.princeton.edu/55compression">Section 5.5</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class LZW {
  private static final int R = 256;        // number of input chars
  private static final int L = 4096;       // number of codewords = 2^W
  private static final int W = 12;         // codeword width

  // Do not instantiate.
  private LZW() {
  }

  /**
   * 优点是可以用一个编码表示多个字符，即longestPrefix
   * Reads a sequence of 8-bit bytes from standard input; compresses
   * them using LZW compression with 12-bit codewords; and writes the results
   * to standard output.
   */
  public static void compress(InputStream in, OutputStream out) {
    BinaryIn bin = new BinaryIn(in);
    BinaryOut bout = new BinaryOut(out);
    String input = bin.readString();

    TST<Integer> st = new TST<Integer>();

    // since TST is not balanced, it would be better to insert in a different order
    for (int code = 0; code < R; code++)
      st.put("" + (char) code, code);

    int code = R + 1;  // R is codeword for EOF

    while (input.length() > 0) {
      String s = st.longestPrefixOf(input);  // Find max prefix match s.
      bout.write(st.get(s), W);      // Print s's encoding.
      int t = s.length();
      if (t < input.length() && code < L)    // Add s to symbol table.
        st.put(input.substring(0, t + 1), code++);
      input = input.substring(t);            // Scan past s in input.
    }
    bout.write(R, W);
    bout.close();
  }

  /**
   * Reads a sequence of bit encoded using LZW compression with
   * 12-bit codewords from standard input; expands them; and writes
   * the results to standard output.
   */
  public static void expand(InputStream in, OutputStream out) {
    BinaryIn bin = new BinaryIn(in);
    BinaryOut bout = new BinaryOut(out);
    String[] st = new String[L];
    int i; // next available codeword value

    // initialize symbol table with all 1-character strings
    for (i = 0; i < R; i++)
      st[i] = "" + (char) i;
    st[i++] = "";                        // (unused) lookahead for EOF

    int codeword = bin.readInt(W);
    if (codeword == R) return;           // expanded message is empty string
    String val = st[codeword];

    while (true) {
      bout.write(val);
      codeword = bin.readInt(W);
      if (codeword == R) break;
      String s = st[codeword];
//      StdOut.println("val=" + val + ",codeword=" + codeword + ",s=" + s);
      if (i == codeword) {
        /**只有 codeword < i 的时候st里才有它对应的值， 在i==codeword的情况下上一行代码s=st[codeword]=null
         * 在这种情况下，val就是codeword本应对应的那个字符串(s)的最大前缀，
         * 即 |s|=|val|+1 && s.substring(0,|s|-1)=val ,因此s[0]=val[0]，进而得出 s = val+s[0]=val+val[0]
         */
        s = val + val.charAt(0);   // special case hack
//        StdOut.println("i == codeword, s=" + s);
      }
      if (i < L) {
        st[i++] = val + s.charAt(0);
//        StdOut.println("st_" + (i - 1) + ":" + st[i - 1]);
      }
      val = s;
    }
    bout.close();
  }

  /**
   * Sample client that calls {@code compress()} if the command-line
   * argument is "-" an {@code expand()} if it is "+".
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    try {
      compress(inputStreamOf("LZW.txt"), new FileOutputStream("LZW.bin"));
      expand(inputStreamOf("LZW.bin"), System.out);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  public static void test(String[] args) {
    if (args[0].equals("-")) compress(System.in, System.out);
    else if (args[0].equals("+")) expand(System.in, System.out);
    else throw new IllegalArgumentException("Illegal command line argument");
  }

  private static InputStream inputStreamOf(String name) {
    if (name == null) throw new IllegalArgumentException("argument is null");
    if (name.length() == 0) throw new IllegalArgumentException("argument is the empty string");
    try {
      // first try to read file from local file system
      File file = new File(name);
      if (file.exists()) {
        // for consistency with StdIn, wrap with BufferedInputStream instead of use
        // file as argument to Scanner
        FileInputStream fis = new FileInputStream(file);
        return fis;
      }
      // resource relative to .class file
      URL url = LZW.class.getResource(name);

      // resource relative to classloader root
      if (url == null) {
        url = LZW.class.getClassLoader().getResource(name);
      }

      // or URL from web
      if (url == null) {
        url = new URL(name);
      }

      URLConnection site = url.openConnection();

      // in order to set User-Agent, replace above line with these two
      // HttpURLConnection site = (HttpURLConnection) url.openConnection();
      // site.addRequestProperty("User-Agent", "Mozilla/4.76");

      InputStream is = site.getInputStream();
      return is;
    } catch (IOException ioe) {
      throw new IllegalArgumentException("Could not open " + name, ioe);
    }
  }

}
