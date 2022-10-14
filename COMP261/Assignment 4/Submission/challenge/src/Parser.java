import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;
import javax.swing.JFileChooser;

/**
* The parser and interpreter. The top level parse function, a main method for
* testing, and several utility methods are provided. You need to implement
* parseProgram and all the rest of the parser.
*/
public class Parser {

	/**
	* Top level parse method, called by the World
	*/
	static RobotProgramNode parseFile(File code) {
		Scanner scan = null;
		try {
			scan = new Scanner(code);

			// the only time tokens can be next to each other is
			// when one of them is one of (){},;
			scan.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

			RobotProgramNode n = parseProgram(scan); // You need to implement this!!!

			scan.close();
			return n;
		} catch (FileNotFoundException e) {
			System.out.println("Robot program source file not found");
		} catch (ParserFailureException e) {
			System.out.println("Parser error:");
			System.out.println(e.getMessage());
			scan.close();
		}
		return null;
	}

	/** For testing the parser without requiring the world */

	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				File f = new File(arg);
				if (f.exists()) {
					System.out.println("Parsing '" + f + "'");
					RobotProgramNode prog = parseFile(f);
					System.out.println("Parsing completed ");
					if (prog != null) {
						System.out.println("================\nProgram:");
						System.out.println(prog);
					}
					System.out.println("=================");
				} else {
					System.out.println("Can't find file '" + f + "'");
				}
			}
		} else {
			while (true) {
				JFileChooser chooser = new JFileChooser(".");// System.getProperty("user.dir"));
				int res = chooser.showOpenDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					break;
				}
				RobotProgramNode prog = parseFile(chooser.getSelectedFile());
				System.out.println("Parsing completed");
				if (prog != null) {
					System.out.println("Program: \n" + prog);
				}
				System.out.println("=================");
			}
		}
		System.out.println("Done");
	}

	// Useful Patterns

	static Pattern VAR = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");
	static Pattern NUMPAT = Pattern.compile("-?(0|[1-9][0-9]*)");
	static Pattern OPENPAREN = Pattern.compile("\\(");
	static Pattern CLOSEPAREN = Pattern.compile("\\)");
	static Pattern OPENBRACE = Pattern.compile("\\{");
	static Pattern CLOSEBRACE = Pattern.compile("\\}");
	static Pattern SEMICOLON = Pattern.compile(";");
	static Pattern COMMA = Pattern.compile(",");
	static Pattern ACT = Pattern.compile("(move|turnL|turnR|turnAround|shieldOn|shieldOff|takeFuel|wait)");
	static Pattern IF = Pattern.compile("if");
	static Pattern LOOP = Pattern.compile("loop");
	static Pattern ELIF = Pattern.compile("(else|elif)");
	static Pattern WHILE = Pattern.compile("while");
	static Pattern RELOP = Pattern.compile("(<|<=|>|>=|==|!=)");
	static Pattern LOGIC = Pattern.compile("(&&|\\|\\|)");
	static Pattern BOOL = Pattern.compile("(true|false)");
	static Pattern SEN = Pattern.compile("(fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist)");
	static Pattern OP = Pattern.compile("(\\+|-|\\*|/)");

	/**
	* See assignment handout for the grammar.
	*/
	static RobotProgramNode parseProgram(Scanner s) {
		List<Stmt> prgmelems = new ArrayList<Stmt>();
		Map<String, Variable> vars = new HashMap<String, Variable>();
		while (s.hasNext()) {
			prgmelems.add(parseSTMT(s, vars));
		}
		return new Prgm(prgmelems, vars.values());
	}

	// utility methods for the parser

	static Stmt parseSTMT(Scanner s, Map<String, Variable> vars) {
		if (s.hasNext(ACT)) {return parseAct(s, vars);}
		if (s.hasNext(LOOP)) {return parseLoop(s, vars);}
		if (s.hasNext(IF)) {return parseIf(s, vars);}
		if (s.hasNext(WHILE)) {return parseWhile(s, vars);}
		if (s.hasNext(VAR)) {return assignVar(s, vars);}
		if (s.hasNext(OPENBRACE)) {return parseBlockStmt(s, vars);}
		fail("No valid expression", s);
		return null;
	}

	/**
	* Parses an action with no conditions into a program.
	*/
	static Stmt parseAct(Scanner s, Map<String, Variable> vars) {
		String type = require(ACT, "Expecting a valid action", s);
		if (s.hasNext(OPENPAREN)) {
			require(OPENPAREN, "Expecting (", s);
			Expr expr = parseExp(s, vars);
			require(CLOSEPAREN, "Expecting )", s);
			require(SEMICOLON, "Expecting ;", s);
			if (type.equals("move")) return new Move(expr);
			if (type.equals("wait")) return new Wait(expr);
			fail("Expecting a valid action", s);
			return null;
		}
		require(SEMICOLON, "Expecting ;", s);
		if (type.equals("move")) return new Move();
		if (type.equals("turnL")) return new TurnL();
		if (type.equals("turnR")) return new TurnR();
		if (type.equals("turnAround")) return new TurnRound();
		if (type.equals("shieldOn")) return new ShieldOn();
		if (type.equals("shieldOff")) return new ShieldOff();
		if (type.equals("takeFuel")) return new TakeFuel();
		if (type.equals("wait")) return new Wait();
		fail("Expecting a valid action", s);
		return null;
	}

	/**
	* Parses a loop with a block of functions into a program.
	*/
	static Stmt parseLoop(Scanner s, Map<String, Variable> vars) {
		require(LOOP, "Expected loop", s);
		Stmt block = parseSTMT(s, vars);
		return new Loop(block);
	}

	/**
	* Parses an if statement with a block of functions into a program.
	*/
	static Stmt parseIf(Scanner s, Map<String, Variable> vars) {
		if (s.hasNext(IF)) require(IF, "Expected if", s);
		require(OPENPAREN, "Expected (", s);
		Cond cond = parseCond(s, vars);
		require(CLOSEPAREN, "Expected )", s);
		Block block = parseBlock(s, vars);
		Else elif = null;
		if (s.hasNext(ELIF)) elif = parseElse(s, vars);
		return new If(block, cond, elif);
	}

	/**
	* Parses an else statement with a block of functions into a program.
	*/
	static Else parseElse(Scanner s, Map<String, Variable> vars) {
		String type = require(ELIF, "Expected else statement", s);
		if (type.equals("else")) return new Else(parseBlock(s, vars), false);
		return new Else(parseIf(s, vars), true);
	}

	/**
	* Parses a while statement with a block of functions into a program.
	*/
	static Stmt parseWhile(Scanner s, Map<String, Variable> vars) {
		require(WHILE, "Expected while", s);
		require(OPENPAREN, "Expected (", s);
		Cond cond = parseCond(s, vars);
		require(CLOSEPAREN, "Expected )", s);
		Block block = parseBlock(s, vars);
		return new While(block, cond);
	}

	/**
	* Parses a while statement with a block of functions into a program.
	*/
	static Stmt parseBlockStmt(Scanner s, Map<String, Variable> vars) {
		return new BlockStmt(parseBlock(s, vars));
	}

	/**
	* Parses a block of functions into a program.
	*/
	static Block parseBlock(Scanner s, Map<String, Variable> vars) {
		require(OPENBRACE, "Expected {", s);
		List<Stmt> blockElems = new ArrayList<Stmt>();
		Map<String, Variable> blockVars = new HashMap<String, Variable>();
		while (s.hasNext()) {
			if (s.hasNext(CLOSEBRACE)) break;
			blockElems.add(parseSTMT(s, vars));
		}
		require(CLOSEBRACE, "Expected }", s);
		// if (blockElems.isEmpty()) fail("Block cannot be empty", s);
		return new Block(blockElems, blockVars.values());
	}

	/**
	* Parses a conditional statement into a program.
	*/
	static Cond parseCond(Scanner s, Map<String, Variable> vars) {
		Cond conditional = null;

		boolean needParen = false;
		if (s.hasNext(OPENPAREN)) {
			require(OPENPAREN, "Expecting (", s);
			if (!(s.hasNext(NUMPAT)||s.hasNext(SEN)||s.hasNext(VAR))) {
				Cond cond = new CondWrap(parseCond(s, vars));
				require(CLOSEPAREN, "Expecting )", s);
				conditional = cond;
			} else needParen = true;
		}
		if (s.hasNext("!")) {
			require("!", "Expecting !", s);
			conditional = new NotGate(parseCond(s, vars));
		}
		if (s.hasNext(BOOL)) {
			String type = require(BOOL, "Expecting a boolean", s);
			conditional = new Bool(type.equals("true"));
		}

		if (s.hasNext(NUMPAT)||s.hasNext(SEN)||s.hasNext(VAR)) {
			Expr one = parseExp(s, vars);
			String type = require(RELOP, "Expecting comparison", s);
			Expr two = parseExp(s, vars);
			if (needParen) require(CLOSEPAREN, "Expecting )", s);
			if (type.equals("<")) conditional = new LessThan(one, two);
			else if (type.equals("<=")) conditional = new LessThanEQ(one, two);
			else if (type.equals(">")) conditional = new GreaterThan(one, two);
			else if (type.equals(">=")) conditional = new GreaterThanEQ(one, two);
			else if (type.equals("==")) conditional = new EqualTo(one, two);
			else if (type.equals("!=")) conditional = new NotEqualTo(one, two);
			else {
				fail("Expecting a valid comparison", s);
				return null;
			}
		}

		if (conditional==null) {
			fail("Expecting a valid condition", s);
			return null;
		}
		if (s.hasNext(LOGIC)) {
			String type = require(LOGIC, "Expecting a valid logic gate", s);
			Cond two = parseCond(s, vars);
			if (type.equals("&&")) return new AndGate(conditional, two);
			if (type.equals("||")) return new OrGate(conditional, two);
		}
		return conditional;
	}

	/**
	* Parses an expression into a program.
	*/
	static Expr parseExp(Scanner s, Map<String, Variable> vars) {
		Expr expr = null;

		if (s.hasNext(NUMPAT)) {expr = new Num(requireInt(NUMPAT, "Not a valid integer", s));}
		if (s.hasNext(SEN)) {expr = parseSen(s, vars);}
		if (s.hasNext(VAR)) {
			String name = require(VAR, "Not a valid variable", s);
			if (!vars.containsKey(name)) {
				fail("Variable "+name+" must be declared before it can be used", s);
			}
			expr = vars.get(name);
		}
		if (s.hasNext(OPENPAREN)) {
			require(OPENPAREN, "Expecting (", s);
			expr = parseExp(s, vars);
			require(CLOSEPAREN, "Expecting )", s);
			expr.addParen();
			return expr;
		}
		if (expr==null) {
			fail("Expecting a valid expression", s);
			return null;
		}
		if (s.hasNext(OP)) {return parseOp(s, vars, expr);}
		return expr;
	}

	/**
	* Parses a robot sensor input into a program.
	*/
	static Expr parseSen(Scanner s, Map<String, Variable> vars) {
		String type = require(SEN, "Expecting a valid sensor", s);
		if (s.hasNext(OPENPAREN)) {
			require(OPENPAREN, "Expecting (", s);
			Expr expr = parseExp(s, vars);
			require(CLOSEPAREN, "Expecting )", s);
			if (type.equals("barrelLR")) return new BarrelLR(expr);
			if (type.equals("barrelFB")) return new BarrelFB(expr);
			fail("Expecting a valid sensor", s);
			return null;
		}
		if (type.equals("fuelLeft")) return new FuelLeft();
		if (type.equals("oppLR")) return new OppLR();
		if (type.equals("oppFB")) return new OppFB();
		if (type.equals("numBarrels")) return new NumBarrels();
		if (type.equals("barrelLR")) return new BarrelLR();
		if (type.equals("barrelFB")) return new BarrelFB();
		if (type.equals("wallDist")) return new WallDist();
		fail("Expecting a valid sensor", s);
		return null;
	}

	/**
	* Parses an operation into a program.
	*/
	static Expr parseOp(Scanner s, Map<String, Variable> vars, Expr one) {
		String type = require(OP, "Expecting a valid operation", s);
		Expr two = parseExp(s, vars);
		if (type.equals("+")) return new AddOp(one, two);
		if (type.equals("-")) return new SubOp(one, two);
		if (type.equals("*")) return new MulOp(one, two);
		if (type.equals("/")) return new DivOp(one, two);
		fail("Expecting a valid operation", s);
		return null;
	}

	/**
	* Parses a new variable into a program.
	*/
	static Stmt assignVar(Scanner s, Map<String, Variable> vars) {
		String name = require(VAR, "Expecting a valid variable declaration", s);
		require("=", "Expecting =", s);
		Expr value = parseExp(s, vars);
		require(SEMICOLON, "Expecting ;", s);
		return new Assignment(vars, name, value);
	}

	/**
	* Report a failure in the parser.
	*/
	static void fail(String message, Scanner s) {
		String msg = message + "\n   @ ...";
		for (int i = 0; i < 5 && s.hasNext(); i++) {
			msg += " " + s.next();
		}
		throw new ParserFailureException(msg + "...");
	}

	/**
	* Requires that the next token matches a pattern if it matches, it consumes
	* and returns the token, if not, it throws an exception with an error
	* message
	*/
	static String require(String p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	static String require(Pattern p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	/**
	* Requires that the next token matches a pattern (which should only match a
	* number) if it matches, it consumes and returns the token as an integer if
	* not, it throws an exception with an error message
	*/
	static int requireInt(String p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	static int requireInt(Pattern p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	/**
	* Checks whether the next token in the scanner matches the specified
	* pattern, if so, consumes the token and return true. Otherwise returns
	* false without consuming anything.
	*/
	static boolean checkFor(String p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

	static boolean checkFor(Pattern p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

}

/**
* A class representing a program which a robot is able to execute.
*/
class Prgm implements RobotProgramNode {
	protected final List<Stmt> nodes;
	protected final Collection<Variable> vars;
	public Prgm(List<Stmt> nodes, Collection<Variable> vars) {this.nodes=nodes; this.vars=vars;}
	public void execute(Robot robot) {
		for (Stmt node : nodes) {node.execute(robot);}
	}

	public String toString() {
		String s = "";
		if (!vars.isEmpty()) {
			s+="vars ";
			int i = 0;
			for (Variable var : vars) {
				s+=var;
				i++;
				if (i<vars.size()) s+=", ";
			}
			s+=";\n";
		}
		for (Stmt node : nodes) {s+=node.toString(0)+"\n";}
		return s;
	}
}

/**
* An interface representing a line of code.
*/
interface Stmt extends RobotProgramNode {
	public String toString(int tabs);
}

//-------------------------------------------------
//ACTIONS:

/**
* A class representing a piece of code telling a robot to move forward.
*/
class Move implements Stmt {
	private final boolean use;
	private final Expr expr;
	public Move(Expr expr) {this.expr=expr; use = true;}
	public Move() {this.expr=new Num(1); use = false;}
	public void execute(Robot robot) {
		for (int i = 0; i < expr.evaluate(robot); i++) {
			robot.move();
		}
	}

	public String toString(int tabs) {
		return use ? "move("+expr+");" : "move;";
	}
}

/**
* A class representing a piece of code telling a robot to turn left.
*/
class TurnL implements Stmt {
	public void execute(Robot robot) {
		robot.turnLeft();
	}

	public String toString(int tabs) {
		return "turnL;";
	}
}

/**
* A class representing a piece of code telling a robot to turn right.
*/
class TurnR implements Stmt {
	public void execute(Robot robot) {
		robot.turnRight();
	}

	public String toString(int tabs) {
		return "turnR;";
	}
}

/**
* A class representing a piece of code telling a robot to turn right around.
*/
class TurnRound implements Stmt {
	public void execute(Robot robot) {
		robot.turnAround();
	}

	public String toString(int tabs) {
		return "turnAround;";
	}
}

/**
* A class representing a piece of code telling a robot to turn its shield on.
*/
class ShieldOn implements Stmt {
	public void execute(Robot robot) {
		robot.setShield(true);
	}

	public String toString(int tabs) {
		return "shieldOn;";
	}
}

/**
* A class representing a piece of code telling a robot to turn its shield on.
*/
class ShieldOff implements Stmt {
	public void execute(Robot robot) {
		robot.setShield(false);
	}

	public String toString(int tabs) {
		return "shieldOff;";
	}
}

/**
* A class representing a piece of code telling a robot to take some fuel.
*/
class TakeFuel implements Stmt {
	public void execute(Robot robot) {
		robot.takeFuel();
	}

	public String toString(int tabs) {
		return "takeFuel;";
	}
}

/**
* A class representing a piece of code telling a robot to wait a turn.
*/
class Wait implements Stmt {
	private final boolean use;
	private final Expr expr;
	public Wait(Expr expr) {this.expr=expr; use = true;}
	public Wait() {this.expr=new Num(1); use = false;}
	public void execute(Robot robot) {
		for (int i = 0; i < expr.evaluate(robot); i++) {
			robot.idleWait();
		}
	}

	public String toString(int tabs) {
		return use ? "wait("+expr+");" : "wait;";
	}
}

//-------------------------------------------------
/**
* An interface representing an expression.
*/
interface Expr {
	public int evaluate(Robot robot);
	public void addParen();
	public String toString();
}

//Expressions:
/**
* A class representing a number
*/
class Num implements Expr {
	private final int value;
	private boolean paren = false;
	public Num(int value) {this.value=value;}
	public int evaluate(Robot robot) {return value;}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "("+value+")" : ""+value;
	}
}

/**
* A class representing a variable
*/
class Variable implements Expr {
	private final String name;
	private Expr value;
	private boolean paren = false;
	public Variable(String name) {this.name=name; this.value = new Num(0);}
	public Variable(String name, Expr value) {this.name=name; this.value=value;}
	public int evaluate(Robot robot) {return value.evaluate(robot);}

	public void addParen() {paren = true;}

	public void setVal(Expr value) {this.value=value;}

	public String toString() {
		return paren ? "("+name+")" : ""+name;
	}
}

/**
* A class representing the amount of fuel left in a robot
*/
class FuelLeft implements Expr {
	private boolean paren = false;
	public int evaluate(Robot robot) {return robot.getFuel();}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "(fuelLeft)" : "fuelLeft";
	}
}

/**
* A class representing the distance to opponent, left or right
*/
class OppLR implements Expr {
	private boolean paren = false;
	public int evaluate(Robot robot) {return robot.getOpponentLR();}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "(oppLR)" : "oppLR";
	}
}

/**
* A class representing the distance to opponent, front or back
*/
class OppFB implements Expr {
	private boolean paren = false;
	public int evaluate(Robot robot) {return robot.getOpponentFB();}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "(oppFB)" : "oppFB";
	}
}

/**
* A class representing the number of barrels in the world
*/
class NumBarrels implements Expr {
	private boolean paren = false;
	public int evaluate(Robot robot) {return robot.numBarrels();}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "(numBarrels)" : "numBarrels";
	}
}

