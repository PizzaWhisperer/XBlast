package ch.epfl.xblast.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.RunLengthEncoder;
import ch.epfl.xblast.server.painter.BoardPainter;
import ch.epfl.xblast.server.painter.ExplosionPainter;
import ch.epfl.xblast.server.painter.PlayerPainter;

/**
 * 
 * This class is used to serialize the GameState, using methods from
 * {@link #RunLengthEncoder}
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public final class GameStateSerializer {

	// private empty constructor so it cannot be instantiated
	private GameStateSerializer() {
	}

	/**
	 * Given a board painter and a gameState, this computes the list of bytes
	 * representing the serialized version of the gameState, adding some
	 * informations, like the size of each component
	 * 
	 * @param boardP
	 * @param s
	 * @return List<Byte>
	 */
	public static List<Byte> serialize(BoardPainter boardP, GameState s) {

		// This list will be returned, we will add it step by step the different
		// serializations (board, explosions...)
		List<Byte> gameStateSerialized = new LinkedList<>();

		// This list will contain the different serializations, but will be
		// cleared before stepping to the other section, in order to work with
		// one section at a time
		List<Byte> currentlySerialization = new LinkedList<>();

		// Serialization of the board
		Board board = s.board();
		for (Cell c : Cell.SPIRAL_ORDER) {
			currentlySerialization.add(boardP.byteForCell(board, c));
		}

		// Creating the serialized version of the board, and adding it to the
		// total list with a prefix of its length;

		currentlySerialization = new LinkedList<>(RunLengthEncoder.encode(currentlySerialization));
		currentlySerialization.add(0, (byte) currentlySerialization.size());
		gameStateSerialized.addAll(currentlySerialization);

		// Serialization of the bombs and explosions;

		currentlySerialization.clear();
		Set<Cell> blastedCells = s.blastedCells();
		Set<Cell> bombedCells = s.bombedCells().keySet();
		for (Cell c : Cell.ROW_MAJOR_ORDER) {
			if (bombedCells.contains(c)) {
				currentlySerialization.add(ExplosionPainter.byteForBomb(s.bombedCells().get(c)));
			}

			else if (blastedCells.contains(c)) {
				if (board.blockAt(c).isFree()) {
					boolean blastAtNorth = blastedCells.contains(c.neighbor(Direction.N));
					boolean blastAtEast = blastedCells.contains(c.neighbor(Direction.E));
					boolean blastAtWest = blastedCells.contains(c.neighbor(Direction.W));
					boolean blastAtSouth = blastedCells.contains(c.neighbor(Direction.S));

					currentlySerialization
							.add(ExplosionPainter.byteForBlast(blastAtNorth, blastAtEast, blastAtSouth, blastAtWest));
				} else {
					currentlySerialization.add(ExplosionPainter.BYTE_FOR_EMPTY);
				}
			} else {
				currentlySerialization.add(ExplosionPainter.BYTE_FOR_EMPTY);
			}
		}
		// Creating the serialized version of explosions and bombs, and
		// adding it to the total list with a prefix of its length;

		currentlySerialization = new LinkedList<>(RunLengthEncoder.encode(currentlySerialization));
		currentlySerialization.add(0, (byte) currentlySerialization.size());
		gameStateSerialized.addAll(currentlySerialization);
		

		// Serialization of the players;
		currentlySerialization.clear();
		List<Player> playersModifiable = new LinkedList<>(s.players());
		Collections.sort(playersModifiable, (x, y) -> Integer.compare(x.id().ordinal(), y.id().ordinal()));
		for (Player player : playersModifiable) {
			byte xPosition = (byte) player.position().x();
			byte yPosition = (byte) player.position().y();
			currentlySerialization.add((byte) player.lives());
			currentlySerialization.add(xPosition);
			currentlySerialization.add(yPosition);
			currentlySerialization.add(PlayerPainter.byteForPlayer(s.ticks(), player));
		}
		// We add the serialized version of the player without the length
		// because it's a constant

		gameStateSerialized.addAll(currentlySerialization);

		// We add the serialized time
		int remainingTime = (int) Math.ceil(s.remainingTime() / 2d);
		gameStateSerialized.add((byte) remainingTime);

		// the total list is complete
		return Collections.unmodifiableList(gameStateSerialized);
	}
}