import java.util.*;
import java.io.*;

public class KMP {

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

  /**
  * Perform KMP substring search on the given text with the given pattern.
  *
  * This should return the starting index of the first substring match if it
  * exists, or -1 if it doesn't.
  */
  public static int search(String text, String pattern) {
    //
    // System.out.println("Should be: " + text.indexOf(pattern));
    // System.out.print("And it is: ");

    int n = text.length();
    int m = pattern.length();
    char[] T = text.toCharArray();
    char[] S = pattern.toCharArray();
    int[] M = generateM(S, m);

    int k = 0;
    int i = 0;
    while (k + i < n) {
      if (S[i] == T[k+i]) {
        i++;
        if (i == m) return k;
      }
      else {
        k+=i-M[i];
        i = Math.max(0, M[i]);
      }
    }
    return -1;
  }

  /**
  * Generates a partial match table which allows fast looking through text to
  * find a desired string.
  */
  public static int[] generateM(char[] S, int m) {
    int[] M = new int[m];
    M[0] = -1;
    if (m==1) return M;
    M[1] = 0;

    int i = 2;
    int j = 0;
    while (i < m) {
      if (S[i-1] == S[j]) {
        M[i] = j+1;
        i++; j++;
      }
      else if (j > 0) j = M[j];
      else {
        M[i] = 0;
        i++;
      }
    }
    return M;
  }
}
