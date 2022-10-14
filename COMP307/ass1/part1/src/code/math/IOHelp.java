package code.math;

import java.io.*;
import java.nio.file.*;

import java.util.Scanner;

import code.OrderMap;

import java.util.List;
import java.util.ArrayList;

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
  * Parses information from a file into a usable set of data
  *
  * @param filename the directory to read from
  * @param keyLen the number of datapoints before looking for classification
  *
  * @return A map of data to classification
  */
  public static OrderMap<double[], Integer> parseData(String filename, int keyLen) {
    List<String> lines = readAllLines(filename);
    OrderMap<double[], Integer> res = new OrderMap<double[], Integer>();

    for (String line : lines) {
      Scanner scan = new Scanner(line);
      if (scan.hasNextDouble()) {
        double[] dp = new double[keyLen];
        for(int i = 0; i < keyLen; i++) {
          dp[i] = scan.nextDouble();
        }
        res.put(dp, scan.nextInt());
      }
      scan.close();
    }
    return res;
  }
}
