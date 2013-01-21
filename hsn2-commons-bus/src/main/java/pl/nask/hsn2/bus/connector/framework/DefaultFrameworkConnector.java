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

package pl.nask.hsn2.bus.connector.framework;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.EndPointFactory;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.connector.AbstractSerializableConnector;
import pl.nask.hsn2.bus.operations.GetConfigReply;
import pl.nask.hsn2.bus.operations.GetConfigRequest;
import pl.nask.hsn2.bus.operations.InfoData;
import pl.nask.hsn2.bus.operations.InfoRequest;
import pl.nask.hsn2.bus.operations.InfoType;
import pl.nask.hsn2.bus.operations.JobAccepted;
import pl.nask.hsn2.bus.operations.JobDescriptor;
import pl.nask.hsn2.bus.operations.JobInfo;
import pl.nask.hsn2.bus.operations.JobListReply;
import pl.nask.hsn2.bus.operations.JobListRequest;
import pl.nask.hsn2.bus.operations.JobRejected;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.operations.Ping;
import pl.nask.hsn2.bus.operations.SetConfigReply;
import pl.nask.hsn2.bus.operations.SetConfigRequest;
import pl.nask.hsn2.bus.operations.WorkflowBasicInfo;
import pl.nask.hsn2.bus.operations.WorkflowListReply;
import pl.nask.hsn2.bus.operations.WorkflowListRequest;
import pl.nask.hsn2.bus.operations.WorkflowUploadReply;
import pl.nask.hsn2.bus.operations.WorkflowUploadRequest;
import pl.nask.hsn2.bus.operations.builder.GetConfigRequestBuilder;
import pl.nask.hsn2.bus.operations.builder.InfoRequestBuilder;
import pl.nask.hsn2.bus.operations.builder.JobDescriptorBuilder;
import pl.nask.hsn2.bus.operations.builder.JobListRequestBuilder;
import pl.nask.hsn2.bus.operations.builder.PingBuilder;
import pl.nask.hsn2.bus.operations.builder.SetConfigRequestBuilder;
import pl.nask.hsn2.bus.operations.builder.WorkflowListRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

/**
 * This is technology independent <code>FrameworkConnector</code>.
 * 
 *
 */
public class DefaultFrameworkConnector extends AbstractSerializableConnector implements FrameworkConnector {

	/**
	 * Internal logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFrameworkConnector.class);
	
	private RequestResponseEndPoint endPoint;
	private Destination destination;

	/**
	 * Default constructor.
	 * 
	 * @param destination
	 *            Routing key of the framework.
	 * @param serializer
	 *            Serializer for messages.
	 * @param factory
	 *            Technology based endpoint factory.
	 * @throws BusException
	 *             The exception cen be thrown if there is any transport
	 *             establishment problem.
	 */
	public DefaultFrameworkConnector(Destination destination,
			MessageSerializer<Operation> serializer,
			EndPointFactory factory) throws BusException {
		super(serializer);
		this.destination = destination;
		this.endPoint = factory.createRequestResponseEndPoint(10);
	}

	@Override
	public final long sendJobDescriptor(String workflowId, String workflowVersion,
			Map<String, Properties> servicesConfigs)
			throws FrameworkConnectorException, JobRejectedException {

		JobDescriptor jd = new JobDescriptorBuilder(workflowId)
				.setWorkflowVersion(workflowVersion)
				.setServicesConfigs(servicesConfigs).build();

		Operation response = sendToFramework(jd);

		if (response instanceof JobRejected) {
			throw new JobRejectedException(((JobRejected) response).getReason());
		}
		
		if (response instanceof JobAccepted) {
			return ((JobAccepted) response).getJobId();
		}
		throw new FrameworkConnectorException("Unexpected response: " + response.getClass().getName());
	}

	@Override
	public final long sendJobDescriptor(String workflowId, String workflowVersion)
			throws FrameworkConnectorException, JobRejectedException {
		return sendJobDescriptor(workflowId, workflowVersion, null);
	}

	@Override
	public final long sendJobDescriptor(String workflowId)
			throws FrameworkConnectorException, JobRejectedException {
		return sendJobDescriptor(workflowId, null, null);
	}

