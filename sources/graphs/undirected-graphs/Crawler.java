import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
  public static void main(String[] args) {
    Queue<String> queue = new Queue<String>();
    SET<String> marked = new SET<String>();
    String root = args[0];
    queue.enqueue(root);
    marked.add(root);
    while (!queue.isEmpty()) {
      String v = queue.dequeue();
      StdOut.println(v);
      String input = null;
      try {
        In in = new In(v);
        input = in.readAll();
      } catch (Exception e) {
        continue;
      }

      String regexp = "https://(\\w+\\.)*(\\w+)";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(input);
      while (matcher.find()) {
        String w = matcher.group();
        if (!marked.contains(w)) {
          marked.add(w);
          queue.enqueue(w);
        }
      }
    }
  }
}
