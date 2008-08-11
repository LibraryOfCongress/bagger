package gov.loc.repository.results;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultList extends AbstractList<Result> implements List<Result> {

	private List<Object[]> resultList;
	private String[] fieldNameArray;
	
	public ResultList(String[] fieldNameArray)
	{
		this.resultList = new ArrayList<Object[]>();
		this.fieldNameArray = fieldNameArray;
	}
	
	public ResultList(List<Object[]> resultList, String[] fieldNameArray)
	{
		this.resultList = resultList;
		this.fieldNameArray = fieldNameArray;
	}
	
	@Override
	public Result get(int index) {
		if (index < 0 || index >= this.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return new Result(this.resultList.get(index), this.fieldNameArray);
	}
	
	@Override
	public int size() {
		return resultList.size();
	}
	
	private Object[] mapToArray(Map<String,Object> element)
	{
		Object[] objArray = new Object[this.fieldNameArray.length];
		for(int i=0; i < this.fieldNameArray.length; i++)
		{
			objArray[i] = element.get(this.fieldNameArray[i]);
		}		
		return objArray;
	}
	
	@Override
	public Result set(int index, Result element) {		
		this.resultList.set(index, this.mapToArray(element));
		return element;
	}
	
	@Override
	public void add(int index, Result element) {
		this.resultList.add(index, this.mapToArray(element));
	}
	
}
