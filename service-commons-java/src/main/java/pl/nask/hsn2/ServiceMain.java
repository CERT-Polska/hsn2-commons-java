/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
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

package pl.nask.hsn2;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.task.TaskFactory;

public abstract class ServiceMain implements Daemon {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMain.class);
	private volatile DaemonController daemonCtrl = null;
	private CommandLineParams cmd;
	private GenericService service;
	private Thread serviceRunner;
		
	protected abstract void prepareService();
	protected abstract TaskFactory createTaskFactory();
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(DaemonContext context) {
		daemonCtrl = context.getController();
		cmd = newCommandLineParams();
		cmd.parseParams(context.getArguments());
		prepareService();
		createService();
	}

	private void createService() {
		service = new GenericService(createTaskFactory(), cmd.getMaxThreads(),	cmd.getRbtCommonExchangeName(), cmd.getRbtNotifyExchangeName());
		service.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
	}
	
	@Override
	public void start() {
		cmd.applyArguments(service);
		
		serviceRunner = new Thread(service, cmd.getServiceName());
		serviceRunner.start();
		try {
			service.waitForStartUp();
			LOGGER.info("Service started.");
		} catch (InterruptedException e) {
			LOGGER.warn("Interrupted while waiting for startup");
		}
	}

	@Override
	public void stop() {
		service.stop();
		serviceRunner.interrupt();
	}
	
	public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler{

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			LOGGER.warn("Service exit.", e);
			if (daemonCtrl != null) {
				daemonCtrl.fail(e.getMessage());
			}
			else {
				System.exit(1);
			}
		}
	}

	protected CommandLineParams getCommandLineParams() {
		return cmd;
	}
	
	protected CommandLineParams newCommandLineParams() {
		return new CommandLineParams();
	}
}