import java.util.*;
import java.io.*;

public class LempelZivDecompress {

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

        System.out.println(decompress(fileText.toString()));
      } catch (FileNotFoundException e) {
        System.out.println("Unable to find file called " + args[0]);
      }
    }
  }

  private static int length = 0;

  /**
  * Take compressed input as a text string, decompress it, and return it as a
  * text string.
  */
  public static String decompress(String compressed) {
    length = 0;
    List<Tuple> tuples = getTuples(compressed);
    char[] out = new char[length];
    int i = 0;
    for (Tuple tuple : tuples) {
      for (int j = 0; j < tuple.length; j++) {
        out[i++] = out[i-tuple.offset-1];
      }
      out[i++] = tuple.ch;
    }
    return new String(out);
  }

  public static List<Tuple> getTuples(String compressed) {
    List<Tuple> tuples = new ArrayList<Tuple>();
    char[] in = compressed.toCharArray();
    int i = 0;
    int l = in.length;
    while (i < l) {
      //get offset
      i++;
      if (in[i-1]!='[') continue;
      String ofs = "";
      while (i < l && in[i]!='|') {
        ofs+=in[i];
        i++;
      }
      //get length
      i++;
      if (in[i-1]!='|') break;
      String lth = "";
      while (i < l && in[i]!='|') {
        lth+=in[i];
        i++;
      }
      //get next char
      i++;
      if (in[i-1]!='|') break;
      char c = in[i];
      i+=2;
      if (in[i-1]!=']') break;

      //create tuple
      int lLength = Integer.parseInt(lth);
      tuples.add(new Tuple(Integer.parseInt(ofs), lLength, c));
      length+=lLength+1;
    }
    return tuples;
  }
}
