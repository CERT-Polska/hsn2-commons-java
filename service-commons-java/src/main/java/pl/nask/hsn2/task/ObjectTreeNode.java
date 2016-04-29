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

package pl.nask.hsn2.task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.NewUrlObject;
import pl.nask.hsn2.RequiredParameterMissingException;
import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.ServiceConnector;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectResponse;
import pl.nask.hsn2.bus.operations.builder.ObjectDataBuilder;
import pl.nask.hsn2.utils.DataStoreHelper;

/**
 * Represents a tree of the objects which are being reported by the service. 
 * The service would normally add attributes to the tree node and create new leafs if it's required by the task.
 * 
 *
 */
public class ObjectTreeNode {
	private final static Logger LOG = LoggerFactory.getLogger(ObjectTreeNode.class);
	
	private ObjectDataBuilder objectDataBuilder = new ObjectDataBuilder();
	
	private int contextHeight = 1;
	private ObjectTreeNode parent;
	private List<ObjectTreeNode> children = new LinkedList<ObjectTreeNode>();

	private List<NewUrlObject> newObjects = new LinkedList<NewUrlObject>();

	private Long objectDataId;

	protected ObjectTreeNode(ObjectTreeNode parent) {
		this.parent = parent;
		this.contextHeight = parent.contextHeight() + 1;		
	}

	public ObjectTreeNode(long objectDataId) {
		this.objectDataId = objectDataId;
		objectDataBuilder.setId(objectDataId);
	}

	/**
	 * Creates a new ObjectTree with a root in this object. 
	 * 
	 * @return created instance.
	 */
	public final ObjectTreeNode newObject() {
		ObjectTreeNode newObject = newObjectInstance();
		children.add(newObject);
		return newObject;
	}

	protected ObjectTreeNode newObjectInstance() {
		return new ObjectTreeNode(this);
	}

	/**
	 * @return
	 * the depth of the object in the whole object tree
	 */
	public int contextHeight() {
		return contextHeight;
	}

	/**
	 * @return
	 * the object's parent. If the object is a tree root, null will be returned
	 */
	public ObjectTreeNode getParent() {
		return this.parent;
	}

	/**
	 *
	 * @return the size of the tree this object is a root.
	 */
	public int treeSize() {
		int size = 1;
		for (ObjectTreeNode obj: children) {
			size += obj.treeSize();
		}
		
		return size;
	}

	public void addIntAttribute(String name, int value) {
		objectDataBuilder.addIntAttribute(name, value);
	}

	public void addTimeAttribute(String name, long value) {
		objectDataBuilder.addTimeAttribute(name, value);
	}

	public void addBoolAttribute(String name, boolean value) {
		objectDataBuilder.addBoolAttribute(name, value);
	}

	public void addStringAttribute(String name, String value) {
		objectDataBuilder.addStringAttribute(name, value);
	}

	public void addRefAttribute(String name, int storeId, long referenceId) {
		objectDataBuilder.addRefAttribute(name, storeId, referenceId);
	}
	
	public void addRefAttribute(String name, long referenceId) {
		addRefAttribute(name, DataStoreHelper.DEFAULT_STORE_ID, referenceId);
	}

	public void addNewObject(NewUrlObject newObject) {
		newObjects.add(newObject);
	}
	
	protected long getId() {
		return objectDataId;
	}

	public void flush(ServiceConnector connector, long jobId, List<Long> addedObjects) throws StorageException, ResourceException, RequiredParameterMissingException {
		prepareForSave(connector, jobId);
		saveOrUpdateObject(connector, jobId, addedObjects);
		saveNewObjects(connector, jobId, addedObjects);
		flushChildren(connector, jobId, addedObjects);
	}
	
	public void cleanNode() {
		newObjects.clear();
	}

	protected void saveNewObjects(ServiceConnector connector, long jobId,
			List<Long> addedObjects) throws StorageException {
		if (!newObjects.isEmpty()) {
			List<ObjectData> objectsToBeSaved = newObjectsAsObjectDataBuilders();
			Set<Long> ids = saveObjects(connector, jobId, objectsToBeSaved);
			addedObjects.addAll(ids);
		}
	}

	protected void flushChildren(ServiceConnector connector, long jobId,
			List<Long> addedObjects) throws StorageException,
			ResourceException, RequiredParameterMissingException {
		for (ObjectTreeNode treeNode: children) {
			treeNode.flush(connector, jobId, addedObjects);
		}
	}
	
	
	
	protected void prepareForSave(ServiceConnector connector, long jobId) throws StorageException, ResourceException, RequiredParameterMissingException {
		
	}

	private List<ObjectData> newObjectsAsObjectDataBuilders() {
		LinkedList<ObjectData> res = new LinkedList<ObjectData>();
		
		for (NewUrlObject obj: newObjects) {
			// FIXME: this flag should be parameterized
			res.add(obj.asDataObject(objectDataId)); 
		}
		return res;
	}

	private void saveOrUpdateObject(ServiceConnector connector, long jobId, List<Long> addedObjects) throws StorageException {
		if (getParent() == null) {
			// only the root have it's id present, other objects have to be created manually
			updateObject(jobId,connector);
		} else {			
			objectDataBuilder.addObjAttribute("parent", getParent().getId());
			objectDataId = saveObject(connector, jobId);
			addedObjects.add(objectDataId);
		}
	}

	protected long saveObject(ServiceConnector connector, long jobId) throws StorageException {
		ObjectData objectData = objectDataBuilder.build();			
		Set<Long> res = saveObjects(connector, jobId, Collections.singletonList(objectData));
		if (res.size() != 1) {
			LOG.error("Failed to save the context. contextData: {}", objectData);
			throw new StorageException("Failed to save the context: " + objectData );
		} else {
			return res.iterator().next();
		}
	}

	private Set<Long> saveObjects(ServiceConnector connector, long jobId, List<ObjectData> objectDataList) throws StorageException {
		ObjectResponse res = connector.saveObjects(jobId, objectDataList);
		return res.getObjects();
	}

	protected void updateObject(long jobId, ServiceConnector connector) throws StorageException {
		saveAttributes(jobId, connector);
    }

	protected void saveAttributes(long jobId, ServiceConnector connector)
			throws StorageException {
		ObjectData data = objectDataBuilder.build();
		int attrsCount = data.getAttributes().size();
		if (attrsCount > 0) {
			LOG.debug("Writing {} attributes into ObjectStore : {}...", data.getAttributes().size(), data.getAttributes());
			pl.nask.hsn2.bus.operations.ObjectResponse response = connector.updateObject(jobId, data);
            LOG.debug("... done, response = {}", response );
        } else {
            LOG.debug("No attribute to be saved in the store");
        }
	}
	
	public ObjectDataBuilder getObjectDataBuilder(){
		return objectDataBuilder;
	}
	
	public String getPrintableTreeContext(){
		StringBuilder tree = new StringBuilder(this.toString());
		for (ObjectTreeNode child : children){
			tree.append("\n");
			for (int i = 1; i < child.contextHeight; i++){
				tree.append("\t");
			}
			tree.append("|-").append(child.getPrintableTreeContext());
		}
		return tree.toString();
	}
}
