package ch.epfl.xblast.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.debug.GameStatePrinter;

public class GameStateTests {

    public static Board normalBoard() {

        List<List<Block>> temporaryList = new ArrayList<List<Block>>();

        for (int i = 0; i < 6; i++) {
            if (i % 2 == 0) {
                temporaryList
                        .add(Collections.nCopies(7, Block.INDESTRUCTIBLE_WALL));
            } else {
                temporaryList.add(Collections.nCopies(7, Block.FREE));
            }
        }

        Board temporaryBoard = Board.ofQuadrantNWBlocksWalled(temporaryList);

        return temporaryBoard;
    }

    public static List<Player> players() {
        List<Player> players = new ArrayList<>();
        // PlayerID id, int lives, Cell position, int maxBombs,
        players.add(new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 4, 3));
        players.add(new Player(PlayerID.PLAYER_2, 3, new Cell(13, 11), 4, 3));
        players.add(new Player(PlayerID.PLAYER_3, 3, new Cell(13, 1), 4, 3));
        players.add(new Player(PlayerID.PLAYER_4, 3, new Cell(1, 11), 4, 3));
        return players;
    }

    @Test
    public void initialGameStateIsRight() {
        List<Player> players = players();
        GameState s = new GameState(normalBoard(), players);

        assertTrue(s.alivePlayers().equals(players));

        assertTrue(s.blastedCells().equals(Collections.EMPTY_SET));
        assertFalse(s.isGameOver());
        assertTrue(s.remainingTime() == Ticks.TOTAL_TICKS
                / (Ticks.TICKS_PER_SECOND));
        assertTrue(s.players().size() == 4);
    }

    // (Map<PlayerID, Optional<Direction>> speedChangeEvents, Set<PlayerID>
    // bombDropEvents
    @Test
    public void playersMoveCorrectly() {
        List<Player> players = new ArrayList<>(players());
        GameState s = new GameState(Board.defaultBoard(), players);

        Map<PlayerID, Optional<Direction>> speedChangeEvents = new HashMap<>();
        for (Player player : players) {
            speedChangeEvents.put(player.id(), Optional.of(Direction.S));
        }
        Set<PlayerID> bombDropEvents = Collections.emptySet();
        s = s.next(speedChangeEvents, bombDropEvents);
        GameStatePrinter.printGameState(s);

        SubCell newPositionPlayer1 = getPlayerNumber(1, s.players()).position();
        SubCell newPositionPlayer3 = getPlayerNumber(3, s.players()).position();
        SubCell anteriorPositionPlayer1 = getPlayerNumber(1, players())
                .position();
        SubCell anteriorPositionPlayer3 = getPlayerNumber(3, players())
                .position();
        SubCell expectedPositionPlayer1 = new SubCell(
                anteriorPositionPlayer1.x(), anteriorPositionPlayer1.y() + 1);
        SubCell expectedPositionPlayer3 = new SubCell(
                anteriorPositionPlayer3.x(), anteriorPositionPlayer3.y() + 1);
        assertTrue(newPositionPlayer1.equals(expectedPositionPlayer1));
        assertTrue(newPositionPlayer3.equals(expectedPositionPlayer3));

    }

    public static Player getPlayerNumber(int i, List<Player> players) {
        for (Player p : players) {
            if (p.id().ordinal() == i - 1) {
                return p;
            }

        }
        return null;

    }
}