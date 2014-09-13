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
 * Represents an average value over a given set of values.
 */
public class Average {

	private long count = 0;
	private long[] values = new long[100];
	private int index = 0;
	private int filled = 0;

	/**
	 * Adds value to the set of values on which the average is based. If the sum
	 * of all values is > Long.MAX_VALUE or the count of all values is >
	 * Long.Max_VALUE, the value is ignored.
	 */
	public void addValue(long value) {
		synchronized (values) {
			values[index++] = value;
			if (index >= values.length - 1) {
				index = 0;
			}
			if (index > filled) {
				filled = index;
			}
		}
		if (count >= Long.MAX_VALUE - 1) {
			count = 0;
		}
		count++;
	}

	public double getAvg() {
		if (filled == 0) {
			return 0.0D;
		}
		double result = 0.0d;
		for (int i = 0; i <= filled; i++) {
			result += values[i];
		}
		return result / (double) filled;
	}

	public long getCount() {
		return count;
	}
}
