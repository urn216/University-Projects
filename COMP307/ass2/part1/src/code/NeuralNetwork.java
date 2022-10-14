package code;

import java.util.Arrays;

public class NeuralNetwork {
  public final double[][] hidden_layer_weights;
  public final double[][] output_layer_weights;
  private final int num_inputs;
  private final int num_hidden;
  private final int num_outputs;
  private final double learning_rate;
  private final boolean biases;

  public NeuralNetwork(int num_inputs, int num_hidden, int num_outputs, double[][] initial_hidden_layer_weights, double[][] initial_output_layer_weights, double learning_rate, boolean biases) {
    //Initialise the network
    this.num_inputs = num_inputs;
    this.num_hidden = num_hidden;
    this.num_outputs = num_outputs;

    this.hidden_layer_weights = initial_hidden_layer_weights;
    this.output_layer_weights = initial_output_layer_weights;

    this.learning_rate = learning_rate;

    this.biases = biases;
  }

  //Calculate neuron activation for an input
  public double sigmoid(double input) {
    double output = 1/(1+Math.exp(-input));
    return output;
  }

  //Feed forward pass input to a network output
  public double[][] forward_pass(double[] inputs) {
    double[] hidden_layer_outputs = new double[num_hidden];
    for (int i = 0; i < num_hidden; i++) {
      if (biases && i==num_hidden-1) {
        hidden_layer_outputs[i] = 1;
      }
      else {
        double weighted_sum = 0;
        for (int j = 0; j < num_inputs; j++) {
          weighted_sum+=hidden_layer_weights[j][i]*inputs[j];
        }
        double output = sigmoid(weighted_sum);
        hidden_layer_outputs[i] = output;
      }
    }

    double[] output_layer_outputs = new double[num_outputs];
    for (int i = 0; i < num_outputs; i++) {
      double weighted_sum = 0;
      for (int j = 0; j < num_hidden; j++) {
        weighted_sum+=output_layer_weights[j][i]*hidden_layer_outputs[j];
      }
      double output = sigmoid(weighted_sum);
      output_layer_outputs[i] = output;
    }
    return new double[][]{hidden_layer_outputs, output_layer_outputs};
  }

  public double[][][] backward_propagate_error(double[] inputs, double[] hidden_layer_outputs,
  double[] output_layer_outputs, int desired_outputs[], boolean print, StringBuilder string) {

    double[] output_layer_betas = new double[num_outputs];
    for (int z = 0; z < num_outputs; z++) {
      output_layer_betas[z] = desired_outputs[z] - output_layer_outputs[z];
    }
    if (print) string.append("OL betas: " + Arrays.toString(output_layer_betas) + "\n");

    double[] hidden_layer_betas = new double[num_hidden];
    for (int j = 0; j < num_hidden; j++) {
      hidden_layer_betas[j] = 0;
      for (int k = 0; k < num_outputs; k++) {
        hidden_layer_betas[j] += output_layer_weights[j][k]*output_layer_outputs[k]*(1-output_layer_outputs[k])*output_layer_betas[k];
      }
    }
    if (print) string.append("HL betas: " + Arrays.toString(hidden_layer_betas) + "\n");

    // This is a HxO array (H hidden nodes, O outputs)
    double[][] delta_output_layer_weights = new double[num_hidden][num_outputs];
    for (int i = 0; i < num_hidden; i++) {
      for (int j = 0; j < num_outputs; j++) {
        delta_output_layer_weights[i][j] = learning_rate*hidden_layer_outputs[i]*output_layer_outputs[j]*(1-output_layer_outputs[j])*output_layer_betas[j];
      }
    }

    // This is a IxH array (I inputs, H hidden nodes)
    double[][] delta_hidden_layer_weights = new double[num_inputs][num_hidden];
    for (int i = 0; i < num_inputs; i++) {
      for (int j = 0; j < num_hidden; j++) {
        delta_hidden_layer_weights[i][j] = learning_rate*inputs[i]*hidden_layer_outputs[j]*(1-hidden_layer_outputs[j])*hidden_layer_betas[j];
      }
    }

    // Return the weights we calculated, so they can be used to update all the weights.
    return new double[][][]{delta_output_layer_weights, delta_hidden_layer_weights};
  }

  public void update_weights(double[][] delta_output_layer_weights, double[][] delta_hidden_layer_weights) {
    for (int i = 0; i < num_hidden; i++) {
      for (int j = 0; j < num_outputs; j++) {
        output_layer_weights[i][j] += delta_output_layer_weights[i][j];
      }
    }

    for (int i = 0; i < num_inputs; i++) {
      for (int j = 0; j < num_hidden; j++) {
        hidden_layer_weights[i][j] += delta_hidden_layer_weights[i][j];
      }
    }
  }

  public void train(double[][] instances, int[][] desired_outputs, int epochs, StringBuilder string) {
    for (int epoch = 0; epoch < epochs; epoch++) {
      string.append("\nepoch = " + epoch + "\n");
      // int[] predictions = new int[instances.length];
      int num_right = 0;

      for (int i = 0; i < instances.length; i++) {
        double[] instance = instances[i];
        double[][] outputs = forward_pass(instance);
        double[][][] delta_weights = backward_propagate_error(instance, outputs[0], outputs[1], desired_outputs[i], false, string);
        int predicted_class = -1;
        int correct_class = -1;
        double largest = -1;
        for (int j = 0; j < num_outputs; j++) {
          if (outputs[1][j] > largest) {largest = outputs[1][j]; predicted_class = j;}
          if (desired_outputs[i][j] == 1) correct_class = j;
        }
        // predictions[i] = predicted_class;
        if (predicted_class==correct_class) num_right++;

        //We use online learning, i.e. update the weights after every instance.
        update_weights(delta_weights[0], delta_weights[1]);
      }

      // Print new weights
      string.append("Hidden layer weights \n" + Arrays.deepToString(hidden_layer_weights) + "\n");
      string.append("Output layer weights  \n" + Arrays.deepToString(output_layer_weights) + "\n");

      double acc = (1.0*num_right)/instances.length;
      string.append("acc = " + String.format("%.2f", acc*100) + "%\n");
    }
  }

  public int[] predict(double[][] instances, boolean print, StringBuilder string) {
    int[] predictions = new int[instances.length];
    for (int i = 0; i < instances.length; i++) {
      double[] instance = instances[i];
      double[][] outputs = forward_pass(instance);
      if (print) string.append("Output: " + Arrays.toString(outputs[1]) + "\n");
      int predicted_class = -1;
      double largest = -1;
      for (int j = 0; j < num_outputs; j++) {
        if (outputs[1][j] > largest) {largest = outputs[1][j]; predicted_class = j;}
      }
      predictions[i] = predicted_class;
    }
    return predictions;
  }

}
