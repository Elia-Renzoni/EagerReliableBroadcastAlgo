
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
		
		this.inMemoryStorage.put(key, this.transalateToBytesClass(value)); 
	}

	@Override
	public byte[] getValueFromKey(final String keytoSearch) throws Exception {
		Byte[] value =  this.inMemoryStorage.get(keytoSearch);
		if (value == null)
		       throw new Exception("Key Not Found!");	

		return this.transalateToBytesArray(value);
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

	private Byte[] transalateToBytesClass(final byte[] importSource) {
		Byte[] result = new Byte[importSource.length]; 	
		final int importSourceLength = importSource.length;
		
		for (int i = 0; i < importSourceLength; i++) 
			result[i] = importSource[i];
		return result;
	}

	private byte[] transalateToBytesArray(final Byte[] importSource) {
		byte[] result = new byte[importSource.length];
		final int importSourceLength = importSource.length;

		for (int i = 0; i < importSourceLength; i++) 
			result[i] = importSource[i].byteValue();
		return result;
	}
}


