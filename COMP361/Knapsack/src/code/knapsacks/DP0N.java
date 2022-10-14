package code.knapsacks;

import java.util.Arrays;

public class DP0N extends Knapsack {
  int[][] array = {};
  int instances = 0;

  public void calculate(Item[] items, int limit) {
    this.items = items;
    this.limit = limit;
    int barometer = 0;
    instances = 0;
    for (Item i : items) {
      instances+=i.number;
    }

    int[][] values = new int[instances+1][limit+1];
    for (int[] line : values) Arrays.fill(line, 0);
    int item = 0;
    int threshold = items[item].number;

    for (int instance = 1; instance < values.length; instance++) {
      if (instance > threshold) {
        item++;
        threshold+=items[item].number;
      }
      for (int weight = 0; weight < values[instance].length; weight++) {
        barometer++;

        values[instance][weight] = weight >= items[item].weight ? //if the available weight is enough to hold the new item
        Math.max(values[instance-1][weight-items[item].weight] + items[item].value, values[instance-1][weight]) : //we take the largest of either the best case without this item, or the best case without the weight of this item with this item's value added
        values[instance-1][weight]; //else, we take the best case without this item
      }
    }

    array = values;
    System.out.println("barometer: " + barometer);
  }

  public String analyse() {
    String ans =
    "Items: " + Arrays.deepToString(items) + "\n" +
    "Number of items: " + items.length + "\n" +
    "Number of instances: " + instances + "\n" +
    "Weight limit: " + limit + "\n\n";

    for (int i = 0; i < array.length; i++) {
      ans += "|";
      for (int j = 0; j < array[i].length; j++) {
        ans += array[i][j] + (array[i][j]<10 ? "  " : (array[i][j]<100 ? " " : "")) + "|";
      }
      ans += "\n";
    }

    ans += "\n" +
    "Best Value: " + array[array.length-1][limit] + "\n\n" +
    "Bag Contents: \n";
    int j = limit;
    int it = items.length-1;
    int threshold = instances-items[it].number;
    for (int i = array.length-1; i > 0; i--) {
      if (i <= threshold) {
        it--;
        threshold-=items[it].number;
      }
      if (array[i][j] == array[i-1][j]) continue;
      Item item = new Item(items[it].weight, items[it].value, 1);;

      ans += " - Item " + (it+1) + ": " + item.toString() + "\n";

      j-=item.weight;
    }

    return ans;
  }
}
