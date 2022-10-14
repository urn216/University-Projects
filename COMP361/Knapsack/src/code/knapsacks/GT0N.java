package code.knapsacks;

import java.util.ArrayList;
import code.mergeSort;

import java.util.Arrays;
import java.util.List;

public class GT0N extends Knapsack {

  Item[] taking = {};
  int totVal = 0;
  int barometer = 0;
  int instances = 0;
  List<String> visited = new ArrayList<String>();

  public void calculate(Item[] items, int limit) {
    this.items = mergeSort.mSort(List.of(items), 0, items.length-1).toArray(new Item[items.length]);
    this.limit = limit;
    barometer = 0;
    instances = 0;
    for (Item i : items) {
      instances+=i.number;
    }

    int[] node = new int[items.length+2];
    Arrays.fill(node, 0);
    visited.clear();

    node = traverse(node);
    taking = new Item[items.length];
    for (int i = 0; i < node.length-2; i++) {
      taking[i] = new Item(this.items[i].weight, this.items[i].value, node[i]);
    }
    totVal = node[node.length-1];
    this.items = items;
    System.out.println("barometer: " + barometer);
  }

  public int[] traverse(int[] node) { // node is the number of items taken, with the weight of those items and the value of those items appended
    barometer++;
    if (visited.contains(Arrays.toString(node))) return node;
    visited.add(Arrays.toString(node));
    int[] bestNode = node;

    for (int i = 0; i < node.length-2; i++) {
      int[] newNode = new int[node.length];
      for (int j = 0; j < node.length; j++) newNode[j]=node[j];
      newNode[i]++;
      int newW = node[node.length-2] + items[i].weight;
      int newV = node[node.length-1] + items[i].value;

      if (
      newW > limit ||
      newNode[i]>items[i].number
      ) continue;

      newNode[node.length-2] = newW;
      newNode[node.length-1] = newV;

      int[] N = traverse(newNode);
      if (N[N.length-1] > bestNode[bestNode.length-1]) {
        bestNode = N;
      }
    }

    return bestNode;
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