/**
* A class representing the distance to nearest barrel, left or right
*/
class BarrelLR implements Expr {
	private final boolean use;
	private final Expr expr;
	private boolean paren = false;
	public BarrelLR(Expr expr) {this.expr=expr; use = true;}
	public BarrelLR() {this.expr=new Num(0); use = false;}
	public int evaluate(Robot robot) {return robot.getBarrelLR(expr.evaluate(robot));}

	public void addParen() {paren = true;}

	public String toString() {
		String s = "";
		if (paren) s+="(";
		s += use ? "barrelLR("+expr+")" : "barrelLR";
		if (paren) s+=")";
		return s;
	}
}

/**
* A class representing the distance to nearest barrel, front or back
*/
class BarrelFB implements Expr {
	private final boolean use;
	private final Expr expr;
	private boolean paren = false;
	public BarrelFB(Expr expr) {this.expr=expr; use = true;}
	public BarrelFB() {this.expr=new Num(0); use = false;}
	public int evaluate(Robot robot) {return robot.getBarrelFB(expr.evaluate(robot));}

	public void addParen() {paren = true;}

	public String toString() {
		String s = "";
		if (paren) s+="(";
		s += use ? "barrelFB("+expr+")" : "barrelFB";
		if (paren) s+=")";
		return s;
	}
}

