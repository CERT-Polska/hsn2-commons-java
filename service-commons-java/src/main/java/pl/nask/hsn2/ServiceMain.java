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

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import pl.nask.hsn2.service.JsAnalyzerTaskFactory;

public abstract class ServiceMain implements Daemon {
	private CommandLineParams cmd;
	GenericService service;
	private Thread serviceRunner;
	
	protected abstract CommandLineParams parseArguments(String[] args);
	protected abstract void initializeService();
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(DaemonContext context) throws DaemonInitException {
		cmd = parseArguments(context.getArguments());
		initializeService();
		service = createService();
	}

	private GenericService createService() {
		new GenericService(createTaskFactory(cmd), cmd.getMaxThreads(),	cmd.getRbtCommonExchangeName(), cmd.getRbtNotifyExchangeName());
		return null;
	}
	@Override
	public void start() throws Exception {
		cmd.applyArguments(service);
		serviceRunner = new Thread(service, cmd.getServiceName());
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub

	}

}
