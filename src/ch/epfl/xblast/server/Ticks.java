package ch.epfl.xblast.server;

import ch.epfl.xblast.Time;

/* Used to define constants of time for the game
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 * 
 */

public interface Ticks {

    public final static int PLAYER_DYING_TICKS = 8;
    public final static int PLAYER_INVULNERABLE_TICKS = 64;
    public final static int BOMB_FUSE_TICKS = 100;
    public final static int EXPLOSION_TICKS = 30;
    public final static int WALL_CRUMBLING_TICKS = EXPLOSION_TICKS;
    public final static int BONUS_DISAPPEARING_TICKS = EXPLOSION_TICKS;
    public final static int TICKS_PER_SECOND = 20;
    public final static int TICK_NANOSECOND_DURATION = Time.NS_PER_S
            / TICKS_PER_SECOND;
    public final static int TOTAL_TICKS = TICKS_PER_SECOND * 2 * Time.S_PER_MIN;
}
