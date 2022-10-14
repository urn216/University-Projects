package code.knapsacks;

import java.util.Arrays;

public class DP01 extends Knapsack {
  int[][] array = {};

  public void calculate(Item[] items, int limit) {
    this.items = items;
    this.limit = limit;
    int barometer = 0;

    int[][] values = new int[items.length+1][limit+1];
    for (int[] line : values) Arrays.fill(line, 0);

    for (int item = 1; item < values.length; item++) {
      for (int weight = 0; weight < values[item].length; weight++) {
        barometer++;

        values[item][weight] = weight >= items[item-1].weight ? //if the available weight is enough to hold the new item
        Math.max(values[item-1][weight-items[item-1].weight] + items[item-1].value, values[item-1][weight]) : //we take the largest of either the best case without this item, or the best case without the weight of this item with this item's value added
        values[item-1][weight]; //else, we take the best case without this item
      }
    }

    array = values;
    System.out.println("barometer: " + barometer);
  }

  public String analyse() {
    String ans =
    "Items: " + Arrays.deepToString(items) + "\n" +
    "Number of items: " + items.length + "\n" +
    "Weight limit: " + limit + "\n\n";

    for (int i = 0; i < array.length; i++) {
      ans += "|";
      for (int j = 0; j < array[i].length; j++) {
        ans += array[i][j] + (array[i][j]<10 ? "  " : (array[i][j]<100 ? " " : "")) + "|";
      }
      ans += "\n";
    }

    ans += "\n" +
    "Best Value: " + array[items.length][limit] + "\n\n" +
    "Bag Contents: \n";
    int j = limit;
    for (int i = items.length; i > 0; i--) {
      if (array[i][j] == array[i-1][j]) continue;
      Item item = items[i-1];

      ans += " - Item " + i + ": " + item.toString() + "\n";

      j-=item.weight;
    }

    return ans;
  }
}
