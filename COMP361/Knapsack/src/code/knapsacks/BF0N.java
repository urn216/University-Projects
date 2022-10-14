package code.knapsacks;


import code.mergeSort;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class BF0N extends Knapsack {

  Item[] taking = {};
  int totVal = 0;
  int instances = 0;

  public void calculate(Item[] items, int limit) {
    this.items = items;
    this.limit = limit;
    int barometer = 0;
    instances = 0;
    for (Item i : items) {
      instances+=i.number;
    }

    List<Item> sorted = mergeSort.mSort(List.of(items), 0, items.length-1);

    int weight = limit;
    List<Item> taking = new ArrayList<Item>();

    for (int i = 0; i < sorted.size(); i++) {
      barometer++;
      Item item = sorted.get(i);
      if (item.weight <= weight) {
        Item take = new Item(item.weight, item.value, Math.min(item.number, weight/item.weight));
        taking.add(take);
        weight-=take.weight*take.number;
      }
    }
    this.taking = taking.toArray(new Item[taking.size()]);
    for (Item take : taking) totVal+=take.value*take.number;
    System.out.println("barometer: " + barometer);
  }

  public String analyse() {
    String ans =
    this.getClass().getSimpleName() + "\n" +
    "Items: " + Arrays.deepToString(items) + "\n" +
    "Number of items: " + items.length + "\n" +
    "Number of instances: " + instances + "\n" +
    "Weight limit: " + limit + "\n\n";

    ans += "\n" +
    "Best Value: " + totVal + "\n\n" +
    "Bag Contents: \n";
    for (int i = 0; i < taking.length; i++) {
      for (int j = 0; j < items.length; j++) if (taking[i].equals(items[j]))
      ans += " - Item " + (j+1) + ": " + taking[i].toString() + "\n";
    }

    return ans;
  }
}
