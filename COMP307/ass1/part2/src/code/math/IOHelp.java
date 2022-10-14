package code.math;

import code.Instance;

import java.io.*;
import java.nio.file.*;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
* class helping do file stuff
*/
public class IOHelp {

  /**
  * Prints out a string to a file.
  *
  * @param filename the directory to store the new file in
  * @param content the string to put into the file
  */
  public static void saveToFile(String filename, String content) {
    try {
      File f = new File(filename);
      f.createNewFile();
      PrintStream out = new PrintStream(f);
      out.print(content);
      out.close();
    } catch(IOException e){System.err.println("Saving failed " + e);}
  }

  /**
  * Reads all the lines of a file into a list of strings.
  *
  * @param filename the directory to read the data from

  * @return a list of all the lines within the given file
  */
  public static List<String> readAllLines(String filename) {
    try {
      return Files.readAllLines(Paths.get(filename));
    } catch(IOException e){System.err.println("Reading failed: " + e);}
    return new ArrayList<String>();
  }

  /**
  * Parses data from a file into a usable set of training data for a decision tree generator.
  *
  * @param lines the list of strings to read the data from
  *
  * @return A list of lists of {Strings, Strings, and Instances} for categories, attributes, and instances
  */
  public static List<List<?>> parseTraining(List<String> lines) {
    Scanner scan = new Scanner(lines.get(0));
    scan.next(); // throw out 'Class'

    //find all the attributes
    List<String> attNames = new ArrayList<String>();
    while (scan.hasNext()) {
      attNames.add(scan.next());
    }
    scan.close();
    // System.out.print("Class ");
    // for (String s : attNames) System.out.print(s + " ");
    // System.out.println();

    //create all the instances
    List<Instance> allInstances = parseInstances(lines);

    //finds all the categories
    Set<String> categories = new HashSet<String>();
    for (Instance i : allInstances) {
      categories.add(i.getCategory());
    }
    List<String> categoryNames = List.copyOf(categories);

    return List.of(categoryNames, attNames, allInstances);
  }

  /**
  * Parses data from a file into a usable set of instances for use in a decision tree.
  *
  * @param lines the list of strings to read the data from
  */
  public static List<Instance> parseInstances(List<String> lines) {
    List<Instance> allInstances = new ArrayList<Instance>();
    for (int i = 1; i < lines.size(); i++) {
      Scanner scan = new Scanner(lines.get(i));
      String cat = scan.next();
      List<Boolean> vals = new ArrayList<Boolean>();
      while (scan.hasNextBoolean()) {
        vals.add(scan.nextBoolean());
      }
      scan.close();

      allInstances.add(new Instance(cat, vals));
      // System.out.println(allInstances.get(allInstances.size()-1));
    }
    return allInstances;
  }
}
