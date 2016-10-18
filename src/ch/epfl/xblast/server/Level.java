package ch.epfl.xblast.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.server.painter.BlockImage;
import ch.epfl.xblast.server.painter.BoardPainter;

/**
 * Represents a level of the game. A level has its design (the board painter)
 * and its game state
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public final class Level {

    public static final Map<Block, BlockImage> DEFAULT_PALET = defaultPalet();
    private final BoardPainter boardPainter;
    private final GameState initialGameState;

    /**
     * Constructs a level, with a given graphic style (boardPainter) and a given
     * inital state of the game (initialGameState)
     * 
     * @param boardPainter
     * @param initialGameState
     * 
     */
    public Level(BoardPainter boardPainter, GameState initialGameState) {
        this.boardPainter = Objects.requireNonNull(boardPainter);
        this.initialGameState = Objects.requireNonNull(initialGameState);
    }

    /**
     * @return the boardPainter
     */
    public final BoardPainter boardPainter() {
        return boardPainter;
    }

    /**
     * @return the initialGameState
     */
    public final GameState initialGameState() {
        return initialGameState;
    }

    /**
     * A palet is a map which associates an image to a block. Images are taken
     * from {@link ch.epfl.xblast.server.painter#BlockImage}
     *
     * @return a default palet
     * 
     */
    public static Map<Block, BlockImage> defaultPalet() {

        Map<Block, BlockImage> palette = new HashMap<>();
        palette.put(Block.FREE, BlockImage.IRON_FLOOR);
        palette.put(Block.INDESTRUCTIBLE_WALL, BlockImage.DARK_BLOCK);
        palette.put(Block.DESTRUCTIBLE_WALL, BlockImage.EXTRA);
        palette.put(Block.CRUMBLING_WALL, BlockImage.EXTRA_O);
        palette.put(Block.BONUS_BOMB, BlockImage.BONUS_BOMB);
        palette.put(Block.BONUS_RANGE, BlockImage.BONUS_RANGE);
        palette.put(Block.BONUS_LIFE, BlockImage.BONUS_LIFE);
        palette.put(Block.BONUS_STATE, BlockImage.BONUS_STATE);
        palette.put(Block.BONUS_REMOTE, BlockImage.BONUS_REMOTE);

        return Collections.unmodifiableMap(palette);
    }

    /**
     * Represent a specific level with the player placed at the four edges of a
     * walled board.
     * 
     * @author Mathilde Raynal(259176)
     * @author Richard Roubaty (260549)
     *
     */
    public static class LevelWithPlayerAtFourEdges {

        // Creation of the different parameters
        private static final Cell INITIAL_CELL_FOR_P1 = new Cell(1, 1);
        private static final Cell INITIAL_CELL_FOR_P2 = new Cell(13, 1);
        private static final Cell INITIAL_CELL_FOR_P3 = new Cell(13, 11);
        private static final Cell INITIAL_CELL_FOR_P4 = new Cell(1, 11);
        private static final int INITIAL_NB_OF_LIVES = 3;
        private static final int INITIAL_MAX_BOMB = 2;
        private static final int INITIAL_MAX_RANGE = 3;

        /**
         * Provides an initial level, where the players are placed at the four
         * inner edges (supposed free) of a board and using the default palet
         * 
         * @return a level using the default board, palet.
         */
        public static Level initialDefaultLevel() {
            GameState gs = new GameState(Board.defaultBoard(),
                    Player.players(INITIAL_CELL_FOR_P1, INITIAL_CELL_FOR_P2,
                            INITIAL_CELL_FOR_P3, INITIAL_CELL_FOR_P4,
                            INITIAL_NB_OF_LIVES, INITIAL_MAX_BOMB,
                            INITIAL_MAX_RANGE));
            BoardPainter bp = new BoardPainter(DEFAULT_PALET,
                    BlockImage.IRON_FLOOR_S);
            return new Level(bp, gs);
        }
    }
}