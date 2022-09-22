/**
 * A perfect calculator
 * <p>
 * Dijkstra’s Two-Stack Algorithm for Expression Evaluation
 * <p>
 * Reference:
 * http://faculty.cs.niu.edu/~hutchins/csci241/eval.htm
 * https://en.wikipedia.org/wiki/Shunting_yard_algorithm
 * Discrete mathmatics and its application 7th --> 11.3 Tree Travesal
 *
 * <p>
 * % java Calculator
 * 2 +  ( 3 + 4 ) * ( 5 * 6 )
 * 212.0
 * <p>
 * 1 +  ( 2 + 3 ) * ( 4 * 5 )
 * 101.0
 * <p>
 * ( 1 + sqrt( 5.0+4*3+sqrt(3+30*2) ) ) / 2.0
 * 2.996860725651001
 * <p>
 * ( 1 + sqrt ( 5.0 ) ) / 2.0
 * 1.618033988749895
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;

public class Calculator {

  // key is operator, value is precedence
  private final static Map<String, Integer> OPERATOR_MAP = new TreeMap<>();

  static {
    // <Operator, Precedence>
    OPERATOR_MAP.put("+", 1);
    OPERATOR_MAP.put("-", 1);
    OPERATOR_MAP.put("*", 2);
    OPERATOR_MAP.put("/", 2);
    OPERATOR_MAP.put("^", 3); // power
    OPERATOR_MAP.put("sqrt", 4);
    OPERATOR_MAP.put("sin", 4);
    OPERATOR_MAP.put("cos", 4);
    OPERATOR_MAP.put("log", 4); //natural logarithm
    OPERATOR_MAP.put("max", 4);
    OPERATOR_MAP.put("min", 4);
  }

  public Calculator() {
  }

  public double evaluate(String expression) {
    if (expression == null || expression.length() == 0) {
      throw new IllegalArgumentException("empty expression");
    }
    return evaluatePostfix(infixToPostfix(tokenizeExpression(expression)));
  }

  private List<String> tokenizeExpression(String expression) {
    List<String> infixQ = new ArrayList<>();
    StringBuilder buf = new StringBuilder();
    int len = expression.length();
    for (int i = 0; i < len; i++) {
      char c = expression.charAt(i);
      if (Character.isWhitespace(c)) {
        if (buf.length() > 0) {
          infixQ.add(buf.toString());
          buf = new StringBuilder();
        }
        continue;
      }
      if (isSymbol(c)) {
        if (buf.length() > 0) {
          infixQ.add(buf.toString());
          buf = new StringBuilder();
        }
        infixQ.add(String.valueOf(c));
      } else
        buf.append(c);
    }

    if (buf.length() > 0)
      infixQ.add(buf.toString());
    return infixQ;
  }

  /**
   * Transform an infix queue to postfix queue
   *
   * @param infixQ
   * @return
   */
  private List<String> infixToPostfix(List<String> infixQ) {
    if (infixQ == null || infixQ.isEmpty()) {
      throw new IllegalArgumentException("empty infixQ");
    }
    List<String> postfixQ = new ArrayList<>();
    //operator stack , a stack in which each item may be a left parenthesis or the symbol for an operation.
    Stack<String> stack = new Stack<String>();
    for (String token : infixQ) {
      //If (an operand is found) Add it to P
      if (isNumeric(token)) {
        postfixQ.add(token);
      } else if (token.equals("(")) {
        stack.push(token);
      } else if (token.equals(")")) {
        while (!stack.isEmpty() && !stack.peek().equals("(") && !stack.peek().equals(",")) {
          postfixQ.add(stack.pop());
        }
        if (stack.peek().equals(",")) {
          stack.pop();
          while (!stack.isEmpty() && !stack.peek().equals("(")) {
            postfixQ.add(stack.pop());
          }
        }
        //Pop the left parenthesis from the stack and discard it
        if (stack.isEmpty() || !stack.pop().equals("(")) {
          throw new IllegalArgumentException("The infixQ contained unbalanced parentheses ");
        }
      } else if (token.equals(",")) {
        while (!stack.isEmpty() && !stack.peek().equals("(") && !stack.peek().equals(",")) {
          postfixQ.add(stack.pop());
        }
        if (stack.peek().equals(",")) {
          stack.pop();
        }
        stack.push(token);
      } else if (isOperator(token)) {
        while (!stack.isEmpty() && !stack.peek().equals("(") && !stack.peek().equals(",") && precedence(stack.peek()) >= precedence(token)) {
          postfixQ.add(stack.pop());
        }
        stack.push(token);
      } else {
        throw new IllegalArgumentException("Invalid token found:" + token);
      }
    }

    while (!stack.isEmpty()) {
      String op = stack.pop();
      if (!op.equals("("))
        postfixQ.add(op);
      else
        throw new IllegalArgumentException("The infixQ contained unbalanced parentheses ");
    }
    return postfixQ;
  }

  private static boolean isOperator(String token) {
    return OPERATOR_MAP.containsKey(token);
  }

  private static boolean isSymbol(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '(' || c == ')' || c == ',';
  }

  /**
   * Evaluate a postfix queue
   *
   * @param postfixQ
   * @return
   */
  private Double evaluatePostfix(List<String> postfixQ) {
    if (postfixQ == null || postfixQ.isEmpty()) {
      throw new IllegalArgumentException("empty postfixQ");
    }
    //operand stack, a stack to hold the operands.
    Stack<Double> stack = new Stack<>();
    for (String token : postfixQ) {
      //If an operand is found push it onto the stack
      if (isNumeric(token)) {
        stack.push(Double.parseDouble(token));
      } else if (isOperator(token)) {
        //If an operator is found
        double v = stack.pop();
        switch (token) {
          case "+":
            v = stack.pop() + v;
            break;
          case "-":
            v = stack.pop() - v;
            break;
          case "*":
            v = stack.pop() * v;
            break;
          case "/":
            v = stack.pop() / v;
            break;
          case "^":
            v = Math.pow(stack.pop(), v);
            break;
          case "sqrt":
            v = Math.sqrt(v);
            break;
          case "sin":
            v = Math.sin(degreeToRadian(v));
            break;
          case "cos":
            v = Math.cos(degreeToRadian(v));
            break;
          case "log":
            v = Math.log(v);
            break;
          case "max":
            v = Math.max(stack.pop(), v);
            break;
          case "min":
            v = Math.min(stack.pop(), v);
            break;
          default:
            break;
        }
        stack.push(v);
      } else {
        throw new IllegalArgumentException(" invalid token found:" + token);
      }
    }
    //At the end, there should be only one element left on the stack.
    if (stack.size() != 1) {
      throw new IllegalArgumentException("Invalid postfix");
    }
    return stack.pop();
  }


  private int precedence(String token) {
//    System.out.println(token);
    return OPERATOR_MAP.get(token);
  }

  private static double degreeToRadian(double degree) {
    return degree / 180 * Math.PI;
  }

  private static boolean isNumeric(String token) {
    if (token == null) {
      return false;
    }
    try {
      Double.parseDouble(token);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  /**
   * match a number with optional '-' and decimal.
   */
  private static boolean isNumeric2(String str) {
    return str.matches("-?\\d+(\\.\\d+)?");
  }

  private double debug(String expression) {
    System.out.println("---expression---");
    System.out.println(expression);
    List<String> infixQ = tokenizeExpression(expression);
    System.out.println("---tokenize---");
    for (String token : infixQ) {
      System.out.print(token + " ");
    }
    System.out.println("\n---transform to postfix---");
    List<String> postfixQ = infixToPostfix(infixQ);
    for (String token : postfixQ) {
      System.out.print(token + " ");
    }
    System.out.println();
    return evaluatePostfix(postfixQ);
  }

  private static void simulate() {
    //    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    //    String line = br.readLine();
    Calculator cal = new Calculator();
    Scanner scanner = new Scanner(System.in, "UTF-8");
    while (scanner.hasNextLine()) {
      String expression = scanner.nextLine().trim();
      if (expression.length() == 0)
        continue;
      if (expression.equals("quit"))
        break;
      System.out.println(cal.evaluate(expression));
    }
  }

  public static void main(String[] args) {

//    simulate();
    testCorrect();
    test1();
//    test2();
    // testCorrect();
  }

  private static void test1() {

    String expression = "1+2*3+max(5+4+2*3,4+5*100)";
    //String expression = "3 4 2 * + 1 - ";
    // String expression = "3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3";

    Calculator cal = new Calculator();

    System.out.println("value:" + cal.debug(expression));
  }

  private static void test2() {
    List<String> expressions = new ArrayList<>();

    expressions.add("1+2*max(5,4)");
    // expressions.add("");
    Calculator cal = new Calculator();
    for (String expression : expressions) {
      System.out.println(expression + " = " + cal.evaluate(expression));
    }
  }

  private static void testCorrect() {
    Calculator cal = new Calculator();

    if (cal.evaluate("(8+9)^2+(8-4)/3") != (Math.pow(8 + 9, 2) + (8.0 - 4.0) / 3)) {
      throw new Error("false");
    }
    if (cal.evaluate("(2+sqrt(5+4))") != ((2 + Math.sqrt(5 + 4)))) {
      throw new Error("false");
    }
    if (cal.evaluate("2 + ( 3 + 4 ) * ( 5 * 6 )") != (2 + (3 + 4) * (5 * 6))) {
      throw new Error("false");
    }
    if (cal.evaluate("(1 + sqrt(5.0+4*3+sqrt(30*2+3)) ) / 2.0") != ((1 + Math.sqrt(5.0 + 4 * 3 + Math.sqrt(30 * 2 + 3))) / 2.0)) {
      throw new Error("false");
    }
    if (cal.evaluate("(1 + log( 5.0+4*3+sqrt(30*2+3) ) ) / 2.0") != ((1 + Math.log(5.0 + 4 * 3 + Math.sqrt(30 * 2 + 3))) / 2.0)) {
      throw new Error("false");
    }
    if (cal.evaluate("(1 + sqrt ( 5.0 ) ) / 2.0") != ((1 + Math.sqrt(5.0)) / 2.0)) {
      throw new Error("false");
    }
    if (cal.evaluate("6*sin(30)") != (6 * Math.sin(degreeToRadian(30)))) {
      throw new Error(cal.evaluate("6*sin(30)") + " = " + (6 * Math.sin(degreeToRadian(30))));
    }
    if (cal.evaluate("6*(5+4+3)") != (6 * (5 + 4 + 3))) {
      throw new Error("false");
    }
    if (cal.evaluate("2*(5+4+3+2*2*2+1)") != (2 * (5 + 4 + 3 + 2 * 2 * 2 + 1))) {
      throw new Error("false");
    }
    if (cal.evaluate("3 + 4 * 2 - 1") != (3 + 4 * 2 - 1)) {
      throw new Error("false");
    }
    if (cal.evaluate("3 + 4 * (2 - 1)") != (3 + 4 * (2 - 1))) {
      throw new Error("false");
    }

    if (cal.evaluate("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3") != (3 + 4 * 2 / Math.pow(Math.pow(1 - 5, 2), 3))) {
      throw new Error("false");
    }
    if (cal.evaluate("1 + 3 + 2 * 4  * (2 - 1)") != (1 + 3 + 2 * 4 * (2 - 1))) {
      throw new Error("false");
    }
    if (cal.evaluate("1+2*max(5,4)") != (1 + 2 * Math.max(5, 4))) {
      throw new Error("false");
    }

    if (cal.evaluate("1+2*3+max(5+4+2*3,4+5*100)") != (1 + 2 * 3 + Math.max(5 + 4 + 2 * 3, 4 + 5 * 100))) {
      throw new Error("false");
    }
    System.out.println("Pass!");

//    if (cal.evaluate("") != () ) {
//      throw new Error("false");
//    }
  }
}
