import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * % java-algs4 MoveToFront - < abra.txt | java-algs4 edu.princeton.cs.algs4.HexDump 16
 * <p>
 * % java-algs4 MoveToFront - < abra.txt | java-algs4 MoveToFront +
 */
public class MoveToFront {
  private static final int R = 256;


  // apply move-to-front encoding, reading from standard input and writing to standard output
  public static void encode() {
    char[] aux = new char[R];
    for (int i = 0; i < R; i++)
      aux[i] = (char) i;

//    String input = BinaryStdIn.readString();
    while (!BinaryStdIn.isEmpty()) {
      char c = BinaryStdIn.readChar();
      BinaryStdOut.write((char) moveCharToFront(aux, c));
    }

    BinaryStdOut.close();
  }

  // apply move-to-front decoding, reading from standard input and writing to standard output
  public static void decode() {
    char[] aux = new char[R];
    for (int i = 0; i < R; i++)
      aux[i] = (char) i;

    while (!BinaryStdIn.isEmpty()) {
      int pos = BinaryStdIn.readChar();
      BinaryStdOut.write(aux[pos]);
      moveCharAtPosToFront(aux, pos);
    }
    BinaryStdOut.close();
  }

  private static void moveCharAtPosToFront(char[] a, int pos) {
    char c = a[pos];
    for (int i = pos; i > 0; i--) {
      a[i] = a[i - 1];
    }
    a[0] = c;
  }

  private static int moveCharToFront(char[] aux, char c) {
    char pre = aux[0];
    for (int i = 0; i < R; i++) {
      if (aux[i] == c) {
        aux[0] = c;
        aux[i] = pre;
        return i;
      }
      char tmp = aux[i];
      aux[i] = pre;
      pre = tmp;
    }
    throw new IllegalArgumentException(c + " is not in the character array");
  }

  // if args[0] is "-", apply move-to-front encoding
  // if args[0] is "+", apply move-to-front decoding
  public static void main(String[] args) {
    if (args[0].equals("-")) encode();
    else if (args[0].equals("+")) decode();
    else throw new IllegalArgumentException("Illegal command line argument");
  }
}
