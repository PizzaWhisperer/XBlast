package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;

/**
 * This class represents a gameState, which will be used by the receiver
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public final class GameState {

	// GameState has a list of player and 4 lists of images to represent the
	// board,
	// the bombs and explosions, the score line and the time line·
	private final List<Player> players;
	private final List<Image> board;
	private final List<Image> bombsAndExplosions;
	private final List<Image> scoreLine;
	private final List<Image> timeLine;

	/**
	 * Statically imbricated class which represents a player
	 * 
	 * @author Mathilde Raynal (259176)
	 * @author Richard Roubaty (260549)
	 */
	public final static class Player {
		private final PlayerID id;
		private final int lives;
		private final SubCell position;
		private final Image playerImage;

		/**
		 * Constructs a player given its parameters
		 * 
		 * @param id
		 * @param lives
		 * @param position
		 * @param playerImage
		 */
		public Player(PlayerID id, int lives, SubCell position, Image playerImage) {
			this.id = Objects.requireNonNull(id);
			this.lives = ArgumentChecker.requireNonNegative(lives);
			this.position = Objects.requireNonNull(position);
			// the image can be null e.g. : the player is dead
			this.playerImage = playerImage;
		}

		/**
		 * @return the lives of the player
		 */
		public final int lives() {
			return lives;
		}

		/**
		 * @return the ID of the player
		 */
		public final PlayerID id() {
			return id;
		}

		/**
		 * @return the x coordinate of the player
		 */
		public final int x() {
			return position.x();
		}

		/**
		 * @return the y coordinate of the player
		 */
		public final int y() {
			return position.y();
		}

		/**
		 * @return the image used to represent the player
		 */
		public final Image playerImage() {
			return playerImage;
		}
	}

	/**
	 * Constructs a GameState according to the parameters
	 * 
	 * @param players
	 * @param board
	 * @param bombsAndExplosions
	 * @param scoreLine
	 * @param timeLine
	 */
	public GameState(List<Player> players, List<Image> board, List<Image> bombsAndExplosions, List<Image> scoreLine,
			List<Image> timeLine) {

		this.players = new ArrayList<>(Objects.requireNonNull(players));
		this.board = new ArrayList<>(Objects.requireNonNull(board));
		this.bombsAndExplosions = new ArrayList<>(Objects.requireNonNull(bombsAndExplosions));
		this.scoreLine = new ArrayList<>(Objects.requireNonNull(scoreLine));
		this.timeLine = new ArrayList<>(Objects.requireNonNull(timeLine));
	}

	/**
	 * @return the list of the players
	 */
	public final List<Player> players() {
		return Collections.unmodifiableList(players);
	}

	/**
	 * @return the list of images used to represent the board
	 */
	public final List<Image> board() {
		return Collections.unmodifiableList(board);
	}

	/**
	 * @return the list of images used to represent the bombs and explosions of
	 *         the GameState
	 */
	public final List<Image> bombsAndExplosions() {
		return Collections.unmodifiableList(bombsAndExplosions);
	}

	/**
	 * @return the list of images used to represent the scoreLine
	 */
	public final List<Image> scoreLine() {
		return Collections.unmodifiableList(scoreLine);
	}

	/**
	 * @return the list of images used to represent the timeLine
	 */
	public final List<Image> timeLine() {
		return Collections.unmodifiableList(timeLine);
	}
}