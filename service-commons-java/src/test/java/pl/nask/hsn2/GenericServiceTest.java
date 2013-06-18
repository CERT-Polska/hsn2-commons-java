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

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.ResponseType;
import pl.nask.hsn2.protobuff.Process.TaskRequest;
import pl.nask.hsn2.task.Task;
import pl.nask.hsn2.task.TaskFactory;
import pl.nask.hsn2.wrappers.ObjectDataWrapper;
import pl.nask.hsn2.wrappers.ParametersWrapper;

public class GenericServiceTest {
	@Mocked
	private ServiceConnectorImpl connector;

	@SuppressWarnings("unchecked")
	@Test()
	public void testShutDown() throws Exception {
		final AtomicLong counter = new AtomicLong(0);
		
		TaskFactory jobFactory = new TaskFactory() {
			@Override
			public Task newTask(TaskContext jobContext, ParametersWrapper parameters,
					ObjectDataWrapper data) throws ParameterException {
				return new Task() {
					@Override
					public boolean takesMuchTime() {
						return false;
					}
					
					@Override
					public void process() throws ParameterException, ResourceException,
							StorageException {
						counter.incrementAndGet();
					}
				};
			}
		};			

		new NonStrictExpectations() {
			{
				connector.getTaskRequest();
				result=TaskRequest.newBuilder().setJob(1).setTaskId(1).setObject(1).build();
				
				connector.getObjectStoreData(anyLong, withInstanceOf(List.class));
				result = ObjectResponse.newBuilder().setType(ResponseType.SUCCESS_GET).addData(ObjectData.newBuilder().setId(1)).build();
			}
		};
		GenericService gs = new GenericService(jobFactory , 3, "", "");
		
		Assert.assertEquals(counter.get(), 0);
		
		Thread gsThread = new Thread(gs);
		gsThread.start(); 
		
		Thread.sleep(1000);
		
		Assert.assertTrue(counter.get() > 0, "Some tasks were processed");
		gs.stop();		
		// Give some time to stop processing
		Thread.sleep(10);
		// check if delta is 0
		long c1 = counter.get();
		Thread.sleep(10);
		long c2 = counter.get();
		Assert.assertEquals(c1-c2, 0, "Tasks processed after stop()");
	}
}
