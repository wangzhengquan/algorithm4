/******************************************************************************
 *  Compilation:  javac KMP.java
 *  Execution:    java KMP pattern text
 *  Dependencies: StdOut.java
 *
 *  Reads in two strings, the pattern and the input text, and
 *  searches for the pattern in the input text using the
 *  KMP algorithm.
 *
 *  % java KMP abracadabra abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:               abracadabra
 *
 *  % java KMP rab abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:         rab
 *
 *  % java KMP bcara abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                                   bcara
 *
 *  % java KMP rabrabracad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                        rabrabracad
 *
 *  % java KMP abacad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern: abacad
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code KMP} class finds the first occurrence of a pattern string
 * in a text string.
 * <p>
 * This implementation uses a version of the Knuth-Morris-Pratt substring search
 * algorithm. The version takes time proportional to <em>n</em> + <em>m R</em>
 * in the worst case, where <em>n</em> is the length of the text string,
 * <em>m</em> is the length of the pattern, and <em>R</em> is the alphabet size.
 * It uses extra space proportional to <em>m R</em>.
 * <p>
 * For additional documentation,
 * see <a href="https://algs4.cs.princeton.edu/53substring">Section 5.3</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class KMP {
  private final int R;       // the radix
  private final int m;       // length of pattern
  private int[][] dfa;       // the KMP automoton

  /**
   * Preprocesses the pattern string.
   *
   * @param pat the pattern string
   */
  public KMP(String pat) {
    this.R = 256;
    this.m = pat.length();

    // build DFA from pattern
    dfa = new int[R][m + 1];
    dfa[pat.charAt(0)][0] = 1;
    int x = 0;
    for (int j = 1; j < m; j++) {
      for (int c = 0; c < R; c++)
        dfa[c][j] = dfa[c][x];     // Copy mismatch cases.
      dfa[pat.charAt(j)][j] = j + 1;   // Set match case.
      x = dfa[pat.charAt(j)][x];     // Update restart state.
      //  the start state of x was 1 less than the start state of j, 
      //so x simulate j go back j-1，amount to i forward 1, 
      //the character from 1 to j-1 in the pattern is same as that from i+1 to i+j-1 in the text.
      /**
       * J: 0-->1-A->2-B->3-C->X
       * X:     0-A->X-B->X-C->X
       * j是从1状态开始，x是从0状态开始的，x匹配的顺序就是J失败后重新匹配的顺序
       *
       *
      */
    }
    // for matched situation
    for (int c = 0; c < R; c++)
      dfa[c][m] = dfa[c][x];
  }

  /**
   * Returns the index of the first occurrrence of the pattern string
   * in the text string.
   *
   * @param txt the text string
   * @return the index of the first occurrence of the pattern string
   * in the text string; N if no such match
   */
  public List<Integer> search(String txt) {
    // simulate operation of DFA on text
    List<Integer> index = new ArrayList<>();
    int n = txt.length();
    int i, j;
    for (i = 0, j = 0; i < n; i++) {
      j = dfa[txt.charAt(i)][j];
      if (j == m) {
        // found
        index.add(i + 1 - m);
        System.out.print(txt.substring(i + 1 - m, i + 1));
        System.out.println(" occurs in " + (i + 1 - m));
      }
    }
    return index;

  }


  /**
   * Takes a pattern string and an input string as command-line arguments;
   * searches for the pattern string in the text string; and prints
   * the first occurrence of the pattern string in the text string.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    test2(args);
  }

  public static void test1() {

    KMP kmp = new KMP("pattern");
    kmp.search(new In("kmp.txt").readAll());

    kmp = new KMP("import");
    kmp.search(new In("kmp.txt").readAll());
    kmp = new KMP("aa");
    kmp.search(new In("test.txt").readAll());
  }


  public static void test2(String[] args) {
    String pat = args[0];
    String txt = args[1];

    KMP kmp1 = new KMP(pat);
    List<Integer> offsets = kmp1.search(txt);

    // print results
    StdOut.println("text:    " + txt);

    StdOut.print("pattern: ");
    int i = 0;
    int m = pat.length();
    for (int offset : offsets) {
      if (i > offset) {
        StdOut.print(pat.substring((i - offset), m));
      } else {
        for (; i < offset; i++)
          StdOut.print(" ");
        StdOut.print(pat);
      }
      i = offset + pat.length();
    }
    StdOut.println();


  }
}
