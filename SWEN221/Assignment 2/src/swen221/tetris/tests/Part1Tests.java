// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import swen221.tetris.logic.Game;
import swen221.tetris.moves.*;
import swen221.tetris.tetromino.*;
import swen221.tetris.tetromino.Tetromino;
import swen221.tetris.tetromino.Tetromino.Color;
import swen221.tetris.tetromino.Tetromino.Orientation;

@SuppressWarnings("javadoc")
public class Part1Tests {
	@Test public void test_01() {
		Tetromino[] tetrominos = {new I_Tetromino(Orientation.NORTH, Color.YELLOW), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// I Tetromino
		game.clock();
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|Y|Y|Y|Y|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_02() {
		Tetromino[] tetrominos = {new J_Tetromino(Orientation.NORTH, Color.BLUE), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// J Tetromino
		game.clock();
		// O Tetromino
		String expected=
			"|_|B|_|_|_|\n" +
			"|_|B|B|B|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_03() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.ORANGE), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		// O Tetromino
		String expected=
			"|_|O|O|_|_|\n" +
			"|_|_|O|O|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_04() {
		Tetromino[] tetrominos = {new J_Tetromino(Orientation.NORTH, Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// J Tetromino
		game.clock();
		game.apply(new MoveLeft());
		// O Tetromino
		String expected=
			"|D|_|_|_|_|\n" +
			"|D|D|D|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_05() {
		Tetromino[] tetrominos = {new L_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// L Tetromino
		game.clock();
		game.apply(new MoveLeft());
		// O Tetromino
		String expected=
			"|_|_|G|_|_|\n" +
			"|G|G|G|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_06() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		game.apply(new MoveLeft());
		// O Tetromino
		String expected=
			"|G|G|_|_|_|\n" +
			"|_|G|G|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_07() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		game.apply(new MoveLeft());
		// O Tetromino
		String expected=
			"|D|D|_|_|_|\n" +
			"|_|D|D|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_08() {
		Tetromino[] tetrominos = {new J_Tetromino(Orientation.NORTH, Color.MAGENTA), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// J Tetromino
		game.clock();
		game.apply(new MoveRight());
		// O Tetromino
		String expected=
			"|_|_|M|_|_|\n" +
			"|_|_|M|M|M|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_09() {
		Tetromino[] tetrominos = {new S_Tetromino(Orientation.NORTH, Color.RED), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// S Tetromino
		game.clock();
		game.apply(new MoveRight());
		// O Tetromino
		String expected=
			"|_|_|_|R|R|\n" +
			"|_|_|R|R|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_10() {
		Tetromino[] tetrominos = {new S_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// S Tetromino
		game.clock();
		game.apply(new MoveRight());
		// O Tetromino
		String expected=
			"|_|_|_|G|G|\n" +
			"|_|_|G|G|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_11() {
		Tetromino[] tetrominos = {new I_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// I Tetromino
		game.clock();
		game.apply(new MoveDown());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|G|G|G|G|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_12() {
		Tetromino[] tetrominos = {new J_Tetromino(Orientation.NORTH, Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// J Tetromino
		game.clock();
		game.apply(new MoveDown());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|D|_|_|_|\n" +
			"|_|D|D|D|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_13() {
		Tetromino[] tetrominos = {new S_Tetromino(Orientation.NORTH, Color.YELLOW), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// S Tetromino
		game.clock();
		game.apply(new MoveDown());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|Y|Y|_|\n" +
			"|_|Y|Y|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_14() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.BLUE), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		game.apply(new MoveDown());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|B|B|_|_|\n" +
			"|_|_|B|B|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_15() {
		Tetromino[] tetrominos = {new J_Tetromino(Orientation.NORTH, Color.RED), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// J Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|R|R|_|\n" +
			"|_|_|R|_|_|\n" +
			"|_|_|R|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_16() {
		Tetromino[] tetrominos = {new O_Tetromino(Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// O Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|G|G|_|\n" +
			"|_|_|G|G|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_17() {
		Tetromino[] tetrominos = {new O_Tetromino(Color.BLUE), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// O Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|B|B|_|\n" +
			"|_|_|B|B|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_18() {
		Tetromino[] tetrominos = {new J_Tetromino(Orientation.NORTH, Color.RED), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// J Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|R|R|R|_|\n" +
			"|_|_|_|R|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_19() {
		Tetromino[] tetrominos = {new L_Tetromino(Orientation.NORTH, Color.ORANGE), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// L Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|O|O|O|_|\n" +
			"|_|O|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_20() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|G|G|_|_|\n" +
			"|_|_|G|G|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_21() {
		Tetromino[] tetrominos = {new I_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// I Tetromino
		game.clock();
		game.apply(new MoveDown());
		game.clock();
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|G|G|G|G|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_22() {
		Tetromino[] tetrominos = {new T_Tetromino(Orientation.NORTH, Color.ORANGE), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// T Tetromino
		game.clock();
		game.apply(new MoveDown());
		game.clock();
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|O|_|_|\n" +
			"|_|O|O|O|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_23() {
		Tetromino[] tetrominos = {new O_Tetromino(Color.MAGENTA), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// O Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.clock();
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|M|M|_|\n" +
			"|_|_|M|M|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_24() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.YELLOW), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.apply(new ClockwiseRotation());
		game.clock();
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|Y|Y|_|_|\n" +
			"|_|_|Y|Y|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_25() {
		Tetromino[] tetrominos = {new L_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// L Tetromino
		game.clock();
		game.clock();
		game.apply(new MoveLeft());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|G|_|_|\n" +
			"|G|G|G|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_26() {
		Tetromino[] tetrominos = {new O_Tetromino(Color.YELLOW), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// O Tetromino
		game.clock();
		game.clock();
		game.apply(new MoveRight());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|Y|Y|\n" +
			"|_|_|_|Y|Y|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_27() {
		Tetromino[] tetrominos = {new O_Tetromino(Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// O Tetromino
		game.clock();
		game.clock();
		game.apply(new MoveRight());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|D|D|\n" +
			"|_|_|_|D|D|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_28() {
		Tetromino[] tetrominos = {new O_Tetromino(Color.RED), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// O Tetromino
		game.clock();
		game.apply(new MoveDown());
		game.clock();
		game.apply(new MoveRight());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|R|R|\n" +
			"|_|_|_|R|R|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_29() {
		Tetromino[] tetrominos = {new T_Tetromino(Orientation.NORTH, Color.RED), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// T Tetromino
		game.clock();
		game.clock();
		game.apply(new MoveDown());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|R|_|_|\n" +
			"|_|R|R|R|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_30() {
		Tetromino[] tetrominos = {new S_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// S Tetromino
		game.clock();
		game.apply(new MoveRight());
		game.clock();
		game.apply(new MoveDown());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|G|G|\n" +
			"|_|_|G|G|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_31() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		game.apply(new MoveRight());
		game.clock();
		game.apply(new MoveDown());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|D|D|_|\n" +
			"|_|_|_|D|D|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_32() {
		Tetromino[] tetrominos = {new J_Tetromino(Orientation.NORTH, Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// J Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.clock();
		game.apply(new MoveDown());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|D|D|_|\n" +
			"|_|_|D|_|_|\n" +
			"|_|_|D|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_33() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.RED), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		game.clock();
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|R|_|\n" +
			"|_|_|R|R|_|\n" +
			"|_|_|R|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_34() {
		Tetromino[] tetrominos = {new O_Tetromino(Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// O Tetromino
		game.clock();
		game.apply(new MoveLeft());
		game.clock();
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|D|D|_|_|\n" +
			"|_|D|D|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_35() {
		Tetromino[] tetrominos = {new Z_Tetromino(Orientation.NORTH, Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// Z Tetromino
		game.clock();
		game.apply(new MoveRight());
		game.clock();
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|D|\n" +
			"|_|_|_|D|D|\n" +
			"|_|_|_|D|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_36() {
		Tetromino[] tetrominos = {new I_Tetromino(Orientation.NORTH, Color.YELLOW), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// I Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.clock();
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|Y|Y|Y|Y|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_37() {
		Tetromino[] tetrominos = {new T_Tetromino(Orientation.NORTH, Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// T Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.clock();
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|D|D|D|_|\n" +
			"|_|_|D|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_38() {
		Tetromino[] tetrominos = {new O_Tetromino(Color.ORANGE), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// O Tetromino
		game.clock();
		game.apply(new MoveRight());
		game.clock();
		game.apply(new ClockwiseRotation());
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|O|O|\n" +
			"|_|_|_|O|O|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_39() {
		Tetromino[] tetrominos = {new T_Tetromino(Orientation.NORTH, Color.DARK_GRAY), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// T Tetromino
		game.clock();
		game.apply(new MoveDown());
		game.clock();
		game.apply(new ClockwiseRotation());
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|D|D|D|_|\n" +
			"|_|_|D|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}

	@Test public void test_40() {
		Tetromino[] tetrominos = {new L_Tetromino(Orientation.NORTH, Color.GREEN), new O_Tetromino(Color.BLUE)};
		Game game = new Game(Arrays.asList(tetrominos).iterator(),5,5);
		// L Tetromino
		game.clock();
		game.apply(new ClockwiseRotation());
		game.apply(new ClockwiseRotation());
		game.clock();
		game.apply(new ClockwiseRotation());
		game.apply(new ClockwiseRotation());
		// O Tetromino
		String expected=
			"|_|_|_|_|_|\n" +
			"|_|_|_|G|_|\n" +
			"|_|G|G|G|_|\n" +
			"|_|_|_|_|_|\n" +
			"|_|_|_|_|_|\n";
		assertEquals(expected, game.getActiveBoard().toString());
	}
}