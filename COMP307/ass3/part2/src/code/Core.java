package code;

public abstract class Core {
  public static final Label[] labels = createLabels();


  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Must include training data and testing data.\nExiting...");
      return;
    }

    String res = "Training...\n\n";

    train(Util.parseData(Util.readAllLines(args[0])));

    res +=
    "        , "+labels[0].name+", "+labels[1].name+"\n" +
    "P(Class), "+labels[0].prob+", "+labels[1].prob+"\n";
    for (int X = 0; X < labels[0].features.length; X++) {
      for (int x = 0; x < labels[0].features[X].values.length; x++) {
        res+="P("+labels[0].features[X].name+" = "+labels[0].features[X].values[x].name+" | Class), "
        + labels[0].features[X].values[x].prob + ", " + labels[1].features[X].values[x].prob + "\n";
      }
    }

    res+="\nTesting...\n\n";

    int i = 0;
    double acc = 0;
    for (String[] instance : Util.parseData(Util.readAllLines(args[1]))) {
      res+="Instance "+i+":\n";
      double best = 0;
      Label bestY = null;
      for (Label y : labels) {
        double score = test(instance, y);
        res+="    Score for " + y.name + ": " + score + "\n";
        if (score>best) {best = score; bestY = y;}
      }
      res+="    Chosen class: " + bestY.name + ". ";
      if (bestY.name.equals(instance[1])) {res+="Correct!\n"; acc++;}
      else {res+="Incorrect...\n";}
      i++;
    }

    res += "\nAccuracy: "+String.format("%.2f", (acc/i)*100) + "%\n";

    System.out.print(res);
    Util.saveToFile("output.txt", res);
  }

  private static void train(String[][] instances) {
    // Count the numbers of each class and feature value based on the training instances.
    for (String[] instance : instances) {
      Label y = null;
      for (Label l : labels) if(l.name.equals(instance[1])) {y = l; y.count++; break;}
      for (int i = 2; i < instance.length; i++) {
        for (Value x : y.features[i-2].values) if(x.name.equals(instance[i])) {x.count++; break;}
      }
    }

    // Calculate the total/denominators.
    int classTotal = 0;
    for (Label y : labels) {
      classTotal += y.count;
      for (Feature X : y.features) {
        for (Value x : X.values) {
          X.total += x.count;
        }
      }
    }

    // Calculate the probabilities from the counting numbers.
    for (Label y : labels) {
      y.prob = (1.0*y.count)/classTotal;
      for (Feature X : y.features) {
        for (Value x : X.values) {
          x.prob = (1.0*x.count)/X.total;
        }
      }
    }
  }

  private static double test(String[] instance, Label y) {
    double score = y.prob;
    for (int i = 2; i < instance.length; i++) {
      for (Value x : y.features[i-2].values) if(x.name.equals(instance[i])) {score *= x.prob; break;}
    }
    return score;
  }

  private static Label[] createLabels() {
    Label[] res = {new Label("no-recurrence-events", createFeatures()), new Label("recurrence-events", createFeatures())};
    return res;
  }

  private static Feature[] createFeatures() {
    Feature[] features = {
      new Feature("age", new Value("10-19"), new Value("20-29"), new Value("30-39"), new Value("40-49"), new Value("50-59"), new Value("60-69"), new Value("70-79"), new Value("80-89"), new Value("90-99")),
      new Feature("menopause", new Value("lt40"), new Value("ge40"), new Value("premeno")),
      new Feature("tumor-size", new Value("0-4"), new Value("5-9"), new Value("10-14"), new Value("15-19"), new Value("20-24"), new Value("25-29"), new Value("30-34"), new Value("35-39"), new Value("40-44"), new Value("45-49"), new Value("50-54"), new Value("55-59")),
      new Feature("inv-nodes", new Value("0-2"), new Value("3-5"), new Value("6-8"), new Value("9-11"), new Value("12-14"), new Value("15-17"), new Value("18-20"), new Value("21-23"), new Value("24-26"), new Value("27-29"), new Value("30-32"), new Value("33-35"), new Value("36-39")),
      new Feature("node-caps", new Value("yes"), new Value("no")),
      new Feature("deg-malig", new Value("1"), new Value("2"), new Value("3")),
      new Feature("breast", new Value("left"), new Value("right")),
      new Feature("breast-quad", new Value("left_up"), new Value("left_low"), new Value("right_up"), new Value("right_low"), new Value("central")),
      new Feature("irradiat", new Value("yes"), new Value("no"))
    };
    return features;
  }
}

class Label {
  final String name;
  final Feature[] features;

  int count = 1;
  double prob = 0;

  public Label(String name, Feature... features) {
    this.name = name;
    this.features = features;
  }
}

class Feature implements Cloneable {
  final String name;
  final Value[] values;

  int total = 0;

  public Feature(String name, Value... values) {
    this.name = name;
    this.values = values;
  }

  @Override
  public Feature clone() {
    Value[] newValues = values.clone();
    for (int i = 0; i < newValues.length; i++) newValues[i]=newValues[i].clone();
    return new Feature(name, newValues);
  }
}

class Value implements Cloneable {
  final String name;

  int count = 1;
  double prob = 0;

  public Value(String name) {
    this.name = name;
  }

  @Override
  public Value clone() {
    return new Value(name);
  }
}
