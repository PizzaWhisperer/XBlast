package ch.epfl.xblast.server;

import java.util.NoSuchElementException;

/**
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public enum Block {

    FREE, INDESTRUCTIBLE_WALL, DESTRUCTIBLE_WALL, CRUMBLING_WALL, BONUS_BOMB(
            Bonus.INC_BOMB), BONUS_RANGE(Bonus.INC_RANGE), BONUS_LIFE(
                    Bonus.INC_LIFE), BONUS_STATE(Bonus.INVULNERABILITY), BONUS_REMOTE(
                            Bonus.REMOTE);

    private Bonus maybeAssociatedBonus;

    /**
     * @return true if and only if the block is a free space (Block.FREE)
     */
    public boolean isFree() {
        return (this == FREE);
    }

    /**
     * @return true if <Block> this </Block> is free {@link #isFree()} or is a
     *         Bonus {@link #isPlayerBonus()}
     */
    public boolean canHostPlayer() {
        return (this.isFree() || this.isPlayerBonus()
                || this.isGameStateBonus());
    }

    /**
     * @return true if <Block> this </Block> is a bonus
     */
    public boolean isBonus() {
        return (this.isGameStateBonus() || this.isPlayerBonus());
    }

    /**
     * @return true if <Block> this </Block> is a bonus that can be applied to a
     *         gameState
     */
    public boolean isGameStateBonus() {
        return (this == BONUS_REMOTE);
    }
    /**
     * @return true if <Block> this </Block> is a bonus that can be applied to a
     *         player
     */
    public boolean isPlayerBonus() {
        return (this == BONUS_BOMB || this == BONUS_RANGE || this == BONUS_LIFE
                || this == BONUS_STATE);
    }

    /**
     * @return true if and only if <Block> this </Block> is a wall
     */
    public boolean castsShadow() {
        return (this == INDESTRUCTIBLE_WALL || this == DESTRUCTIBLE_WALL
                || this == CRUMBLING_WALL);
    }

    /**
     * Constructs a block with an associated bonus
     * 
     * @param maybeAssociatedBonus
     */
    private Block(Bonus maybeAssociatedBonus) {
        this.maybeAssociatedBonus = maybeAssociatedBonus;
    }

    /**
     * Constructs a block without bonus associated
     */
    private Block() {
        this.maybeAssociatedBonus = null;
    }

    /**
     * @return the associated bonus of the block or throws a
     *         <tt>NoSuchElementException</tt>
     */
    public Bonus associatedBonus() {
        if (this.maybeAssociatedBonus == null)
            throw new NoSuchElementException("No bonus associated");
        else
            return this.maybeAssociatedBonus;
    }
}