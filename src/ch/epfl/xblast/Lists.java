package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is an util class. It provides static methods to work on lists.
 * Hence, it is not instanciable. Contains the static methods :
 * {@link #mirrored}, {@link #permutations}
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public final class Lists {

	private Lists() {
	}

	/**
	 * @param List
	 * @return the list and her reverse, but the central element is not repeated
	 */
	public static <T> List<T> mirrored(List<T> l) {
		if (l.isEmpty())
			throw new IllegalArgumentException("Empty list");
		else {
			// the part to be repeated is subList (without central element)
			List<T> subList = new ArrayList<>(l.subList(0, l.size() - 1));
			Collections.reverse(subList);
			List<T> mirList = new ArrayList<>();
			// l is the first part with the central element
			// subList is reversed and does not contain the central element
			mirList.addAll(l);
			mirList.addAll(subList);
			return mirList;
		}
	}

	/**
	 * Create a list of all the permutations possible of a list
	 * 
	 * @param l
	 *            a list
	 * @return all the permutations in a list
	 */
	public static <T> List<List<T>> permutations(List<T> l) {

		List<List<T>> listPerm = new ArrayList<>();

		// the list is empty, we return a list containing the empty list
		// the list has one element, we return the list itself in a list
		if (l.size() <= 1) {
			listPerm.add(l);
			return listPerm;
		}
		// the list has strictly more than one element
		else {
			List<T> listTail = new ArrayList<>(l.subList(1, l.size()));
			T firstEl = l.get(0);
			// this for iterates on each list given by the method itself
			for (List<T> tailPermutations : permutations(listTail)) {
				for (int i = 0; i < tailPermutations.size() + 1; i++) {
					List<T> listCompl = new LinkedList<>(tailPermutations);

					// for each list, the first element will take all the places
					// that are possible.
					listCompl.add(i, firstEl);
					listPerm.add(listCompl);
				}
			}
			return listPerm;
		}
	}
}