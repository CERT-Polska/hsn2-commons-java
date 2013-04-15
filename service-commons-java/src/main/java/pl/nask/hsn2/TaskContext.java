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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.protobuff.Object.Reference;
import pl.nask.hsn2.task.ObjectTreeNode;
import pl.nask.hsn2.utils.DataStoreHelper;

/**
 * Represents a context for the task processed by the service. The context is backed by the tree of subcontexts. A user creates the contextTree by walking through it using openSubContext() and closeSubContext() methods.
 * 
 *
 */
public class TaskContext {
    private static final Logger LOG = LoggerFactory.getLogger(TaskContext.class);

    protected final long jobId;
    protected final int reqId;
    protected final ServiceConnector connector;

    protected static final long DEFAULT_REFERENCE_ID = -1;

    private List<Long> addedObjects = new ArrayList<Long>();
    private List<String> warnings = new ArrayList<String>();

    private ObjectTreeNode objectTreeRoot;
    private ObjectTreeNode currentObject;
    
    protected int treeHeightLimit = Integer.MAX_VALUE;
	protected int treeSizeLimit = Integer.MAX_VALUE;

    public TaskContext(long jobId, int reqId, long objectDataId, ServiceConnector connector) {
    	this(jobId, reqId, objectDataId, connector, new ObjectTreeNode(objectDataId));
    }
    
    protected TaskContext(long jobId, int reqId, long objectDataId, ServiceConnector connector, ObjectTreeNode objectTreeRoot) {
        this.jobId = jobId;
        this.reqId = reqId;
        this.objectTreeRoot = objectTreeRoot;
        this.currentObject = objectTreeRoot;
        this.connector = connector;
    }
    
    /**
     * Creates a subcontext in the scope of the current context and make it the current context (put's it on the top of the stac context)
     * 
     * Until closeSubContext() is called, all attributes and new objects will be created in the scope of the newly created context   
     */
    public void openSubContext() throws ContextSizeLimitExceededException {
    	if (currentObject.contextHeight() >= treeHeightLimit)
    		throw new ContextSizeLimitExceededException("Tree height limit (" + treeHeightLimit +") exceeded");
    	if (objectTreeRoot.treeSize() >= treeSizeLimit)
    		throw new ContextSizeLimitExceededException("Tree size limit (" + treeSizeLimit +") exceeded");
    	
    	currentObject = currentObject.newObject();
    }
    
    /**
     * removes the subcontext from the context stack.
     */
    public void closeSubContext() {
    	currentObject = currentObject.getParent();
    }
    
    protected ObjectTreeNode getCurrentContext() {
    	return currentObject;
    }

    public void newObject(NewUrlObject newObject) throws StorageException {
    	currentObject.addNewObject(newObject);
    }

    protected Reference.Builder asReferenceBuilder(long referenceId) {
        return Reference.newBuilder().setKey(referenceId).setStore(DataStoreHelper.DEFAULT_STORE_ID);
    }

    public Reference asReference(long referenceId) {
        return asReferenceBuilder(referenceId).build();
    }

    public long getJobId() {
        return jobId;
    }

    public int getReqId() {
        return reqId;
    }

    public void addAttribute(String attrName, int value) {  
        logAttribute(attrName, value, AttributeType.INT);
        currentObject.addIntAttribute(attrName, value);
    }

    public void addTimeAttribute(String attrName, long value) {
        logAttribute(attrName, value, AttributeType.TIME);
    	currentObject.addTimeAttribute(attrName, value);
    }

    public void addAttribute(String attrName, boolean value) {
        logAttribute(attrName, value, AttributeType.BOOL);
    	currentObject.addBoolAttribute(attrName, value);
    }

    public void addAttribute(String attrName, String value) {
        logAttribute(attrName, value, AttributeType.STRING);
        currentObject.addStringAttribute(attrName, value);
    }

    public void addReference(String attrName, long referenceId) {    	
        logAttribute(attrName, new Object[]{DataStoreHelper.DEFAULT_STORE_ID, referenceId}, AttributeType.BYTES);
        currentObject.addRefAttribute(attrName, DataStoreHelper.DEFAULT_STORE_ID, referenceId);
    }

    public void flush() throws StorageException, RequiredParameterMissingException, ResourceException {
    	if(objectTreeRoot != currentObject){
    		LOG.warn("Current object is not root. Tree size = {}, stack size={}", objectTreeRoot.treeSize(), currentObject.contextHeight());
    	}
    	objectTreeRoot.flush(connector, jobId, addedObjects);
    }

    public List<Long> getAddedObjects() {
        return addedObjects;
    }
    public List<String> getWarnings() {
    	return warnings;
    }
    
    public boolean hasWarnings() {
    	synchronized (warnings) {
    		return warnings.size()>0;
    	}
    }
    public void addWarning(String msg) {
    	warnings.add(msg);
    }

    private void logAttribute(String attrName, Object value, AttributeType type) {
   		if (currentObject == objectTreeRoot) {
   			LOG.debug("Adding job attribute with name={} and value={} of type {}", new Object[]{attrName, value, type});
   		} else {
   			LOG.debug("Adding attribute to a subcontext, with name={} and value={} of type {}", new Object[]{attrName, value, type});
   		}
    }

    public long saveInDataStore(byte[] contentAsBytes) throws StorageException {        
        return DataStoreHelper.saveInDataStore(connector, jobId, contentAsBytes);
    }

    public long saveInDataStore(InputStream is) throws StorageException, ResourceException {
        return DataStoreHelper.saveInDataStore(connector, jobId, is);
    }

    public InputStream getFileAsInputStream(long referenceId) throws StorageException {
    	return DataStoreHelper.getFileAsInputStream(connector, jobId, referenceId);
    }
}
