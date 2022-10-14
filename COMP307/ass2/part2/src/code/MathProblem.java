package code;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.*;
import org.jgap.gp.function.*;
import org.jgap.gp.impl.*;
import org.jgap.gp.terminal.*;

public class MathProblem extends GPProblem {
  private Variable x;

  public MathProblem(double[] xs, double[] ys) throws InvalidConfigurationException {
    super(new GPConfiguration());

    GPConfiguration config = getGPConfiguration();

    x = Variable.create(config, "x", CommandGene.DoubleClass);

    config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
    config.setMaxInitDepth(10);
    config.setPopulationSize(1000);
    config.setMaxCrossoverDepth(8);
    config.setFitnessFunction(new MathFitness(xs, ys, x));
    config.setStrictProgramCreation(true);
  }

  public GPGenotype create() throws InvalidConfigurationException {
    GPConfiguration config = getGPConfiguration();

    //The return type
    Class<?>[] types = { CommandGene.DoubleClass };

    //Arguments of chromosome
    Class<?>[][] argTypes = { {} };

    //Commands and terminals to use
    CommandGene[][] nodeSets = {
      {
        x,
        new Add(config, CommandGene.DoubleClass),
        new Subtract(config, CommandGene.DoubleClass),
        new Multiply(config, CommandGene.DoubleClass),
        new Divide(config, CommandGene.DoubleClass),
        new Terminal(config, CommandGene.DoubleClass, -100, 100, true)
      }
    };

    GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes,
    nodeSets, 20, true);

    return result;
  }
}
