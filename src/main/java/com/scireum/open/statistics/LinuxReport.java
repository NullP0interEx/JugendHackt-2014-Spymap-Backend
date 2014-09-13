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

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.scireum.open.commons.DataCollector;
import com.scireum.open.commons.Tuple;
import com.scireum.open.nucleus.core.Register;

@Register(classes = ProbeReport.class)
public class LinuxReport implements ProbeReport {

	private static final Pattern STATS_PATTERN = Pattern
			.compile("(cpu\\d+) +(\\d+).+");
	private Map<String, Long> lastStats;
	private long lastRead;
	private Map<String, Tuple<Long, Long>> lastDiskStats;
	private long lastDiskRead;
	private static final long USER_HZ_IN_MILLIS = 10; // Factor 10 between
														// USER_HZ and
														// Milliseconds

	private Map<String, Long> getStats() throws IOException {
		Map<String, Long> data = new LinkedHashMap<String, Long>();
		File stats = new File("/proc/stat");
		if (!stats.exists()) {
			return data;
		}
		BufferedReader br = new BufferedReader(new FileReader(stats));
		try {
			String line = br.readLine();
			while (line != null) {
				Matcher m = STATS_PATTERN.matcher(line);
				if (m.matches()) {
					data.put(m.group(1), Long.parseLong(m.group(2)));
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return data;
	}

	private static final Pattern DISKSTATS_PATTERN = Pattern
			.compile(" +\\d+ +\\d+ +([a-z]+) +(\\d+) +\\d+ +(\\d+).*");

	private Map<String, Tuple<Long, Long>> getDiskstats() throws IOException {
		Map<String, Tuple<Long, Long>> data = new LinkedHashMap<String, Tuple<Long, Long>>();
		File stats = new File("/proc/diskstats");
		if (!stats.exists()) {
			return data;
		}
		BufferedReader br = new BufferedReader(new FileReader(stats));
		try {
			String line = br.readLine();
			while (line != null) {
				Matcher m = DISKSTATS_PATTERN.matcher(line);
				if (m.matches()) {
					data.put(m.group(1),
							new Tuple<Long, Long>(Long.parseLong(m.group(2)),
									Long.parseLong(m.group(3))));
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return data;
	}

	@Override
	public void report(DataCollector<Probe> collector) {
		reportStats(collector);
		reportDiskStats(collector);
	}

	protected void reportStats(DataCollector<Probe> collector) {
		try {
			Map<String, Long> currentStats = getStats();
			long delta = System.currentTimeMillis() - lastRead;
			lastRead = System.currentTimeMillis();
			if (lastStats != null) {
				for (Map.Entry<String, Long> entry : currentStats.entrySet()) {
					Long lastCPU = lastStats.get(entry.getKey());
					if (lastCPU != null) {
						double usage = (entry.getValue() - lastCPU)
								* USER_HZ_IN_MILLIS / (double) delta;
						collector.add(new ConstantValueProbe("SYS", entry
								.getKey(), "%", usage * 100d));
					}
				}
			}
			lastStats = currentStats;
		} catch (IOException e) {
			e.printStackTrace(); // TODO handle exception better
		}
	}

	protected void reportDiskStats(DataCollector<Probe> collector) {
		try {
			Map<String, Tuple<Long, Long>> currentStats = getDiskstats();
			long delta = System.currentTimeMillis() - lastDiskRead;
			lastDiskRead = System.currentTimeMillis();
			if (lastDiskStats != null) {
				for (Map.Entry<String, Tuple<Long, Long>> entry : currentStats
						.entrySet()) {
					Tuple<Long, Long> lastIO = lastDiskStats
							.get(entry.getKey());
					if (lastIO != null) {
						long reads = entry.getValue().getFirst()
								- lastIO.getFirst();
						collector.add(new ConstantValueProbe("SYS", entry
								.getKey() + "-reads", "1/s", reads
								/ (double) TimeUnit.SECONDS.convert(delta,
										TimeUnit.MILLISECONDS)));
						long writes = entry.getValue().getSecond()
								- lastIO.getSecond();
						collector.add(new ConstantValueProbe("SYS", entry
								.getKey() + "-writes", "1/s", writes
								/ (double) TimeUnit.SECONDS.convert(delta,
										TimeUnit.MILLISECONDS)));
					}
				}
			}
			lastDiskStats = currentStats;
		} catch (IOException e) {
			e.printStackTrace(); // TODO handle exception better
		}
	}
}
