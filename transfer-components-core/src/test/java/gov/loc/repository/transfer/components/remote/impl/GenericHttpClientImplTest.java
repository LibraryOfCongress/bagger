package gov.loc.repository.transfer.components.remote.impl;

import static org.junit.Assert.*;
import org.junit.Test;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import gov.loc.repository.transfer.components.remote.GenericHttpClient;
import gov.loc.repository.transfer.components.remote.impl.GenericHttpClientImpl;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class GenericHttpClientImplTest {

	@Test
	public void testExecute() throws Exception {

		Handler handler=new AbstractHandler()
		{
		    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) 
		        throws IOException, ServletException
		    {		    	
		    	assertEquals("b", request.getParameter("a"));
		    	assertEquals("d", request.getParameter("c"));
		        response.setStatus(HttpServletResponse.SC_OK);
		        ((Request)request).setHandled(true);
		    }
		};

		Server server = new Server(7999);
		server.setHandler(handler);
		server.start();
		
		GenericHttpClient client = new GenericHttpClientImpl();
		Map<String,String> parameterMap = new HashMap<String,String>();
		parameterMap.put("a","b");
		parameterMap.put("c", "d");
		assertTrue(client.execute("http://localhost:7999/test.html", parameterMap));
		server.stop();
	}
	
	//No parameters
	@Test
	public void testExecuteNoParameters() throws Exception {

		Handler handler=new AbstractHandler()
		{
		    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) 
		        throws IOException, ServletException
		    {		    	
		    	assertEquals(0, request.getParameterMap().size());
		        response.setStatus(HttpServletResponse.SC_OK);
		        ((Request)request).setHandled(true);
		    }
		};

		Server server = new Server(7999);
		server.setHandler(handler);
		server.start();
		
		GenericHttpClient client = new GenericHttpClientImpl();
		Map<String,String> parameterMap = new HashMap<String,String>();
		assertTrue(client.execute("http://localhost:7999/test.html", parameterMap));
		server.stop();
	}

	//Non-200
	@Test
	public void testExecuteNon200() throws Exception {

		Handler handler=new AbstractHandler()
		{
		    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) 
		        throws IOException, ServletException
		    {		    	
		        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        ((Request)request).setHandled(true);
		    }
		};

		Server server = new Server(7999);
		server.setHandler(handler);
		server.start();
		
		GenericHttpClient client = new GenericHttpClientImpl();
		Map<String,String> parameterMap = new HashMap<String,String>();
		assertFalse(client.execute("http://localhost:7999/test.html", parameterMap));
		server.stop();
	}

}
