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
 * This is response operation if job has been rejected by a framework.
 * 
 *
 */
public class JobRejected implements Operation {

	/**
	 * Optional reason why job has been rejected.
	 */
	private String reason;

	/**
	 * Default constructor.
	 */
	public JobRejected() {
		this(null);
	}

	/**
	 * Constructor with reason argument.
	 * 
	 * @param reason Reason why job has been rejected by a framework.
	 */
	public JobRejected(String reason) {
		this.reason = reason;
	}

	/**
	 * Gets reason of reject.
	 * 
	 * @return reason Reason why job has been rejected by a framework.
	 */
	public final String getReason() {
		return reason;
	}

	/**
	 * Sets reason of reject.
	 * 
	 * @param reason Reason why job has been rejected by a framework.
	 */
	public final void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return new StringBuffer(128).append("JobRejected={")
				.append("reason=").append(reason).append("}")
				.toString();
	}
}
