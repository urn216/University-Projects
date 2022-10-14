package code;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

import javax.imageio.ImageIO;

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
  public static boolean saveToFile(String filename, String content) {
    try {
      File f = new File(filename);
      f.createNewFile();
      PrintStream out = new PrintStream(f);
      out.print(content);
      out.close();
      return true;
    } catch(IOException e){System.err.println("Saving failed " + e);}
    return false;
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
  * Prints out an image to a png file.
  *
  * @param filename the directory to store the new file in
  * @param content the image to put into the file
  */
  public static boolean saveImage(String filename, BufferedImage content) {
    try {
      File f = new File(filename);
      f.createNewFile();
      return ImageIO.write(content, "png", f);
    } catch(IOException e){System.err.println("Saving failed " + e);}
    return false;
  }
}