/**
* A class representing the distance to wall in front of robot
*/
class WallDist implements Expr {
	private boolean paren = false;
	public int evaluate(Robot robot) {return robot.getDistanceToWall();}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "(wallDist)" : "wallDist";
	}
}

/**
* A class representing the sum of two expressions
*/
class AddOp implements Expr {
	private final Expr one;
	private final Expr two;
	private boolean paren = false;

	public AddOp(Expr one, Expr two) {this.one=one; this.two=two;}
	public int evaluate(Robot robot) {return one.evaluate(robot)+two.evaluate(robot);}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "("+one+" + "+two+")" : one+" + "+two;
	}
}

/**
* A class representing the subtraction of one expression from another
*/
class SubOp implements Expr {
	private final Expr one;
	private final Expr two;
	private boolean paren = false;

	public SubOp(Expr one, Expr two) {this.one=one; this.two=two;}
	public int evaluate(Robot robot) {return one.evaluate(robot)-two.evaluate(robot);}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "("+one+" - "+two+")" : one+" - "+two;
	}
}

/**
* A class representing the product of two expressions
*/
class MulOp implements Expr {
	private final Expr one;
	private final Expr two;
	private boolean paren = false;

	public MulOp(Expr one, Expr two) {this.one=one; this.two=two;}
	public int evaluate(Robot robot) {return one.evaluate(robot)*two.evaluate(robot);}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "("+one+" * "+two+")" : one+" * "+two;
	}
}

