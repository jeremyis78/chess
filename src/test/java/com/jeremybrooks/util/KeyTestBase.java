package com.jeremybrooks.util;

import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Assert;

public class KeyTestBase {

	protected static HashMap<Long, Integer> keyMapper = new HashMap<>();
	
	
	protected static void addKey(long key) {
		keyMapper.put(key, 1);
	}

	protected static void assertKeyIsUnique(long key) {
		boolean keyAlreadyUsed = keyMapper.get(key) != null;
		if(keyAlreadyUsed)
		{
			fail("each hash key must be unique; the key "+key+" was used twice");
		}
	}

	protected static void addKeyOrFailIfNotUnique(long key) {
		boolean keyAlreadyUsed = keyMapper.get(key) != null;
		if(keyAlreadyUsed)
		{
			fail("each hash key must be unique; the key "+key+" was used twice");
		}
		keyMapper.put(key, 1);
		Assert.assertNotNull(keyMapper.get(key));
	}

	public KeyTestBase() {
		super();
	}
	
	public KeyTestBase(int mapSize)
	{
		keyMapper = new HashMap<>(mapSize);
	}

}