package ch.epfl.xblast;

/**
 * Defines constants for the time
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public interface Time {

    public final static int S_PER_MIN = 60;
    public final static int MS_PER_S = 1000;
    public final static int US_PER_S = 1000 * MS_PER_S;
    public final static int NS_PER_S = 1000 * US_PER_S;
    public final static int NS_PER_MS = NS_PER_S/ MS_PER_S;
}
