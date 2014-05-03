package edu.vu.isis.magnum.cloudstats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * A collection of generic useful methods for 
 * 
 * @author Michael A. Walker
 * 
 */

public class Utils {

	/**
	 * Generate the Cartesian Product of a 2-Dimensional array
	 * 
	 * @param sets
	 *           2-Dimensional array of a Type
	 * @return An ArrayList of ArrayLists of the same Type that contains the
	 *         Cartesian Product of the input
	 */
	public static <E> ArrayList<ArrayList<E>> calculateCartesianProduct(E[][] sets) {
		ArrayList<ArrayList<E>> rValue = new ArrayList<ArrayList<E>>();
		int solutions = 1;
		// Determine the number of solutions to calculate
		for (int i = 0; i < sets.length; i++) {
			solutions *= sets[i].length;
		}
		// start calculating the solutions
		// calculate each row of the solution
		for (int i = 0; i < solutions; i++) {
			int j = 1;
			ArrayList<E> row = new ArrayList<E>();
			// calculate the row's components
			for (E[] set : sets) {
				row.add(set[(i / j) % set.length]);
				j *= set.length;
			}
			rValue.add(row);
		}
		return rValue;
	}

	/**
	 * Create a Power Set. Power Set is the set of all subsets of set S,
	 * including the empty set, and S
	 * 
	 * @param inputList
	 *           in Math terms the set "S" The Collection of <T> to use
	 * @return The power set of the inputList
	 */
	static <T> List<List<T>> calculatePowerSet(Collection<T> inputList) {
		List<List<T>> ps = new ArrayList<List<T>>();
		ps.add(new ArrayList<T>()); // add the empty set

		// for every item in the original list
		for (T item : inputList) {
			List<List<T>> newPs = new ArrayList<List<T>>();
			for (List<T> subset : ps) {
				// copy all of the current powerset's subsets
				newPs.add(subset);
				// plus the subsets appended with the current item
				List<T> newSubset = new ArrayList<T>(subset);
				newSubset.add(item);
				newPs.add(newSubset);
			}
			// powerset is now powerset of list.subList(0, list.indexOf(item)+1)
			ps = newPs;
		}
		return ps;
	}

}
