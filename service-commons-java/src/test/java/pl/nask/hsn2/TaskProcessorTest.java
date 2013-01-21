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

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Mocked;

import org.apache.commons.httpclient.URIException;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.operations.builder.ObjectResponseBuilder;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.ResponseType;
import pl.nask.hsn2.protobuff.Process.TaskRequest;
import pl.nask.hsn2.task.Task;
import pl.nask.hsn2.task.TaskFactory;
import pl.nask.hsn2.wrappers.ObjectDataWrapper;
import pl.nask.hsn2.wrappers.ParametersWrapper;

@Test
public class TaskProcessorTest {

    @Mocked
    private ServiceConnector connector;

    TaskFactory shortTaskFactory = new TaskFactory() {
        public Task newTask(TaskContext ctx, ParametersWrapper parameters, ObjectDataWrapper data) {
            return new Task() {
                public boolean takesMuchTime() {
                    return false;
                }
                public void process() {
                }
            };
        }
    };

    TaskFactory longTaskFactory = new TaskFactory() {
        public Task newTask(TaskContext ctx, ParametersWrapper parameters, ObjectDataWrapper data) {
            return new Task() {
                public boolean takesMuchTime() {
                    return true;
                }
                public void process() {
                }
            };
        }
    };

    TaskFactory normalJobFactory = new TaskFactory() {
        public Task newTask(final TaskContext ctx, ParametersWrapper parameters, ObjectDataWrapper data) {
            return new Task() {
                public boolean takesMuchTime() {
                    return true;
                }
                public void process() throws StorageException {
                    try {
                        ctx.newObject(new NewUrlObject("http://nask.pl/", "test"));
                        ctx.addAttribute("name", 1);
                    } catch (URIException e) {
                        throw new IllegalStateException("THIS HAS TO WORK!", e);
                    }
                }
            };
        }
    };

    // service receives TaskRequest, processes the job and sends TaskCompleted
    public void testEmptyShortTask() throws Exception {
        TaskProcessor processor = new TaskProcessor(shortTaskFactory, connector);
        new Expectations() {
            {
                connector.getTaskRequest(); result=testRequest();
                connector.getObjectStoreData(anyLong, withInstanceOf(List.class)); result=objectResponseGet();               
                result=new ObjectResponseBuilder(pl.nask.hsn2.bus.operations.ObjectResponse.ResponseType.SUCCESS_PUT).addObject(0).build();
                connector.sendTaskComplete(anyLong, anyInt, null);
            }
        };

        processor.processOneTask();
    }

    // service receives TastRequest, sends TaskAccepted, processes the job and sends TaskCompleted
    public void testEmptyLongTask() throws Exception {
        TaskProcessor processor = new TaskProcessor(longTaskFactory, connector);

        new Expectations() {
            {
            connector.getTaskRequest(); result=testRequest();
            connector.getObjectStoreData(anyLong, withInstanceOf(List.class)); result=objectResponseGet();
            connector.sendTaskAccepted(anyLong, anyInt);
            result=new ObjectResponseBuilder(pl.nask.hsn2.bus.operations.ObjectResponse.ResponseType.SUCCESS_PUT).addObject(0).build();
            connector.sendTaskComplete(anyLong, anyInt, null);
        }};

        processor.processOneTask();
    }

    // service receives TastRequest, sends TaskAccepted, processes the job and sends TaskCompleted.
    public void testLongTask() throws Exception {
        TaskProcessor processor = new TaskProcessor(normalJobFactory, connector);
        final List<Long> obs = new ArrayList<Long>();
        obs.add(1L);
        new Expectations() {
            {
            connector.getTaskRequest(); result=testRequest();
            connector.getObjectStoreData(anyLong, withInstanceOf(List.class)); result=objectResponseGet();
            connector.sendTaskAccepted(anyLong, anyInt);
            connector.updateObject(anyLong, withInstanceOf(pl.nask.hsn2.bus.operations.ObjectData.class));
            connector.saveObjects(anyLong, withInstanceOf(List.class));
            result=new ObjectResponseBuilder(pl.nask.hsn2.bus.operations.ObjectResponse.ResponseType.SUCCESS_PUT).addObject(0).build();
            connector.sendTaskComplete(anyLong, anyInt, null);
        }};

        processor.processOneTask();
    }

    TaskRequest testRequest() {
        try {
            return TaskRequest.newBuilder().setJob(1l).setTaskId(1).setObject(0).build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    ObjectResponse objectResponseGet() {
        return ObjectResponse.newBuilder().addData(ObjectData.newBuilder().setId(0).build()).setType(ResponseType.SUCCESS_GET).build();
    }
    
    ObjectResponse objectResponseSend() {
        return ObjectResponse.newBuilder().setType(ResponseType.SUCCESS_PUT).addObjects(0).build();
    }
}
