/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.bus.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.recovery.Recoverable;
import pl.nask.hsn2.bus.recovery.RecoveryMonitor;

public class RecoveryMonitorTest {

	@Test
	public void simpleTest() throws InterruptedException {
		RecoveryMonitor monitor = new RecoveryMonitor();
		
		monitor.setRecoveryInterval(100);
		monitor.start();
		Assert.assertTrue(monitor.isRunning());
		
		SomeRecoverable service = new SomeRecoverable();
		
		monitor.registerRecoverable(service);
		Thread.sleep(1150); // a bit more then recoverable time

		Assert.assertTrue(service.getCount() > 0);

		monitor.unregisterRecoverable(service);
		int aCount = service.getCount();
		Thread.sleep(150);
		Assert.assertTrue(service.getCount() == aCount);
		
		monitor.stop();
		Assert.assertTrue(!monitor.isRunning());
		
		// test restart
		monitor.registerRecoverable(service);
		monitor.start();
		Thread.sleep(150);
		monitor.stop();
	}

	private class SomeRecoverable implements Recoverable {
		private Logger LOGGER = LoggerFactory.getLogger(SomeRecoverable.class);
		
		private AtomicInteger count = new AtomicInteger(0);
		@Override
		public void recovery() {
			LOGGER.info("Recovering...");
			this.count.incrementAndGet();
		}
		public int getCount() {
			return this.count.get();
		}
	}
}
