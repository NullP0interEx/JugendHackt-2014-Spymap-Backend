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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.scireum.open.commons.DataCollector;
import com.scireum.open.nucleus.core.Register;

/**
 * Provides statistics about the Java Runtime.
 */
@Register(classes = ProbeReport.class)
public class SystemReport implements ProbeReport {
	private OperatingSystemMXBean os = ManagementFactory
			.getOperatingSystemMXBean();
	private MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
	private List<MemoryPoolMXBean> pools = ManagementFactory
			.getMemoryPoolMXBeans();
	private ThreadMXBean t = ManagementFactory.getThreadMXBean();
	private List<GarbageCollectorMXBean> gcs = ManagementFactory
			.getGarbageCollectorMXBeans();
	private Map<Long, ThreadStats> threadStats = Collections
			.synchronizedMap(new TreeMap<Long, ThreadStats>());

	private Probe heapProbe = new ValueProbe("JVM", "Heap") {

		@Override
		public double readProbe() {
			return (mem.getHeapMemoryUsage().getUsed() / 1024d) / 1024d;
		}

		@Override
		public String getUnit() {
			return "MB";
		}

		@Override
		public Double getMaxValue() {
			return (mem.getHeapMemoryUsage().getMax() / 1024d) / 1024d;
		}
	};

	private class MemoryPoolProbe extends ValueProbe {
		public MemoryPoolProbe(MemoryPoolMXBean pool) {
			super("JVM", "MEM-" + pool.getName());
			this.pool = pool;
		}

		private MemoryPoolMXBean pool;

		@Override
		public double readProbe() {
			if (pool == null) {
				return 0d;
			}
			return (pool.getUsage().getUsed() / 1024d) / 1024d;
		}

		@Override
		public String getUnit() {
			return "MB";
		}

		@Override
		public Double getMaxValue() {
			if (pool == null) {
				return 0d;
			}
			return (pool.getUsage().getMax() / 1024d) / 1024d;
		}
	}

	private List<MemoryPoolProbe> poolProbes;

	private List<MemoryPoolProbe> getPoolProbes() {
		if (poolProbes == null) {
			List<MemoryPoolProbe> result = new ArrayList<SystemReport.MemoryPoolProbe>();
			for (MemoryPoolMXBean pool : pools) {
				result.add(new MemoryPoolProbe(pool));
			}
			poolProbes = result;
		}
		return poolProbes;
	}

	private Probe cpuProbe = new ValueProbe("JVM", "CPU") {

		private long lastMeasurement = 0;
		private long lastTime = 0;

		@Override
		public double readProbe() {
			long sum = 0;
			long now = System.nanoTime();
			Set<Long> ids = new TreeSet<Long>(threadStats.keySet());
			for (ThreadInfo threadInfo : t.dumpAllThreads(false, false)) {
				long cpuTime = t.getThreadCpuTime(threadInfo.getThreadId());
				ThreadStats stat = threadStats.get(threadInfo.getThreadId());
				if (stat == null) {
					stat = new ThreadStats();
					stat.name = threadInfo.getThreadName();
					threadStats.put(threadInfo.getThreadId(), stat);
				} else {
					double deltaCPU = Math.abs(cpuTime - stat.lastCPUTime);
					double deltaWallClock = Math.abs(now - stat.lastProbe);
					if (deltaWallClock > 0.0d) {
						stat.cpuUtilisation = Math
								.round(10000d * (deltaCPU / deltaWallClock)) / 100d;
					}
					ids.remove(threadInfo.getThreadId());
				}
				stat.lastCPUTime = cpuTime;
				stat.lastProbe = now;
				stat.state = threadInfo.getThreadState();
				sum += cpuTime;
			}
			// Remove statistics of terminated threads...
			for (Long unusedId : ids) {
				threadStats.remove(unusedId);
			}
			double result = 0d;
			if (lastTime > 0) {
				double deltaCPU = Math.abs(sum - lastMeasurement);
				double deltaWallClock = Math.abs(now - lastTime);
				if (deltaWallClock == 0.0d) {
					result = 0d;
				} else {
					result = Math.round(10000d * (deltaCPU
							/ os.getAvailableProcessors() / deltaWallClock)) / 100d;
				}
			}
			lastMeasurement = sum;
			lastTime = now;

			return result;
		}

		@Override
		public String getUnit() {
			return "%";
		}

		@Override
		public Double getMaxValue() {
			return 100d;
		}
	};

	private Probe gcProbe = new ValueProbe("JVM", "GC") {

		private long lastMeasurement = 0;
		private long lastTime = 0;

		@Override
		public double readProbe() {
			long sum = 0;
			long now = System.nanoTime();
			for (GarbageCollectorMXBean gc : gcs) {
				if (gc.getCollectionTime() > 0) {
					sum += gc.getCollectionTime();
				}
			}
			double result = 0d;
			if (lastTime > 0) {
				double deltaCPU = Math.abs((double) sum
						- (double) lastMeasurement);
				double deltaWallClock = Math.abs(now - lastTime);
				result = Math.round(10000d * (deltaCPU / deltaWallClock)) / 100d;
			}
			lastMeasurement = sum;
			lastTime = now;

			return result;
		}

		@Override
		public String getUnit() {
			return "%";
		}

		@Override
		public Double getMaxValue() {
			return 100d;
		}
	};

	@Override
	public void report(DataCollector<Probe> collector) {
		collector.add(heapProbe);
		collector.add(getPoolProbes());
		collector.add(cpuProbe);
		collector.add(gcProbe);
	}

	public List<ThreadStats> getThreadStats() {
		List<ThreadStats> result = new ArrayList<ThreadStats>(
				threadStats.values());
		Collections.sort(result);
		return result;
	}
}
