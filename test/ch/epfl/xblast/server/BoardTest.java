package ch.epfl.xblast.server;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

/**
 * This test tests the class Board.
 * 
 * @author Maire Cedric (259314)
 * @author Délèze Benjamin (259992)
 *
 */

public class BoardTest {
    
    @SuppressWarnings("unused")
    @Test (expected = IllegalArgumentException.class)
    public void constructorFailTest() {
        Board board = new Board(new ArrayList<Sq<Block>>());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ofRowsCheckBlockMatrixFailTest() {
        Board.ofRows(new ArrayList<List<Block>>());
    }
    
    /**
     * Uncomment the test and change the modifier of the "blockSequences" attribute to public.
     */
//    @Test
//    public void ofRowsCheckBlockMatrixAchieveTest() {
//        List<List<Block>> temporaryList = new ArrayList<List<Block>>();
//        
//        for (int i = 0; i < Cell.ROWS; i++) {
//            if (i%2 == 0) {
//                temporaryList.add(Collections.nCopies(Cell.COLUMNS, Block.INDESTRUCTIBLE_WALL));
//            }
//            else {
//                temporaryList.add(Collections.nCopies(Cell.COLUMNS, Block.FREE));
//            }
//        }
//        
//        Board temporaryBoard = Board.ofRows(temporaryList);
//        
//        List<Sq<Block>> resultBlockSq = new ArrayList<Sq<Block>>();
//        
//        for (int i = 0; i < Cell.ROWS; i++) {
//            for (int j = 0; j < Cell.COLUMNS; j++) {
//                if (i%2 == 0) {
//                    resultBlockSq.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
//                }
//                else {
//                    resultBlockSq.add(Sq.constant(Block.FREE));
//                }
//            }
//        }
//        
//        for (int i = 0; i < Cell.COUNT; i++) {
//            if (resultBlockSq.get(i).head() != temporaryBoard.blockSequences.get(i).head()) {
//                assertTrue(false);
//            }
//        }
//        
//        assertTrue(true);
//    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ofInnerBlocksWalledCheckBlockMatrixFailTest() {
        Board.ofInnerBlocksWalled(new ArrayList<List<Block>>());
    }
    
    @Test
    public void ofInnerBlocksWalledCheckBlockMatrixAchieveTest() {
        List<List<Block>> temporaryList = new ArrayList<List<Block>>();
        
        for (int i = 0; i < Cell.ROWS - 2; i++) {
            temporaryList.add(Collections.nCopies(Cell.COLUMNS - 2, Block.FREE));
        }
        
        Board temporaryBoard = Board.ofInnerBlocksWalled(temporaryList);
        
        Cell temporaryCell0 = new Cell(3, 0);
        Cell temporaryCell1 = new Cell(7, Cell.ROWS - 1);
        Cell temporaryCell2 = new Cell(0, 5);
        Cell temporaryCell3 = new Cell(Cell.COLUMNS - 1, 9);
        Cell temporaryCell4 = new Cell(6, 4);
        
        if (temporaryBoard.blockAt(temporaryCell0) != Block.INDESTRUCTIBLE_WALL) {
            assertTrue(false);
        }
        if (temporaryBoard.blockAt(temporaryCell1) != Block.INDESTRUCTIBLE_WALL) {
            assertTrue(false);
        }
        if (temporaryBoard.blockAt(temporaryCell2) != Block.INDESTRUCTIBLE_WALL) {
            assertTrue(false);
        }
        if (temporaryBoard.blockAt(temporaryCell3) != Block.INDESTRUCTIBLE_WALL) {
            assertTrue(false);
        }
        if (temporaryBoard.blockAt(temporaryCell4) != Block.FREE) {
            assertTrue(false);
        }
        
        assertTrue(true);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ofQuadrantNWBlocksWalledCheckBlockMatrixFailTest() {
        Board.ofQuadrantNWBlocksWalled(new ArrayList<List<Block>>());
    }
    
    @Test
    public void ofQuadrantNWBlocksWalledCheckBlockMatrixAchieveTest() {
        List<List<Block>> temporaryList = new ArrayList<List<Block>>();
        
        for (int i = 0; i < Cell.ROWS/2; i++) {
            if (i == 3) {
                temporaryList.add(Collections.nCopies(Cell.COLUMNS/2, Block.DESTRUCTIBLE_WALL));
            }
            else {
                temporaryList.add(Collections.nCopies(Cell.COLUMNS/2, Block.FREE));
            }
        }
        
        Board temporaryBoard = Board.ofQuadrantNWBlocksWalled(temporaryList);
        
        Cell temporaryCell0 = new Cell(3, 4);
        Cell temporaryCell1 = new Cell(3, 8);
        Cell temporaryCell2 = new Cell(13, 4);
        Cell temporaryCell3 = new Cell(11, 8);
        Cell temporaryCell4 = new Cell(6, 5);
        
        if (temporaryBoard.blockAt(temporaryCell0) != Block.DESTRUCTIBLE_WALL) {
            assertTrue(false);
        }
        if (temporaryBoard.blockAt(temporaryCell1) != Block.DESTRUCTIBLE_WALL) {
            assertTrue(false);
        }
        if (temporaryBoard.blockAt(temporaryCell2) != Block.DESTRUCTIBLE_WALL) {
            assertTrue(false);
        }
        if (temporaryBoard.blockAt(temporaryCell3) != Block.DESTRUCTIBLE_WALL) {
            assertTrue(false);
        }
        if (temporaryBoard.blockAt(temporaryCell4) != Block.FREE) {
            assertTrue(false);
        }
        
        assertTrue(true);
    }
    
    @SuppressWarnings("unused")
    @Test (expected = IllegalArgumentException.class)
    public void checkBlockMatrixRowFailTest() {
        List<List<Block>> temporaryList = new ArrayList<List<Block>>();
        temporaryList.add(new ArrayList<Block>());
        
        Board board = Board.ofRows(temporaryList);
    }
    
    @SuppressWarnings("unused")
    @Test (expected = IllegalArgumentException.class)
    public void checkBlockMatrixColumnsFailTest() {
        List<List<Block>> temporaryList = new ArrayList<List<Block>>();
        
        for (int i = 0; i < Cell.ROWS; i++) {
            temporaryList.add(new ArrayList<Block>());
            
            for (int j = 0; j < Cell.COLUMNS; j++) {
                temporaryList.get(i).add(Block.FREE);
            }
        }
        
        temporaryList.get(7).remove(3);
        
        Board board = Board.ofRows(temporaryList);
    }
    
    @Test
    public void blocksAndBlockAtTest() {
        final int COORD_X = 3; // You can change!
        final int COORD_Y = 0; // !!! DO NOT CHANGE !!!
        
        Cell temporaryCell = new Cell(COORD_X, COORD_Y);
        
        List<List<Block>> temporaryList0 = new ArrayList<List<Block>>();
        
        temporaryList0.add(new ArrayList<Block>());
        
        for (int i = 0; i < Cell.COLUMNS; i++) {
            temporaryList0.get(COORD_Y).add(Block.INDESTRUCTIBLE_WALL);
        }
        
        temporaryList0.get(COORD_Y).set(COORD_X, Block.FREE);
        
        for (int i = 1; i < Cell.ROWS; i++) {
            temporaryList0.add(Collections.nCopies(Cell.COLUMNS, Block.INDESTRUCTIBLE_WALL));
        }
        
        Board temporaryBoard = Board.ofRows(temporaryList0);
        
        Sq<Block> resultSq = Sq.constant(Block.FREE);
        
        assertEquals(resultSq.head(), temporaryBoard.blockAt(temporaryCell));
    }
}