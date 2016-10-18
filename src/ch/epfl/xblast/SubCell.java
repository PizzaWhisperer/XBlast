
package ch.epfl.xblast;

import java.util.NoSuchElementException;

/**
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 * Represent a subCell of the board, given its coodonates x and y. Note : there
 * are 240 columns of subcells, 208 rows of subcells.
 */

public final class SubCell {

    private final int x;
    private final int y;
    private final static int SUBDIVISION = 16;
    private final static int halfCell = 8;
    private final static int COLUMNS = SUBDIVISION * Cell.COLUMNS;
    private final static int ROWS = SUBDIVISION * Cell.ROWS;

    /**
     * Construct a subCell given its coordinates
     * 
     * @param x
     * @param y
     */
    public SubCell(int x, int y) {
        this.x = Math.floorMod(x, COLUMNS);
        this.y = Math.floorMod(y, ROWS);
    }

    /**
     * @return x;
     */
    public int x() {
        return x;
    }

    /**
     * @return y
     */
    public int y() {
        return y;
    }

    /**
     * @param cell
     * @return the central subCell of the given cell
     */
    public static SubCell centralSubCellOf(Cell cell) {
        int xCell = Math.floorMod(cell.rowMajorIndex(), Cell.COLUMNS);
        int yCell = (cell.rowMajorIndex() - xCell) / Cell.COLUMNS;
        return new SubCell(xCell * SUBDIVISION + halfCell, yCell * SUBDIVISION + halfCell);
    }

    /**
     * @return the distance of the subCell toward the central subCell of the
     *         same containingCell {@link #containingCell()}
     */
    public int distanceToCentral() {
        int xCentral = centralSubCellOf(this.containingCell()).x();
        int yCentral = centralSubCellOf(this.containingCell()).y();

        return Math.abs(xCentral - x) + Math.abs(yCentral - y);
    }

    /**
     * @return true if the subCell is the central subCell of its containing cell
     *         {@link #centralSubCellOf(Cell)} {@link #containingCell()}
     */
    public boolean isCentral() {
        return (this.equals(centralSubCellOf(this.containingCell())));
    }

    /**
     * @throws NoSuchElementException
     *             if d isn't a valid direction
     * @param d
     * @return the neighbor of the subCell in the given direction
     */
    public SubCell neighbor(Direction d) {
        switch (d) {
        case N:
            if (y == 0)
                return new SubCell(x, ROWS - 1);
            else
                return new SubCell(x, y - 1);
        case S:
            if (y == ROWS - 1)
                return new SubCell(x, 0);
            else
                return new SubCell(x, y + 1);
        case E:
            if (x == COLUMNS - 1)
                return new SubCell(0, y);
            else
                return new SubCell(x + 1, y);
        case W:
            if (x == 0)
                return new SubCell(COLUMNS - 1, y);
            else
                return new SubCell(x - 1, y);
        default:
            throw new NoSuchElementException("Not a valid direction");
        }
    }

    /**
     * @return the containing cell of this
     */
    public Cell containingCell() {
        int xCell = (x - Math.floorMod(x, SUBDIVISION)) / SUBDIVISION;
        int yCell = (y - Math.floorMod(y, SUBDIVISION)) / SUBDIVISION;
        return new Cell(xCell, yCell);
    }

    /**
     * overrides equals
     */
    @Override
    public boolean equals(Object that) {
        if (that == null || that.getClass() != getClass())
            return false;
        else {
            return (that.hashCode() == this.hashCode());
        }
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * overrides hashCode
     */
    @Override
    public int hashCode() {
        return x + y * COLUMNS;
    }
}