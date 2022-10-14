package code;

import java.util.List;

/**
* An instance of data for use in decision tree categorisation
*/
public class Instance {
  private final String category;
  private final List<Boolean> vals;

  /**
  * Constructs an instance of data for use in decision tree categorisation
  *
  * @param category the category this instance belongs to
  * @param vals the list of attribute responses from this instance
  */
  public Instance(String category, List<Boolean> vals) {
    this.category = category;
    this.vals = vals;
  }

  /**
  * gets an attribute response from this instance
  *
  * @param index the index of this attribute response
  * @return the attribute response
  */
  public boolean getAtt(int index) {
    return vals.get(index);
  }

  /**
  * gets the category this instance belongs to.
  *
  * @return the category.
  */
  public String getCategory() {
    return category;
  }

  @Override
  public String toString() {
    String ans = category + " ";
    for (Boolean val : vals) {
      ans += val ? "true " : "false ";
    }
    return ans;
  }
}
