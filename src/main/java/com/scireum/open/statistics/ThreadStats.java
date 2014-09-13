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

import java.lang.Thread.State;

/**
 * Provides information about a java-thread.
 */
public class ThreadStats implements Comparable<ThreadStats> {
	protected String name;
	protected State state;
	protected double cpuUtilisation;
	protected long lastProbe;
	protected long lastCPUTime;

	public String getName() {
		return name;
	}

	public State getState() {
		return state;
	}

	public double getCpuUtilisation() {
		return cpuUtilisation;
	}

	@Override
	public int compareTo(ThreadStats o) {
		if (o == null) {
			return -1;
		}
		return cpuUtilisation > o.cpuUtilisation ? -1
				: cpuUtilisation == o.cpuUtilisation ? 0 : 1;
	}

}
