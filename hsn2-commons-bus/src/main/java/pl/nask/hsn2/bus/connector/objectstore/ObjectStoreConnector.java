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

package pl.nask.hsn2.bus.connector.objectstore;

import java.util.Collection;
import java.util.Set;

import pl.nask.hsn2.bus.operations.JobStatus;
import pl.nask.hsn2.bus.operations.ObjectData;

/**
 * This is an interface to access to Object Store on technological level.
 * Here are no business method but only technical get/put/update. 
 * 
 * All methods of the interface will return only basic types
 * (references in special cases). Here are no business objects!
 * 
 *
 */
public interface ObjectStoreConnector {

	/**
	 * Creating single job object in Object Store.
	 * 
	 * @param jobId Framework identifier of the job.
	 * @param object Object to be created in Object Store.
	 * 
	 * @return Identifier of the job object in Object Store.
	 *  
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
	 */
    Long sendObjectStoreData(
    		long jobId, ObjectData object)
    				throws ObjectStoreConnectorException;

	/**
	 * Creating a collection of job objects in Object Store.
	 * 
	 * @param jobId Framework identifier of the job.
	 * @param taskId Task identifier, can be null
	 * @param dataList List of associated data objects.
	 * 
	 * @return Set of identifiers of created objects in Object Store.
	 *  
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
	 */
    Set<Long> sendObjectStoreData(
    		long jobId, Integer taskId, Collection<? extends ObjectData> dataList)
    				throws ObjectStoreConnectorException;

    /**
	 * Sending message to ObjectStore that the job has finished.
	 * 
	 * @param jobId Job identifier
	 * @param status Final status of the job
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
	 */
    void sendJobFinished(long jobId, JobStatus status) throws ObjectStoreConnectorException;
    
    /**
     * Update job object data in Object Store.
     * 
     * @param jobId Framework job identifier.
     * @param dataList Updated data to be set for job object.
     * 
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
	 */
    void updateObjectStoreData(
    		long jobId, Collection<? extends ObjectData> dataList)
    				throws ObjectStoreConnectorException;

    /**
     * Gets Job object from Object Store.
     * 
     * @param jobId Identifier of the job as the objects context.
     * @param objectId Identifier of the object to get. 
     * 
     * @return ObjectData Received object.
     * 
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
     */
    ObjectData getObjectStoreData(
    		long jobId, long objectId)
    				throws ObjectStoreConnectorException;

    /**
     * Find identifiers of all objects in specified job context
     * which contain attribute with <code>attributeName</code> name.
     * 
     * @param jobId Identifier of the job as the objects context.
     * @param attributeName Name of the attribute we are looking for.
     * 
     * @return List of identifiers of objects associated with the job
     *         and contains attribute with name <code>attributeName</code>
     *         
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
     */
    Set<Long> findByAttributeName(
    		long jobId, String attributeName)
    				throws ObjectStoreConnectorException;

    /**
     * Find identifiers of all objects in specified job context
     * which contain attribute with name <code>attributeName</code>
     * has value type String and value is equal to <code>value</code>.
     * 
     * @param jobId Identifier of the job as the objects context.
     * @param attributeName Name of the attribute we are looking for.
     * @param value Expected value of each matched attributes.
     * 
     * @return List of identifiers of objects associated with the job
     *         and contains attribute with name <code>attributeName</code>.
     *         
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
     */
    Set<Long> findByAttributeValue(
    		long jobId, String attributeName, String value)
    				throws ObjectStoreConnectorException;

    /**
     * Find identifiers of all objects in specified job context
     * which contain attribute with name <code>attributeName</code>
     * has value type Boolean and value is equal to <code>value</code>.
     * 
     * @param jobId Identifier of the job as the objects context.
     * @param attributeName Name of the attribute we are looking for.
     * @param value Expected value of each matched attributes.
     * 
     * @return List of identifiers of objects associated with the job
     *         and contains attribute with name <code>attributeName</code>.
     *         
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
     */
    Set<Long> findByAttributeValue(
    		long jobId, String attributeName, boolean value)
    				throws ObjectStoreConnectorException;

    /**
     * Find identifiers of all objects in specified job context
     * which contain attribute with name <code>attributeName</code>
     * has value type Integer and value is equal to <code>value</code>.
     * 
     * @param jobId Identifier of the job as the objects context.
     * @param attributeName Name of the attribute we are looking for.
     * @param value Expected value of each matched attributes.
     * 
     * @return List of identifiers of objects associated with the job
     *         and contains attribute with name <code>attributeName</code>.
     *         
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
     */
    Set<Long> findByAttributeValue(
    		long jobId, String attributeName, int value)
    				throws ObjectStoreConnectorException;

    /**
     * Find identifiers of all objects in specified job context
     * which contain attribute with name <code>attributeName</code>
     * has value type Long and value is equal to <code>value</code>.
     * 
     * @param jobId Identifier of the job as the objects context.
     * @param attributeName Name of the attribute we are looking for.
     * @param value Expected value of each matched attributes.
     * 
     * @return List of identifiers of objects associated with the job
     *         and contains attribute with name <code>attributeName</code>.
     *         
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
     */
    Set<Long> findByAttributeValue(
    		long jobId, String attributeName, long value)
    				throws ObjectStoreConnectorException;

    /**
     * Find identifiers of all objects in specified job context
     * which contain attribute with name <code>attributeName</code>
     * has value type Long and value is equal to <code>value</code>.
     * 
     * @param jobId Identifier of the job as the objects context.
     * @param attributeName Name of the attribute we are looking for.
     * @param value Expected value of each matched attributes.
     * 
     * @return List of identifiers of objects associated with the job
     *         and contains attribute with name <code>attributeName</code>.
     *         
	 * @throws ObjectStoreConnectorException Any problems with access
	 *  	   or communication with Object Store will rise this exception.
     */
    Set<Long> findByObjectId(
    		long jobId, String attributeName, long value)
    				throws ObjectStoreConnectorException;
}