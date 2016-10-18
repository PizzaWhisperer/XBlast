package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * Represents a player of the game. id : name, lifeStates : the sequence of his
 * {@link LifeState}, directedPos : the sequence of his {@link DirectedPosition}
 * , maxBombs : the maximal number a player can lay at the same time, bombRange
 * : the range of the bomb laid by the player.
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public class Player {

    private final PlayerID id;
    private final Sq<LifeState> lifeStates;
    private final Sq<DirectedPosition> directedPos;
    private final int maxBombs;
    private final int bombRange;

    /**
     * Constructs the player with the given parameters. The constructor checks
     * that none of theme is null or not in the bounds wanted.
     * 
     * @param id
     * @param lifeStates
     * @param directedPos
     * @param maxBombs
     * @param bombRange
     * 
     */
    public Player(PlayerID id, Sq<LifeState> lifeStates,
            Sq<DirectedPosition> directedPos, int maxBombs, int bombRange) {
        this.id = Objects.requireNonNull(id);
        this.lifeStates = Objects.requireNonNull(lifeStates);
        this.directedPos = Objects.requireNonNull(directedPos);
        this.maxBombs = ArgumentChecker.requireNonNegative(maxBombs);
        this.bombRange = ArgumentChecker.requireNonNegative(bombRange);
    }

    /**
     * Constructs the player with the given parameters. The sequence of
     * LifeStates and sequence of DirectedPosition are created according to the
     * static methods : {@link #SqOfLifeStatesWith} and
     * {@link SqOfDirectedPositionToSouthWith}
     * 
     * @param id
     * @param lives
     * @param position
     * @param maxBombs
     * @param bombRange
     * 
     */

    public Player(PlayerID id, int lives, Cell position, int maxBombs,
            int bombRange) {
        this(id, SqOfLifeStatesWith(lives),
                SqOfDirectedPositionToSouthWith(position), maxBombs, bombRange);

    }

    /**
     * * Construct a sequence of life states so that : the player is
     * invulnerable during the according tick times, the player is then
     * vulnerable. The LifeStates of the sequence have the number of lives given
     * in the parameter.
     * 
     * @param newLives
     * @return the sequence of LifeStates
     */

    private final static Sq<LifeState> SqOfLifeStatesWith(int newLives) {
        if (newLives > 0)
            return Sq
                    .repeat(Ticks.PLAYER_INVULNERABLE_TICKS,
                            new LifeState(newLives, State.INVULNERABLE))
                    .concat(Sq.constant(
                            new LifeState(newLives, State.VULNERABLE)));
        else
            return Sq.constant(new LifeState(newLives, State.DEAD));

    }

    /**
     * Construct a sequence of life states so that the player is vulnerable. The
     * LifeStates of the sequence have the number of lives given in the
     * parameter.
     * 
     * @param newLives
     * @return the sequence of LifeStates
     */

    private final static Sq<LifeState> SqOfLifeStateWithoutInvulnerabilityWithLife(
            int newLives) {
        return Sq.constant(new LifeState(newLives, State.VULNERABLE));
    }

    /**
     * The DirectedPosition is so that : the position is the central SubCell of
     * the given Cell and the direction is the South. The sequence is such that
     * the Player is stopped at the DirectedPosition.
     * 
     * @param position
     * @return the Sq of DirectedPosition
     * 
     * 
     */
    private final static Sq<DirectedPosition> SqOfDirectedPositionToSouthWith(
            Cell position) {
        return DirectedPosition.stopped(new DirectedPosition(
                SubCell.centralSubCellOf(position), Direction.S));
    }

    /**
     * @return the id of the current player
     */
    public final PlayerID id() {
        return id;
    }

    /**
     * @return the number of lives of the player
     */
    public final int lives() {
        return lifeState().lives;
    }

    /**
     * @return the sequence of directed positions of the current player
     */
    public final Sq<DirectedPosition> directedPositions() {
        return directedPos;
    }

    /**
     * @return the sequence of lifeStates of the current player
     */
    public final Sq<LifeState> lifeStates() {
        return lifeStates;
    }

    /**
     * @return the current lifeState of the current player
     */
    public final LifeState lifeState() {
        return new LifeState(lifeStates().head().lives,
                lifeStates().head().state);
    }

    /**
     * @return if the player is alive (it means, the player has strictly more
     *         live than 0)
     * 
     * 
     */
    public final boolean isAlive() {
        return (lives() > 0);
    }

    /**
     * @return the current position (a SubCell) of the current player
     */
    public final SubCell position() {
        return directedPositions().head().position;
    }

    /**
     * @return the current Direction the current player is looking at
     */
    public final Direction direction() {
        return directedPositions().head().direction;
    }

    /**
     * @return the number of bombs that the player can hold at maximum
     */
    public final int maxBombs() {
        return maxBombs;
    }

    /**
     * @return the range of the bombs of the current player.
     */
    public final int bombRange() {
        return bombRange;
    }

    /**
     * The bomb is owned by the current player, is laid at the current
     * containing Cell, has the range of the current player BombRange field.
     * 
     * @return a Bomb with a brand new fuse
     * 
     * 
     */
    public final Bomb newBomb() {
        return new Bomb(id(), position().containingCell(),
                Ticks.BOMB_FUSE_TICKS, bombRange());
    }

    /**
     * @param newBombRange
     * @return a new player with a new BombRange
     */
    public final Player withBombRange(int newBombRange) {
        return new Player(id(), lifeStates(), directedPositions(), maxBombs(),
                newBombRange);
    }

    /**
     * @param newMaxBombs
     * @return a new player with a new number of max bombs
     */
    public final Player withMaxBombs(int newMaxBombs) {
        return new Player(id(), lifeStates(), directedPositions(), newMaxBombs,
                bombRange());
    }

    /**
     * @param newMaxLife
     * @return a new player with a life incremented by one
     */
    public final Player withLife(int newMaxLife) {
        return new Player(id(),
                SqOfLifeStateWithoutInvulnerabilityWithLife(newMaxLife),
                directedPositions(), maxBombs(), bombRange());
    }

    /**
     * @param newState
     * @return a new player with the same number of lives but invulnerable
     *         first, during 128 ticks
     */
    public final Player withTempoInvState() {

        return new Player(id(),
                Sq.repeat(2 * Ticks.PLAYER_INVULNERABLE_TICKS,
                        new LifeState(lives(), State.INVULNERABLE))
                .concat(Sq.constant(new LifeState(lives(), State.VULNERABLE))),
                directedPositions(), maxBombs(), bombRange());
    }

    /**
     * Creates a new sequence of lifeStates, the player is first dying
     * (according to the DYING tick value) then the player is either died for
     * the rest of the game (is not alive) or invulnerable according to the
     * corresponding tick value, if the player is alive {@link #isAlive()}.
     * 
     * @return a new sequence
     */
    public final Sq<LifeState> statesForNextLife() {

        return Sq
                .repeat(Ticks.PLAYER_DYING_TICKS,
                        new LifeState(lives(), State.DYING))
                .concat(SqOfLifeStatesWith(lives() - 1));
    }

    /**
     * This class represents the LifeState of a player : the number of his lives
     * and his state related to the enum {@link State}
     * 
     * @author Mathilde Raynal (259176)
     * @author Richard Roubaty (260549)
     */

    public final static class LifeState {
        /**
         * This enum represents the state of the player : INVULNERABLE : can not
         * lose one life if touched by a bomb VULNERABLE : can lose one life if
         * touched by a bomb and finally DYING and DEAD (the player loosed all
         * his lives).
         * 
         * @author Mathilde Raynal (259176)
         * @author Richard Roubaty (260549)
         */

        public enum State {
            INVULNERABLE, VULNERABLE, DYING, DEAD;
        }

        private final int lives;
        private final State state;

        /**
         * Construct a lifeState given the parameters.
         * 
         * @param lives
         * @param state
         * 
         * 
         */
        public LifeState(int lives, State state) {
            this.lives = ArgumentChecker.requireNonNegative(lives);
            this.state = Objects.requireNonNull(state, "state is null");
        }

        /**
         * @return number of lives of the current lifeState
         */
        public int lives() {
            return lives;
        }

        /**
         * @return current state of the current lifeState
         */
        public State state() {
            return state;
        }

        /**
         * By definition, a player can move if it is not dying or died.
         * 
         * @return if the player can move
         */
        public boolean canMove() {
            return ((state == State.INVULNERABLE)
                    || (state == State.VULNERABLE));
        }
    }

    /**
     * represent the position and the direction the player looks at.
     * 
     * @author Mathilde Raynal (259176)
     * @author Richard Roubaty (260549)
     */

    public static final class DirectedPosition {

        private final SubCell position;
        private final Direction direction;

        /**
         * Constructs a directed position given the parameters
         * 
         * @param position
         * @param direction
         */

        public DirectedPosition(SubCell position, Direction direction) {
            this.position = Objects.requireNonNull(position,
                    "position is null");
            this.direction = Objects.requireNonNull(direction,
                    "direction is null");
        }

        /**
         * Given a directed position, we create a sequence of directed position
         * as the player was stopped. He stays at the same position and looks
         * always in the same direction.
         *
         * @param DirectedPosition
         * @return SequenceOfDirectedPosition
         * 
         */
        public final static Sq<DirectedPosition> stopped(DirectedPosition p) {
            return Sq.constant(p);
        }

        /**
         * 
         * We create a sequence of directed position as if the player continue
         * to move straight forward from his initial position (given by the
         * parameter) in the direction given. (also by the parameter)
         * 
         * @param DirectedPosition
         * @return SequenceOfDirectedPositions
         * 
         */
        public final static Sq<DirectedPosition> moving(DirectedPosition p) {
            return Sq.iterate(p,
                    q -> q.withPosition(q.position().neighbor(q.direction())));
        }

        /**
         * @return the current position of the player
         */
        public final SubCell position() {
            return position;
        }

        /**
         * @return the direction the player looks at
         */
        public final Direction direction() {
            return direction;
        }

        /**
         * @param newDir
         *            a direction
         * @return a new DirectedPosition with the given parameter
         */
        public final DirectedPosition withDirection(Direction newDir) {
            return new DirectedPosition(this.position, newDir);
        }

        /**
         * @param position
         * @return a new DirectedPosition with the given position (as parameter)
         */
        public final DirectedPosition withPosition(SubCell position) {
            return new DirectedPosition(position, this.direction);
        }
    }

    /**
     * @param cellP1
     * @param cellP2
     * @param cellP3
     * @param cellP4
     *            the 4 positions of the player
     * @param lifes
     *            the number of initial lives of the player
     * @param maxBombs
     *            the number of bombs the player can drop at most
     * @param maxRange
     *            the maximum range of the bomb of a player
     * @return
     */
    public static List<Player> players(Cell cellP1, Cell cellP2, Cell cellP3,
            Cell cellP4, int lifes, int maxBombs, int maxRange) {
        List<Player> players = new ArrayList<>();
        List<Cell> initialCells = Arrays.asList(cellP1, cellP2, cellP3, cellP4);
        PlayerID[] ids = PlayerID.values();
        for (int i = 0; i < ids.length; i++) {
            Player p = new Player(ids[i], lifes, initialCells.get(i), maxBombs,
                    maxRange);
            players.add(p);
        }
        return Collections.unmodifiableList(players);
    }
}