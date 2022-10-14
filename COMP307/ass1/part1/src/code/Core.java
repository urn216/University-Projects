package code;

import code.math.IOHelp;
import code.math.MathHelp;

import java.util.List;
import java.util.ArrayList;

public class Core {

  public static final int K = 3;

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Must include training data and testing data.\nExiting...");
      return;
    }

    OrderMap<double[], Integer> trainingMap = IOHelp.parseData(args[0], 13);
    double[] ranges = train(trainingMap);

    test(IOHelp.parseData(args[1], 13), trainingMap, ranges);
  }

  public static double[] train(OrderMap<double[], Integer> data) {
    double[] ranges = new double[13];
    double[][] dps = data.getKeys().toArray(new double[0][0]);
    for (int i = 0; i < ranges.length; i++) {
      double[] col = new double[dps.length];
      for (int j = 0; j < dps.length; j++) col[j] = dps[j][i];
      ranges[i] = MathHelp.range(col);
    }

    return ranges;
  }

  public static void test(OrderMap<double[], Integer> testMap, OrderMap<double[], Integer> trainingMap, double[] ranges) {
    List<double[]> testSet = testMap.getKeys();

    String results = "";
    int totCorrect = 0;

    for (double[] p : testSet) {
      int expected = testMap.get(p);
      results += "Expected Class: " + expected + ", ";
      int estimated = kNearest(p, trainingMap, ranges);
      results += "Estimated Class: " + estimated + "\n";
      if (expected==estimated) totCorrect++;
    }

    double percentage = (100.0*totCorrect)/testSet.size();
    results+= "\nAccuracy for K = " + K + ": " + percentage + "%\n";

    System.out.print(results);
    IOHelp.saveToFile("output.txt", results);
  }

  public static int kNearest(double[] p, OrderMap<double[], Integer> trainingMap, double[] ranges) {
    List<Pair> nearest = new ArrayList<Pair>();
    for (double[] trainP : trainingMap.getKeys()) {
      nearest.add(new Pair(MathHelp.distance(p, trainP, ranges), trainingMap.get(trainP)));
    }
    nearest = Sorter.sort(nearest);

    int[] frequencies = {-1, 0, 0, 0};
    for (int i = 0; i < K; i++) {
      frequencies[nearest.get(i).b]++;
    }
    int bi = 0;
    int bf = -1;
    for (int i = 0; i < frequencies.length; i++) {
      if (frequencies[i] > bf) {
        bi = i;
        bf = frequencies[i];
      }
    }

    return bi;
  }
}

/**
* Helper class. Allows me to order by distance while keeping track of classifications
*/
class Pair implements Comparable<Pair>{
  public final Double a;
  public final int b;

  public Pair(double a, int b) {
    this.a = a;
    this.b = b;
  }

  public int compareTo(Pair other) {
    return a.compareTo(other.a);
  }
}
