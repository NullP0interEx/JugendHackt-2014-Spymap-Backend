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
 * Provides a simple counter.
 */
public class CountingProbe extends ValueProbe {

	private volatile long value = 0;
	private volatile long lastRead;
	private final TimeUnit unit;
	private final Double limit;
	private String unitName;
	private double lastCollectedValue;

	public CountingProbe(String category, String name, TimeUnit unit,
			Double limit, String unitName) {
		super(category, name);
		this.unit = unit;
		this.unitName = unitName;
		this.limit = limit;
		this.lastRead = System.currentTimeMillis();
	}

	@Override
	public String getUnit() {
		return unitName;
	}

	public void inc() {
		if (value < Long.MAX_VALUE - 10) {
			value++;
		}
	}

	public void add(long increment) {
		if (Long.MAX_VALUE - 10 - increment > value) {
			value += increment;
		}
	}

	@Override
	public double readProbe() {
		long now = System.currentTimeMillis();
		double val = value;
		double duration = (now - lastRead)
				/ (double) TimeUnit.MILLISECONDS.convert(1, unit);
		value = 0;
		lastRead = now;
		if (duration == 0.0d) {
			lastCollectedValue = 0d;
		} else {
			lastCollectedValue = val / duration;
		}
		return lastCollectedValue;
	}

	public double getValue() {
		return lastCollectedValue;
	}

	@Override
	public Double getMaxValue() {
		return limit;
	}

}