/**
* A class representing the division of one expression from another
*/
class DivOp implements Expr {
	private final Expr one;
	private final Expr two;
	private boolean paren = false;

	public DivOp(Expr one, Expr two) {this.one=one; this.two=two;}
	public int evaluate(Robot robot) {return one.evaluate(robot)/two.evaluate(robot);}

	public void addParen() {paren = true;}

	public String toString() {
		return paren ? "("+one+" / "+two+")" : one+" / "+two;
	}
}

//------------------------------------------------------
/**
* An interface representing a conditional statement.
*/
interface Cond {
	public boolean evaluate(Robot robot);
	public String toString();
}

//Conditionals:

/**
* A class representing a boolean
*/
class Bool implements Cond {
	private final boolean bool;

	public Bool(boolean bool) {this.bool=bool;}
	public boolean evaluate(Robot robot) {return bool;}

	public String toString() {
		return bool ? "true" : "false";
	}
}

/**
* A class representing a parenthesis wrapper
*/
class CondWrap implements Cond {
	private final Cond cond;

	public CondWrap(Cond cond) {this.cond=cond;}
	public boolean evaluate(Robot robot) {return cond.evaluate(robot);}

	public String toString() {
		return "("+cond+")";
	}
}

/**
* A class representing the lesser of two expressions
*/
class LessThan implements Cond {
	private final Expr one;
	private final Expr two;

