/**
 * https://www.geeksforgeeks.org/expression-evaluation
 * <p>
 * Using two stacks , one for operands, and one for operators to Evaluate an Expression in one pass
 */

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

public class CalculatorX {
  private final static Map<Character, Integer> OPERATOR_MAP = new TreeMap<>();

  static {
    // <Operator, Precedence>
    OPERATOR_MAP.put('+', 1);
    OPERATOR_MAP.put('-', 1);
    OPERATOR_MAP.put('*', 2);
    OPERATOR_MAP.put('/', 2);
    OPERATOR_MAP.put('^', 3); // power

  }

  public static double evaluate(String expression) {
    char[] tokens = expression.toCharArray();
    // Stack for numbers: 'values'
    Stack<Double> values = new Stack<>();
    // Stack for Operators: 'ops'
    Stack<Character> ops = new Stack<>();
    for (int i = 0; i < tokens.length; i++) {
      // Current token is a whitespace, skip it
      if (Character.isWhitespace(tokens[i]))
        continue;

      // Current token is a number, push it to stack for numbers
      if (isDigit(tokens[i])) {
        StringBuffer sbuf = new StringBuffer();

        // There may be more than one digits in number
        // while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9')
        while (i < tokens.length && isDigit(tokens[i]))
          sbuf.append(tokens[i++]);
        values.push(Double.parseDouble(sbuf.toString()));

        /* right now the i points to the character next to the digit, since the for loop also increases the i, we would
         skip one token position; we need to decrease the value of i by 1 to correct the offset.*/
        i--;
      } else if (tokens[i] == '(') {
        // Current token is an opening brace, push it to 'ops'
        ops.push(tokens[i]);
      } else if (tokens[i] == ')') {
        // Closing brace encountered, solve entire brace
        while (ops.peek() != '(')
          values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        //Pop the left parenthesis from the stack and discard it
        if (ops.isEmpty() || ops.pop() != '(') {
          throw new IllegalArgumentException("The expression contained unbalanced parentheses ");
        }
      } else if (isOperator(tokens[i])) {
        // Current token is an operator.

        /* While top of 'ops' has same or greater precedence to current token, which is an operator.
        Apply operator on top of 'ops' to top two elements in values stack*/
        while (!ops.empty() && ops.peek() != '(' && precedence(ops.peek()) > precedence(tokens[i]))
          values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        // Push current token to 'ops'.
        ops.push(tokens[i]);
      } else {
        throw new IllegalArgumentException("Invalid token found:" + tokens[i]);
      }
    }

    // Entire expression has been parsed at this point, apply remaining ops to remaining values
    while (!ops.empty()) {
      char op = ops.pop();
      if (op != '(')
        values.push(applyOp(op, values.pop(), values.pop()));
      else
        throw new IllegalArgumentException("The infixQ contained unbalanced parentheses ");
    }


    // Top of 'values' contains result, return it
    return values.pop();
  }

  private static boolean isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
  }

  private static boolean isDigit(char c) {
    // c >= '0' && c <= '9' || c == '.'
    return Character.isDigit(c) || c == '.';
  }

  // Returns true if 'op2' has higher or same precedence as 'op1', otherwise returns false.
  public static int precedence(char op) {
    return OPERATOR_MAP.get(op);
  }

  // A utility method to apply an operator 'op' on operands 'a' and 'b'. Return the result.
  public static double applyOp(char op, double b, double a) {
    switch (op) {
      case '+':
        return a + b;
      case '-':
        return a - b;
      case '*':
        return a * b;
      case '/':
        if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
        return a / b;
      case '^':
        return Math.pow(a, b);
    }
    return 0;
  }

  // Driver method to test above methods
  public static void main(String[] args) {
    System.out.println(CalculatorX.evaluate("10 + 2 * 6"));
    System.out.println(CalculatorX.evaluate("1.01 * 2 + 12"));
    System.out.println(CalculatorX.evaluate("100 * ( 2 + 12 )"));
    System.out.println(CalculatorX.evaluate("100 * ( 2 + 12 ) / 14 + 2^3"));
    System.out.println(CalculatorX.evaluate("2^3"));
  }
}

