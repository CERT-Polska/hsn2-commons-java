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

import pl.nask.hsn2.logger.LoggerForLog4j;
import pl.nask.hsn2.logger.LoggerManager;

public class CommandLineParams extends GenericCmdParams {
    private final static OptionNameWrapper CONNECTOR_ADDRESS = new OptionNameWrapper("con", "connector");
    private final static OptionNameWrapper OS_QUEUE_NAME = new OptionNameWrapper("osQN","osQueueName");
    private final static OptionNameWrapper SV_QUEUE_NAME = new OptionNameWrapper("svQN","svQueueName");
    private final static OptionNameWrapper SV_NAME = new OptionNameWrapper("svN","svName");
    private final static OptionNameWrapper MAX_THREADS = new OptionNameWrapper("maxT","maxThreads");
    private final static OptionNameWrapper DATA_STORE_ADDRESS = new OptionNameWrapper("ds", "dataStore");
	private final static OptionNameWrapper LOG_LEVEL = new OptionNameWrapper("ll", "logLevel");
	private final static OptionNameWrapper LOG_FILE = new OptionNameWrapper("lf", "logFile");
    private final static OptionNameWrapper RBT_COMMON_EXCHANGE = new OptionNameWrapper("rce", "rbtCommonExchange");
    private boolean hasDataStoreAddressOption = true;

    private static final String SERVICE_PREFIX = "srv-";
	private static final String SERVICE_LOW_PRIORITY = ":l";

    private final LoggerManager loggerManager = LoggerForLog4j.getInstance();

	protected boolean hasOsQueueNameOption() {
        return true;
    }

    protected boolean hasDataStoreAddressOption() {
        return hasDataStoreAddressOption;
    }

    protected boolean hasConnectorAddress() {
        return true;
    }

    protected boolean hasServiceQueueNameOption() {
        return true;
    }

    protected boolean hasServiceNameOption() {
        return true;
    }

    protected boolean hasMaxThreadsOption() {
        return true;
    }

    protected boolean hasLogLevelOption() {
        return true;
    }

    protected boolean hasLogFileOption() {
		return true;
	}

    protected boolean hasRbtCommonExchange() {
    	return true;
    }

    @Override
    public void initOptions() {
        if (hasConnectorAddress()) {
        	addOption(CONNECTOR_ADDRESS, "address", "Connector address");
        }
        if (hasOsQueueNameOption()) {
        	addOption(OS_QUEUE_NAME, "name", "ObjectStore queue name");
        }
        if (hasServiceQueueNameOption()) {
        	addOption(SV_QUEUE_NAME, "name", "Service queue name - it's the queue, where the tasks will be published");
        }
        if (hasServiceNameOption()) {
        	addOption(SV_NAME, "name", "Service name - basing of this service will create queue name");
        }
        if (hasDataStoreAddressOption()) {
        	addOption(DATA_STORE_ADDRESS, "address", "DataStore address");
        }
        if (hasMaxThreadsOption()) {
        	addOption(MAX_THREADS, "num", "Max number of threads used for parallel task processing (each thread processes one task)");
        }
        if (hasLogLevelOption()) {
        	addOption(LOG_LEVEL, "level", "Logger level");
        }
        if (hasLogFileOption()) {
        	addOption(LOG_FILE, "path", "Logger file");
        }
        if (hasRbtCommonExchange()) {
        	addOption(RBT_COMMON_EXCHANGE, "name", "RabbitMQ common exchange name");
        }
    }

	@Override
    protected void initDefaults() {
        setDefaultValue(CONNECTOR_ADDRESS.getName(), "localhost");
        setDefaultValue(OS_QUEUE_NAME.getName(), "os:l");
        setDefaultMaxThreads(1);
        setDefaultValue(LOG_LEVEL, loggerManager.getDefaultLogLevel());
        setDefaultValue(LOG_FILE, loggerManager.getDefaultLogFile());
        setDefaultValue(RBT_COMMON_EXCHANGE, "main");
    }

	public void applyArguments(GenericService service) {
		service.setConnectorAddress(getConnectorAddress());
		service.setObjectStoreQueueName(getObjectStoreQueueName());
		service.setServiceName(getServiceName());
		service.setServiceQueueName(getProperlyServiceQueueName());
		service.setDataStoreAddress(getFormattedDataStoreAddress());
    }

    private String getProperlyServiceQueueName() {
		if (!isOptionInCmd(SV_QUEUE_NAME) && isOptionInCmd(SV_NAME)){
			String serviceName = getServiceName();
			return SERVICE_PREFIX + serviceName + SERVICE_LOW_PRIORITY;
		}
		return getServiceQueueName();
	}

	public String getFormattedDataStoreAddress() {
        String dataStoreAddress = getDataStoreAddress();
        if (dataStoreAddress != null && !dataStoreAddress.endsWith("/")){
        	dataStoreAddress += "/";
        }
        return dataStoreAddress;
    }
    /*
     * set defaults
     */
    public void setDefaultServiceQueueName(String swQueueName) {
        setDefaultValue(SV_QUEUE_NAME, swQueueName);
    }

    public void setDefaultServiceName(String swName) {
        setDefaultValue(SV_NAME, swName);
    }

    public void setDefaultDataStoreAddress(String dataStoreAddress) {
        setDefaultValue(DATA_STORE_ADDRESS, dataStoreAddress);
    }

    public void setDefaultMaxThreads(Integer maxThreads) {
        setDefaultIntValue(MAX_THREADS, maxThreads);
    }
    
	public void setDefaultServiceNameAndQueueName(String swName) {
		setDefaultServiceName(swName);
		setDefaultServiceQueueName(SERVICE_PREFIX + swName + SERVICE_LOW_PRIORITY);
	}

    /*
     * set available options
     */
    public void useDataStoreAddressOption(boolean use) {
        hasDataStoreAddressOption = use;
    }

    /*
     * field getters
     */
    public String getConnectorAddress() {
        return getOptionValue(CONNECTOR_ADDRESS);
    }

    public String getObjectStoreQueueName() {
        return getOptionValue(OS_QUEUE_NAME);
    }

    public String getServiceQueueName() {

    	return getOptionValue(SV_QUEUE_NAME);
    }

    public String getServiceName() {
        return getOptionValue(SV_NAME);
    }

    public String getDataStoreAddress() {
        return getOptionValue(DATA_STORE_ADDRESS);
    }

    public Integer getMaxThreads() {
        return getOptionIntValue(MAX_THREADS);
    }

	@Override
	protected void initLogger() {
		loggerManager.setLogFile(getOptionValue(LOG_FILE));
		loggerManager.setLogLevel(getOptionValue(LOG_LEVEL));

	}

	public String getRbtCommonExchangeName() {
		return getOptionValue(RBT_COMMON_EXCHANGE);
	}
}
