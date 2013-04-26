package pl.nask.hsn2.connector.REST;

public class DataResponse {
	private final boolean isSuccesful;
	private final long keyId;
	private final String error;

	/**
	 * Constructor used to get successful data response.
	 * 
	 * @param key
	 *            Key ID of the object.
	 */
	public DataResponse(long key) {
		keyId = key;
		isSuccesful = true;
		error = null;
	}

	/**
	 * Constructor used to get successful data response.
	 * 
	 * @param key
	 *            Key ID of the object.
	 */
	public DataResponse(String errorMsg) {
		error = errorMsg;
		isSuccesful = false;
		keyId = -1;
	}

	/**
	 * Returns response success state.
	 * 
	 * @return <code>true</code> if connection was successful, <code>false</code> otherwise.
	 */
	public boolean isSuccesful() {
		return isSuccesful;
	}

	/**
	 * Returns key ID of stored object.
	 * 
	 * @return Key ID of stored object. If storing wasn't successful it returns <code>-1</code>.
	 */
	public long getKeyId() {
		return keyId;
	}

	/**
	 * Returns error message, if there is any.
	 * 
	 * @return Error message or <code>null</code> if action was successful.
	 */
	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		return DataResponse.class.getSimpleName() + "{success=" + isSuccesful + ",key=" + keyId + "}";
	}
}
