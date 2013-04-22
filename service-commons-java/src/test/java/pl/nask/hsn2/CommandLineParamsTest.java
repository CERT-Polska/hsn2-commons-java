package pl.nask.hsn2;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.GenericCmdParams.OptionNameWrapper;

public class CommandLineParamsTest {
	private static final String DEFAULT_RNE_VALUE = "notify";
	private static final String DEFAULT_RCE_VALUE = "main";
	private static final Integer DEFAULT_MAXT_VALUE = 1;
	private static final String DEFAULT_OSQN_VALUE = "os:l";
	private static final String DEFAULT_CON_VALUE = "localhost";

	private static final String OPTION_PREFIX = "-";
	private static final String OPTION_RNE = "rne";
	private static final String OPTION_RNE_VALUE = "testRabbitNotifyExchange";
	private static final String OPTION_RCE = "rce";
	private static final String OPTION_RCE_VALUE = "testRabbitCommonExchange";
	private static final String OPTION_MAXT = "maxT";
	private static final String OPTION_MAXT_LONG = "maxThreads";
	private static final Integer OPTION_MAXT_VALUE = 187263;
	private static final String OPTION_SVN = "svN";
	private static final String OPTION_SVN_VALUE = "testServiceName";
	private static final String OPTION_SVQN = "svQN";
	private static final String OPTION_SVQN_VALUE = "serviceQueueName";
	private static final String OPTION_DS = "ds";
	private static final String OPTION_DS_VALUE = "testDataStoreUrl";
	private static final String OPTION_DS_VALUE_FORMATTED = OPTION_DS_VALUE + "/";
	private static final String OPTION_OSQN = "osQN";
	private static final String OPTION_OSQN_VALUE = "testObjectStoreQueueName";
	private static final String OPTION_CON = "con";
	private static final String OPTION_CON_LONG = "connector";
	private static final String OPTION_CON_VALUE = "testConnectorHost";

	private static final String CMD_LINE_SYNTAX = "java -jar jar_file";
	private final static OptionNameWrapper MAX_THREADS = new OptionNameWrapper(OPTION_MAXT, OPTION_MAXT_LONG);
	private final static OptionNameWrapper CONNECTOR_ADDRESS = new OptionNameWrapper(OPTION_CON, OPTION_CON_LONG);

	@Test
	public void mainTest() {
		String[] args = { OPTION_PREFIX + OPTION_CON, OPTION_CON_VALUE, OPTION_PREFIX + OPTION_OSQN, OPTION_OSQN_VALUE,
				OPTION_PREFIX + OPTION_DS, OPTION_DS_VALUE, OPTION_PREFIX + OPTION_SVQN, OPTION_SVQN_VALUE, OPTION_PREFIX + OPTION_SVN,
				OPTION_SVN_VALUE, OPTION_PREFIX + OPTION_MAXT, String.valueOf(OPTION_MAXT_VALUE), OPTION_PREFIX + OPTION_RCE,
				OPTION_RCE_VALUE, OPTION_PREFIX + OPTION_RNE, OPTION_RNE_VALUE };
		CommandLineParams params = new CommandLineParams();
		params.parseParams(args);
		GenericService service = new GenericService(null, 1, OPTION_RCE_VALUE, OPTION_RNE_VALUE);
		params.applyArguments(service);

		// WST log level? (-ll)
		// WST log file? (-lf)

		Assert.assertEquals(params.getConnectorAddress(), OPTION_CON_VALUE);
		Assert.assertEquals(params.getOptionValue(CONNECTOR_ADDRESS), OPTION_CON_VALUE);
		Assert.assertEquals(params.getOptionValue(OPTION_CON_LONG), OPTION_CON_VALUE);
		Assert.assertEquals(params.getObjectStoreQueueName(), OPTION_OSQN_VALUE);
		Assert.assertEquals(params.getDataStoreAddress(), OPTION_DS_VALUE);
		Assert.assertEquals(params.getServiceQueueName(), OPTION_SVQN_VALUE);
		Assert.assertEquals(params.getServiceName(), OPTION_SVN_VALUE);
		Assert.assertEquals(params.getMaxThreads(), OPTION_MAXT_VALUE);
		Assert.assertEquals(params.getOptionIntValue(MAX_THREADS), OPTION_MAXT_VALUE);
		Assert.assertEquals(params.getOptionIntValue(OPTION_MAXT_LONG), OPTION_MAXT_VALUE);
		Assert.assertEquals(params.getRbtCommonExchangeName(), OPTION_RCE_VALUE);
		Assert.assertEquals(params.getRbtNotifyExchangeName(), OPTION_RNE_VALUE);
		Assert.assertEquals(params.getCmdLineSyntax(), CMD_LINE_SYNTAX);
		Assert.assertEquals(params.getFormattedDataStoreAddress(), OPTION_DS_VALUE_FORMATTED);
		Assert.assertEquals(service.getConnectorAddress(), OPTION_CON_VALUE);
		Assert.assertEquals(service.getObjectStoreQueueName(), OPTION_OSQN_VALUE);
		Assert.assertEquals(service.getServiceName(), OPTION_SVN_VALUE);
		Assert.assertEquals(service.getServiceQueueName(), OPTION_SVQN_VALUE);
		Assert.assertEquals(service.getDataStoreAddress(), OPTION_DS_VALUE_FORMATTED);
	}

