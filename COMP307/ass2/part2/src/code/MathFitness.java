package code;

import org.jgap.gp.*;
import org.jgap.gp.terminal.*;

public class MathFitness extends GPFitnessFunction {
  private static final long serialVersionUID = 42l;

  private final double[] xs;
  private final double[] ys;
  private Variable x;

  public MathFitness(double[] xs, double[] ys, Variable x) {
    this.xs = xs;
    this.ys = ys;
    this.x = x;
  }

  public double evaluate(IGPProgram program) {
    double res = 0;

    for (int i = 0; i < xs.length; i++) {
      x.set(xs[i]);
      double val = program.execute_double(0, new Object[0]);
      res += Math.abs(val-ys[i]);
    }

    return res;
  }
}
