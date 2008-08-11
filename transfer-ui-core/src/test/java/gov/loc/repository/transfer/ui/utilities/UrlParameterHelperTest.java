package gov.loc.repository.transfer.ui.utilities;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class UrlParameterHelperTest {
    
	
	@Test
	public void testParseNullDescription() throws Exception {
		
		assertTrue(UrlParameterHelper.parse("foo/100", null).isEmpty());
	}

	@Test
	public void testParse() throws Exception {
		
		Map<String,String> parameterMap = UrlParameterHelper.parse("foo/100?user=bob", "foo/{foo}");
		assertEquals(1, parameterMap.size());
		assertEquals("100", parameterMap.get("foo"));

		parameterMap = UrlParameterHelper.parse("foo/100/bar/200", "foo/{foo}/bar/{bar}");
		assertEquals(2, parameterMap.size());
		assertEquals("100", parameterMap.get("foo"));
		assertEquals("200", parameterMap.get("bar"));
		
		parameterMap = UrlParameterHelper.parse("foo", "foo/{foo}");
		assertTrue(parameterMap.isEmpty());
		
		parameterMap = UrlParameterHelper.parse("bar/100", "foo/{foo}");
		assertTrue(parameterMap.isEmpty());
		
		parameterMap = UrlParameterHelper.parse("bar/100.html", "bar/100\\.{foo}");
		assertEquals("html", parameterMap.get("foo"));
		
		parameterMap = UrlParameterHelper.parse("bar/100.html", "bar/{foo}\\.html");
		assertEquals("100", parameterMap.get("foo"));
		
		parameterMap = UrlParameterHelper.parse("bar/100.html", "bar/{id}\\.{format}");
		assertEquals("100", parameterMap.get("id"));
		assertEquals("html", parameterMap.get("format"));
	}
}
