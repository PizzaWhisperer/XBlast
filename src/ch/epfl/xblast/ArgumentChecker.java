package ch.epfl.xblast;

/**
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public final class ArgumentChecker {

    /**
     * Private empty constructor, so it cannot be instantiated
     */
    private ArgumentChecker() {

    }

    /**
     * @param value
     * @return the value or throws an exception if the value is negative
     */
    public static int requireNonNegative(int value) {
        if (value < 0)
            throw new IllegalArgumentException("Negative value");
        return value;
    }
}
