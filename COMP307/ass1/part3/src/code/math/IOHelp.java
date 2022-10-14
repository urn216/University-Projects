package code.math;

import code.Instance;

import java.io.*;
import java.nio.file.*;

import java.util.List;
import java.util.ArrayList;
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
  *
  * @return a list of all the lines within the given file
  */
  public static List<String> readAllLines(String filename) {
    try {
      return Files.readAllLines(Paths.get(filename));
    } catch(IOException e){System.err.println("Reading failed: " + e);}
    return new ArrayList<String>();
  }

  /**
  * Parses all the strings in a list into a list of Instances.
  *
  * @param lines the list of strings to parse.
  *
  * @return a list of all Instances present in a list of strings
  */
  public static List<Instance> parseData(List<String> lines) {
    int dimension = 0;
    Scanner scan = new Scanner(lines.get(0));
    while (!scan.next().equalsIgnoreCase("class")) dimension++;
    scan.close();

    List<Instance> instances = new ArrayList<Instance>();
    for (int i = 1; i < lines.size(); i++) {
      scan = new Scanner(lines.get(i));
      double[] v = new double[dimension+1];
      v[0] = 1; //bias
      for (int j = 1; j <= dimension; j++) v[j] = scan.nextDouble();
      instances.add(new Instance(new Vector(v), scan.next()));
      scan.close();
    }

    return instances;
  }
}
