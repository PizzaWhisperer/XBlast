package ch.epfl.xblast.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameState.Player;

/**
 * This class represent a component xBlast : a gameState with its parameters
 * (players, board, bombs and explosions, the scoreLine and the timeLine)
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
@SuppressWarnings("serial")
public final class XBlastComponent extends JComponent {

    private static final int FONT_SIZE = 25;
	// Store the id of the client's player
    private PlayerID myPlayerID;
    private GameState gameState;

    // Store the y coordinate if the score line
    private static final int SCORE_LINE_Y = 659;

    // Store the x coordinate to print the lives of each player·
    private static final Integer X_FOR_P1_LIFES = 96;
    private static final Integer X_FOR_P2_LIFES = 240;
    private static final Integer X_FOR_P3_LIFES = 768;
    private static final Integer X_FOR_P4_LIFES = 912;

    private static final List<Integer> X_FOR_LIVES = Arrays.asList(
            X_FOR_P1_LIFES, X_FOR_P2_LIFES, X_FOR_P3_LIFES, X_FOR_P4_LIFES);

    // Dimensions of the component
    private static final int X_PREFERED_SIZE = 960;
    private static final int Y_PREFERED_SIZE = 688;

    private static final List<Cell> rowMajor = Cell.ROW_MAJOR_ORDER;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(X_PREFERED_SIZE, Y_PREFERED_SIZE);
    }

    @Override
    public void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;

        // If the gameState is null (not instantiated for example) we do not paint it.
        if (gameState == null)
            return;

        // Else we get the lists of image representing the elements of the
        // gameState
        List<Image> board = gameState.board();
        List<Image> bombs = gameState.bombsAndExplosions();
        List<Image> scoreLine = gameState.scoreLine();
        List<Image> timeLine = gameState.timeLine();

        // Width of an image
        int width = board.get(0).getWidth(null);
        // Height of an image
        int height = board.get(0).getHeight(null);

        // Printing of the board and the bombs and explosions, iterating in the
        // row major order

        for (Cell c : rowMajor) {
            g.drawImage(board.get(rowMajor.indexOf(c)), c.x() * width,
                    c.y() * height, null);
            g.drawImage(bombs.get(rowMajor.indexOf(c)), c.x() * width,
                    c.y() * height, null);
        }

        // We set the font
        Font font = new Font("Arial", Font.BOLD, FONT_SIZE);
        g.setColor(Color.WHITE);
        g.setFont(font);

        // Printing of the players given the specific order

        // We construct a defensive copy because we will sort this list
        List<Player> players = new ArrayList<>(gameState.players());

        // We sort the list according to their y-position and their id
        sort(players, myPlayerID);

        for (Player p : players) {
            g.drawImage(p.playerImage(), xForPlayerImage(p.x()),
                    yForPlayerImage(p.y()), null);
        }

        // Printing of the ScoreLine

        int x = 0;
        // y is the height of all the board
        int y = Cell.ROWS * height;

        for (Image i : scoreLine) {
            g.drawImage(i, x, y, null);
            x = x + i.getWidth(null);
        }

        // We have to sort the player according to their id to print their head
        // and their lives
        Collections.sort(players, (p1, p2) -> Integer.compare(p1.id().ordinal(),
                p2.id().ordinal()));

        for (int i = 0; i < players.size(); i++) {
            g.drawString(Integer.toString(players.get(i).lives()),
                    X_FOR_LIVES.get(i), SCORE_LINE_Y);
        }

        // Finally, printing of the timeLine

        x = 0;
        // We add the height of the score line
        y += scoreLine.get(0).getHeight(null);

        for (Image i : timeLine) {
            g.drawImage(i, x, y, null);
            x += i.getWidth(null);
        }
    }

    /**
     * @param the
     *            position x of the player
     * @return the x position where the player will be drawn
     */
    private static int xForPlayerImage(int x) {
        return 4 * x - 24;
    }

    /**
     * @param the
     *            position y of the player
     * @return the y position where the player will be drawn
     */
    private static int yForPlayerImage(int y) {
        return 3 * y - 52;
    }

    /**
     * Given the list of player, this method sort it according to their y
     * coordinate, and if equality, print in last our player, so that it will be
     * drawn above the others
     * 
     * @param list
     * @param id
     */
    private static void sort(List<Player> list, PlayerID id) {

        // 1st : compare by y coordinate
        Comparator<Player> c1 = (p1, p2) -> Integer.compare(p1.y(), p2.y());

        // 2nd compare according to a rotation that put the id of our player in
        // the last position
        Collections.rotate(list, id.ordinal() + 1);

        Comparator<Player> c2 = (p1, p2) -> Integer.compare(
                list.indexOf(p1.id().ordinal()),
                list.indexOf(p2.id().ordinal()));

        // the final comparator
        Comparator<Player> c = c1.thenComparing(c2);
        Collections.sort(list, c);
    }

    /**
     * Change the gameState and call repaint so that the new gameState can be
     * print
     * 
     * @param gs
     * @param favorisedID
     */
    public void setGameState(GameState gs, PlayerID favorisedID) {
        gameState = gs;
        myPlayerID = favorisedID;
        repaint();
    }
}