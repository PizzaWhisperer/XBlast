package ch.epfl.xblast.server.debug;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameStateDeserializer;
import ch.epfl.xblast.client.XBlastComponent;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.GameStateSerializer;
import ch.epfl.xblast.server.Level;
import ch.epfl.xblast.server.painter.BlockImage;
import ch.epfl.xblast.server.painter.BoardPainter;

public class GamePrinterComponent {

    private static void createUi(XBlastComponent xbc) {
        JFrame frame = new JFrame("XBlast");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(new BorderLayout());
        p.add(xbc);
        xbc.setVisible(true);
        frame.getContentPane().add(p, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        GameState s = new GameState(Board.defaultBoard(),
                GameStatePrinter.players());
        RandomEventGenerator n = new RandomEventGenerator(2016, 30, 100);

        XBlastComponent xbc = new XBlastComponent();

        SwingUtilities.invokeLater(() -> createUi(xbc));

        while (!s.isGameOver()) {

            List<Byte> sSerialized = GameStateSerializer
                    .serialize(new BoardPainter(Level.DEFAULT_PALET,
                            BlockImage.IRON_FLOOR_S), s);

            ch.epfl.xblast.client.GameState sDeserialized = GameStateDeserializer
                    .deserializeGameState(sSerialized);
            xbc.setGameState(sDeserialized, PlayerID.PLAYER_1);

            s = s.next(n.randomSpeedChangeEvents(), n.randomBombDropEvents());

        }
    }
}
