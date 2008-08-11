package gov.loc.repository.workflow.hamcrest.collection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;

import java.util.Map;

public class IsEmptyMap<K,V> extends TypeSafeMatcher<Map<K,V>> {
	
	public IsEmptyMap() {
	}
	
	@Override
	public boolean matchesSafely(Map<K,V> map) {
		if (map.isEmpty())
		{
			return true;
		}
		return false;
	}

	public void describeTo(Description description) {
		description.appendText("empty map");

	}

    @Factory
    public static <K,V> Matcher<Map<K,V>> isEmpty(Class<K> k, Class<V> v) {
        return new IsEmptyMap<K,V>();
    }

}
