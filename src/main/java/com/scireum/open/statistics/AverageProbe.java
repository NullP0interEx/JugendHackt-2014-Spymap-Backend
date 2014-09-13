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
package com.scireum.open.statistics;

/**
 * Provides a {@link Probe} which constantly computes the average of the
 * supplied values. The method readProbe resets the average, no sliding or
 * smoothing is implemented, to better represent the real system state.
 */
public class AverageProbe extends ValueProbe {
	private volatile long sum;
	private volatile long count;
	private final String unit;
	private static final long LIMIT = Long.MAX_VALUE / 2L;
	private final Double limit;

	public AverageProbe(String category, String name, String unit, Double limit) {
		super(category, name);
		this.unit = unit;
		this.limit = limit;
	}

	@Override
	public String getUnit() {
		return unit;
	}

	/**
	 * Adds a value to the average.
	 */
	public void add(long value) {
		long newSum = sum;
		long newCount = count;
		if (newCount > LIMIT || newSum > LIMIT) {
			newCount /= 2;
			newSum /= 2;
		}
		if (Long.MAX_VALUE - newSum > value) {
			sum = newSum + value;
			count = newCount + 1;
		}
	}

	@Override
	public double readProbe() {
		long newSum = sum;
		long newCount = count;
		sum = 0L;
		count = 0L;
		if (newCount == 0L) {
			return 0d;
		}
		return (double) newSum / (double) newCount;
	}

	@Override
	public Double getMaxValue() {
		return limit;
	}

}