	public LessThan(Expr one, Expr two) {this.one=one; this.two=two;}
	public boolean evaluate(Robot robot) {return one.evaluate(robot)<two.evaluate(robot);}

	public String toString() {
		return one+" < "+two;
	}
}

/**
* A class representing the lesser of two expressions
*/
class LessThanEQ implements Cond {
	private final Expr one;
	private final Expr two;

	public LessThanEQ(Expr one, Expr two) {this.one=one; this.two=two;}
	public boolean evaluate(Robot robot) {return one.evaluate(robot)<=two.evaluate(robot);}

	public String toString() {
		return one+" <= "+two;
	}
}

/**
* A class representing the greater of two expressions
*/
class GreaterThan implements Cond {
	private final Expr one;
	private final Expr two;

	public GreaterThan(Expr one, Expr two) {this.one=one; this.two=two;}
	public boolean evaluate(Robot robot) {return one.evaluate(robot)>two.evaluate(robot);}

	public String toString() {
		return one+" > "+two;
	}
}

/**
* A class representing the greater of two expressions
*/
class GreaterThanEQ implements Cond {
	private final Expr one;
	private final Expr two;

	public GreaterThanEQ(Expr one, Expr two) {this.one=one; this.two=two;}
	public boolean evaluate(Robot robot) {return one.evaluate(robot)>=two.evaluate(robot);}

