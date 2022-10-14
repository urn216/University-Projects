package code.math;

public class Vector {
  /**this vector's values*/
  public final double[] vals;
  /**this vector's length*/
  public final int length;

  /**
  * Constructs a vector.
  *
  * @param vals the values to put into this vector
  */
  public Vector(double... vals) {
    this.vals = vals;
    this.length = vals.length;
  }

  /**
  * Returns the dot product of two vectors of the same size.<p>
  * This is done by multiplying each element of one vector with its corresponding counterpart, and then summing the products.
  *
  * @param o the other vector to dot against this one
  *
  * @return the dot product
  */
  public double dot(Vector o) {
    if (o.length != length) return Double.NaN;
    double tot = 0;
    for (int i = 0; i < length; i++) tot+=vals[i]*o.vals[i];
    return tot;
  }

  /**
  * Returns a new vector obtained by the sum of two vectors of the same size.
  *
  * @param o the other vector to add to this one
  *
  * @return a new vector
  */
  public Vector add(Vector o) {
    if (o.length != length) return null;
    double[] v = new double[length];
    for (int i = 0; i < length; i++) v[i]=vals[i]+o.vals[i];
    return new Vector(v);
  }

  /**
  * Returns a new vector obtained by scaling this vector by a double.
  *
  * @param s the amount to scale by
  *
  * @return a new scaled vector
  */
  public Vector scale(double s) {
    double[] v = new double[length];
    for (int i = 0; i < length; i++) v[i]=vals[i]*s;
    return new Vector(v);
  }

  public String toString() {
    String res = "(";
    for (int i = 0; i < length-1; i++) res += vals[i] + ", ";
    res += vals[length-1] + ")";
    return res;
  }
}