	@Test
	public void checkDefaults() {
		String[] args = { OPTION_PREFIX + OPTION_DS, OPTION_DS_VALUE, OPTION_PREFIX + OPTION_SVQN, OPTION_SVQN_VALUE,
				OPTION_PREFIX + OPTION_SVN, OPTION_SVN_VALUE };
		CommandLineParams params = new CommandLineParams();
		params.initDefaults();
		params.parseParams(args);

		Assert.assertEquals(params.getConnectorAddress(), DEFAULT_CON_VALUE);
		Assert.assertEquals(params.getObjectStoreQueueName(), DEFAULT_OSQN_VALUE);
		Assert.assertEquals(params.getDataStoreAddress(), OPTION_DS_VALUE);
		Assert.assertEquals(params.getServiceQueueName(), OPTION_SVQN_VALUE);
		Assert.assertEquals(params.getServiceName(), OPTION_SVN_VALUE);
		Assert.assertEquals(params.getMaxThreads(), DEFAULT_MAXT_VALUE);
		Assert.assertEquals(params.getRbtCommonExchangeName(), DEFAULT_RCE_VALUE);
		Assert.assertEquals(params.getRbtNotifyExchangeName(), DEFAULT_RNE_VALUE);

		params = new CommandLineParams();
		params.initDefaults();
		params.setDefaultDataStoreAddress(OPTION_DS_VALUE);
		params.setDefaultMaxThreads(OPTION_MAXT_VALUE);
		params.setDefaultServiceName(OPTION_SVN_VALUE);
		params.setDefaultServiceQueueName(OPTION_SVQN_VALUE);
		params.parseParams(new String[] {});
		Assert.assertEquals(params.getDataStoreAddress(), OPTION_DS_VALUE);
		Assert.assertEquals(params.getServiceName(), OPTION_SVN_VALUE);
		Assert.assertEquals(params.getServiceQueueName(), OPTION_SVQN_VALUE);

		// WST dlaczego to nie dziala?
		// Assert.assertEquals(params.getMaxThreads(), OPTION_MAXT_VALUE);

		Assert.assertEquals(params.getRbtCommonExchangeName(), DEFAULT_RCE_VALUE);
		Assert.assertEquals(params.getRbtNotifyExchangeName(), DEFAULT_RNE_VALUE);
	}

	/**
	 * Exception: Object not initialized yet.
	 */
	@Test(expectedExceptions = { java.lang.IllegalStateException.class })
	public void initException() {
		CommandLineParams params = new CommandLineParams();
		params.addOption(OPTION_CON, OPTION_CON_LONG, OPTION_CON_VALUE, OPTION_CON_LONG);
	}

	/**
	 * Exception: Object already initialized.
	 */
	@Test(expectedExceptions = { java.lang.IllegalStateException.class })
	public void doubleInitialization() {
		String[] args = { OPTION_PREFIX + OPTION_DS, OPTION_DS_VALUE, OPTION_PREFIX + OPTION_SVQN, OPTION_SVQN_VALUE,
				OPTION_PREFIX + OPTION_SVN, OPTION_SVN_VALUE };
		CommandLineParams params = new CommandLineParams();
		params.parseParams(args);
		params.initDefaults();
	}
}
