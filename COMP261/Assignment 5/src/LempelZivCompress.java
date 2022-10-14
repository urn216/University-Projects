import java.util.*;
import java.io.*;

public class LempelZivCompress {

  public static final int WINDOW = 100;

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Please call this program with one argument which is the input file name.");
    } else {
      try {
        Scanner s = new Scanner(new File(args[0]));

        // Read the entire file into one String.
        StringBuilder fileText = new StringBuilder();
        while (s.hasNextLine()) {
          fileText.append(s.nextLine() + "\n");
        }

        s.close();

        System.out.println(compress(fileText.toString()));
      } catch (FileNotFoundException e) {
        System.out.println("Unable to find file called " + args[0]);
      }
    }
  }

  /**
  * Take uncompressed input as a text string, compress it, and return it as a
  * text string.
  */
  public static String compress(String input) {
    Queue<Tuple> tuples = new ArrayDeque<Tuple>();
    char[] in = input.toCharArray();
    int inL = in.length;
    int i = 0;
    String out = "";
    while (i < inL) {
      i += findMatch(tuples, in, i);
      out += tuples.poll();
    }
    return out;
  }

  public static int findMatch(Queue<Tuple> tuples, char[] in, int i) {
    int length = 1;
    int offset = 0;
    List<Integer> matches = new ArrayList<Integer>();
    for (int j = Math.max(0, i-WINDOW); j < i; j++) {
      if (in[j]==in[i]) matches.add(j);
    }
    while (!matches.isEmpty() && i+length <= in.length) {
      if (i+length+1 >= in.length) break;
      offset = i-matches.get(0);
      length++;
      for (int j = matches.size()-1; j >= 0; j--) {
        if (matches.get(j)+length-1 >= i
        || in[matches.get(j)+length-1]!=in[i+length-1]) matches.remove(j);
      }
    }
    Tuple t = new Tuple(offset, length-1, in[i+length-1]);
    tuples.add(t);
    return length;
  }
}
