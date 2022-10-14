import java.util.*;
import java.io.*;

public class BM {

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Please call this program with two arguments which is the input file name and the string to search.");
    } else {
      try {
        Scanner s = new Scanner(new File(args[0]));

        // Read the entire file into one String.
        StringBuilder fileText = new StringBuilder();
        while (s.hasNextLine()) {
          fileText.append(s.nextLine() + "\n");
        }

        s.close();

        System.out.println(search(fileText.toString(), args[1]));
      } catch (FileNotFoundException e) {
        System.out.println("Unable to find file called " + args[0]);
      }
    }
  }

  private static int[] presuf;

  /**
  * Perform BM substring search on the given text with the given pattern.
  *
  * This should return the starting index of the first substring match if it
  * exists, or -1 if it doesn't.
  */
  public static int search(String text, String pattern) {

    int n = text.length();
    int m = pattern.length();
    char[] T = text.toCharArray();
    char[] S = pattern.toCharArray();

    presuf = new int[m];
    for (int q = 0; q < m; q++) presuf[q] = m;
    for (int p = 0; p < m; p++) {
      if (S[p]!=S[m-1-p]) break;
      presuf[p] = m-1-p;
    }

    int i = m-1;
    int k = 0;
    while (k+m < n) {
      if (S[i] == T[k+i]) {
        i--;
        if (i == 0) return k;
        continue;
      }
      int badChar = badChar(S, T, k, i);
      int goodSuf = goodSuf(S, m, i+1);
      k+=Math.max(badChar, goodSuf);
      i=m-1;
    }
    return -1;
  }

  public static int badChar(char[] S, char[] T, int k, int i) {
    int steps = 1;
    char match = T[k+i];
    for (int j = i-1; j >= 0; j--) {
      if (S[j] == match) break;
      steps++;
    }
    return steps;
  }

  public static int goodSuf(char[] S, int m, int i) {
    if (i==m) return 0;
    int minpre = Integer.MAX_VALUE;
    for (int p = m-i-1; p >= 0; p--) {
      if (presuf[p] < minpre) minpre = presuf[p];
    }
    String t = "";
    for (int p = i; p < m; p++) {t+=S[p];}
    int occur = KMP.search(new String(S), t);
    if (occur >= i || occur == -1) {return minpre;}
    return Math.min(minpre, i-occur);
  }
}
