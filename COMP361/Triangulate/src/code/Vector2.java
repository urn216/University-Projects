package code;

public class Vector2 {
  public final double x, y;

  /**
  * Creates an immutable two dimensional vector
  *
  * @param x the first coordinate of the Vector2
  * @param y the second coordinate of the Vector2
  */
  public Vector2(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
  * Scales this Vector2 by values given in another Vector2
  *
  * @param o A Vector2 containing values to scale this Vector2 by
  *
  * @return A new scaled Vector2
  */
  public Vector2 scale(Vector2 o) {
    return new Vector2(x*o.x, y*o.y);
  }

  public String toString() {
    return "(" + x + ", " + y + ")";
  }

  /**
  * Finds the distance between two Vector2s
  *
  * @param v The first Vector2
  * @param w The second Vector2
  *
  * @return The distance between the two Vector2s
  */
  public static double dist(Vector2 v, Vector2 w) {
    return Math.sqrt(dist2(v, w));
  }

  /**
  * Finds the distance squared between two Vector2s
  *
  * @param v The first Vector2
  * @param w The second Vector2
  *
  * @return The distance squared between the two Vector2s
  */
  public static double dist2(Vector2 v, Vector2 w) {
    double i = w.x-v.x;
    double j = w.y-v.y;
    return i*i+j*j;
  }
}
