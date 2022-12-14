/******************************************************************************
 *  Compilation:  javac NFA.java
 *  Execution:    java NFA regexp text
 *  Dependencies: Stack.java Bag.java Digraph.java DirectedDFS.java
 *
 *  % java NFA "(A*B|AC)D" AAAABD
 *  true
 *
 *  % java NFA "(A*B|AC)D" AAAAC
 *  false
 *
 *  % java NFA "(a|(bc)*d)*" abcbcd
 *  true
 *
 *  % java NFA "(a|(bc)*d)*" abcbcbcdaaaabcbcdaaaddd
 *  true
 *
 *  Remarks
 *  -----------
 *  Enhanced version of NFA
 *  The following features are not supported:
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedDFS;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;


public class NFAX {

  private static class Vertice {
    boolean escape = false;
    boolean exclude = false;
    int nextState;
    Set<Character> specifiedSet;
    boolean isRange = false;
    char[] range;
  }

  private Digraph graph;     // digraph of epsilon transitions
  private String regexp;     // regular expression
  private final int m;       // number of characters in regular expression
  private final static Set<Character> METACHARACTERS_SET = new HashSet<>();
  private final static Set<Character> ESCAPECHARACTERS_SET = new HashSet<>();
  private Vertice[] vertices;
  private final static String METACHARACTERS = "()[]*+?";

  /**
   * Initializes the NFA from the specified regular expression.
   *
   * @param regexp the regular expression
   */
  public NFAX(String regexp) {
    this.regexp = regexp;
    m = regexp.length();
    vertices = new Vertice[m];
    for (char c : METACHARACTERS.toCharArray()) {
      METACHARACTERS_SET.add(c);
    }
    ESCAPECHARACTERS_SET.addAll(METACHARACTERS_SET);
    ESCAPECHARACTERS_SET.add('\\');

    Stack<Integer> ops = new Stack<Integer>();
    boolean isEscape = false;
    graph = new Digraph(m + 1);
    for (int i = 0; i < m; i++) {
//      StdOut.println(i + ":" + regexp.charAt(i));
      int lp = i;
      vertices[i] = new Vertice();
      vertices[i].nextState = i + 1;
      char c = regexp.charAt(i);
      if (c == '\\' && !isEscape) {
        isEscape = true;
        graph.addEdge(i, i + 1);
        continue;
      } else if (isEscape) {
        vertices[i].escape = true;
        isEscape = false;
        continue;
      }

      if (c == '(' || c == '|' || c == '[')
        ops.push(i);
      else if (c == ')') {
        // "(a|b|c)"
        int or = ops.pop();
        Bag<Integer> orbag = new Bag<>();
        while (regexp.charAt(or) == '|') {
          orbag.add(or);
          or = ops.pop();
        }
        for (int _or : orbag) {
          graph.addEdge(_or, i);
          graph.addEdge(lp, _or + 1);
        }

        if (regexp.charAt(or) == '(')
          lp = or;
        else assert false;
      } else if (c == ']') {
        lp = ops.pop();
        if (regexp.charAt(lp) != '[')
          throw new IllegalArgumentException("Invalid regular expression");
        int lo = lp + 1;
        boolean isExclude = false;
        if (regexp.charAt(lp + 1) == '^') {
          isExclude = true;
          lo = lp + 2;
        }
        // range
        boolean isRange = false;
        for (int a = lo; a < i; a++) {
          if (regexp.charAt(a) == '-') {
            graph.addEdge(lp, a);
            vertices[a].nextState = i;
            vertices[a].exclude = isExclude;

            vertices[a].isRange = true;
            char[] range = new char[2];
            range[0] = regexp.charAt(a - 1);
            range[1] = regexp.charAt(a + 1);
            vertices[a].range = range;

            isRange = true;
          }
        }
        if (!isRange) {
          Set<Character> specifiedSet = new HashSet<>();
          for (int a = lo; a < i; a++) {
            if (!isExclude) {
              graph.addEdge(lp, a);
              vertices[a].nextState = i;
//              vertices[a].exclude = isExclude;
            } else
              specifiedSet.add(regexp.charAt(a));
          }
          if (isExclude) {
            graph.addEdge(lp, lp + 1);
            vertices[lp + 1].nextState = i;
            vertices[lp + 1].exclude = true;
            vertices[lp + 1].specifiedSet = specifiedSet;
          }
        }
      }

      // closure operator (uses 1-character lookahead)
      if (i < m - 1) {
        if (regexp.charAt(i + 1) == '*') {
          graph.addEdge(lp, i + 1);
          graph.addEdge(i + 1, lp);
        } else if (regexp.charAt(i + 1) == '+') {
          graph.addEdge(i + 1, lp);
        } else if (regexp.charAt(i + 1) == '?') {
          graph.addEdge(lp, i + 1);
        }
        // To Do:exactly k , [0-9]{5}-[0-9]{4}
      }

      if (METACHARACTERS_SET.contains(c)) {
        graph.addEdge(i, i + 1);
      }

    }
    if (ops.size() != 0)
      throw new IllegalArgumentException("Invalid regular expression");
  }


  /**
   * Returns true if the text is matched by the regular expression.
   *
   * @param txt the text
   * @return {@code true} if the text is matched by the regular expression,
   * {@code false} otherwise
   */
  public boolean recognizes(String txt) {
//    StdOut.println(graph);
    StringBuilder machedBuf = new StringBuilder();
    DirectedDFS dfs = new DirectedDFS(graph, 0);
    //states reachable from start by ??-transitions
    Bag<Integer> pc = new Bag<Integer>();
    for (int v = 0; v < graph.V(); v++)
      if (dfs.marked(v)) pc.add(v);
    Bag<Integer> pc0 = pc;
    boolean mached = false;
//    printBag(pc);

    // Compute possible NFA states for txt[i+1]
    for (int i = 0; i < txt.length(); i++) {

      Bag<Integer> match = new Bag<Integer>();
      char c = txt.charAt(i);
      for (int v : pc) {
        if (v == m) continue;
        if (!ESCAPECHARACTERS_SET.contains(regexp.charAt(v)) || vertices[v].escape) {
          if (matCharWithRegexp(c, regexp, v)) {
            match.add(vertices[v].nextState);
            machedBuf.append(c);
          }
        }
      }
      if (match.isEmpty()) continue;
//      if (match.isEmpty()) {
//        return false;
//      }

      dfs = new DirectedDFS(graph, match);
      // set of states reachable after scanning past txt.charAt(i)
      pc = new Bag<Integer>();
      for (int v = 0; v < graph.V(); v++) {
        if (dfs.marked(v)) {
          pc.add(v);
          if (v == m) {
            // mache ?????? ?????????????????????
            StdOut.println("matched at " + (i - machedBuf.length() + 1) + " :" + machedBuf.toString());
            mached = true;
            machedBuf = new StringBuilder();
            pc = pc0;
            break;
          }
        }
      }

//      printBag(pc);

      // optimization if no states reachable
      if (pc.size() == 0) return false;
    }

    return mached;
  }


  private boolean matCharWithRegexp(char c, String regexp, int v) {
    if (vertices[v].exclude) {
      if (vertices[v].isRange) {
        if (c >= vertices[v].range[0] && c <= vertices[v].range[1])
          return false;
        else
          return true;
      } else {
        return !vertices[v].specifiedSet.contains(c);
      }
    } else {
      if (vertices[v].isRange) {
        if (c >= vertices[v].range[0] && c <= vertices[v].range[1])
          return true;
        else return false;
      } else {
        if (regexp.charAt(v) == c || regexp.charAt(v) == '.')
          return true;
        else return false;
      }
    }
  }

  private void printBag(Bag<Integer> bag) {
    Stack<Integer> st = new Stack<>();
    for (Integer v : bag) {
      st.push(v);
    }
    for (Integer v : st) {
      StdOut.print(v + " ");
    }
    StdOut.println();
    for (Integer v : st) {
      if (v < regexp.length())
        StdOut.print(this.regexp.charAt(v) + " ");
    }
    StdOut.println();
  }

  // NFA (A*B|AC)D AABD
  // .*AB((C|D|E)F)*G  HIGABCFG
  //.U.U.U.  SUCCUBUS
  //[a-z]+@([a-z]+\.)+(edu|com) wayne@princeton.edu
  //[0-9]+-[0-9]+-[0-9]+ 166-11-4433
  //[$_A-Za-z][$_A-Za-z0-9]* ident3
  //"<blink>.*</blink>" "<blink>text</blink>some text<blink>more text</blink>"

  /**
   * Unit tests the {@code NFA} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
//[a-z]+@([a-z]+\.)+(edu|com) wayne@princeton.edu
    String regexp = "(" + args[0] + ")";
    String txt = args[1];
    NFAX nfa = new NFAX(regexp);
    StdOut.println("\n" + regexp + " " + txt + " " + nfa.recognizes(txt));

  }

}
