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
 * Used to measure the duration of arbitrary code pieces.
 */
public class Watch {

	private long startTime = 0L;

	/**
	 * Starts a new watch.
	 */
	public static Watch start() {
		return new Watch();
	}

	/**
	 * Creates a new watch.
	 */
	private Watch() {
		super();
		reset();
	}

	/**
	 * Resets the watch, so that elapsed will return the millis since this call.
	 */
	public void reset() {
		startTime = System.nanoTime();
	}

	/**
	 * Returns the amount of millis since the last call to reset or object
	 * instantiation.
	 */
	public long elapsed() {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
	}

	/**
	 * Returns the formatted duration
	 */
	public String duration() {
		// TODO use intelligent time-formatter, like:
		// http://joda-time.sourceforge.net/api-release/org/joda/time/format/PeriodFormatter.html
		return elapsed() + "ms";
	}

	public String microDuration(boolean reset) {
		String result = String
				.valueOf(((double) System.nanoTime() - (double) startTime) / 1000d);
		if (reset) {
			reset();
		}
		return result;
	}

	/**
	 * Combines duration and reset...
	 */
	public String durationReset() {
		// TODO use intelligent time-formatter, like:
		// http://joda-time.sourceforge.net/api-release/org/joda/time/format/PeriodFormatter.html
		String result = elapsed() + "ms";
		reset();
		return result;
	}
}
