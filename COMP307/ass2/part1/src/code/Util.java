package code;

import java.util.Arrays;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

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

  public static List<String[]> getLines(String path) {
    List<String[]> rowList = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] lineItems = line.split(",");
        rowList.add(lineItems);
      }
    } catch (Exception e) {
      // Handle any I/O problems
      throw new Error(e);
    }

    return rowList;
  }

  public static double[][] getData(List<String[]> rows, boolean biases) {
    double[][] data = biases ? new double[rows.size()][rows.get(0).length] : new double[rows.size()][rows.get(0).length - 1];
    for (int i = 0; i < rows.size(); i++) {
      String[] row = rows.get(i);
      //exclude the label row!
      for (int j = 0; j < row.length - 1; j++) {
        data[i][j] = Double.parseDouble(row[j]);
      }
      if (biases) data[i][row.length-1] = 1.0;
    }
    return data;
  }

  public static String[] getLabels(List<String[]> rows) {
    String[] labels = new String[rows.size()];
    for (int i = 0; i < rows.size(); i++) {
      String[] row = rows.get(i);
      labels[i] = row[row.length - 1];
    }
    return labels;
  }
}
