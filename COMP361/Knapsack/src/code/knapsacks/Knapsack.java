package code.knapsacks;

public abstract class Knapsack {
  Item[] items = {};
  int limit = 0;

  public Item[] getItems() {return items;}

  public int getLimit() {return limit;}

  public abstract void calculate(Item[] items, int limit);

  public abstract String analyse();
}
