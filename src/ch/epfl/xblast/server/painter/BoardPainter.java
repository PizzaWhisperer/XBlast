package ch.epfl.xblast.server.painter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;

/**
 * Represents a painter of the board
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public final class BoardPainter {

	private final Map<Block, BlockImage> palette;
	private final BlockImage shadowB;

	/**
	 * Constructor of a board painter given a palet (a map linking a block to q
	 * block image) and a block image with shadow
	 * 
	 * @param palette
	 * @param shadowB
	 */
	public BoardPainter(Map<Block, BlockImage> palette, BlockImage shadowB) {

		this.palette = Collections.unmodifiableMap(new HashMap<>(palette));
		this.shadowB = shadowB;
	}

	/**
	 * Given the board and a call, this method computes the byte linked to the
	 * image which represents the block of the board at this cell
	 *
	 * @param board
	 * @param c
	 *            the cell for which we want the byte number of its image
	 * @return the byte linked to the blockImage
	 */
	public byte byteForCell(Board board, Cell c) {

		BlockImage blockImageForCell = palette.get(board.blockAt(c));
		Block neighbor = board.blockAt(c.neighbor(Direction.W));

		// If its a free block and its neighbor casts shadow, we must choose the
		// image with shadow
		if (board.blockAt(c).isFree() && neighbor.castsShadow()) {
			return (byte) shadowB.ordinal();
		} else
			return (byte) (blockImageForCell.ordinal());
	}
}
