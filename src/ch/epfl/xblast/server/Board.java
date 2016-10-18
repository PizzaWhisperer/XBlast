package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Lists;

/**
 * Creates a board with a list of sequence of blocks as field
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public final class Board {

	private final List<Sq<Block>> blockSequence;
	private static final int INNER_ROWS = Cell.ROWS - 2;
	private static final int INNER_COLUMNS = Cell.COLUMNS - 2;
	private static final int QUARTER_ROW = Cell.ROWS / 2;
	private static final int QUARTER_COLUMNS = Cell.COLUMNS / 2;

	/**
	 * Constructor of the class
	 * 
	 * @param blocks,
	 *            A list of sequences of blocks
	 */
	public Board(List<Sq<Block>> blocks) {
		if (blocks.size() != Cell.COUNT)
			throw new IllegalArgumentException("Wrong number of block, expected 195, was " + blocks.size());
		blockSequence = Collections.unmodifiableList(new ArrayList<>(blocks));
	}

	/**
	 * Checks if a matrix has the desire number of rows/columns.
	 * 
	 * @throws IllegalArgumentException
	 *             if the numbers do not match
	 * @param matrix
	 *            : a list of list of block
	 * @param rows
	 *            : the number of rows wanted
	 * @param columns
	 *            : the number of columns wanted
	 * 
	 */
	private static void checkBlockMatrix(List<List<Block>> matrix, int rows, int columns) {
		if (matrix.size() != rows)
			throw new IllegalArgumentException(
					"Wrong number of rows, expected " + rows + " instead of " + matrix.size());
		for (int i = 0; i < rows; ++i)
			if (matrix.get(i).size() != columns)
				throw new IllegalArgumentException(
						"Wrong number of columns, expected " + columns + " instead of " + matrix.get(i).size());
	}

	/**
	 * @param rows
	 *            a matrix of Blocks For each square of the board we construct a
	 *            constant <Sq> sequence </Sq> with the block given in the
	 *            matrix.
	 * @return a <Board> board </Board> of constant sequences of blocks
	 * 
	 *         {@link #checkBlockMatrix(List, int, int)}
	 */
	public static Board ofRows(List<List<Block>> rows) {
		checkBlockMatrix(rows, Cell.ROWS, Cell.COLUMNS);

		List<Sq<Block>> board = new ArrayList<>();

		rows.stream().forEachOrdered(l -> l.stream().forEachOrdered(b -> board.add(Sq.constant(b))));

		return new Board(board);
	}

	/**
	 * @param innerBlocks
	 * @return <Board> a board which is circle around with
	 *         <Block> indestructibles walls </Block> and filled with the given
	 *         Blocks of <matrix> innerBlocks </matrix>
	 *         {@link #checkBlockMatrix(List, int, int)}
	 */
	public static Board ofInnerBlocksWalled(List<List<Block>> innerBlocks) {

		checkBlockMatrix(innerBlocks, INNER_ROWS, INNER_COLUMNS);

		int ite = 0;
		List<List<Block>> board = new ArrayList<>();
		do {
			board.add(new ArrayList<>());
			ite++;
		} while (ite < Cell.ROWS);
		ite = 0;
		// Add upper row of indestructible walls

		do {
			board.get(0).add(Block.INDESTRUCTIBLE_WALL);
			++ite;
		} while (ite < Cell.COLUMNS);

		// Creates a ring around the inner board given

		for (int row = 1; row < Cell.ROWS - 1; row++) {
			board.get(row).add(0, Block.INDESTRUCTIBLE_WALL);
			for (int column = 1; column < Cell.COLUMNS - 1; column++) {
				board.get(row).add((innerBlocks.get(row - 1)).get(column - 1));
			}
			board.get(row).add(Block.INDESTRUCTIBLE_WALL);
		}

		// Add lower row of indestructible walls

		board.get(Cell.ROWS - 1).addAll(Collections.nCopies(Cell.COLUMNS, Block.INDESTRUCTIBLE_WALL));

		return ofRows(board);
	}

	/**
	 * Given a quadrant, we construct a whole board constitued of four
	 * identicals quadrants.
	 * 
	 * The idea is to mirror the quadrant first. Here is the upper part. Then
	 * apply the {@link #mirrored} on the list upper board. The elements inside
	 * (the lists of blocks) are mirrored so we get the whole four quadrants.
	 * Then make the walls around it.
	 * 
	 * Calls {@link #checkBlockMatrix}
	 * 
	 * @param quadrantNWBlocks
	 *            a quadrant of blocks
	 * @return the board with the four quadrants
	 */
	public static Board ofQuadrantNWBlocksWalled(List<List<Block>> quadrantNWBlocks) {
		checkBlockMatrix(quadrantNWBlocks, QUARTER_ROW, QUARTER_COLUMNS);

		// construction of the upper part
		List<List<Block>> upperBoard = new ArrayList<>();
		quadrantNWBlocks.stream().forEachOrdered(l -> upperBoard.add(Lists.mirrored(l)));

		return ofInnerBlocksWalled(Lists.mirrored(upperBoard));
	}

	/**
	 * @param cell
	 * 
	 * @return the sequence of the blocks at this cell
	 */
	public Sq<Block> blocksAt(Cell c) {
		return blockSequence.get(c.rowMajorIndex());
	}

	/**
	 * @param cell
	 * 
	 * @return the first block of the sequence at this cell
	 */
	public Block blockAt(Cell c) {
		return blocksAt(c).head();
	}

	/**
	 * Creates a default board
	 * @return a board
	 */
	public static Board defaultBoard() {
		Block __ = Block.FREE;
		Block XX = Block.INDESTRUCTIBLE_WALL;
		Block xx = Block.DESTRUCTIBLE_WALL;
		return Board
				.ofQuadrantNWBlocksWalled(Arrays.asList(
						Arrays.asList(__, __, __, __, __, xx, __),
						Arrays.asList(__, XX, xx, XX, xx, XX, xx),
						Arrays.asList(__, xx, __, __, __, xx, __),
						Arrays.asList(xx, XX, __, XX, XX, XX, XX),
						Arrays.asList(__, xx, __, xx, __, __, __),
						Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
	}
}