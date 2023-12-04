package p2015sosym.helperclasses;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Given a set of iterables, iterates through all combinations.
 * 
 * @author sleemans
 * @param <X>
 *
 */
public class CartesianProductIterator implements Iterator<short[][]> {

	private final List<TObjectIntMap<short[]>> maps;

	private final List<TObjectIntIterator<short[]>> iterators;
	private short[][] lastResult;
	private final boolean containsEmptyMap;

	public CartesianProductIterator(List<TObjectIntMap<short[]>> maps) {
		this.maps = maps;

		//initialise the iterators
		iterators = new ArrayList<>(maps.size());
		boolean containsEmpty = false;
		for (int i = 0; i < maps.size(); i++) {
			iterators.add(maps.get(i).iterator());
			containsEmpty |= maps.get(i).isEmpty();
		}

		containsEmptyMap = containsEmpty;
	}

	public boolean hasNext() {
		if (containsEmptyMap) {
			return false;
		}
		for (int i = 0; i < maps.size(); i++) {
			if (iterators.get(i).hasNext()) {
				return true;
			}
		}
		return false;
	}

	public void remove() {
		//just, no!
		throw new RuntimeException("remove not implemented");
	}

	public short[][] next() {
		if (lastResult == null) {
			//if the result was not initialised yet, initialise it
			lastResult = new short[maps.size()][];
			for (int i = 0; i < maps.size(); i++) {
				iterators.get(i).advance();
				lastResult[i] = iterators.get(i).key();
			}
		} else {

			int mapNr = 0;
			while (!iterators.get(mapNr).hasNext()) {
				iterators.set(mapNr, maps.get(mapNr).iterator());
				iterators.get(mapNr).advance();
				lastResult[mapNr] = iterators.get(mapNr).key();
				mapNr++;
			}
			iterators.get(mapNr).advance();
			lastResult[mapNr] = iterators.get(mapNr).key();
		}
		return lastResult;
	}

	public int getLastNumberOfChoicesSum() {
		int sum = 0;
		for (int i = 0; i < maps.size(); i++) {
			sum += iterators.get(i).value();
		}
		return sum;
	}
}
