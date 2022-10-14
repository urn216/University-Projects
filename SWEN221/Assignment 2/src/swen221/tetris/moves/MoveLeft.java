package swen221.tetris.moves;

/**
 * Move the active tetromino one square to the left.
 *
 * @author David J. Pearce
 *
 */
public class MoveLeft extends AbstractTranslation {

	/**
	 * a move in the leftwards direction
	 */
	public MoveLeft() {
		super(-1,0);
	}

}
