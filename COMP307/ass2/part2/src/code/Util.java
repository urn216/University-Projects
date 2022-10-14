package code;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util {

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
  public static double[][] parseData(List<String> lines) {
    int dimension = 0;
    Scanner scan = new Scanner(lines.get(0));
    while (scan.hasNext()) {scan.next(); dimension++;}
    scan.close();

    double[][] variables = new double[dimension][lines.size()-2];
    for (int i = 2; i < lines.size(); i++) {
      scan = new Scanner(lines.get(i));
      for (int j = 0; j < dimension; j++) variables[j][i-2] = scan.nextDouble();
      scan.close();
    }

    return variables;
  }
}
