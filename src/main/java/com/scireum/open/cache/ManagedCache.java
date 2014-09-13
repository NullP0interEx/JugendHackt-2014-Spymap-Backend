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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.LRUMap;

import com.scireum.open.statistics.Counter;

class ManagedCache<K, V> implements Cache<K, V> {

	private static final int MAX_HISTORY = 25;
	protected int maxSize;
	protected ValueComputer<K, V> computer;
	protected Map<K, CacheEntry<K, V>> data;
	protected Counter hits = new Counter();
	protected Counter misses = new Counter();
	protected List<Long> usesHistory = new ArrayList<Long>(MAX_HISTORY);
	protected List<Long> hitRateHistory = new ArrayList<Long>(MAX_HISTORY);
	protected Date lastEvictionRun = null;
	protected final String name;
	protected final long timeToLive;
	protected final ValueVerifier<V> verifier;
	private final long verificationInterval;

	@SuppressWarnings("unchecked")
	public ManagedCache(String name, int maxSize, long timeToLive,
			ValueComputer<K, V> valueComputer, ValueVerifier<V> verifier,
			long verificationInterval) {
		this.name = name;
		this.maxSize = maxSize;
		this.verificationInterval = verificationInterval;
		if (maxSize > 0) {
			this.data = Collections.synchronizedMap(new LRUMap(maxSize));
		} else {
			this.data = Collections
					.synchronizedMap(new HashMap<K, CacheEntry<K, V>>(maxSize));
		}
		this.timeToLive = timeToLive;
		this.computer = valueComputer;
		this.verifier = verifier;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public int getSize() {
		return data.size();
	}

	@Override
	public long getUses() {
		return hits.getCount() + misses.getCount();
	}

	@Override
	public Long getHitRate() {
		long h = hits.getCount();
		long m = misses.getCount();
		return h + m == 0L ? 0L : Math.round(100d * (double) h
				/ (double) (h + m));
	}

	@Override
	public Date getLastEvictionRun() {
		return lastEvictionRun;
	}

	@Override
	public void runEviction() {
		usesHistory.add(getUses());
		if (usesHistory.size() > MAX_HISTORY) {
			usesHistory.remove(0);
		}
		hitRateHistory.add(getHitRate());
		if (hitRateHistory.size() > MAX_HISTORY) {
			hitRateHistory.remove(0);
		}
		hits.reset();
		misses.reset();
		lastEvictionRun = new Date();
		if (timeToLive <= 0) {
			return;
		}
		// Remove all outdated entries...
		long now = System.currentTimeMillis();
		Iterator<Entry<K, CacheEntry<K, V>>> iter = data.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<K, CacheEntry<K, V>> next = iter.next();
			if (next.getValue().getMaxAge() == 0
					|| next.getValue().getMaxAge() > now) {
				return;
			}
			iter.remove();
		}
	}

	@Override
	public void clear() {
		data.clear();
		misses.reset();
		hits.reset();
		lastEvictionRun = new Date();
	}

	@Override
	public V get(K key) {
		return get(key, this.computer);
	}

	@Override
	public boolean contains(K key) {
		return data.containsKey(key);
	};

	@Override
	public V get(K key, ValueComputer<K, V> computer) {
		long now = System.currentTimeMillis();
		CacheEntry<K, V> entry = data.get(key);
		if (entry != null && entry.getMaxAge() > 0 && entry.getMaxAge() < now) {
			data.remove(key);
			entry = null;
		}
		if (verifier != null && entry != null && verificationInterval > 0
				&& entry.getNextVerification() < now) {
			if (!verifier.valid(entry.getValue())) {
				entry = null;
			} else {
				entry.setNextVerification(verificationInterval + now);
			}
		}
		if (entry != null) {
			hits.inc();
			entry.getHits().inc();
			return entry.getValue();
		} else {
			misses.inc();
			if (computer != null) {
				V value = computer.compute(key);
				put(key, value);
				return value;
			}
			return null;
		}
	}

	@Override
	public void put(K key, V value) {
		CacheEntry<K, V> cv = new CacheEntry<K, V>(key, value,
				timeToLive > 0 ? timeToLive + System.currentTimeMillis() : 0,
				verificationInterval + System.currentTimeMillis());
		data.put(key, cv);
	}

	@Override
	public void remove(K key) {
		data.remove(key);
	}

	@Override
	public Iterator<K> keySet() {
		return data.keySet().iterator();
	}

	@Override
	public List<CacheEntry<K, V>> getContents() {
		return new ArrayList<CacheEntry<K, V>>(data.values());
	}

	@Override
	public List<Long> getUseHistory() {
		return usesHistory;
	}

	@Override
	public List<Long> getHitRateHistory() {
		return hitRateHistory;
	}

}
