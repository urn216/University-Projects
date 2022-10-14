package code.knapsacks;

public class Item implements Comparable<Item> {
  public final int weight;
  public final int value;
  public final int number;

  public Item(int weight, int value, int number) {
    this.weight = weight;
    this.value = value;
    this.number = number;
  }

  public String toString() {
    return number+"*( W: " + weight + ", V: " + value + ")";
  }

  public boolean equals(Item other) {
    return this.weight==other.weight && this.value==other.value;
  }

  @Override
  public int compareTo(Item other) {
    return ((Double)((1.0*value)/weight)).compareTo((Double)(1.0*other.value)/other.weight);
  }
}
