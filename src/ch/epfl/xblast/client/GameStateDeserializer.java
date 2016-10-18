package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.RunLengthEncoder;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.client.GameState.Player;
import ch.epfl.xblast.server.Ticks;

/**
 * Used to deserialize the gameState received
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public final class GameStateDeserializer {

	private final static ImageCollection PLAYER_IMAGES = new ImageCollection("player");
	private final static ImageCollection EXPLOSION_IMAGES = new ImageCollection("explosion");
	private final static ImageCollection BLOCK_IMAGES = new ImageCollection("block");
	private final static ImageCollection SCORE_IMAGES = new ImageCollection("score");
	private final static int INDEX_FOR_LED_OFF = 20;
	private final static int INDEX_FOR_LED_ON = 21;
	private final static int BYTES_SERIALIZED_PER_PLAYER = 4;
	private final static int NUMBER_OF_PLAYERS = PlayerID.values().length;
	private final static int TEXT_MIDDLE_INDEX = 10;
	private final static int TEXT_RIGHT_INDEX = 11;
	private final static int TILE_VOID_INDEX = 12;

	private GameStateDeserializer() {
	}

	/**
	 * This static method must receive a list of bytes which is a game state
	 * serialized with
	 * {@link ch.epfl.xblast.server.GameStateSerializer#serialize}
	 * 
	 * @param list
	 * @return the game state desererialized
	 */
	public static GameState deserializeGameState(List<Byte> list) {
		// initialisation of the fields of GameState to be computed
		// when said "obtaining a list" we only take the elements not the size
		// at the first index

		List<Player> players = new ArrayList<>();
		List<Image> board = new ArrayList<>();
		List<Image> bombsAndExplosions = new ArrayList<>();
		List<Image> scoreLine = new ArrayList<>();
		List<Image> timeLine = new ArrayList<>();
		// the beginning index is the first element of the list we want to
		// obtain

		// obtaining the list for the board and deserializes it
		int beginningIndex = 1;
		int endingIndex = Byte.toUnsignedInt(list.get(0)) + 1;
		List<Byte> byteForBlocksList = new ArrayList<>(list.subList(beginningIndex, endingIndex));
		board = imagesInRowMajorOrderFrom(RunLengthEncoder.decode(byteForBlocksList));

		// obtaining the list for the bombs and explosions
		// + 1 because the element at endingIndex is the size of the list
		beginningIndex = endingIndex + 1;
		endingIndex = beginningIndex + Byte.toUnsignedInt(list.get(endingIndex));
		List<Byte> bytesForBombList = RunLengthEncoder.decode(new ArrayList<>(list.subList(beginningIndex, endingIndex)));
		bombsAndExplosions = bombsAndExplosionsDeserialization(bytesForBombList);

		// obtaining the list for the players, as no size are given, then the
		// element at ending index is the first element for the list of player.
		beginningIndex = endingIndex;
		endingIndex = beginningIndex + BYTES_SERIALIZED_PER_PLAYER * (NUMBER_OF_PLAYERS);
		List<Byte> bytesForPlayerList = new ArrayList<>(list.subList(beginningIndex, endingIndex));
		players = playerDeserialization(bytesForPlayerList);

		// making the scoreLine with the list of players
		scoreLine = imagesForScoreLine(players);

		// making the timeLine
		int remainingTime = Byte.toUnsignedInt(list.get(endingIndex));
		timeLine.addAll(Collections.nCopies(remainingTime, SCORE_IMAGES.image(INDEX_FOR_LED_ON)));
		timeLine.addAll(Collections.nCopies(Ticks.TOTAL_TICKS / Ticks.TICKS_PER_SECOND / 2 - remainingTime,
				SCORE_IMAGES.image(INDEX_FOR_LED_OFF)));

		return new GameState(players, board, bombsAndExplosions, scoreLine, timeLine);
	}

	/**
	 * @param bytesInSpiralOrder
	 *            the board decoded, in spiral order
	 * @return the list of images of the board in the row major order
	 */
	private static List<Image> imagesInRowMajorOrderFrom(List<Byte> bytesInSpiralOrder) {
		Image[] inRowMajor = new Image[Cell.COUNT];

		for (int i = 0; i < Cell.COUNT; ++i) {
			// getting the index of an element in the spiralOrder board,
			// considered as a row-major one.
			int index = Cell.SPIRAL_ORDER.get(i).rowMajorIndex();
			// we have to place that element at the index
			inRowMajor[index] = BLOCK_IMAGES.image(bytesInSpiralOrder.get(i));
		}
		return Collections.unmodifiableList(Arrays.asList(inRowMajor));
	}

	/**
	 * @param bombsByte
	 *            the list of bytes decoded of the bytes for the bombs and the
	 *            explosions
	 * @return the list of images used for the bombs and explosions (remind : we
	 *         have null when there is no bomb or explosion on the tile)
	 */
	private static List<Image> bombsAndExplosionsDeserialization(List<Byte> bombsByte) {
		List<Image> bombsAndExplosionsDeserialized = new ArrayList<>();
		for (Byte b : bombsByte) {
			// if there is no bomb, then there is no image => imageOrNull
			bombsAndExplosionsDeserialized.add(EXPLOSION_IMAGES.imageOrNull(b));
		}
		return Collections.unmodifiableList(bombsAndExplosionsDeserialized);
	}

	/**
	 * @param playersBytes
	 *            the list of the byte of the images used to represent the
	 *            players
	 * @return the list of player
	 */
	private static List<Player> playerDeserialization(List<Byte> playersBytes) {
		List<Player> playersDeserialized = new ArrayList<>();
		for (PlayerID id : PlayerID.values()) {
			// getting parameters for each player and constructing them
			// the numbers 1, 2 and 3 are added in consequence of the ordering
			// of the images.
			int lives = Byte.toUnsignedInt(playersBytes.get(id.ordinal() * NUMBER_OF_PLAYERS));
			int xPosition = Byte.toUnsignedInt(playersBytes.get(id.ordinal() * NUMBER_OF_PLAYERS + 1));
			int yPosition = Byte.toUnsignedInt(playersBytes.get(id.ordinal() * NUMBER_OF_PLAYERS + 2));
			int numberOfImage = Byte.toUnsignedInt(playersBytes.get(id.ordinal() * NUMBER_OF_PLAYERS + 3));
			// there is no image for player that are dead for example so we have
			// to use imageOrNull
			Player player = new Player(id, lives, new SubCell(xPosition, yPosition),
					PLAYER_IMAGES.imageOrNull(numberOfImage));
			playersDeserialized.add(player);
		}
		return Collections.unmodifiableList(playersDeserialized);
	}

	/**
	 * @param players
	 * @return the list of the images used to print the scoreLine
	 */
	private static List<Image> imagesForScoreLine(List<Player> players) {
		List<Image> scoreLine = new ArrayList<>();
		for (Player p : players) {
			// image for the head of the player, 2 is a number used in
			// consequence of the ordering of images
			scoreLine.add(SCORE_IMAGES.image(p.id().ordinal() * 2 + (p.lives() > 0 ? 0 : 1)));
			// images between two players
			scoreLine
					.addAll(Arrays.asList(SCORE_IMAGES.image(TEXT_MIDDLE_INDEX), SCORE_IMAGES.image(TEXT_RIGHT_INDEX)));
			// adding the images to fill the middle part
			if (p.id() == PlayerID.PLAYER_2)
				scoreLine.addAll(Collections.nCopies(8, SCORE_IMAGES.image(TILE_VOID_INDEX)));
		}
		return Collections.unmodifiableList(scoreLine);
	}
}