package ch.epfl.xblast;

import java.util.ArrayList;
/**
 * Contains methods used to compressed (serialize) the GameState
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
import java.util.Collections;
import java.util.List;

public final class RunLengthEncoder {

	// We create this constant so that the number of repetitions cannot
	// overflow the maximum negative byte (128)
	private static final int MAX_COUNT = 130;

	// We create this constant to find the real tag, as told in the algorithm
	private static final int TO_ADD = 2;

	private static final int REPEATED_ONCE = 1;
	private static final int REPEATED_TWICE = 2;
	/**
	 * private empty constructor so it cannot be instantiated
	 */
	private RunLengthEncoder() {
	}

	/**
	 * Given a list, it returns a compressed version. Calls
	 * {@link RunLengthEncoder#countToTag(int)}
	 * 
	 * @param list
	 * @throws IllegalArgumentException
	 *             if there is a negative byte
	 * @return the encoded version of the list
	 */
	public static List<Byte> encode(List<Byte> list) {

		List<Byte> encodedList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			int count = 1;
			byte currentByte = list.get(i);

			// We check if our byte if not negative
			ArgumentChecker.requireNonNegative(currentByte);

			while (i < list.size() - 1 && currentByte == list.get(i + 1) && count < MAX_COUNT) {
				// If we go in the while, we get a sequence
				count++;
				i++;
			}

			// Sequence ended or never started
			switch (count) {
			// for the case in which the element is repeated once or twice,
			// there is no advantage of encoding it specially here.
			case REPEATED_ONCE:
			case REPEATED_TWICE:
				encodedList.addAll(Collections.nCopies(count, currentByte));
				break;
			default:
				encodedList.add(countToTag(count));
				encodedList.add(currentByte);
			}
		}
		// If our list is empty, we don't go through the iteration and return an
		// empty list
		return Collections.unmodifiableList(encodedList);
	}

	/**
	 * Given a count, create a tag
	 * 
	 * @param count
	 * @return the byte tag associated
	 */
	private static byte countToTag(int count) {
		return (byte) (-count + TO_ADD);
	}

	/**
	 * Given a list, this method returns the full length version of this list.
	 * Calls {@link RunLengthEncoder#tagToCount(byte)}
	 * 
	 * @param list
	 * @throws IllegalArgumentException
	 *             if the last byte is negative
	 * @return the uncompressed version of the list
	 */
	public static List<Byte> decode(List<Byte> list) {

		List<Byte> decodedList = new ArrayList<>();
		// the last element can not be negative
		if (!list.isEmpty())
			ArgumentChecker.requireNonNegative(list.get(list.size() - 1));

		// to decode the list we check if the current byte is negative, if so we
		// know that we have to add the next byte this number of time + 2
		for (int i = 0; i < list.size(); i++) {
			byte currentB = list.get(i);
			if (currentB < 0) {

				// we can get the i+1-th element because the last byte cannot be
				// negative without an exception, so the currentB is not the
				// last
				decodedList.addAll(Collections.nCopies(tagToCount(currentB), list.get(i + 1)));
				i++;
			} else
				decodedList.add(currentB);
		}
		// if list is empty, we don't go through the iteration and return an
		// empty list
		return Collections.unmodifiableList(decodedList);
	}

	/**
	 * Given a tag, create a count
	 * 
	 * @param tag
	 * @return the count associated
	 */
	private static int tagToCount(byte count) {
		return Math.abs(count) + TO_ADD;
	}
}