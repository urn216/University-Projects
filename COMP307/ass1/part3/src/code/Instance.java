package code;

import code.math.Vector;

/**
* Instance for use in training and testing a machine learning classifier
*/
public class Instance {
  /**this Instance's data*/
  public final Vector v;
  /**this Instance's classification*/
  public final String c;

  /**
  * Constructs a new instance for use in training and testing a machine learning classifier
  *
  * @param v the vector holding information about this instance
  * @param c the string to classify the instance with
  */
  public Instance(Vector v, String c) {
    this.v = v;
    this.c = c;
  }

  public String toString() {
    return v + " " + c;
  }
}
