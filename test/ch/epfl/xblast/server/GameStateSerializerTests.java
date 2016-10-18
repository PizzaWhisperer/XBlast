package ch.epfl.xblast.server;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameStateDeserializer;
import ch.epfl.xblast.server.debug.GameStatePrinter;
import ch.epfl.xblast.server.painter.BlockImage;
import ch.epfl.xblast.server.painter.BoardPainter;

public class GameStateSerializerTests {

    public List<Bomb> bombOnCase2x3() {
        return Arrays.asList(new Bomb(PlayerID.PLAYER_1, new Cell(3, 4), 1, 4));
    }

    @Test
    public void blocksBytesTest() {

        GameState s = new GameState(Board.defaultBoard(),
                GameStatePrinter.players());
        BoardPainter bp = new BoardPainter(Level.defaultPalet(),
                BlockImage.IRON_FLOOR_S);
        for (Cell c : Cell.SPIRAL_ORDER) {

            Byte indW = (byte) 2;
            Byte actuel = bp.byteForCell(s.board(), c);

            if (c.x() == 1 && indW != actuel) {
                Byte expected = (byte) 1;
                Byte actual = bp.byteForCell(s.board(), c);
                assertEquals(expected, actual);
            }
            if (indW != actuel && c.x() != 1) {
                Byte expected = (byte) 0;
                Byte actual = bp.byteForCell(s.board(), c);
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    public void byteForBombsWorks() {
        GameState s = new GameState(16, Board.defaultBoard(),
                GameStatePrinter.players(), bombOnCase2x3(),
                Collections.emptyList(), Collections.emptyList());
        BoardPainter bp = new BoardPainter(Level.defaultPalet(),
                BlockImage.IRON_FLOOR_S);
        List<Byte> gameSerialized = GameStateSerializer.serialize(bp, s);
        GameStatePrinter.printGameState(s);

        System.out.println(
                GameStateDeserializer.deserializeGameState(gameSerialized));
    }
}