	public String toString() {
		return one+" >= "+two;
	}
}

/**
* A class representing a true response if both expressions are equal
*/
class EqualTo implements Cond {
	private final Expr one;
	private final Expr two;

	public EqualTo(Expr one, Expr two) {this.one=one; this.two=two;}
	public boolean evaluate(Robot robot) {return one.evaluate(robot)==two.evaluate(robot);}

	public String toString() {
		return one+" == "+two;
	}
}

/**
* A class representing a true response if both expressions are not equal
*/
class NotEqualTo implements Cond {
	private final Expr one;
	private final Expr two;

	public NotEqualTo(Expr one, Expr two) {this.one=one; this.two=two;}
	public boolean evaluate(Robot robot) {return one.evaluate(robot)!=two.evaluate(robot);}

	public String toString() {
		return one+" != "+two;
	}
}

/**
* A class representing a true response if both conditions are true
*/
class AndGate implements Cond {
	private final Cond one;
	private final Cond two;

	public AndGate(Cond one, Cond two) {this.one=one; this.two=two;}
	public boolean evaluate(Robot robot) {return one.evaluate(robot)&&two.evaluate(robot);}

	public String toString() {
		return one+" && "+two;
	}
}

/**
* A class representing a true response if one or both conditions are true
*/
class OrGate implements Cond {
	private final Cond one;
	private final Cond two;

