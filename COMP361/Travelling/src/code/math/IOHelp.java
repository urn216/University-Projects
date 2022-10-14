package code.math;

import java.util.Scanner;
import java.io.*;
import java.nio.file.*;

import java.util.List;
import java.util.ArrayList;

/**
* class helping do file stuff
*/
public abstract class IOHelp {
  public static void saveToFile(String filename, String content) {
    try {
      File f = new File(filename);
      f.createNewFile();
      PrintStream out = new PrintStream(f);
      out.print(content);
      out.close();
    } catch(IOException e){System.err.println("Saving failed " + e);}
  }

  public static List<String> readAllLines(String filename) {
    try {
      return Files.readAllLines(Paths.get(filename));
    } catch(IOException e){System.err.println("Reading failed: " + e);}
    return new ArrayList<String>();
  }

  public static List<Vector2> parsePoints(String filename) {
    List<String> lines = readAllLines(filename);
    List<Vector2> res = new ArrayList<Vector2>();

    for (String line : lines) {
      Scanner scan = new Scanner(line);
      if (scan.hasNextInt()) {
        scan.next();
        res.add(new Vector2(scan.nextInt(), scan.nextInt()));
      }
      scan.close();
    }

    System.out.println("Number of points parsed: " + res.size());

    return res;
  }
}
