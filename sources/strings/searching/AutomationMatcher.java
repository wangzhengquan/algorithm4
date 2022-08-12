/**
 * reference CLRS
 */

import edu.princeton.cs.algs4.In;

public class AutomationMatcher {
  String pattern;
  int[][] dfa;

  public AutomationMatcher(String pattern) {
    this.pattern = pattern;
    this.dfa = computeTransition(pattern);
  }

  public void search(String text) {
    int n = text.length();
    int m = pattern.length();
    int q = 0;
    for (int i = 0; i < n; i++) {
      q = dfa[q][text.charAt(i)];
      if (q == m) {
        System.out.print(text.substring(i + 1 - m, i + 1));
        System.out.println(" occurs in " + (i + 1 - m));
      }
    }
  }

  /**
   * Proof in 'Introduction to Algorithm 3, 32.4-8'
   */
  private int[][] computeTransition(String pattern) {
    int[] pi = computePrefix(pattern);
    int R = 256;
    int m = pattern.length();

    // build DFA from pattern
    int[][] dfa = new int[m + 1][R];
    dfa[0][pattern.charAt(0)] = 1;

    for (int c = 0; c < R; c++)
      for (int q = 1; q < m; q++) {
        if (pattern.charAt(q) != c)
          dfa[q][c] = dfa[pi[q]][c];
        else
          dfa[q][c] = q + 1;
      }

    for (int c = 0; c < R; c++)
      dfa[m][c] = dfa[pi[m]][c];
    return dfa;
  }


  private int[] computePrefix(String pattern) {
    int m = pattern.length();
    int[] pi = new int[m + 1];
    pi[1] = 0;
    int k = 0;
    for (int i = 1; i < m; i++) {
      while (k > 0 && pattern.charAt(k) != pattern.charAt(i)) {
        k = pi[k];
      }
      if (pattern.charAt(k) == pattern.charAt(i)) {
        k++;
      }
      pi[i + 1] = k;
    }
    return pi;
  }


  public static void main(String[] args) {
    test1();
  }

  public static void test1() {

    AutomationMatcher kmp = new AutomationMatcher("pattern");
    kmp.search(new In("kmp.txt").readAll());
    kmp = new AutomationMatcher("aa");
    kmp.search(new In("test.txt").readAll());
  }

  public static void test(String[] args) {
    In in = new In(args[0]);
    String text = in.readAll();
    String pattern = args[1];
    AutomationMatcher kmp = new AutomationMatcher(pattern);
    kmp.search(text);
  }
}
