package code;

import java.util.Arrays;
import java.util.List;

public class Core {

  public static void main(String[] _ignored) {
    StringBuilder string = new StringBuilder();
    StringBuilder dud = new StringBuilder();
    string.append("Part 1:\n\n");
    runSuite(string, false, 100, 0.2);
    string.append("\n\nPart 1.1:\n\n");
    runSuite(string, true, 100, 0.2);
    string.append(
    "\n\nPart 1.2:\n\n" +
    "First Element to alter: Epochs\n" +
    "  0   Epochs Accuracy: " + String.format("%.2f", runSuite(dud, false, 0  , 0.2)*100) + "%\n" +
    "  50  Epochs Accuracy: " + String.format("%.2f", runSuite(dud, false, 50 , 0.2)*100) + "%\n" +
    "  100 Epochs Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 0.2)*100) + "%\n" +
    "  200 Epochs Accuracy: " + String.format("%.2f", runSuite(dud, false, 200, 0.2)*100) + "%\n" +
    "  300 Epochs Accuracy: " + String.format("%.2f", runSuite(dud, false, 300, 0.2)*100) + "%\n" +
    "  400 Epochs Accuracy: " + String.format("%.2f", runSuite(dud, false, 400, 0.2)*100) + "%\n" +
    "  500 Epochs Accuracy: " + String.format("%.2f", runSuite(dud, false, 500, 0.2)*100) + "%\n" +
    "  600 Epochs Accuracy: " + String.format("%.2f", runSuite(dud, false, 600, 0.2)*100) + "%\n" +
    "Second Element to alter: Learning Rate\n" +
    "  0.00 LearnR Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 0.0)*100) + "%\n" +
    "  0.10 LearnR Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 0.1)*100) + "%\n" +
    "  0.20 LearnR Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 0.2)*100) + "%\n" +
    "  0.30 LearnR Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 0.3)*100) + "%\n" +
    "  0.40 LearnR Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 0.4)*100) + "%\n" +
    "  0.50 LearnR Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 0.5)*100) + "%\n" +
    "  1.00 LearnR Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 1.0)*100) + "%\n" +
    "  5.00 LearnR Accuracy: " + String.format("%.2f", runSuite(dud, false, 100, 5.0)*100) + "%\n");

    String res = string.toString();
    System.out.println(res);
    Util.saveToFile("output.txt", res);
  }

  public static double runSuite(StringBuilder string, boolean biases, int numEps, double learning_rate) {
    List<String[]> lines = Util.getLines("penguins307-train.csv");
    String[] header = lines.remove(0);
    String[] labels = Util.getLabels(lines);
    double[][] instances = Util.getData(lines, biases);

    // scale features to [0,1] to improve training
    Rescaler rescaler = new Rescaler(instances);
    rescaler.rescaleData(instances, biases);
    System.out.println(Arrays.deepToString(instances));

    // We can"t use strings as labels directly in the network, so need to do some transformations
    LabelEncoder label_encoder = new LabelEncoder(labels);
    // encode "Adelie" as 1, "Chinstrap" as 2, "Gentoo" as 3
    int[] integer_encoded = label_encoder.intEncode(labels);

    // encode 1 as [1, 0, 0], 2 as [0, 1, 0], and 3 as [0, 0, 1] (to fit with our network outputs!)
    int[][] onehot_encoded = label_encoder.oneHotEncode(labels);

    // Parameters. As per the handout.
    int n_in = 4, n_hidden = 2, n_out = 3;
    if (biases) {n_in++; n_hidden++;}

    double[][] initial_hidden_layer_weights = biases ?
    new double[][]{{-0.28, -0.22, 0}, {0.08, 0.20, 0}, {-0.30, 0.32, 0}, {0.10, 0.01, 0}, {-0.02, -0.20, 0}} :
    new double[][]{{-0.28, -0.22}, {0.08, 0.20}, {-0.30, 0.32}, {0.10, 0.01}};
    double[][] initial_output_layer_weights = biases ?
    new double[][]{{-0.29, 0.03, 0.21}, {0.08, 0.13, -0.36}, {-0.33, 0.26, 0.06}} :
    new double[][]{{-0.29, 0.03, 0.21}, {0.08, 0.13, -0.36}};

    NeuralNetwork nn = new NeuralNetwork(n_in, n_hidden, n_out, initial_hidden_layer_weights, initial_output_layer_weights, learning_rate, biases);

    string.append(String.format("First instance has label %s, which is %d as an integer, and %s as a list of outputs.\n\n",
    labels[0], integer_encoded[0], Arrays.toString(onehot_encoded[0])));

    // need to wrap it into a 2D array
    int[] instance1_prediction = nn.predict(new double[][]{instances[0]}, true, string);
    String instance1_predicted_label;
    if (instance1_prediction[0] == -1) {
      // This should never happen once you have implemented the feedforward.
      instance1_predicted_label = "???";
    } else {
      instance1_predicted_label = label_encoder.inverse_transform(instance1_prediction[0]);
    }
    string.append("Predicted label for the first instance is: " + instance1_predicted_label + "\n\n");

    string.append("Weights before performing BP:\n");
    string.append("Hidden layer weights:\n" + Arrays.deepToString(nn.hidden_layer_weights) + "\n");
    string.append("Output layer weights:\n" + Arrays.deepToString(nn.output_layer_weights) + "\n\n");

    double[][] outputs = nn.forward_pass(instances[0]);
    double[][][] deltas = nn.backward_propagate_error(instances[0], outputs[0], outputs[1], onehot_encoded[0], true, string);
    nn.update_weights(deltas[0], deltas[1]);

    string.append("\nWeights after performing BP for first instance only:\n");
    string.append("Hidden layer weights:\n" + Arrays.deepToString(nn.hidden_layer_weights) + "\n");
    string.append("Output layer weights:\n" + Arrays.deepToString(nn.output_layer_weights) + "\n");

    nn.train(instances, onehot_encoded, numEps, string);

    string.append("\nAfter training:\n");
    string.append("Hidden layer weights:\n" + Arrays.deepToString(nn.hidden_layer_weights) + "\n");
    string.append("Output layer weights:\n" + Arrays.deepToString(nn.output_layer_weights) + "\n");

    List<String[]> lines_test = Util.getLines("penguins307-test.csv");
    String[] header_test = lines_test.remove(0);
    String[] labels_test = Util.getLabels(lines_test);
    double[][] instances_test = Util.getData(lines_test, biases);

    // scale the test according to our training data.
    rescaler.rescaleData(instances_test, biases);

    string.append("\nTesting data:\n\n");

    int[] desired_labels_test = label_encoder.intEncode(labels_test);
    int[] actual_labels_test = nn.predict(instances_test, false, string);

    int num_right = 0;
    for (int i = 0; i < desired_labels_test.length; i++) {
      if (desired_labels_test[i]==actual_labels_test[i]) num_right++;
    }

    double acc = (1.0*num_right)/desired_labels_test.length;
    string.append("acc = " + String.format("%.2f", acc*100) + "%\n\n");

    string.append("Finished!");
    return acc;
  }

}
