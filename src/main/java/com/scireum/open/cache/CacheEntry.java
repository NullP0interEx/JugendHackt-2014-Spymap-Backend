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

import com.scireum.open.statistics.Counter;

/**
 * Represents a cached entry.
 */
public class CacheEntry<K, V> {
	/**
	 * Provides the number of hits.
	 */
	protected Counter hits = new Counter();
	/**
	 * Timestamp when the entry was added to the cache.
	 */
	protected long created = 0;
	/**
	 * Timestamp when the entry was last used.
	 */
	protected long used = 0;
	/**
	 * The key for this value
	 */
	protected final K key;
	/**
	 * The cached value.
	 */
	protected V value;
	/**
	 * Returns the max age of an entry.
	 */
	protected long maxAge;
	protected long nextVerification;

	public Counter getHits() {
		return hits;
	}

	public long getUses() {
		return hits.getCount();
	}

	public Date getCreated() {
		return new Date(created);
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public Date getUsed() {
		return new Date(used);
	}

	public Date getTtl() {
		return new Date(maxAge);
	}

	public void setUsed(long used) {
		this.used = used;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public CacheEntry(K key, V value, long maxAge, long nextVerification) {
		super();
		this.key = key;
		this.maxAge = maxAge;
		this.nextVerification = nextVerification;
		this.used = System.currentTimeMillis();
		this.created = used;
		this.value = value;
	}

	public long getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(long maxAge) {
		this.maxAge = maxAge;
	}

	public long getNextVerification() {
		return nextVerification;
	}

	public void setNextVerification(long nextVerification) {
		this.nextVerification = nextVerification;
	}

	public K getKey() {
		return key;
	}

}
