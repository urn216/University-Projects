package code.math;

/**
* class helping do file stuff
*/
public class MathHelp {

  /**
  * Gives the total range of a set of doubles. (largest - smallest)
  *
  * @param data The data to get the range of
  *
  * @return The range of the data.
  */
  public static double range(double[] data) {
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    for (double d : data) {
      min = d < min ? d : min;
      max = d > max ? d : max;
    }
    return max - min;
  }

  /**
  * Gives a distance between two points in any dimensional space.<p>
  * Normalises measurements to give an equal-weighted distance.
  *
  * @param one The first point
  * @param two The second point.
  * @param ranges The range of values of each coordinate.
  *
  * @return The distance between two points.
  */
  public static double distance(double[] one, double[] two, double[] ranges) {
    if (one.length != two.length || one.length != ranges.length) return Double.NaN;

    double resSquare = 0;
    for (int i = 0; i < one.length; i++) {
      resSquare += (Math.pow(one[i]-two[i], 2)/Math.pow(ranges[i], 2));
    }

    return Math.sqrt(resSquare);
  }
}
