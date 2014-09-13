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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.scireum.open.commons.Tuple;

/**
 * Represents a log which provides an accumulated history of the last 30 minuts
 * and 24 hours.
 */
public class ProbeLog implements Comparable<ProbeLog> {

	private final Probe probe;
	// Contains a value for each minute of the last half hour
	private final List<Tuple<Date, Double>> last30Minutes = new ArrayList<Tuple<Date, Double>>();
	// Contains a value for each half hour of the last 24 hours
	private final List<Tuple<Date, Double>> last24Hours = new ArrayList<Tuple<Date, Double>>();
	// Collects values for a "unix load" style output (in our case: 1min 30min
	// 24h)
	private double lastValue = 0;
	private double avg30Min = 0;
	private double avg24h = 0;

	public ProbeLog(Probe probe) {
		this.probe = probe;
	}

	/**
	 * Computes the difference of the two dates in the given unit.
	 */
	protected static long diff(Date from, Date to, TimeUnit unit) {
		if (from == null || to == null) {
			return 0;
		}
		return unit.convert(Math.abs(to.getTime() - from.getTime()),
				TimeUnit.MILLISECONDS);
	}

	/**
	 * Returns the first element of the given list.
	 */
	protected static <T> T first(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * Returns the first element of the given list.
	 */
	protected static <T> T last(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	/**
	 * Adds a value to the log.
	 */
	public void add(double value) {
		if (Double.isNaN(value)) {
			value = 0d;
		}
		Date now = new Date();
		lastValue = value;
		avg30Min += value;

		last30Minutes.add(new Tuple<Date, Double>(new Date(), value));
		if (last30Minutes.size() > 30) {
			avg30Min -= first(last30Minutes).getSecond();
			last30Minutes.remove(0);
		}
		if (last24Hours.isEmpty()) {
			avg24h += value;
			last24Hours.add(new Tuple<Date, Double>(now, value));
		} else {
			Tuple<Date, Double> l = last(last24Hours);
			if (diff(l.getFirst(), now, TimeUnit.MINUTES) >= 30) {
				double avg = 0.0;
				for (Tuple<Date, Double> t : last30Minutes) {
					avg += t.getSecond();
				}
				avg = avg / last30Minutes.size();
				avg24h += avg;
				last24Hours.add(new Tuple<Date, Double>(now, avg));
				if (last24Hours.size() > 48) {
					avg24h -= first(last24Hours).getSecond();
					last24Hours.remove(0);
				}
			}
		}
	}

	public Probe getProbe() {
		return probe;
	}

	/**
	 * Returns a value for each minute of the last half hour.
	 */
	public List<Tuple<Date, Double>> getLast30Minutes() {
		return last30Minutes;
	}

	/**
	 * Returns a value of each half hour of the last 24 hours.
	 */
	public List<Tuple<Date, Double>> getLast24Hours() {
		return last24Hours;
	}

	/**
	 * Returns the most current value of the probe.
	 */
	public double getCurrentValue() {
		return lastValue;
	}

	/**
	 * Returns the 30 minutes average.
	 */
	public double getAvg30Min() {
		if (last30Minutes.isEmpty()) {
			return 0.0d;
		}
		return avg30Min / last30Minutes.size();
	}

	/**
	 * Returns the 24 hours average.
	 */
	public double getAvg24h() {
		if (last24Hours.isEmpty()) {
			return 0.0d;
		}
		return avg24h / last24Hours.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(probe.getCategory());
		sb.append(" - ");
		sb.append(probe.getName());
		if (probe.getUnit() != null) {
			sb.append(" [");
			sb.append(probe.getUnit());
			sb.append("]");
		}
		sb.append(": ");
		sb.append(DecimalFormat.getNumberInstance().format(getCurrentValue()));
		sb.append(" ");
		sb.append(DecimalFormat.getNumberInstance().format(getAvg30Min()));
		sb.append(" ");
		sb.append(DecimalFormat.getNumberInstance().format(getAvg24h()));
		return sb.toString();
	}

	/**
	 * Compares two given objects for equality and gracefully handles
	 * <code>null</code> values.
	 */
	protected static boolean equal(Object left, Object right) {
		if (left == null) {
			return right == null;
		} else if (right == null) {
			return false;
		}
		return left.equals(right);
	}

	@Override
	public int compareTo(ProbeLog o) {
		if (o == null) {
			return 1;
		}
		if (equal(getProbe().getCategory(), o.getProbe().getCategory())) {
			return getProbe().getName().compareTo(o.getProbe().getName());
		}

		return getProbe().getCategory().compareTo(o.getProbe().getCategory());
	}

}
