public class KeyIndexedCounting {
  private static void sort(char[] a) {
    int R = 256;
    int n = a.length;
    char[] aux = new char[n];
    int[] count = new int[R + 1];
    for (int i = 0; i < n; i++) {
      count[a[i] + 1]++;
    }
    for (int i = 0; i < R; i++) {
      count[i + 1] += count[i];
    }
    for (int i = 0; i < n; i++) {
      aux[count[a[i]]++] = a[i];
    }
    for (int i = 0; i < n; i++) {
      a[i] = aux[i];
    }
  }

  public static void main(String[] args) {
    int R = 6;
    char[] a = {'b', 'e', 'e', 'h', 'b', 'f', 'd', 'g', 'a', 'c', 'a', 'b'};
    sort(a);
    for (char c : a) {
      System.out.print(c + " ");
    }
    System.out.println();
  }
}
