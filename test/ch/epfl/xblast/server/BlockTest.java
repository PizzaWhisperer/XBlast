package ch.epfl.xblast.server;

import ch.epfl.xblast.server.Block;
import static org.junit.Assert.*;

import org.junit.Test;

public class BlockTest {
    
    @Test
    public void isFreeTest() {
        if (Block.FREE.isFree() != true || Block.INDESTRUCTIBLE_WALL.isFree() == true ||
                Block.DESTRUCTIBLE_WALL.isFree() == true || Block.CRUMBLING_WALL.isFree() == true) {
            assertTrue(false);
        }
        else {
            assertTrue(true);
        }
    }
    
    @Test
    public void canHostPlayerTest() {
        if (Block.FREE.canHostPlayer() != true || Block.INDESTRUCTIBLE_WALL.canHostPlayer() == true ||
                Block.DESTRUCTIBLE_WALL.canHostPlayer() == true || Block.CRUMBLING_WALL.canHostPlayer() == true) {
            assertTrue(false);
        }
        else {
            assertTrue(true);
        }
    }
    
    @Test
    public void castsShadowTest() {
        if (Block.FREE.castsShadow() == true || Block.INDESTRUCTIBLE_WALL.castsShadow() != true ||
                Block.DESTRUCTIBLE_WALL.castsShadow() != true || Block.CRUMBLING_WALL.castsShadow() != true) {
            assertTrue(false);
        }
        else {
            assertTrue(true);
        }
    }
}