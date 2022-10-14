package swen221.tetris.moves;

/**
 * Move the active tetromino one square downwards.
 *
 * @author David J. Pearce
 *
 */

public class MoveDown extends AbstractTranslation {

	/**
	 * a move in the downwards direction
	 */
	public MoveDown() {
		super(0,-1);
	}

}
