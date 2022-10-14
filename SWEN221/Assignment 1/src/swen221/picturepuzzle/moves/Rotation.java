// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.picturepuzzle.moves;

import java.util.Arrays;

import swen221.picturepuzzle.model.Board;
import swen221.picturepuzzle.model.Cell;
import swen221.picturepuzzle.model.Location;
import swen221.picturepuzzle.model.Operation;

/**
 * Responsible for rotating the image data in a given cell in a clockwise
 * direction.
 *
 * @author betty
 *
 */
public class Rotation implements Operation {
	/** 
	 * The Location to try to rotate. 
	 */
	private final Location location;
	/** 
	 * The number of steps to rotate. 
	 */
	private final int steps;

	/**
	 * Constructor for a rotation for the cell at a given location, rotating a given
	 * number of steps.
	 *
	 * @param loc 
	 * 				Location of the cell on which the rotation is applied.
	 * @param steps
	 * 				Number of steps to rotate in clockwise direction.
	 */
	public Rotation(Location loc, int steps) {
		this.location = loc;
		this.steps = steps;
	}
	/**
	 * Apply rotation to the selected cell.
	 *
	 * @param board The board on which the move is applied.
	 *            
	 */
	@Override
	public void apply(Board board) {
		Cell cell = board.getCellAt(location);
		if (cell == null) {return;}
		for (int i = 0; i < steps; i++) {
			rotate(cell);
		}
	}
	
	/**
	 * Does the actual rotation on a cell
	 *
	 * @param cell
	 *            The cell on which the rotation is applied.
	 */
	private static void rotate(Cell cell) {
		int[] image = cell.getImage().clone();
		int width = cell.getWidth();
		// takes the rgb value from the copied image array, and applies it, rotated 90 degrees, to the source cell.
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < width; x++) {
				cell.setRGB(width-(y+1), x, image[y * width + x]);
			}
		}
		// keeps track of how many times it's been rotated for use in declaring a win state.
		cell.incrementRotation();
	}
}
