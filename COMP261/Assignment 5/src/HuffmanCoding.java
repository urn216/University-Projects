import java.util.*;
import java.io.*;

/**
* A new instance of HuffmanCoding is created for every run. The constructor is
* passed the full text to be encoded or decoded, so this is a good place to
* construct the tree. You should store this tree in a field and then use it in
* the encode and decode methods.
*/
public class HuffmanCoding {

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Please call this program with two arguments, which are " +
      "the input file name and either 0 for constructing tree and printing it, or " +
      "1 for constructing the tree and encoding the file and printing it, or " +
      "2 for constructing the tree, encoding the file, and then decoding it and " +
      "printing the result which should be the same as the input file.");
    } else {
      try {
        Scanner s = new Scanner(new File(args[0]));

        // Read the entire file into one String.
        StringBuilder fileText = new StringBuilder();
        while (s.hasNextLine()) {
          fileText.append(s.nextLine() + "\n");
        }

        s.close();

        if (args[1].equals("0")) {
          System.out.println(constructTree(fileText.toString()));
        } else if (args[1].equals("1")) {
          constructTree(fileText.toString()); // initialises the tree field.
          System.out.println(encode(fileText.toString()));
        } else if (args[1].equals("2")) {
          constructTree(fileText.toString()); // initialises the tree field.
          String codedText = encode(fileText.toString());
          // DO NOT just change this code to simply print fileText.toString() back. ;-)
          System.out.println(decode(codedText));
        } else {
          System.out.println("Unknown second argument: should be 0 or 1 or 2");
        }
      } catch (FileNotFoundException e) {
        System.out.println("Unable to find file called " + args[0]);
      }
    }
  }

  private static Map<Character, String> dictionary;
  private static HuffmanTree tree;

  /**
  * This would be a good place to compute and store the tree.
  */
  public static Map<Character, String> constructTree(String text) {
    //construct priorityqueue
    char[] chars = text.toCharArray();
    PriorityQueue<HuffmanTree> queue = new PriorityQueue<HuffmanTree>();
    Map<Character, Integer> frequencies = new HashMap<Character, Integer>();
    for (char c : chars) {
      Integer freq = frequencies.putIfAbsent(c, 1);
      if (freq!=null) frequencies.replace(c, freq+1);
    }
    for (Character c : frequencies.keySet()) {
      queue.offer(new HuffmanTree(c, frequencies.get(c)));
    }

    //construct tree
    while (queue.size() > 1) {
      queue.offer(new HuffmanTree(queue.poll(), queue.poll()));
    }
    tree = queue.poll();

    dictionary = new HashMap<Character, String>();
    assignBin(tree, "");
    return dictionary;
  }

  public static void assignBin(HuffmanTree node, String s) {
    if (node == null) return;
    if (node.c!=null) {
      dictionary.put(node.c, s);
      return;
    }
    assignBin(node.zero, s+"0");
    assignBin(node.one, s+"1");
  }

  /**
  * Take an input string, text, and encode it with the tree computed from the text. Should
  * return the encoded text as a binary string, that is, a string containing
  * only 1 and 0.
  */
  public static String encode(String text) {
    char[] chars = text.toCharArray();
    String out = "";
    for (char c : chars) {
      out+=dictionary.get(c);
    }
    return out;
  }

  /**
  * Take encoded input as a binary string, decode it using the stored tree,
  * and return the decoded text as a text string.
  */
  public static String decode(String encoded) {
    char[] chars = encoded.toCharArray();
    String out = "";
    HuffmanTree node = tree;
    for (char c : chars) {
      if (c!='0' && c!='1') return "invalid binary input";
      node = c=='0' ? node.zero : node.one;
      if (node.c!=null) {
        out+=node.c;
        node = tree;
      }
    }
    return out;
  }
}

class HuffmanTree implements Comparable<HuffmanTree>{
  public final HuffmanTree zero;
  public final HuffmanTree one;
  public final Character c;
  public final int frequency;

  public HuffmanTree(HuffmanTree zero, HuffmanTree one) {
    this.zero = zero;
    this.one = one;
    this.c = null;
    this.frequency = zero.frequency+one.frequency;
  }

  public HuffmanTree(char c, int frequency) {
    this.zero = null;
    this.one = null;
    this.c = c;
    this.frequency = frequency;
  }

  public int compareTo(HuffmanTree other) {
    if (other.frequency==this.frequency) {
      return Character.compare(this.getC(), other.getC());
    }
    return this.frequency-other.frequency;
  }

  private char getC() {
    if (c!=null) return c;
    char o = zero.getC();
    char l = one.getC();
    return Character.compare(o, l) < 0 ? o : l;
  }
}
