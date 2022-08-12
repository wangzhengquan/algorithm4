/**
 * fully parenthesized arithmetic expressions calculator
 * Dijkstra’s Two-Stack Algorithm for Expression Evaluation
 * Algorithms 4th -->1.3 Statack--> Arithmetic expression evaluation.
 * <p>
 * % java Evaluate
 * ( 2 + ( ( 3 + 4 ) * ( 5 * 6 ) ) )
 * [Ctrl-d]
 * 212.0
 * <p>
 * % java Evaluate
 * ( 1 + ( ( 2 + 3 ) * ( 4 * 5 ) ) )
 * [Ctrl-d]
 * 101.0
 * <p>
 * % java Evaluate
 * ( ( 1 + sqrt ( 5.0 ) ) / 2.0 )
 * [Ctrl-d]
 * 1.618033988749895
 */

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Evaluate {
  public static void main(String[] args) {
    Stack<String> ops = new Stack<String>();
    Stack<Double> vals = new Stack<Double>();
    while (!StdIn.isEmpty()) { // Read token, push if operator.
      String s = StdIn.readString();
      if (s.equals("(")) ;
      else if (s.equals("+")) ops.push(s);
      else if (s.equals("-")) ops.push(s);
      else if (s.equals("*")) ops.push(s);
      else if (s.equals("/")) ops.push(s);
      else if (s.equals("sqrt")) ops.push(s);
      else if (s.equals(")")) {
        // Pop, evaluate, and push result if token is ")".
        String op = ops.pop();
        double v = vals.pop();
        if (op.equals("+")) v = vals.pop() + v;
        else if (op.equals("-")) v = vals.pop() - v;
        else if (op.equals("*")) v = vals.pop() * v;
        else if (op.equals("/")) v = vals.pop() / v;
        else if (op.equals("sqrt")) v = Math.sqrt(v);
        vals.push(v);
      } else {
        vals.push(Double.parseDouble(s)); // Token not operator or paren: push double value.
      }
    }
    StdOut.println(vals.pop());
  }

}
