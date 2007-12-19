package gov.loc.repository.utilities;

import java.util.HashMap;

public class EnhancedHashMap<K,V> extends HashMap<K,V>{
	
	private static final long serialVersionUID = 1L;

	public V getRequired(K key) throws Exception
	{
		V obj = this.get(key);
		if (obj == null)
		{
			throw new Exception("Value not found for key " + key.toString());
		}
		return obj;
	}
	
	public V get(K key, V defaultValue)
	{
		V obj = this.get(key);
		if (obj == null)
		{
			return defaultValue;
		}
		return obj;
		
	}
}
