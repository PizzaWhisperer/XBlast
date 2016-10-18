package ch.epfl.xblast.server.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;

public final class GameStatePrinter {

    public static Board normalBoard() {

        List<List<Block>> temporaryList = new ArrayList<List<Block>>();

        for (int i = 0; i < 6; i++) {
            if (i == 0 || i == 5) {
                temporaryList
                        .add(Collections.nCopies(7, Block.INDESTRUCTIBLE_WALL));

            } else if (i == 2) {
                temporaryList
                        .add(Collections.nCopies(7, Block.DESTRUCTIBLE_WALL));
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

    public static void main(String[] args) throws InterruptedException {
        GameState s = new GameState(normalBoard(), players());
        String clear = "\u001b[ 2 J";
        RandomEventGenerator n = new RandomEventGenerator(2016, 30, 100);

        do {
            System.out.println(clear);
            for (Player p : s.players()) {
                System.out.println("J" + (p.id().ordinal() + 1) + " : lives "
                        + p.lives() + " ( " + p.lifeState().state()
                        + ") Bomb max : " + p.maxBombs() + " portee : "
                        + p.bombRange() + " position : " + p.position());
            }
            printGameState(s);
            s = s.next(n.randomSpeedChangeEvents(), n.randomBombDropEvents());
            Thread.sleep(30);
            System.out.println(s.ticks());
        } while (!s.isGameOver());
    }

    private GameStatePrinter() {
    }

    public static void printGameState(GameState s) {
        List<Player> ps = s.alivePlayers();
        Board board = s.board();
        Set<Cell> bombs = s.bombedCells().keySet();
        Set<Cell> blasts = s.blastedCells();

        for (int y = 0; y < Cell.ROWS; ++y) {
            xLoop: for (int x = 0; x < Cell.COLUMNS; ++x) {
                Cell c = new Cell(x, y);
                for (Player p : ps) {
                    if (p.position().containingCell().equals(c)) {
                        printInCyan(stringForPlayer(p));
                        continue xLoop;
                    }
                }
                for (Cell d : blasts) {
                    if (d.equals(c)){
                        printInYellow(stringForBlast());
                        continue xLoop;
                    }
                }
                for (Cell d : bombs) {
                    if (d.equals(c)){
                        printInBlue(stringForBomb());
                        continue xLoop;
                        }
                }
                Block b = board.blockAt(c);
                if (b == Block.CRUMBLING_WALL)
                    printInRed(stringForBlock(b));
                else
                    System.out.print(stringForBlock(b));
            }
            System.out.println();
        }
    }

    private static String stringForPlayer(Player p) {
        StringBuilder b = new StringBuilder();
        b.append(p.id().ordinal() + 1);
        switch (p.direction()) {
        case N:
            b.append('^');
            break;
        case E:
            b.append('>');
            break;
        case S:
            b.append('v');
            break;
        case W:
            b.append('<');
            break;
        }
        return b.toString();
    }

    private static String stringForBlock(Block b) {
        switch (b) {
        case FREE:
            return "  ";
        case INDESTRUCTIBLE_WALL:
            return "##";
        case DESTRUCTIBLE_WALL:
            return "??";
        case CRUMBLING_WALL:
            return "¿¿";
        case BONUS_BOMB:
            return "+b";
        case BONUS_RANGE:
            return "+r";
        default:
            throw new Error();
        }
    }

    private static String stringForBomb() {
        return "òò";
    }

    private static String stringForExplosion() {
        return "!!";
    }

    private static String stringForBlast() {
        return "**";
    }

    private static void printInRed(String s) {
        String red = "\u001b[31m";
        String std = "\u001b[m";
        System.out.print(red + s + std);
    }

    private static void printInCyan(String s) {
        String blue = "\u001b[36m";
        String std = "\u001b[m";
        System.out.print(blue + s + std);
    }

    private static void printInBlue(String s) {
        String blue = "\u001b[34m";
        String std = "\u001b[m";
        System.out.print(blue + s + std);
    }

    private static void printInYellow(String s) {
        String yellow = "\u001b[33m";
        String std = "\u001b[m";
        System.out.print(yellow + s + std);
    }
}