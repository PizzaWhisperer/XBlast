package ch.epfl.xblast.server;

import static ch.epfl.xblast.server.Block.BONUS_BOMB;
import static ch.epfl.xblast.server.Block.BONUS_RANGE;
import static ch.epfl.xblast.server.Block.CRUMBLING_WALL;
import static ch.epfl.xblast.server.Block.DESTRUCTIBLE_WALL;
import static ch.epfl.xblast.server.Block.FREE;
import static ch.epfl.xblast.server.Block.INDESTRUCTIBLE_WALL;
import static ch.epfl.xblast.server.Bonus.INC_BOMB;
import static ch.epfl.xblast.server.Bonus.INC_RANGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Test;


public class BlockEnumTests {

    @Test
    public void isFreeBehaviourIsCorrect() {
        assertTrue(FREE.isFree());
        assertFalse(INDESTRUCTIBLE_WALL.isFree());
        assertFalse(DESTRUCTIBLE_WALL.isFree());
        assertFalse(CRUMBLING_WALL.isFree());
        assertFalse(BONUS_BOMB.isFree());
        assertFalse(BONUS_RANGE.isFree());
    }

    @Test
    public void canHostPlayerBehaviorIsCorrect() {
        assertTrue(FREE.canHostPlayer());
        assertTrue(BONUS_BOMB.canHostPlayer());
        assertTrue(BONUS_RANGE.canHostPlayer());
        assertFalse(INDESTRUCTIBLE_WALL.canHostPlayer());
        assertFalse(DESTRUCTIBLE_WALL.canHostPlayer());
        assertFalse(CRUMBLING_WALL.canHostPlayer());
    }

    @Test
    public void castsShadowBehaviourIsCorrect() {
        assertTrue(INDESTRUCTIBLE_WALL.castsShadow());
        assertTrue(DESTRUCTIBLE_WALL.castsShadow());
        assertTrue(CRUMBLING_WALL.castsShadow());
        assertFalse(FREE.castsShadow());
        assertFalse(BONUS_BOMB.castsShadow());
        assertFalse(BONUS_RANGE.castsShadow());
    }

    @Test(expected = NoSuchElementException.class)
    public void freeBlockThrowsExceptionWhenRetrievingBonus() {
        FREE.associatedBonus();
    }

    @Test(expected = NoSuchElementException.class)
    public void indestructibleWallBlockThrowsExceptionWhenRetrievingBonus() {
        INDESTRUCTIBLE_WALL.associatedBonus();
    }

    @Test(expected = NoSuchElementException.class)
    public void destructibleWallBlockThrowsExceptionWhenRetrievingBonus() {
        DESTRUCTIBLE_WALL.associatedBonus();
    }

    @Test(expected = NoSuchElementException.class)
    public void crumblingWallBlockThrowsExceptionWhenRetrievingBonus() {
        CRUMBLING_WALL.associatedBonus();
    }

    @Test
    public void bonusBombBlockHasCorrectBonus() {
        assertEquals(INC_BOMB, BONUS_BOMB.associatedBonus());
    }

    @Test
    public void bonusRangeBlockHasCorrectBonus() {
        assertEquals(INC_RANGE, BONUS_RANGE.associatedBonus());
    }

    @Test
    public void isBonusBehaviourIsCorrect() {
        assertTrue(BONUS_RANGE.isPlayerBonus());
        assertTrue(BONUS_BOMB.isPlayerBonus());
        assertFalse(FREE.isPlayerBonus());
        assertFalse(INDESTRUCTIBLE_WALL.isPlayerBonus());
        assertFalse(DESTRUCTIBLE_WALL.isPlayerBonus());
        assertFalse(CRUMBLING_WALL.isPlayerBonus());
    }
}
