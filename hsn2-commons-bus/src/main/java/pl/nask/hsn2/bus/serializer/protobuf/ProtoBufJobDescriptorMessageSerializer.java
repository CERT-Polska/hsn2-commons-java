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

package pl.nask.hsn2.bus.serializer.protobuf;

import java.util.Properties;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.JobDescriptor;
import pl.nask.hsn2.bus.operations.builder.JobDescriptorBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;
/**
 * This is protobuf serializer for <code>JobDescriptor</code> operation.
 * 
 *
 */
public class ProtoBufJobDescriptorMessageSerializer implements MessageSerializer<JobDescriptor> {

	@Override
	public final Message serialize(JobDescriptor operation) throws MessageSerializerException {
		pl.nask.hsn2.protobuff.Jobs.JobDescriptor.Builder builder = pl.nask.hsn2.protobuff.Jobs.JobDescriptor
				.newBuilder().setWorkflow(operation.getWorkflowId());

		if (operation.getWorkflowVersion() !=null
				&& !"".equals(operation.getWorkflowVersion())) {
			builder.setVersion(operation.getWorkflowVersion());
		}

		if (operation.getServicesConfigs() != null
				&& operation.getServicesConfigs().size() > 0) {

			for (String serviceName : operation.getServicesConfigs().keySet()) {
				Properties serviceProperties = operation.getServicesConfigs().get(serviceName);
				if (serviceProperties.size() > 0) {
					pl.nask.hsn2.protobuff.Service.ServiceConfig.Builder serviceConfigBuilder =
							pl.nask.hsn2.protobuff.Service.ServiceConfig.newBuilder();
					serviceConfigBuilder.setServiceLabel(serviceName);

					for (Object key : serviceProperties.keySet()) {
						pl.nask.hsn2.protobuff.Service.Parameter.Builder paramBuilder
							= pl.nask.hsn2.protobuff.Service.Parameter.newBuilder();
						paramBuilder.setName((String) key);
						paramBuilder.setValue((String) serviceProperties.getProperty((String)key));
						serviceConfigBuilder.addParameters(paramBuilder.build());
					}
					builder.addConfig(serviceConfigBuilder.build());
				}
			}
		}
		return new Message("JobDescriptor", builder.build().toByteArray(), null);
	}

	@Override
	public final JobDescriptor deserialize(Message message) throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Jobs.JobDescriptor pbJobDescriptor =
					pl.nask.hsn2.protobuff.Jobs.JobDescriptor.parseFrom(message.getBody());

			JobDescriptorBuilder jdb = new JobDescriptorBuilder(pbJobDescriptor.getWorkflow());
			if (pbJobDescriptor.hasVersion()) {
				jdb.setWorkflowVersion(pbJobDescriptor.getVersion());
			}

			if (pbJobDescriptor.getConfigList() != null
					&& pbJobDescriptor.getConfigList().size() > 0) {
				for (pl.nask.hsn2.protobuff.Service.ServiceConfig pbServiceConfig
						: pbJobDescriptor.getConfigList()) {
					if (pbServiceConfig.getParametersList() == null
							|| pbServiceConfig.getParametersList().size() == 0) {
						continue;
					}
					String serviceName =  pbServiceConfig.getServiceLabel();
					for (pl.nask.hsn2.protobuff.Service.Parameter pbParameter
							: pbServiceConfig.getParametersList()) {
						jdb.addServiceConfig(serviceName, pbParameter.getName(), pbParameter.getValue());
					}
				}
			}
			
			return jdb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}
	

}
