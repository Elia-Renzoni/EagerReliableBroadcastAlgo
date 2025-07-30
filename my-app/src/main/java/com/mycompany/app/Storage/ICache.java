
package com.mycompany.app;

public interface ICache {
	void setKV(final String key, final byte[] value) throws Exception;
	byte[] getValueFromKey(final String keytoSearch) throws Exception;
	boolean removeValueFromKey(final String key) throws Exception;
}
