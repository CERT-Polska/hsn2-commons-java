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

package pl.nask.hsn2.bus.operations;

/**
 * This is a enum of reasons why single step in job processing
 * could not be completed. There is strait mapping from error
 * reasons reported by service and this enum.
 * 
 * Beside errors reported by services the enum can be used
 * by workflow engine as well if there is an issue to restart
 * or move forward job processing.
 * 
 * The reason is generic and more details must be provided
 * in description. So the error type should be treated as
 * category of the error.
 *
 */
public enum TaskErrorReasonType {
	/**
	 * If there is something unexpected with processing
	 * particular step in the job <code>DEFUNC</code>
	 * error type should be set. For instance this reason
	 * will occur if there is a problem with resume a job.
	 */
    DEFUNCT,
    
    /**
     * If there is a problem with connection to an Object Store
     * particular step in job processing can be finished with
     * error and reason will <code>OBJ_STORE</code>.
     */
    OBJ_STORE,
    
    /**
     * If there is a problem with access to a Data Store
     * particular step in job processing can be finished with
     * error and reason will <code>DATA_STORE</code>.
     */
    DATA_STORE,

    /**
     * If the step in job processing cannot be accomplished
     * because parameters provided to the service are incomplete
     * or incorrect then task error will report with
     * <code>PARAMS</code> reason and detailed description.
     */
    PARAMS,
    
    /**
     * If the service needs any external resource to process
     * task and the resource is unavailable then task will
     * finishes with <code>RESOURCE</code> reason.
     * 
     * In this case Object Store and Data Store resources are
     * NOT external.
     */
    RESOURCE,
    
    /**
     * The object passed in TaskRequest does not have attributes 
     * required by the service or values of these attributes is incorrect. This also
     * applies to content of other objects or entries in data store referenced from
     * the input object.
     */
    INPUT
}
