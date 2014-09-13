/**
 * Copyright (c) 2012 scireum GmbH - Andreas Haufler - aha@scireum.de
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.scireum.open.cache;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a cache which contains a key value mapping.
 */
public interface Cache<K, V> {

	/**
	 * Returns the name of the cache.
	 */
	String getName();

	/**
	 * Returns the max size of the cache.
	 */
	int getMaxSize();

	/**
	 * Returns the number of entries in the cache.
	 */
	int getSize();

	/**
	 * Returns the number of reads since the last eviction.
	 */
	long getUses();

	/**
	 * Returns the statistical values of "uses" for the last some eviction
	 * intervals.
	 */
	List<Long> getUseHistory();

	/**
	 * Returns the cache-hitrate (in percent)
	 */
	Long getHitRate();

	/**
	 * Returns the statistical values of "hit rate" for the last some eviction
	 * intervals.
	 */
	List<Long> getHitRateHistory();

	/**
	 * Returns the date of the last eviction run.
	 */
	Date getLastEvictionRun();

	/**
	 * Executes the eviction strategy. An external timer is expected to call
	 * this method regularly.
	 */
	void runEviction();

	/**
	 * Clears up the complete cache.
	 */
	void clear();

	/**
	 * Returns the value associated with the given key.
	 */
	V get(K key);

	/**
	 * Returns the value associated with the given key. If the value is not
	 * found, the {@link ValueComputer} is invoked.
	 */
	V get(K key, ValueComputer<K, V> computer);

	/**
	 * Stores the given key value mapping.
	 */
	void put(K key, V value);

	/**
	 * Removes the given item from the cache.
	 */
	void remove(K key);

	/**
	 * Checks if there is a cache entry for the given key.
	 */
	boolean contains(K key);

	/**
	 * Provides access to the keys stored in this cache.
	 */
	Iterator<K> keySet();

	/**
	 * Provides access to the contents of this cache.
	 */
	List<CacheEntry<K, V>> getContents();
}
