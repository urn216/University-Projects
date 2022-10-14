package code;

import java.util.Arrays;
import code.math.Vector;
import code.math.IOHelp;

import java.util.List;

public class Core {

  /**
  * Main method. Constructs a perceptron and trains it until satisfactory.
  *
  * @param args the <i>one</i> file to train and test from, or two if you so desire
  */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Must include data.\nExiting...");
      return;
    }


    //create the training (and testing) data
    List<Instance> instances = IOHelp.parseData(IOHelp.readAllLines(args[0]));

    List<Instance> testIns = args.length > 1 ? IOHelp.parseData(IOHelp.readAllLines(args[1])) : instances;

    //create the perceptron
    double[] w = new double[instances.get(0).v.length];
    Arrays.fill(w, 0);
    Perceptron p = new Perceptron(new Vector(w), "g", 0.2);
    double accuracy = 0;
    int iterations = 0;

    //train the perceptron
    while (accuracy < 95 && iterations < 10000) {
      iterations++;
      //train against all training data
      for (int i = 0; i < instances.size(); i++) {
        p.train(instances.get(i));

        //every time we train, test it against the testing data
        double correct = 0;
        for (Instance t : testIns) {
          if (p.test(t)) correct++;
        }
        accuracy = 100*correct/testIns.size();
        //if we get an awesome result, stop training
        if (accuracy > 95) break;
      }
      System.out.printf("Iteration %d: %.2f\n", iterations, accuracy);
    }

    //generate output
    String s = "It took " + iterations + " cycles through the dataset,\n"
    + "with " + p.getIter() + " total training calls on the Perceptron\n"
    + "to reach an accuracy of " + String.format("%.2f", accuracy) + "%.\n"
    + "\n"
    + "This leaves " + (int)(testIns.size()*(1-accuracy/100)) + " of the " + testIns.size() + " total test instances estimated incorrectly\n"
    + "\n"
    + "The weights the Perceptron used to reach this accuracy were\n"
    + p.getWeights();
    System.out.println(s);
    IOHelp.saveToFile("output.txt", s);
  }
}

/**
* Perceptron with the ability to learn how to turn a given input into a desired output.
*/
class Perceptron {
  private Vector w;
  private int iterations;
  public final String positive;
  public final double learningRate;

  /**
  * Constructs a Perceptron, with a set of weights, giving it the ability to learn how to turn a given input into a desired output.
  *
  * @param w the weights applied to inputs
  * @param positive the "correct" answer to check against
  * @param learningRate the scale at which the weights are shifted every training call
  */
  public Perceptron(Vector w, String positive, double learningRate) {
    this.w = w;
    iterations = 0;
    this.positive = positive;
    this.learningRate = learningRate;
  }

  /**
  * Trains the perceptron. Takes in data, tests it, and adjusts the weights accordingly if it is incorrect.
  *
  * @param i the instance to train against
  */
  public void train(Instance i) {
    iterations++;

    if (test(i)) return;
    double scale = i.c.equals(positive) ? learningRate : -learningRate;
    w = w.add(i.v.scale(scale));
  }

  /**
  * Tests the perceptron. Takes in data, tests it, and returns whether or not it was correct.
  *
  * @param i the instance to test against
  *
  * @return true if the result matches the expected output
  */
  public boolean test(Instance i) {
    if (w.dot(i.v) > 0) return i.c.equals(positive);
    return !i.c.equals(positive);
  }

  /**@return the number of training calls made*/
  public int getIter() {return iterations;}

  /**@return the current weights*/
  public Vector getWeights() {return w;}

}
