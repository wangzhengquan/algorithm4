import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class WordNet {
  private Map<String, Set<Integer>> st;  // string -> index
  // inverted index to get string keys in an array
  private List<String> nounList;
  private Digraph graph;           // the underlying digraph
  private SAP sap;
  private final static String delimiter = ",";

  // constructor takes the name of the two input files
  public WordNet(String synsets, String hypernyms) {
    if (synsets == null || hypernyms == null)
      throw new IllegalArgumentException("WordNet construct argument should not be null");
    st = new TreeMap<String, Set<Integer>>();
    // inverted index to get string keys in an array
    nounList = new ArrayList<>();
    // First pass builds the index by reading strings to associate
    // distinct strings with an index
    In in = new In(synsets);
    while (in.hasNextLine()) {
      String[] a = in.readLine().split(delimiter);
      nounList.add(a[1]);
      int id = Integer.parseInt(a[0]);
      String[] a1 = a[1].split(" ");
      for (String key : a1) {
        Set<Integer> set = st.get(key);
        if (set == null) {
          set = new TreeSet<>();
          st.put(key, set);
        }
        set.add(id);
      }
    }

    // second pass builds the digraph by connecting first vertex on each
    // line to all others
//    StdOut.println("nounList.size():" + nounList.size());
    graph = new Digraph(nounList.size());
    in = new In(hypernyms);
    while (in.hasNextLine()) {
      String[] a = in.readLine().split(delimiter);
      int v = Integer.parseInt(a[0]);
      for (int i = 1; i < a.length; i++) {
        int w = Integer.parseInt(a[i]);
        graph.addEdge(v, w);
      }
    }


    DirectedCycle finder = new DirectedCycle(graph);
    if (finder.hasCycle())
      throw new IllegalArgumentException("WordNet, input is not a rooted DAG");

    if (!isRootedDAG(graph)) {
      throw new IllegalArgumentException("WordNet should not have more than one root");
    }

    sap = new SAP(graph);
  }

  private boolean isRootedDAG(Digraph g) {
    int roots = 0;
    for (int i = 0; i < graph.V(); i++) {
      if (graph.outdegree(i) == 0) {
        roots++;
        if (roots >= 2) {
          return false;
        }
      }
    }
    return true;
  }

  // returns all WordNet nouns
  public Iterable<String> nouns() {
    return st.keySet();
  }

  // is the word a WordNet noun?
  public boolean isNoun(String word) {
    if (word == null)
      throw new IllegalArgumentException("word should not be null");
    return st.containsKey(word);
  }

  // distance between nounA and nounB (defined below)
  public int distance(String nounA, String nounB) {
    if (!isNoun(nounA) || !isNoun(nounB)) {
      throw new IllegalArgumentException("In distance method, " + nounA + " or " + nounB + " is not a WordNet noun.");
    }
    Set<Integer> v = st.get(nounA);
    Set<Integer> w = st.get(nounB);
    return sap.length(v, w);
  }

  // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
  // in a shortest ancestral path (defined below)
  public String sap(String nounA, String nounB) {
    if (!isNoun(nounA) || !isNoun(nounB)) {
      throw new IllegalArgumentException("In sap method, " + nounA + " or " + nounB + " is not a WordNet noun.");
    }
    Set<Integer> v = st.get(nounA);
    Set<Integer> w = st.get(nounB);
    int ancestor = sap.ancestor(v, w);
    if (ancestor == -1)
      return null;
    else
      return nounList.get(ancestor);
  }

  // do unit testing of this class
  public static void main(String[] args) {
    testNoRoot();
  }

  private static void testNoRoot() {
    new WordNet("synsets3.txt", "hypernyms3InvalidTwoRoots.txt");
  }

  private static void test() {
    WordNet net = new WordNet("synsets.txt", "hypernyms.txt");
    String nounA = "horse";
    String nounB = "cat";
    int dist = net.distance(nounA, nounB);
    String ancestor = net.sap(nounA, nounB);
    StdOut.printf("length = %d, ancestor = %s\n", dist, ancestor);
  }
}
