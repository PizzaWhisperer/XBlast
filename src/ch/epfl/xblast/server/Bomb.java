package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;

/**
 * Represents a bomb.
 *
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public final class Bomb {

    private final PlayerID ownerId;
    private final Cell position;
    private final Sq<Integer> fuseLengths;
    private final int range;

    /**
     * Constructs the bomb with the given parameters. The constructor checks
     * that none of theme is empty, null or negative.
     *
     * @param ownerId
     * @param position
     * @param fuseLengths
     * @param range
     */
    public Bomb(PlayerID ownerId, Cell position, Sq<Integer> fuseLengths,
            int range) {

        if (fuseLengths.isEmpty())
            throw new IllegalArgumentException("Empty sequence");

        this.ownerId = Objects.requireNonNull(ownerId, "id is null");
        this.position = Objects.requireNonNull(position, "position is null");
        this.fuseLengths = Objects.requireNonNull(fuseLengths,
                "fuseLengths is null");
        this.range = ArgumentChecker.requireNonNegative(range);
    }

    /**
     * Constructs the bomb with the given parameters and calls the principal
     * constructor to check the parameters. The constructor makes a sequence
     * fuseLenghts out of an integer fuseLength with a lambda function.
     *
     * @param ownerId
     * @param position
     * @param fuseLength
     * @param range
     */
    public Bomb(PlayerID ownerId, Cell position, int fuseLength, int range) {

        this(ownerId, position,
                Sq.iterate(fuseLength, u -> u - 1).limit(fuseLength), range);
    }

    /**
     * @return the owner
     */
    public PlayerID ownerId() {
        return ownerId;

    }

    /**
     * @return the position of the bomb
     */
    public Cell position() {
        return position;

    }

    /**
     * @return the sequence of fuse lengths
     */
    public Sq<Integer> fuseLengths() {
        return fuseLengths;

    }

    /**
     * @return the fuse length
     */
    public int fuseLength() {
        return fuseLengths().head();

    }

    /**
     * @return the range of the bomb
     */
    public int range() {
        return range;

    }

    /**
     * Construct a list of sequence of sequence of cell touched by the explosion
     * by constructing the 4 arms (one in each direction) of the explosion
     * 
     * @return List<Sq<Sq<Cell>>>
     */
    public List<Sq<Sq<Cell>>> explosion() {
        List<Sq<Sq<Cell>>> arms = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            arms.add(explosionArmTowards(dir));
        }
        return Collections.unmodifiableList(arms);
    }

    /**
     * Constructs one arm of the explosion in the given direction
     *
     * @param dir
     * @return Sq<Sq<Cell>>
     */
    private Sq<Sq<Cell>> explosionArmTowards(Direction dir) {
        Sq<Cell> particule = Sq.iterate(position(), c -> c.neighbor(dir))
                .limit(range());
        return Sq.repeat(Ticks.EXPLOSION_TICKS, particule);
    }
}