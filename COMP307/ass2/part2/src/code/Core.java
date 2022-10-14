package code;

import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.GPProblem;

public class Core {

  public static void main(String[] _ignored) throws Exception{
    StringBuilder string = new StringBuilder();

    double[][] vars = Util.parseData(Util.readAllLines("regression.txt"));
    GPProblem problem = new MathProblem(vars[0], vars[1]);

    string.append("Creating new genetic programming program...\n");

    GPGenotype gp = problem.create();
    string.append("Done. Evolving...\n");
    int evs = 1;
    gp.evolve(1);
    while (gp.getFittestProgram().getFitnessValue() > 0.1) {
      string.append("Fitness at evolution " + evs + ": " + gp.getFittestProgram().getFitnessValue() + "\n");
      gp.evolve(1);
      evs++;
    }
    string.append("Done.\n\n");
    string.append("Best solution: " + gp.getFittestProgram().toStringNorm(0)+"\n");
    string.append("Fitness achieved: " + gp.getFittestProgram().getFitnessValue());
    gp.outputSolution(gp.getFittestProgram());

    String res = string.toString();
    System.out.println(res);
    Util.saveToFile("output.txt", res);
  }
}
