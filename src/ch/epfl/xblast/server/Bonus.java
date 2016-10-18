package ch.epfl.xblast.server;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public enum Bonus {
    /**
     * Definition of the method {@link #applyTo(Player)} or {@link #applyTo(List)}
     * Checks is the bonus can be applied ie the fields maxBombs, bombRange,
     * lives are not at their maximum
     *
     */

    INC_BOMB {
        private static final int MAX_BOMB = 9;

        @Override
        public Player applyTo(Player player) {

            if (player.maxBombs() == MAX_BOMB)
                return player;
            else
                return player.withMaxBombs(player.maxBombs() + 1);
        }
    },

    INC_RANGE {
        private static final int MAX_RANGE = 9;

        @Override
        public Player applyTo(Player player) {

            if (player.bombRange() == MAX_RANGE)
                return player;
            else
                return player.withBombRange(player.bombRange() + 1);
        }
    },

    INC_LIFE {
        /**
         * The player obtains a life
         */
        private static final int MAX_LIFE = 5;

        @Override
        public Player applyTo(Player player) {

            if (player.lives() == MAX_LIFE)
                return player;
            else
                return player.withLife(player.lives() + 1);
        }
    },

    INVULNERABILITY {
        /**
         * The player become invulnerable
         */

        @Override
        public Player applyTo(Player player) {
            return player.withTempoInvState();
        }
    },

    REMOTE {
        /**
         * Put the fuseLenght of the bombs of the current GameState to 1 so they
         * will explode
         */

        @Override
        public List<Bomb> applyTo(List<Bomb> bombs) {
            return bombs.stream()
                    .map(b -> new Bomb(b.ownerId(), b.position(), 1, b.range()))
                    .collect(Collectors.toList());
        }
    };

    /**
     * @param bombs
     * @return the bombs of the gameState according to the bonus applied by
     *         default return the list itself
     */
    public List<Bomb> applyTo(List<Bomb> bombs) {
        return bombs;
    }

    /**
     * @param player
     * @return a new player according to the bonus applied by default returns
     *         the player itself
     */
    public Player applyTo(Player player) {
        return player;
    }

}