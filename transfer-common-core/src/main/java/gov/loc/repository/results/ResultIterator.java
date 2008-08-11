package gov.loc.repository.results;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class ResultIterator implements Iterator<Map<String, Object>> {

	private Iterator iter;
	private String[] fieldNameArray;
	
	public ResultIterator(List resultList, String[] fieldNameArray)
	{		
		this.iter = resultList.iterator();
		this.fieldNameArray = fieldNameArray;
	}
	
	public boolean hasNext() {
		return iter.hasNext();
	}

	public Map<String, Object> next() {
		if (! iter.hasNext())
		{
			throw new NoSuchElementException();	
		}
		return new Result((Object[])iter.next(), this.fieldNameArray);
	}

	public void remove() {
		throw new UnsupportedOperationException();		
	}

}
