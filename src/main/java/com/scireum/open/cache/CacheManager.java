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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Provides access to all managed system caches.
 */
public class CacheManager {
	private static List<Cache<?, ?>> caches = new ArrayList<Cache<?, ?>>();

	/**
	 * Returns a list of all caches.
	 */
	public static List<Cache<?, ?>> getCaches() {
		return caches;
	}

	/**
	 * Creates a cache with the given parameters.
	 * 
	 * @param name
	 *            Contains the name of the cache. This is only used to display
	 *            usage data and not used internally.
	 * @param maxSize
	 *            Contains the maximal number of entries or 0 which means
	 *            "unlimited".
	 * @param valueComputer
	 *            If a cache miss occurs, this helper will be invoked to compute
	 *            the missing value. May be <code>null</code>, then the cache
	 *            will return <code>null</code> for missing values.
	 * 
	 * @param ttl
	 *            Contains the time to live for each cached entry.
	 * @param ttlUnit
	 *            Contains the unit in which ttl is expressed.
	 * @param verifier
	 *            Used to verify is a result is still valid before it is
	 *            returned. May also be <code>null</code>.
	 * @param verificationInterval
	 *            Sets the minimal interval in which a given entry is verified.
	 * @param verificationUnit
	 *            Contains the unit in which verificationInterval is expressed.
	 */
	public static <K, V> Cache<K, V> createCache(String name, int maxSize,
			ValueComputer<K, V> valueComputer, long ttl, TimeUnit ttlUnit,
			ValueVerifier<V> verifier, long verificationInterval,
			TimeUnit verificationUnit) {
		Cache<K, V> result = new ManagedCache<K, V>(name, maxSize,
				TimeUnit.MILLISECONDS.convert(ttl, ttlUnit), valueComputer,
				verifier, TimeUnit.MILLISECONDS.convert(verificationInterval,
						verificationUnit));
		caches.add(result);
		return result;
	}

	/**
	 * Creates a cache with the given parameters.
	 * 
	 * @param name
	 *            Contains the name of the cache. This is only used to display
	 *            usage data and not used internally.
	 * @param maxSize
	 *            Contains the maximal number of entries or 0 which means
	 *            "unlimited".
	 * @param ttl
	 *            Contains the time to live for each cached entry.
	 * @param ttlUnit
	 *            Contains the unit in which ttl is expressed.
	 */
	public static <K, V> Cache<K, V> createCache(String name, int maxSize,
			long ttl, TimeUnit ttlUnit) {
		Cache<K, V> result = new ManagedCache<K, V>(name, maxSize,
				TimeUnit.MILLISECONDS.convert(ttl, ttlUnit), null, null, 10000);
		caches.add(result);
		return result;
	}

	/**
	 * Cleans up all caches.
	 */
	public static void runEviction() {
		for (Cache<?, ?> c : getCaches()) {
			c.runEviction();
		}
	}
}