	@Override
	public final List<JobInfo> sendJobsListRequest()
			throws FrameworkConnectorException {
		JobListRequest jlr = new JobListRequestBuilder().build();
		Operation response = sendToFramework(jlr);
		if (response instanceof JobListReply) {
			return ((JobListReply) response).getJobs();
		}
		throw new FrameworkConnectorException("Unexpected response: " + response.getClass().getName());
	}

	@Override
	public final ObjectData sendJobDetails(long jobId)
			throws FrameworkConnectorException {
		InfoRequest ir = new InfoRequestBuilder(InfoType.JOB)
				.setId(jobId)
				.build();
		Operation response = sendToFramework(ir);
		if (response instanceof InfoData) {
			return ((InfoData) response).getData();
		}
		throw new FrameworkConnectorException("Unexpected response: " + response.getClass().getName());
	}

	@Override
	public final List<WorkflowBasicInfo> sendListWorkflows(boolean enabledOnly)
			throws FrameworkConnectorException {
		WorkflowListRequest wlr = new WorkflowListRequestBuilder(enabledOnly).build();
		Operation response = sendToFramework(wlr);
		if (response instanceof WorkflowListReply) {
			return ((WorkflowListReply) response).getWorkflows();
		}
		throw new FrameworkConnectorException("Unexpected response: " + response.getClass().getName());
	}

	@Override
	public final List<WorkflowBasicInfo> sendListWorkflows()
			throws FrameworkConnectorException {
		return sendListWorkflows(false);
	}

	@Override
	public final String sendWorkflowUpload(String name, String content,
			boolean override) throws FrameworkConnectorException {
		WorkflowUploadRequest wur = new WorkflowUploadRequest(name, content, override);
		Operation response = sendToFramework(wur);
		if (response instanceof WorkflowUploadReply) {
			return ((WorkflowUploadReply) response).getRevision();
		}
		throw new FrameworkConnectorException("Unexpected response: " + response.getClass().getName());
	}

	@Override
	public final String sendWorkflowUpload(String name, String content)
			throws FrameworkConnectorException {
		return sendWorkflowUpload(name, content, false);
	}

	@Override
	public final boolean sendPing() {
		Ping ping = new PingBuilder().build();
		try {
			Operation response = sendToFramework(ping);
			if (response instanceof Ping) {
				return true;
			}
		} catch (FrameworkConnectorException e) { }
		return false;
	}

	@Override
	public final Properties sendGetConfig() throws FrameworkConnectorException {
		GetConfigRequest gcr = new GetConfigRequestBuilder().build();
		Operation response = sendToFramework(gcr);
		if (response instanceof GetConfigReply) {
			return ((GetConfigReply) response).getProperties();
		}
		throw new FrameworkConnectorException("Unexpected response: " + response.getClass().getName());
	}

	@Override
	public final boolean sendSetConfig(Properties properties, boolean override)
			throws FrameworkConnectorException {
		SetConfigRequest scr = new SetConfigRequestBuilder(override, properties).build();
		Operation response = sendToFramework(scr);
		if (response instanceof SetConfigReply) {
			return ((SetConfigReply) response).isSuccess();
		}
		throw new FrameworkConnectorException("Unexpected response: " + response.getClass().getName());
	}

	@Override
	public final boolean sendSetConfig(Properties properties)
			throws FrameworkConnectorException {
		return sendSetConfig(properties, false);
	}

	private Operation sendToFramework(Operation operation)
			throws FrameworkConnectorException {
		try {
			Message request = getSerializer().serialize(operation);
			request.setDestination(this.destination);
			String correlationId = java.util.UUID.randomUUID().toString();
			request.setCorrelationId(correlationId);
			Message response = endPoint.sendAndGet(request);
			if (!correlationId.equals(response.getCorrelationId())) {
				LOGGER.warn("Correlarion id doesn't match. We are relax but it should be fixed! expected: {} got: {}",
						correlationId, response.getCorrelationId());
			}
			return getSerializer().deserialize(response);
		} catch (MessageSerializerException e) {
			throw new FrameworkConnectorException(e);
		} catch (BusException e) {
			throw new FrameworkConnectorException(e);
		}
	}
}
