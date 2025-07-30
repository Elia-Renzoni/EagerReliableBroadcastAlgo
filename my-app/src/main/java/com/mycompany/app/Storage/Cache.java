
package com.mycompany.app.Storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Cache implements ICache {
	private ConcurrentMap<String, Byte[]> inMemoryStorage;

	public Cache() { this.inMemoryStorage = new ConcurrentHashMap<>(); }

	@Override
	public void setKV(final String key, final byte[] value) throws Exception {
		if (!(this.inMemoryStorage.containsKey(key))) 
			throw new Exception("The Given Key Already Exists!"); 
		// converting the value
		var actualValue = new Byte[value.length];
		for (int i = 0; i < actualValue.length; i++) {
			actualValue[i] = (Byte) value[i];
		}
		this.inMemoryStorage.put(key, actualValue); 
	}

	@Override
	public byte[] getValueFromKey(final String keytoSearch) throws Exception {
		Byte[] value =  this.inMemoryStorage.get(keytoSearch);
		if (value == null)
		       throw new Exception("Key Not Found!");	

		byte[] castedResult = new byte[value.length];	
		for (int i = 0; i < castedResult.length; i++) {
			castedResult[i] = value[i].byteValue();
		}
		return castedResult;
	}

	@Override
	public boolean removeValueFromKey(final String key) throws Exception {
		if (!(this.inMemoryStorage.containsKey(key)))
			throw new Exception("Key Not Found!");

		this.inMemoryStorage.remove(key);
		if (this.inMemoryStorage.containsKey(key))
			return false;
		return true;
	}
}