	public OrGate(Cond one, Cond two) {this.one=one; this.two=two;}
	public boolean evaluate(Robot robot) {return one.evaluate(robot)||two.evaluate(robot);}

	public String toString() {
		return one+" || "+two;
	}
}

/**
* A class representing a true response if the condition is false
*/
class NotGate implements Cond {
	private final Cond one;

	public NotGate(Cond one) {this.one=one;}
	public boolean evaluate(Robot robot) {return !one.evaluate(robot);}

	public String toString() {
		return "!"+one;
	}
}

//----------------------------------------------------------

/**
* A class representing a block of code telling a robot to perform some actions.
*/
class Block extends Prgm {
	public Block(List<Stmt> nodes, Collection<Variable> vars) {super(nodes, vars);}

	public String toString(int tabs) {
		String indent = "";
		for (int i = 0; i < tabs; i++) {indent+="\t";}
		String s = "{ ";
		if (!vars.isEmpty()) {
			s+="vars ";
			int i = 0;
			for (Variable var : vars) {
				s+=var;
				i++;
				if (i<vars.size()) s+=", ";
			}
			s+=";";
		}
		for (Stmt node : nodes) {s+="\n"+indent+"\t"+node.toString(tabs+1);}
		s+="\n"+indent+"}";
		return s;
	}
}

