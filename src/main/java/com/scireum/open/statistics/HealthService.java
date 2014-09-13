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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.scireum.open.commons.BasicDataCollector;
import com.scireum.open.nucleus.core.InjectList;
import com.scireum.open.nucleus.core.Register;
import com.scireum.open.nucleus.timer.EveryMinute;

/**
 * Used to collect statistics for all available {@link ProbeReport} instances.
 * The collected statistics can retrieved by calling
 * {@link HealthService#getStatistics()}.
 */
@Register(classes = { EveryMinute.class, HealthService.class })
public class HealthService implements EveryMinute {

	@InjectList(ProbeReport.class)
	private List<ProbeReport> reports;
	private static Map<Probe, ProbeLog> stats = Collections
			.synchronizedMap(new LinkedHashMap<Probe, ProbeLog>());

	@Override
	public void runTimer() throws Exception {
		for (ProbeReport report : reports) {
			report.report(new BasicDataCollector<Probe>() {

				@Override
				public void add(Probe p) {
					ProbeLog log = stats.get(p);
					if (log == null) {
						log = new ProbeLog(p);
						stats.put(p, log);
					}
					log.add(p.readProbe());
				}

			});
		}
	}

	public Collection<ProbeLog> getStatistics() {
		return stats.values();
	}

}
