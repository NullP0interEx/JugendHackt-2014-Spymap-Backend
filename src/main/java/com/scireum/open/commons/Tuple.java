package com.scireum.open.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a tuple of two values with arbitrary types.
 */
public class Tuple<F, S> {

	public Tuple() {
		super();
	}

	public Tuple(F first) {
		super();
		this.first = first;
	}

	public Tuple(F first, S second) {
		super();
		this.first = first;
		this.second = second;
	}

	private F first;
	private S second;

	public F getFirst() {
		return first;
	}

	public void setFirst(F first) {
		this.first = first;
	}

	public S getSecond() {
		return second;
	}

	public void setSecond(S second) {
		this.second = second;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Tuple<?, ?>)) {
			return false;
		}
		Tuple<?, ?> other = (Tuple<?, ?>) obj;
		return equal(first, other.getFirst())
				&& equal(second, other.getSecond());
	}

	private boolean equal(Object left, Object right) {
		if (left == null) {
			return right == null;
		}
		return left.equals(right);
	}

	@Override
	public String toString() {
		return first + ": " + second;
	}

	@Override
	public int hashCode() {
		return ((first == null ? "" : first).hashCode() / 2)
				+ ((second == null ? "" : second).hashCode() / 2);
	}

	public static <T extends Tuple<K, V>, K, V> List<K> firsts(
			Collection<T> tuples) {
		List<K> result = new ArrayList<K>(tuples.size());
		for (Tuple<K, V> t : tuples) {
			result.add(t.getFirst());
		}
		return result;
	}

	public static <T extends Tuple<K, V>, K, V> List<V> seconds(
			Collection<T> tuples) {
		List<V> result = new ArrayList<V>(tuples.size());
		for (Tuple<K, V> t : tuples) {
			result.add(t.getSecond());
		}
		return result;
	}

	public static <K, V> List<Tuple<K, V>> fromMap(Map<K, V> map) {
		List<Tuple<K, V>> result = new ArrayList<Tuple<K, V>>(map.size());
		for (Map.Entry<K, V> e : map.entrySet()) {
			result.add(new Tuple<K, V>(e.getKey(), e.getValue()));
		}
		return result;
	}

	public static <K, V> Map<K, V> toMap(Collection<Tuple<K, V>> values) {
		Map<K, V> result = new HashMap<K, V>();
		for (Tuple<K, V> e : values) {
			result.put(e.getFirst(), e.getSecond());
		}
		return result;
	}

}
