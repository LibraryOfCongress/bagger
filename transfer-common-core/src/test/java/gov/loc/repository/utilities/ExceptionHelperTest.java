package gov.loc.repository.utilities;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ExceptionHelperTest {

	@Test
	public void testHasCause() {
		assertTrue(ExceptionHelper.hasCause(new TestException(), TestException.class));
		assertFalse(ExceptionHelper.hasCause(new TestException(), IOException.class));
		assertTrue(ExceptionHelper.hasCause(new Exception(new TestException()), TestException.class));
		assertTrue(ExceptionHelper.hasCause(new RuntimeException(new TestException()), TestException.class));
	}

	public class TestException extends Exception
	{

		private static final long serialVersionUID = 1L;
		
	}
	
}
