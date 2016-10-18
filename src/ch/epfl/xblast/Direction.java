package ch.epfl.xblast;

import java.util.NoSuchElementException;

/**
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 * 
 *
 */

public enum Direction {
    N, E, S, W;

    /**
     * Reverses the direction.
     * @throws NoSuchElementException if we have a direction that doesn't exist
     * 
     * @return The opposite direction.
     */
    public Direction opposite() {
        switch (this) {
        case N:
            return S;
        case E:
            return W;
        case S:
            return N;
        case W:
            return E;

        default:
            throw new NoSuchElementException("Not a valid direction");
        }
    }

    /**
     * @return true if the direction is horizontal to the screen (case going
     *         toward east or west)
     */
    public boolean isHorizontal() {
        switch (this) {
        case E:
        case W:
            return true;
        case N:
        case S:
        default:
            return false;
        }
    }

    /**
     * Determines whether the argument is parallel to another. {@link #opposite()}
     * 
     * @param that
     *            the direction to compare with
     * @return <b>true</b> if the direction is parallel to the argument,
     *         <b>false</b> otherwise
     */

    public boolean isParallelTo(Direction that) {
        return (this == that || this == that.opposite());
    }
}