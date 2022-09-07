// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/09/Fraction/Fraction.jack

/**
 * Represents the Fraction type and related operations.
 */
public class Fraction {
  private int numerator, denominator;

  /**
   * Constructs a (reduced) fraction from the given numerator and denominator.
   */
  public Fraction(int x, int y) {
    numerator = x;
    denominator = y;
    reduce();   // reduces the fraction
  }

  /**
   * Float to Fraction
   *
   * @param val
   */
  public Fraction(double val) {
    String str = String.valueOf(val);
    int dotIdx = str.indexOf('.');
    if (dotIdx == -1) {
      numerator = (int) val;
      denominator = 1;
    } else {
      int n = str.length() - dotIdx - 1;
      denominator = 1;
      double numeratorVal = val;
      for (int i = 0; i < n; i++) {
        numeratorVal *= 10;
        denominator *= 10;
      }
      numerator = (int) numeratorVal;
    }

    reduce();   // reduces the fraction
  }

  // Reduces this fraction.
  private void reduce() {
    int gcd = Fraction.gcd(numerator, denominator);
    if (gcd != 1) {
      numerator = numerator / gcd;
      denominator = denominator / gcd;
    }
  }

  /**
   * Accessors.
   */
  public int numerator() {
    return numerator;
  }

  public int denominator() {
    return denominator;
  }

  /**
   * Returns the sum of this fraction and the other one.
   */
  public Fraction plus(Fraction other) {
    int sum = (numerator * other.denominator) + (other.numerator * denominator);
    return new Fraction(sum, denominator * other.denominator);
  }

  public Fraction minus(Fraction other) {
    int sub = (numerator * other.denominator) - (other.numerator * denominator);
    return new Fraction(sub, denominator * other.denominator);
  }

  public Fraction multiply(Fraction other) {
    return new Fraction(numerator * other.numerator, denominator * other.denominator);
  }

  public Fraction divide(Fraction other) {
    return multiply(other.invert());
  }

  public Fraction invert() {
    return new Fraction(denominator, numerator);
  }


  public double toFloat() {
    return (double) numerator / denominator;
  }

  /**
   * Prints this fraction in the format x/y.
   */
  public String toString() {
    return numerator + "/" + denominator;
  }


  /**
   * Computes the greatest common divisor of the given integers.
   * <p>
   * Euclid's algorithm:
   * Let a = bq + r, where a, b, q, and r are integers. Then gcd(a, b) = gcd(b, r).
   */
  public static int gcd(int a, int b) {
    a = Math.abs(a);
    b = Math.abs(b);
    int r;
    while (b != 0) {
      r = a % b;  // r = remainder of the integer division a/b
      a = b;
      b = r;
    }
    return a;
  }

  public static void main(String[] args) {

    Fraction f1 = new Fraction(6, 10);
    System.out.println("f1=" + f1);

    Fraction f2 = new Fraction(-6, 10);
    System.out.println("f2=" + f2);

    Fraction f3 = new Fraction(4, 5);
    System.out.println("f3=" + f3);

    Fraction f4 = new Fraction(5, 10);
    System.out.println("f4=" + f4);

    Fraction f5 = new Fraction(0.5);
    System.out.println("f5=" + f5);

    Fraction f6 = new Fraction(3.14);
    System.out.println("f6=" + f6);

    Fraction f7 = new Fraction(-3.1400);
    System.out.println("f7=" + f7);
    System.out.println("f7=" + f7.toFloat());

    Fraction f8 = new Fraction(3);
    System.out.println("f8=" + f8);

    System.out.println("---------");
    System.out.printf("(%s) + (%s) = %s\n", f1, f2, f1.plus(f2));
    System.out.printf("(%s) - (%s) = %s\n", f3, f4, f3.minus(f4));
    System.out.printf("(%s) - (%s) = %s\n", f4, f3, f4.minus(f3));
    System.out.printf("(%s) * (%s) = %s\n", f3, f4, f3.multiply(f4));
    System.out.printf("(%s) * (%s) = %s\n", f2, f3, f2.multiply(f3));
    System.out.printf("(%s) / (%s) = %s\n", f3, f4, f3.divide(f4));


  }
}

