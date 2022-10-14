package code;

import code.math.IOHelp;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Core {
  private int numCategories;
  private List<String> categoryNames;
  private List<String> attNames;
  private List<Instance> allInstances;

  /**
  * Main method. parses data and which generates a tree from a training set, outputs it, and then tests its accuracy with a testing set
  *
  * @param args the <i>two</i> files to train and test from
  */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Must include training data and testing data.\nExiting...");
      return;
    }

    //setup training and test data, and generate the tree
    Core c = setup(args[0]);

    List<Instance> testInstances = IOHelp.parseInstances(IOHelp.readAllLines(args[1]));

    Tree tree = c.buildTree(c.allInstances, c.attNames);

    //gather results of our testing
    String t = "\nTree:\n~~~~~~~~~\n\n"
    + tree.toString()
    + "\n\nTesting:\n~~~~~~~~~\n\n"
    + testTree("DT Tests", tree, testInstances, c.attNames, true).get(0)
    + testTree("Baseline", c.mostProbable(c.allInstances), testInstances, c.attNames, false).get(0)
    + "\n";

    //now do k-fold cross-validation
    double avg = 0;
    for(int i = 0; i < 10; i++) {
      c = setup(args[0] + "-run-" + i);
      testInstances = IOHelp.parseInstances(IOHelp.readAllLines(args[1] + "-run-" + i));

      tree = c.buildTree(c.allInstances, c.attNames);
      List<Object> testRes = testTree("DT Run "+i, tree, testInstances, c.attNames, false);
      t += testRes.get(0);
      avg += (double)testRes.get(1);
    }

    t += "\n\nAverage accuracy from 10-fold cross-validation: " + String.format("%.2f", avg/10) + "%\n";

    //finally print out results
    System.out.print(t);
    IOHelp.saveToFile("output.txt", t);

  }

  /**
  * Parses data and creates a new Core object.
  *
  * @param filename the file to train from
  *
  * @return a new Core object
  */
  @SuppressWarnings("unchecked")
  public static Core setup(String filename) {
    List<List<?>> lists = IOHelp.parseTraining(IOHelp.readAllLines(filename));

    List<String> categoryNames = (List<String>)lists.get(0);
    List<String> attNames = (List<String>)lists.get(1);
    List<Instance> allInstances = (List<Instance>)lists.get(2);
    int numCategories = categoryNames.size();

    return new Core(numCategories, categoryNames, attNames, allInstances);
  }

  /**
  * Constructs our core object, initialising global variables
  *
  * @param numCategories the number of categories present in the data
  * @param categoryNames the names of all the categories
  * @param attNames the names of all the attributes to create the tree with
  * @param allInstances the list of Instances to create the tree with
  */
  public Core(int numCategories, List<String> categoryNames, List<String> attNames, List<Instance> allInstances) {
    this.numCategories = numCategories;
    this.categoryNames = categoryNames;
    this.attNames = attNames;
    this.allInstances = allInstances;
  }

  /**
  * Tests some data against the given tree to see how accurate a predictor the tree model is
  *
  * @param test the name of this test
  * @param tree the tree to test upon
  * @param instances the test data
  * @param attributes the list of attributes available for the test
  * @param printIndividual whether or not to print out the results for every instance
  *
  * @return a String representing output of the test, and a double representing the accuracy of the test
  */
  public static List<Object> testTree(String test, Tree tree, List<Instance> instances, List<String> attributes, boolean printIndividual) {
    String res = "";
    int correct = 0;
    for (Instance i : instances) {
      Tree current = tree;
      while (current instanceof Node) {
        current = i.getAtt(attributes.indexOf(((Node)current).attribute)) ? ((Node)current).left : ((Node)current).right;
      }
      if (printIndividual) res += ((Leaf)current).toString() + ", Actual: { " + i + "}\n";
      if (((Leaf)current).category.equals(i.getCategory())) correct++;
    }

    double accuracy = (100.0*correct)/instances.size();
    res += "\n" + test + " Accuracy: " + String.format("%.2f", accuracy) + "%";

    return List.of(res, accuracy);
  }

  /**
  * Builds a tree recursively out of a given set of instances and attributes.<p>
  * Looks for the most pure decision to divide the data, and creates two new branches stemming from the node.<p>
  * If the data is as pure as it can be, we instead return a leaf node, stating the final decision and probability of said decision.
  *
  * @param instances the list of instances to organise
  * @param attributes the list of attributes to divide the instances with
  *
  * @return a Tree object containing sorted instances
  */
  public Tree buildTree(List<Instance> instances, List<String> attributes) {

    //base cases. if any of these are true, we return a leaf node instead of looking at attributes
    if (instances.isEmpty()) return mostProbable(allInstances); //most probable world scope
    if (isPure(instances)) return new Leaf(instances.get(0).getCategory(), 1); //100% guarantee based on training data
    if (attributes.isEmpty()) return mostProbable(instances); //most probable local scope

    //keep track of best attribute data
    double bestImpurity = 1;
    String bestAtt = null;
    List<Instance> bestTrue = null;
    List<Instance> bestFalse = null;

    //find the best attribute to split on
    for (int i = 0; i < attNames.size(); i++) {
      if (!attributes.contains(attNames.get(i))) continue; //cannot use attributes we don't have access to
      List<Instance> trues = new ArrayList<Instance>();
      List<Instance> falses = new ArrayList<Instance>();

      //splits the instances on this attribute
      for (Instance ins : instances) {
        if (ins.getAtt(i)) trues.add(ins);
        else {falses.add(ins);}
      }

      //finds the impurity score of this split
      double imp = getWeightedAvgImpurity(trues, falses);
      //if it's the best split so far, we keep track of it
      if (imp < bestImpurity) {
        bestImpurity = imp;
        bestAtt = attNames.get(i);
        bestTrue = trues;
        bestFalse = falses;
      }
    }
    //we have found the best attribute to split on.

    //create list of attributes minus the one we're using now to give new nodes
    List<String> newAtts = new ArrayList<String>();
    newAtts.addAll(attributes);
    newAtts.remove(bestAtt);

    //recursively split into two new branches
    Tree left = buildTree(bestTrue, newAtts);
    Tree right = buildTree(bestFalse, newAtts);
    return new Node(bestAtt, left, right);
  }

  /**
  * Checks to see if a list of instances is 'pure', meaning all instances fit the same category
  *
  * @param instances the list of instances to check
  *
  * @return true if the list is pure
  */
  public boolean isPure(List<Instance> instances) {
    String cat = null;
    for (Instance i : instances) {
      if (cat == null) {cat = i.getCategory();}
      if (!i.getCategory().equals(cat)) return false;
    }
    return true;
  }

  /**
  * gets a weighted 'impurity' score between two lists of instances
  *
  * @param insT the first list of instances
  * @param insF the second list of instances
  *
  * @return the weighted average impurity score of the two lists
  */
  public double getWeightedAvgImpurity(List<Instance>insT, List<Instance> insF) {
    double dT = (1.0*insT.size())/(insT.size()+insF.size());
    double dF = 1.0-dT;

    return dT*getImpurity(insT)+dF*getImpurity(insF);
  }

  /**
  * Gets an 'impurity' score from a list of instances.<p>
  * Uses standard measure if two categories present.<p>
  * Uses gini measure if more categories present.
  *
  * @param ins the list of instances
  *
  * @return the impurity score of the list
  */
  public double getImpurity(List<Instance> ins) {
    if (ins.size() == 0) return 0;
    double tot = ins.size();
    int catots[] = new int[numCategories];
    Arrays.fill(catots, 0);

    //tallies up the frequency of categories
    for (Instance i : ins) {
      catots[categoryNames.indexOf(i.getCategory())]++;
    }

    double res = 1.0;
    if (numCategories == 2) for (int catot : catots) res*=(catot/tot); //Standard impurity
    else for (int catot : catots) res-=((catot/tot)*(catot/tot)); //Gini impurity

    return res;
  }

  /**
  * Creates a leaf node for the most probable outcome of a given list of instances, and finds the probability.
  *
  * @param instances the list of instances to put into the leaf.
  * @return a leaf node.
  *
  */
  public Leaf mostProbable(List<Instance> instances) {
    double tot = instances.size();
    int catots[] = new int[numCategories];
    Arrays.fill(catots, 0);

    //tallies up the frequency of categories
    for (Instance i : instances) {
      catots[categoryNames.indexOf(i.getCategory())]++;
    }

    int bestot = 0;
    int bestci = 0;
    //finds the highest frequency category
    for (int i = 0; i < numCategories; i++) {
      if (catots[i] >= bestot) {bestot = catots[i]; bestci = i;}
    }

    return new Leaf(categoryNames.get(bestci), bestot/tot);
  }
}

