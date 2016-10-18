package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Note :<Code> Cell </Code> contains the values that defines the width and height of
 * the game board.
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public final class Cell {
	public static final int COLUMNS = 15;
	public static final int COUNT = 195;
	public static final int ROWS = 13;
	public static final List<Cell> ROW_MAJOR_ORDER = Collections.unmodifiableList(rowMajorOrder());
	public static final List<Cell> SPIRAL_ORDER = Collections.unmodifiableList(spiralOrder());
	private final int x;
	private final int y;

	/**
	 * Constructs the cell with the given x- and y-coordinates
	 * 
	 * @param x
	 *            the x-coordinate of the cell
	 * 
	 * 
	 * @param y
	 *            the y-coordinate of the cell
	 */
	public Cell(int x, int y) {
		this.x = Math.floorMod(x, COLUMNS);
		this.y = Math.floorMod(y, ROWS);
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	/**
	 * Creates a list with cells in a specific order, it counts from left to
	 * right beginning at the first row, repeating it to the last row.
	 * 
	 * @return the cell in the row major order
	 */
	private static List<Cell> rowMajorOrder() {
		ArrayList<Cell> rowMajorOrder = new ArrayList<Cell>();
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				rowMajorOrder.add(new Cell(j, i));
			}
		}
		return rowMajorOrder;
	}

	/**
	 * Create a list with cells in a specific order - the spiral order. It
	 * counts the first line from left to right, and then make a virtual
	 * rotation of the board (anti-clockwise) and repeat the same procedure (not
	 * repeating the last element of the previous row)
	 * 
	 * @return a list of cells in spiral order.
	 */
	private static List<Cell> spiralOrder() {
		int width = COLUMNS;
		int height = ROWS;
		ArrayList<Integer> listeX = new ArrayList<Integer>();
		ArrayList<Integer> listeY = new ArrayList<Integer>();
		boolean horizontal = true;
		ArrayList<Cell> spiral = new ArrayList<Cell>();

		for (int i = 0; i < width; ++i) {
			listeX.add(i);
		}
		for (int j = 0; j < height; ++j) {
			listeY.add(j);
		}
		ArrayList<Integer> currentColumn = new ArrayList<Integer>();
		ArrayList<Integer> currentRow = new ArrayList<Integer>();
		// decides, according to horizontal or not what is the current row and
		// column
		while (!(listeX.isEmpty() || listeY.isEmpty())) {
			if (horizontal) {
				currentColumn = listeX;
				currentRow = listeY;
			} else {
				currentColumn = listeY;
				currentRow = listeX;
			}

			int c2 = currentRow.get(0);
			currentRow.remove(0);

			// add the cell depending whether we are in a column or in a row.
			for (int i = 0; i < currentColumn.size(); ++i) {
				int c1 = currentColumn.get(i);
				if (horizontal) {
					spiral.add(new Cell(c1, c2));
				} else {
					spiral.add(new Cell(c2, c1));
				}
			}

			Collections.reverse(currentColumn);
			// makes the virtual rotation of the board
			horizontal = !horizontal;
		}
		return spiral;
	}

	/**
	 * Returns the neighbor cell in the direction given
	 * 
	 * @throws NoSuchElementException
	 *             if Neighbor is asked in a direction that doesn't exist
	 * @param dir
	 *            the neighbor's direction
	 * @return the neighbor cell
	 * 
	 */

	public final Cell neighbor(Direction dir) {
		switch (dir) {
		case N:
			if (y == 0)
				return new Cell(x, ROWS - 1);
			else
				return new Cell(x, y - 1);
		case S:
			if (y == ROWS - 1)
				return new Cell(x, 0);
			else
				return new Cell(x, y + 1);
		case E:
			if (x == COLUMNS - 1)
				return new Cell(0, y);
			else
				return new Cell(x + 1, y);
		case W:
			if (x == 0)
				return new Cell(COLUMNS - 1, y);
			else
				return new Cell(x - 1, y);
		default:
			throw new NoSuchElementException("Not a valid direction");
		}
	}

	@Override
	public final boolean equals(Object that) {

		if (that == null || that.getClass() != getClass())
			return false;
		else {
			return (that.hashCode() == this.hashCode());
		}
	}

	@Override
	public final String toString() {
		return "(" + x + "," + y + ")";
	}

	/**
	 * @return the index of the cell in the row major index list
	 */
	public final int rowMajorIndex() {
		return x + y * COLUMNS;
	}

	@Override
	public int hashCode() {
		return this.rowMajorIndex();
	}
}