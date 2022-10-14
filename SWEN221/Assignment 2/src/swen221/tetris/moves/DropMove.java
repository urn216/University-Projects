// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.moves;

import swen221.tetris.logic.Board;
//import swen221.tetris.logic.Rectangle;
//import swen221.tetris.tetromino.ActiveTetromino;
//import swen221.tetris.tetromino.Tetromino;
//import swen221.tetris.tetromino.ActiveTetromino;

/**
 * Implements a "hard drop". That is, when the tetromino is immediately dropped
 * all the way down as far as it can go.
 *
 * @author David J. Pearce
 * @author Marco Servetto
 *
 */
public class DropMove implements Move {
	@Override
	public boolean isValid(Board board) {
		return board.getActiveTetromino() != null;
	}

	@Override
	public Board apply(Board board) {
		MoveDown move = new MoveDown();
		while (move.isValid(board)) {
			board = new MoveDown().apply(board);
		}
		return board;
	}

	@Override
	public String toString() {
		return "drop";
	}
}
