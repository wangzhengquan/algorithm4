import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
  private WordNet wordnet;

  // constructor takes a WordNet object
  public Outcast(WordNet wordnet) {
    this.wordnet = wordnet;
  }

  // given an array of WordNet nouns, return an outcast
  public String outcast(String[] nouns) {
    int maxD = 0;
    String noun = null;
    for (String nounA : nouns) {
      int dist = distance(nounA, nouns);
      if (dist > maxD) {
        maxD = dist;
        noun = nounA;
      }

    }
    return noun;
  }

  private int distance(String nounA, String[] nouns) {
    int dist = 0;
    for (String nounB : nouns) {
      dist += wordnet.distance(nounA, nounB);
    }
    return dist;
  }

  public static void main(String[] args) {
    WordNet wordnet = new WordNet(args[0], args[1]);
    Outcast outcast = new Outcast(wordnet);
    for (int t = 2; t < args.length; t++) {
      In in = new In(args[t]);
      String[] nouns = in.readAllStrings();
      StdOut.println(args[t] + ": " + outcast.outcast(nouns));
    }
  }
}
