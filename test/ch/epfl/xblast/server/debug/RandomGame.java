package ch.epfl.xblast.server.debug;

import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;

public class RandomGame {

    public static void main(String[] args) throws InterruptedException {
        GameState s = new GameState(Board.defaultBoard(),
                GameStatePrinter.players());
        String clear = "\u001b[ 2 J";
        RandomEventGenerator n = new RandomEventGenerator(2016, 30, 100);

        do {
            // GameStatePrinter.printGameState(s);

            s = s.next(n.randomSpeedChangeEvents(), n.randomBombDropEvents());
        } while (!s.isGameOver());
    }

}
