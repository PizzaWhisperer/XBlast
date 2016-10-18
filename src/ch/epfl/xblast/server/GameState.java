package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.Lists;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * The class GameState represents the state of the game (players, board, bombs,
 * explosions, blast at a ticks) and calculates the next state, given a
 * permutation of the player to resolve conflicts
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public final class GameState {

    private final int ticks;
    private final Board board;
    private final List<Player> players;
    private final List<Bomb> bombs;
    private final List<Sq<Sq<Cell>>> explosions;
    private final List<Sq<Cell>> blasts;
    private static final List<List<PlayerID>> permsList = Lists
            .permutations(Arrays.asList(PlayerID.values()));
    private static final Random RANDOM = new Random(2016);

    /**
     * Constructor that checks the arguments, constructs a defensive copy and
     * makes them unmodifiable.
     * 
     * @param ticks
     * @param board
     * @param players
     * @param bombs
     * @param explosions
     * @param blasts
     */
    public GameState(int ticks, Board board, List<Player> players,
            List<Bomb> bombs, List<Sq<Sq<Cell>>> explosions,
            List<Sq<Cell>> blasts) {

        this.ticks = ArgumentChecker.requireNonNegative(ticks);

        if (players.size() != 4)
            throw new IllegalArgumentException("Wrong number of players");
        else
            this.players = Collections
                    .unmodifiableList(new ArrayList<>(players));

        this.board = Objects.requireNonNull(board);
        this.bombs = Objects.requireNonNull(
                Collections.unmodifiableList(new ArrayList<>(bombs)));
        this.explosions = Objects.requireNonNull(
                Collections.unmodifiableList(new ArrayList<>(explosions)));
        this.blasts = Objects.requireNonNull(
                Collections.unmodifiableList(new ArrayList<>(blasts)));
    }

    /**
     * Constructs a new initial GameState.
     * 
     * @param board
     * @param players
     */
    public GameState(Board board, List<Player> players) {
        this(0, board, players, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
    }

    /**
     * @return elapsed ticks
     */
    public int ticks() {
        return ticks;
    }

    /**
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return (ticks() > Ticks.TOTAL_TICKS || winner().isPresent());
    }

    /**
     * @return the remaining time
     */
    public double remainingTime() {
        double remainingTicks = Ticks.TOTAL_TICKS - ticks;
        return remainingTicks / Ticks.TICKS_PER_SECOND;
    }

    /**
     * @return the winner or an optional empty object if there is not
     */
    public Optional<PlayerID> winner() {
        if (alivePlayers().size() == 1) {
            return Optional.of(alivePlayers().get(0).id());
        } else
            return Optional.empty();
    }

    /**
     * @return the current board
     */
    public Board board() {
        return board;
    }

    /**
     * @return the list of the players
     */
    public List<Player> players() {
        return Collections.unmodifiableList(players);
    }

    /**
     * @return the list of the alive players
     */
    public List<Player> alivePlayers() {
        return Collections.unmodifiableList(players.stream()
                .filter(l -> l.isAlive()).collect(Collectors.toList()));
    }

    /**
     * Updates blasts, creates the new ones
     * 
     * @param blasts0
     * @param board0
     * @param explosions0
     * @return a list of sequence of cell where there is a blast
     */
    private static List<Sq<Cell>> nextBlasts(List<Sq<Cell>> blasts0,
            Board board0, List<Sq<Sq<Cell>>> explosions0) {

        List<Sq<Cell>> blasts1 = new LinkedList<>();

        // 1. update the current blasts
        blasts0.stream()
                .filter(b -> (board0.blockAt(b.head()).isFree()
                        && !b.tail().isEmpty()))
                .map(b -> b.tail()).forEach(blasts1::add);

        // 2. creates the new blast from explosions.
        explosions0.stream().filter(arm -> !arm.isEmpty())
                .map(arm -> arm.head()).forEach(blasts1::add);

        return Collections.unmodifiableList(blasts1);
    }

    /**
     * @return the set of the Cell where there is a blast
     */
    public Set<Cell> blastedCells() {
        return blastedCellsWith(this.blasts);
    }

    /**
     * Gives the cells where there is a blast
     * 
     * @param blasts
     * @return the blasted Cells (Set) given a list of blasts
     */
    private static Set<Cell> blastedCellsWith(List<Sq<Cell>> blasts) {

        return Collections
                .unmodifiableSet((blasts.stream().filter(sq -> !sq.isEmpty())
                        .map(sq -> sq.head())).collect(Collectors.toSet()));
    }

    /**
     * Computes the next board (evolution of walls...) according to the consumed
     * bonus and the blasted cells
     * 
     * @param board0
     * @param consumedBonuses
     * @param blastedCells1
     * @return the next board given the parameters
     */
    private static Board nextBoard(Board board0, Set<Cell> consumedBonuses,
            Set<Cell> blastedCells1) {
        List<Sq<Block>> board1 = new ArrayList<>();

        for (Cell c : Cell.ROW_MAJOR_ORDER) {

            // a bonus is consumed and so disappears

            if (consumedBonuses.contains(c))
                board1.add(Sq.constant(Block.FREE));

            // specific actions of a bomb towards the board

            else if (blastedCells1.contains(c)) {

                if (board0.blockAt(c) == Block.DESTRUCTIBLE_WALL) {
                    List<Block> listBlock = Arrays.asList(Block.BONUS_BOMB,
                            Block.BONUS_RANGE, Block.BONUS_LIFE,
                            Block.BONUS_STATE, Block.BONUS_REMOTE, Block.FREE);

                    /// destruction of a crumbling wall

                    board1.add(Sq.constant(Block.CRUMBLING_WALL)
                            .limit(Ticks.WALL_CRUMBLING_TICKS)
                            .concat(Sq.constant(listBlock
                                    .get(RANDOM.nextInt(listBlock.size())))));

                    // destruction of a bonus

                } else if (board0.blockAt(c).isBonus()) {
                    board1.add(board0.blocksAt(c).tail()
                            .limit(Ticks.BONUS_DISAPPEARING_TICKS)
                            .concat(Sq.constant(Block.FREE)));

                } else
                    // there is a blast but the block isn't modified
                    board1.add(board0.blocksAt(c).tail());

            } else
                // there is nothing the block isn't modified
                board1.add(board0.blocksAt(c).tail());

        }
        return new Board(board1);
    }

    /**
     * Computes the next explosions
     * 
     * @param explosions0
     * @return a list<sq<sq<cell>>> of the evolved explosions
     */
    private static List<Sq<Sq<Cell>>> nextExplosions(
            List<Sq<Sq<Cell>>> explosions0) {

        // we check if the explosion is not over
        return Collections.unmodifiableList(
                explosions0.stream().filter(ex -> !ex.tail().isEmpty())
                        .map(ex -> ex.tail()).collect(Collectors.toList()));
    }

    /**
     * 
     * Computes if a bomb can be dropped given the parameters, and according to
     * an order of priority of the players
     * 
     * @param players0
     *            this parameter is assumed to be the list of players in the
     *            right order to cope with conflicts (list computed in
     *            {@link #next})
     * @param bombDropEvents
     * @param bombs0
     * @return the list of the bomb being dropped at the time
     */
    private static List<Bomb> newlyDroppedBombs(List<Player> players0,
            Set<PlayerID> bombDropEvents, List<Bomb> bombs0) {

        List<Bomb> newlyDroppedBombs = new ArrayList<>();
        Set<Cell> cellsAlreadyBombed = new HashSet<>();
        Map<PlayerID, Integer> bombPerPlayer = new HashMap<>();

        bombs0.stream().forEachOrdered(b -> {
            cellsAlreadyBombed.add(b.position());
            players0.stream().forEachOrdered(p -> {
                if (b.ownerId().equals(p.id()))
                    bombPerPlayer.put(p.id(),
                            bombPerPlayer.getOrDefault(p.id(), 0) + 1);
            });
        });

        // Computes how many bombs have each player on the board

        players0.stream().forEachOrdered(p -> {
            if (!cellsAlreadyBombed.contains(p.position().containingCell())
                    && p.isAlive() && bombDropEvents.contains(p.id())
                    && bombPerPlayer.getOrDefault(p.id(), 0) < p.maxBombs()) {
                newlyDroppedBombs.add(p.newBomb());
                cellsAlreadyBombed.add(p.position().containingCell());
            }
        });

        return Collections.unmodifiableList(newlyDroppedBombs);
    }

    /**
     * {@link #bombedCellsWith}
     * 
     * @return a map that associated the bombed cells to their bomb
     */
    public Map<Cell, Bomb> bombedCells() {
        return bombedCellsWith(this.bombs);
    }

    /**
     * Returns the cells which contains a bomb
     * 
     * @parameter bombs a list of bomb
     * @return a map that associated the bombs to their cell
     */
    private static Map<Cell, Bomb> bombedCellsWith(List<Bomb> bombs) {
        return Collections.unmodifiableMap(bombs.stream().collect(
                Collectors.toMap(b -> b.position(), Function.identity())));
    }

    /**
     * 
     * Computes the evolution of the players
     * 
     * @param players0
     * @param playerBonuses
     * @param bombedCells1
     * @param board1
     * @param blastedCells1
     * @param speedChangeEvents
     * @return
     */

    private static List<Player> nextPlayers(List<Player> players0,
            Map<PlayerID, Bonus> playerBonuses, Set<Cell> bombedCells1,
            Board board1, Set<Cell> blastedCells1,
            Map<PlayerID, Optional<Direction>> speedChangeEvents) {

        List<Player> players1 = new ArrayList<>();

        for (Player player : players0) {

            PlayerID id = player.id();
            Sq<LifeState> lifeStates1 = player.lifeStates();
            Sq<DirectedPosition> directedPos1 = player.directedPositions();
            int maxBomb = player.maxBombs();
            int bombRange = player.bombRange();
            SubCell currentPosition = player.position();
            Direction currentDirection = player.direction();
            DirectedPosition currentDirectedPosition = player
                    .directedPositions().head();

            // 1. evolution of the directed positions of the player according to
            // - its will to change direction
            // - its ability to move

            boolean wantsToChange = speedChangeEvents.containsKey(id);

            if (wantsToChange) {

                Optional<Direction> nextDir = speedChangeEvents.get(id);
                DirectedPosition directedPosAtNextCentral = directedPos1
                        .findFirst(p -> p.position().isCentral());

                // if the current direction and the next direction are not
                // parallel then the player
                // can not move to a central subCell
                boolean mustReachNextCentral = nextDir
                        .map(d -> !d.isParallelTo(currentDirection))
                        .orElse(true);

                if (mustReachNextCentral) {

                    // sequence of directed positions before the next
                    // central subCell
                    directedPos1 = DirectedPosition
                            .moving(currentDirectedPosition)
                            .takeWhile(p -> !p.position().isCentral());

                    // if the player stops
                    if (nextDir.equals(Optional.empty())) {
                        directedPos1 = directedPos1.concat(DirectedPosition
                                .stopped(directedPosAtNextCentral));

                    }
                    // if the player moves after the next central
                    else {
                        // way after the next central SubCell including
                        // central

                        directedPos1 = directedPos1.concat(
                                DirectedPosition.moving(directedPosAtNextCentral
                                        .withDirection(nextDir.get())));
                    }
                } else {
                    directedPos1 = DirectedPosition
                            .moving(currentDirectedPosition
                                    .withDirection(nextDir.get()));
                }
            }

            // so far we have computed the next Sequences of directed position
            // without knowing if the player can move
            // consumes the head of the sequence of directed positions if player
            // can move

            if (playerCanMove(player, directedPos1, board1, currentPosition,
                    bombedCells1)) {
                directedPos1 = directedPos1.tail();
            }

            // 2. evolution of the player's lifeState according to :
            // - his new position

            SubCell nextPosition = directedPos1.head().position();
            boolean bombHasEffect = (player.lifeState()
                    .state() == State.VULNERABLE);
            if (blastedCells1.contains(nextPosition.containingCell())
                    && bombHasEffect) {
                lifeStates1 = player.statesForNextLife();
            } else
                lifeStates1 = lifeStates1.tail();

            // 3. evolution of the player's capacities :

            Player player1 = new Player(id, lifeStates1, directedPos1, maxBomb,
                    bombRange);

            if (playerBonuses.containsKey(id)) {
                Bonus currentBonus = playerBonuses.get(id);
                player1 = currentBonus.applyTo(player1);
            }
            players1.add(player1);
        }
        return Collections.unmodifiableList(players1);
    }

    /**
     * 
     * Given the obstacles returns a boolean which says if the player can move
     * in the specific direction
     * 
     * @param player
     * @param directedPos1
     * @param board1
     * @param currentPosition
     * @param bombedCells1
     * @return
     */
    private static boolean playerCanMove(Player player,
            Sq<DirectedPosition> directedPos1, Board board1,
            SubCell currentPosition, Set<Cell> bombedCells1) {

        SubCell nextPosition = directedPos1.tail().head().position();
        Direction nextDirection = directedPos1.tail().head().direction();
        Cell futureContainingCell = nextPosition.containingCell()
                .neighbor(nextDirection);
        Block futureBlock = board1.blockAt(futureContainingCell);

        // check if the player blocked by its state
        if (!player.lifeState().canMove()) {
            return false;
        }
        // see if player blocked by a wall
        if (currentPosition.isCentral() && !futureBlock.canHostPlayer()) {
            return false;
        }
        // see if player blocked by a bomb
        if (bombedCells1.contains(currentPosition.containingCell())) {
            int currentDistanceToCentral = currentPosition.distanceToCentral();
            int nextDistanceToCentral = nextPosition.distanceToCentral();
            boolean isGoingToCentral = currentDistanceToCentral > nextDistanceToCentral;

            if (isGoingToCentral && currentDistanceToCentral == 6) {

                return false;
            }
        }
        // if it reaches this step he can moves
        return true;
    }

    /**
     * It creates the next GameState given the evolution of the current
     * parameters given 2 parameters : the changes of direction and the bomb
     * drop events
     * 
     * @param speedChangeEvents
     * @param bombDropEvents
     *
     * @return the next GameState given this parameters its calls
     *         {@link #nextBoard} {@link #nextPlayers}
     *         {@link #newlyDroppedBombs}
     */
    public GameState next(Map<PlayerID, Optional<Direction>> speedChangeEvents,
            Set<PlayerID> bombDropEvents) {

        List<PlayerID> currentPerm = permsList.get(ticks % permsList.size());

        // We create this list so it can be modified if the bonus
        // Bonus.BONUS_REMOTE is consumed
        List<Bomb> bombsBis = new ArrayList<>(bombs);

        // creation of the list of priority of players given the current
        // permutation of ID's
        List<Player> currentPermOfPlayers = new ArrayList<>();
        for (PlayerID id : currentPerm) {
            players.stream().filter(p -> p.id().equals(id))
                    .forEach(currentPermOfPlayers::add);
        }

        // Map the playerID to his actual trying-to-consumed Bonus.
        // Set of the current consumed Bonuses.

        Set<Cell> consumedBonuses = new HashSet<>();
        Map<PlayerID, Bonus> playerBonuses = new EnumMap<>(PlayerID.class);

        for (Player p : currentPermOfPlayers) {
            SubCell currentPosition = p.position();
            Cell currentCell = currentPosition.containingCell();
            Block currentBlock = board.blockAt(currentCell);

            // can consume iff the block is a bonus and its position is a
            // central subCell
            if ((currentBlock.isBonus()) && currentPosition.isCentral()
                    && !consumedBonuses.contains(currentCell)) {
                consumedBonuses.add(currentCell);

                if (currentBlock.isPlayerBonus())
                    playerBonuses.put(p.id(), currentBlock.associatedBonus());
                if (currentBlock.isGameStateBonus())
                    // If the bonus is consumed we put the fuselenghts to 1
                    bombsBis = currentBlock.associatedBonus().applyTo(bombsBis);
                // Else the list is not modified and is a copy of bombs
            }
        }

        // nextBlast

        List<Sq<Cell>> blasts1 = nextBlasts(blasts, board, explosions);

        // nextBoard

        Set<Cell> newBlasts = blastedCellsWith(blasts1);
        Board board1 = nextBoard(board, consumedBonuses, newBlasts);

        // nextExplosions

        List<Sq<Sq<Cell>>> explosions1 = new ArrayList<>(
                nextExplosions(explosions));
        List<Bomb> explodedBombs = new ArrayList<>();
        bombsBis.stream()
                .filter(b -> newBlasts.contains(b.position())
                        || b.fuseLengths().tail().isEmpty())
                .forEachOrdered(b -> {
                    explodedBombs.add(b);
                    explosions1.addAll(b.explosion());
                });

        // nextBomb

        // Creates a list of the old bombs and the newly dropped ones

        List<Bomb> tempBomb = new ArrayList<>(bombsBis);
        tempBomb.addAll(newlyDroppedBombs(currentPermOfPlayers, bombDropEvents,
                bombsBis));

        List<Bomb> bombs1 = new ArrayList<>();
        tempBomb.stream()
                .filter(b -> !b.fuseLengths().tail().isEmpty()
                        && !newBlasts.contains(b.position()))
                .forEachOrdered(b -> bombs1.add(new Bomb(b.ownerId(),
                        b.position(), b.fuseLengths().tail(), b.range())));
                        // nextPlayers

        // given the parameters of the next GameState, we compute the evolution
        // of the player, given their new direction if they choose to change
        List<Player> players1 = nextPlayers(players, playerBonuses,
                bombedCellsWith(bombs1).keySet(), board1, newBlasts,
                speedChangeEvents);

        // we create a new GameState and increase by one the ticks
        return new GameState(ticks() + 1, board1, players1, bombs1, explosions1,
                blasts1);
    }
}