package ch.epfl.xblast.server.painter;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * Represents a painter of a player
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public final class PlayerPainter {

    // Store the different index which will be used
    private static final Byte ID_RANGE = 20;
    private static final Byte DIR_INDEX = 3;
    private static final Byte LOSING_LIFE_INDEX = 12;
    private static final Byte DYING_INDEX = 13;
    private static final Byte DEAD_INDEX = 14;
    private static final Byte BLINKING_INDEX = 80;

    /**
     * Private empty constructor so it cannot be instantiated
     */
    private PlayerPainter() {
    }

    /**
     * Given a tick an a player, this method computes the byte linked to the
     * image which represents the player in the file "images"
     * 
     * @param ticks
     * @param p
     * @return
     */
    public static byte byteForPlayer(int ticks, Player p) {

        State state = p.lifeState().state();
        Direction d = p.directedPositions().head().direction();

        // Select the range of the image according to its id
        int imageByte = p.id().ordinal() * ID_RANGE;

        // for a direction, we must add the number corresponding below to select
        // the right range of images
        int addForDirection = d.ordinal() * DIR_INDEX;

        // choosing which foot the player has in front of him
        int whichFoot = d.isHorizontal() ? p.position().x() % 4
                : p.position().y() % 4;
        whichFoot = ((whichFoot % 4 == 3) ? 2 : whichFoot % 2);

        // choosing the right image according to the player state
        switch (state) {
        case DEAD:
            imageByte += DEAD_INDEX;
            // the result is an invalid index
            break;
        case DYING:
            // the players stays on place looking at south
            imageByte += (p.lives() > 1 ? LOSING_LIFE_INDEX : DYING_INDEX);
            break;
        case VULNERABLE:
            // the player moves so direction and feet are important
            imageByte += addForDirection + whichFoot;
            break;
        case INVULNERABLE:
            if (ticks % 2 == 0)
                // Same as the vulnerable case
                imageByte += addForDirection + whichFoot;
            else
                // if the tick is odd, the image is white
                imageByte = BLINKING_INDEX + addForDirection + whichFoot;
            break;
        }
        return (byte) imageByte;
    }
}