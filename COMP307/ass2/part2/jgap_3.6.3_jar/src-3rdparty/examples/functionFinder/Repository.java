/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package examples.functionFinder;

import java.util.*;
import com.eteks.parser.*;

/**
 * Specifies available mathematical constants, operators and functions.
 *
 * @author Klaus Meffert
 * @since 2.2
 */
public class Repository
    extends Term {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.3 $";

  private static List constants;

  private static List operators;

  private static List functions;

  private static Syntax syntax;

  private static ExpressionParser parser;

  public Repository() {
    init();
    constants = new Vector();
    operators = new Vector();
    functions = new Vector();
  }

  public static void init() {
    constants = new Vector();
    operators = new Vector();
    functions = new Vector();
    syntax = new JavaSyntax();
    parser = new ExpressionParser(syntax, null);
    defineConstants();
    defineOperators();
    defineFunctions();
  }

  /**
   * Defined constants. Extend as you like.
   */
  public static void defineConstants() {
    // Special numbers
    constants.add("Math.PI");
    constants.add("Math.E");
    // Common numbers
//    constants.add("1");
//    constants.add("-1");
    constants.add("2");
//    constants.add("-2");

    // Variables (X two times to extend probability)
    constants.add("X");
    constants.add("X");
    constants.add("+I"); // this is any positive integer
    constants.add("+D"); // this is any positive double
//    constants.add("-I");// this is any negative integer
//    constants.add("-D");// this is any negative double
  }

  /**
   * Defined operators. Extend as you like.
   */
  public static void defineOperators() {
    operators.add("+");
    operators.add("*");
    operators.add("/");
    operators.add("-");
    operators.add("%"); //Modulo
//    operators.add("^");//bitwise OR: Only works for integer values!
//    operators.add("&");//bitwise AND: Only works for integer values!
  }

  /**
   * Defined functions. Extend as you like.
   */
  public static void defineFunctions() {
//    functions.add("");//dummy-Funktion, doing nothing (Z80: NOP)
    functions.add("Math.log");
    functions.add("Math.exp");
    functions.add("Math.sqrt");
    functions.add("Math.cos");
    functions.add("Math.sin");
    functions.add("Math.tan");
    functions.add("Math.acos");
    functions.add("Math.asin");
    functions.add("Math.atan");
    functions.add("Math.floor");
    functions.add("Math.ceil");
    functions.add("Math.round");
//    functions.add("Math.abs");
  }

  public static List getConstants() {
    return constants;
  }

  public static List getFunctions() {
    return functions;
  }

  public static List getOperators() {
    return operators;
  }

  /**
   * Makes sure to only apply functions being valid for the given input values.
   * @param truthTable table of input/output tuples
   */
  public static void apply(Map truthTable) {
    int j = 0;
    String formula;
    String s;
    while (j < getFunctions().size()) {
      s = (String) getFunctions().get(j);
      if (s.startsWith("/")) {
        j++;
        continue;
      }
      formula = "F(X)=" + s + "(X)";
      Set keySet = truthTable.keySet();
      Iterator keys = keySet.iterator();
      while (keys.hasNext()) {
        double inputValue = ((Double)keys.next()).doubleValue();
        try {
          evaluate(formula, inputValue);
        }
        catch (Exception ex) {
          ex.printStackTrace();
          System.err.println("Kicked function " + formula);
          getFunctions().remove(j);
          j--;
          break;
        }
      }
      j++;
    }
  }

  /**
   * Calculates the output of a formula for a given input value.
   *
   * @param formula the formula to evaluate
   * @param inputValue the input value to use
   * @throws CompilationException in case of invalid input values regarding
   * the formula
   *
   * @return result of formula for input value
   *
   * @author Klaus Meffert
   * @since 2.2
   */
  public static double evaluate(String formula, double inputValue)
      throws CompilationException {
    CompiledFunction ex1 = parser.compileFunction(formula);
    return ex1.computeFunction(new double[] {inputValue});
  }
}
