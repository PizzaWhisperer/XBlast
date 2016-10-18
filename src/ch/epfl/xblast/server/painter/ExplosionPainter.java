package ch.epfl.xblast.server.painter;

import ch.epfl.xblast.server.Bomb;

/**
 * Represents a player or an explosion or a bomb
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */

public final class ExplosionPainter {
	public final static byte BYTE_FOR_EMPTY = (byte) 16;
	public final static byte BYTE_FOR_WHITE_BOMB = (byte) 21;
	public final static byte BYTE_FOR_BLACK_BOMB = (byte) 20;

	// private constructor, the class cannot be instantiated
	private ExplosionPainter() {
	}

	/**
	 * @param b
	 *            a bomb we want to draw
	 * @return the number of the image used, in byte.
	 */
	public static byte byteForBomb(Bomb b) {
		Integer length = b.fuseLength();
		// considering that an integer is 32 bits and that leading zeros are
		// eliminated, if a number has only one bit which equals one then it is
		// the first: by definition of the binary basis, it is power of two.
		boolean isPowerOfTwo = Integer.bitCount(length) == 1;
		return (isPowerOfTwo ? BYTE_FOR_WHITE_BOMB : BYTE_FOR_BLACK_BOMB);
	}

	/**
	 * The four boolean parameters represent the presence or absence of a blast
	 * in the neighbor-Cell in one of the four directions.
	 * 
	 * @param blastAtWest
	 * @param blastAtSouth
	 * @param blastAtEast
	 * @param blastAtNorth
	 * @return the number of the image corresponding to the current visible
	 *         blasts.
	 */
	public static byte byteForBlast(boolean blastAtNorth, boolean blastAtEast, boolean blastAtSouth,
			boolean blastAtWest) {
		Integer numberOfImage = 0;
		// getting the image number in the files "images"
		// the name of files is such that NESw means the number of image
		// 1110 = 14, with blast at north, east and west.
		if (blastAtWest)
			numberOfImage = numberOfImage | 1;
		if (blastAtSouth)
			numberOfImage = numberOfImage | (1 << 1);
		if (blastAtEast)
			numberOfImage = numberOfImage | (1 << 2);
		if (blastAtNorth)
			numberOfImage = numberOfImage | (1 << 3);
		return numberOfImage.byteValue();
	}
}