/**
* A generic node for use in a binary decision tree
*/
interface Tree {

  /**
  * Returns a string representation of the object. Adds a prefix
  *
  * @param prefix a substring to add to the front end of the returned string.
  *
  * @return a string representation of the object.
  */
  public String toString(String prefix);
}

/**
* A binary node for use in a binary decision tree
*/
class Node implements Tree {
  public final String attribute;
  public final Tree left;
  public final Tree right;

  /**
  * Constructs a binary node for use in a binary decision tree.
  *
  * @param attribute the feature to use in decision-making.
  * @param left the result if the decision is 'true'.
  * @param right the result if the decision is 'false'.
  */
  public Node(String attribute, Tree left, Tree right) {
    this.attribute = attribute;
    this.left = left;
    this.right = right;
  }

  @Override
  public String toString() {
    return toString("");
  }

  @Override
  public String toString(String prefix) {
    return ""
    + prefix + attribute + " = True\n"
    + left.toString(prefix+"|   ") + "\n"
    + prefix + attribute + " = False\n"
    + right.toString(prefix+"|   ");
  }
}

/**
* A leaf node for use in a binary decision tree
*/
class Leaf implements Tree {
  public final String category;
  public final double probability;

  /**
  * Constructs a leaf node for use in a binary decision tree.
  *
  * @param category the classification of an instance after having reached this point
  * @param probability the probability that this classification is correct
  */
  public Leaf(String category, double probability) {
    this.category = category;
    this.probability = probability;
  }

  @Override
  public String toString() {
    return toString("");
  }

  @Override
  public String toString(String prefix) {
    return prefix + "Class " + category + ", prob = " + probability;
  }
}