class BlockStmt implements Stmt {
	protected final Block block;
	public BlockStmt(Block block) {this.block=block;}
	public void execute(Robot robot) {
		block.execute(robot);
	}

	public String toString() {
		return block.toString(0);
	}

	public String toString(int tabs) {
		return block.toString(tabs);
	}
}

/**
* A class representing a piece of code telling a robot to do things in a loop.
*/
class Loop implements Stmt {
	protected final Stmt block;
	public Loop(Stmt block) {this.block=block;}
	public void execute(Robot robot) {
		while(true) {
			block.execute(robot);
		}
	}

	public String toString() {
		return "loop "+block.toString(0);
	}

	public String toString(int tabs) {
		return "loop "+block.toString(tabs);
	}
}

/**
* A class representing a piece of code telling a robot to do things if a condition is met.
*/
class If implements Stmt {
	protected final Block block;
	protected final Cond cond;
	protected final Else elseS;
	public If(Block block, Cond cond, Else elseS) {this.block=block; this.cond=cond; this.elseS=elseS;}
	public void execute(Robot robot) {
		if(cond.evaluate(robot)) {
			block.execute(robot);
		}
		else if (elseS!=null) {
			elseS.execute(robot);
		}
	}

	public String toString() {
		String s = "if ("+cond+") "+block.toString(0);
		if (elseS!=null) s+=elseS.toString();
		return s;
	}

	public String toString(int tabs) {
		String s = "if ("+cond+") "+block.toString(tabs);
		if (elseS!=null) s+=elseS.toString(tabs);
		return s;
	}
}

/**
* A class representing a piece of code telling a robot to do things if a condition is not met.
*/
class Else implements Stmt {
	protected final boolean elif;
	protected final RobotProgramNode contents;
	public Else(RobotProgramNode contents, boolean elif) {this.contents = contents; this.elif=elif;}
	public void execute(Robot robot) {
		contents.execute(robot);
	}

	public String toString() {
		if (elif) {
			return "el"+((If)contents).toString(0);
		}
		return "else "+((Block)contents).toString(0);
	}

	public String toString(int tabs) {
		if (elif) {
			return "el"+((If)contents).toString(tabs);
		}
		return "else "+((Block)contents).toString(tabs);
	}
}

/**
* A class representing a piece of code telling a robot to do things while a condition is met.
*/
class While implements Stmt {
	protected final Block block;
	protected final Cond cond;
	public While(Block block, Cond cond) {this.block=block; this.cond=cond;}
	public void execute(Robot robot) {
		while(cond.evaluate(robot)) {
			block.execute(robot);
		}
	}

	public String toString() {
		return "while ("+cond+") "+block.toString(0);
	}

	public String toString(int tabs) {
		return "while ("+cond+") "+block.toString(tabs);
	}
}

/**
* A class representing a piece of code telling a program to recognise a variable as a certain expression.
*/
class Assignment implements Stmt{
	protected final Variable variable;
	protected final Expr value;
	public Assignment(Map<String, Variable> vars, String name, Expr value) {
		vars.putIfAbsent(name, new Variable(name));
		variable = vars.get(name);
		this.value=value;
	}
	public void execute(Robot robot) {
		variable.setVal(new Num(value.evaluate(robot)));
	}

	public String toString() {
		return variable+" = "+value+";";
	}

	public String toString(int tabs) {
		return this.toString();
	}
}
