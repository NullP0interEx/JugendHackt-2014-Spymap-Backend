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

import java.util.concurrent.TimeUnit;

/**
 * Represents a counter for statistical use.
 */
public class Counter {
	/**
	 * When was this counter started or last reseted?
	 */
	private volatile long startTimeMillis = -1;
	private volatile long count = 0;

	/**
	 * Increments the counter by one.
	 */
	public void inc() {
		if (startTimeMillis < 0) {
			reset();
		}
		if (count < Long.MAX_VALUE - 10) {
			count++;
		}
	}

	public double getAvgPer(TimeUnit unit) {
		return (count) / getDuration(unit);
	}

	public long getCount() {
		return count;
	}

	public long getDuration(TimeUnit unit) {
		long delta = System.currentTimeMillis() - startTimeMillis;
		return TimeUnit.MILLISECONDS.convert(delta, unit);
	}

	public void reset() {
		startTimeMillis = System.currentTimeMillis();
		count = 0;
	}
}
