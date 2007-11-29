package gov.loc.repository.utilities.results;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.AbstractSet;

public class Result extends AbstractMap<String, Object> implements Map<String, Object>
{
	Set<Map.Entry<String, Object>> entrySet;
	
	public Result(Object[] fieldArray, String[] fieldNameArray)
	{		
		this.entrySet = new BackingResultSet(fieldArray, fieldNameArray);
	}
	
	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		return this.entrySet;
	}
	
			
	public class BackingResultSet extends AbstractSet<Entry<String, Object>> implements Set<Entry<String, Object>>
	{

		private Object[] fieldArray;
		private String[] fieldNameArray;
		
		public BackingResultSet(Object[] fieldArray, String[] fieldNameArray)
		{
			this.fieldArray = fieldArray;
			this.fieldNameArray = fieldNameArray;			
		}
		
		@Override
		public Iterator<java.util.Map.Entry<String, Object>> iterator() {
			return new BackingResultIterator(fieldArray, fieldNameArray);
		}

		@Override
		public int size() {
			return fieldNameArray.length;
		}
		
	}
	
	public class BackingResultIterator implements Iterator<java.util.Map.Entry<String, Object>>
	{
		private Object[] fieldArray;
		private String[] fieldNameArray;
		private int position = 0;
		
		public BackingResultIterator(Object[] fieldArray, String[] fieldNameArray)
		{
			this.fieldArray = fieldArray;
			this.fieldNameArray = fieldNameArray;
			
		}

		public boolean hasNext() {
			if (position == fieldArray.length)
			{
				return false;
			}
			return true;
		}

		public Map.Entry<String, Object> next() {
			if (! this.hasNext())
			{
				throw new NoSuchElementException();
			}
			Map.Entry<String, Object> entry = new ResultEntry(fieldNameArray[position], fieldArray[position]);			
			position++;
			return entry;
		}

		public void remove() {
			throw new UnsupportedOperationException();			
		}
		
	}
	
	public class ResultEntry implements Map.Entry<String, Object>
	{
		private String key;
		private Object value;
		
		public ResultEntry(String key, Object value)
		{
			this.key = key;
			this.value = value;
		}
				
		public String getKey() {
			return this.key;
		}

		public Object getValue() {
			return this.value;
		}

		public Object setValue(Object arg0) {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
