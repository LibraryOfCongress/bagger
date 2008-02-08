package gov.loc.repository.service.servlets;

import gov.loc.repository.service.ServiceConstants;
import gov.loc.repository.utilities.ConfigurationFactory;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.configuration.Configuration;

public class MessageGeneratorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private Destination replyToDestination;
	
	@Override
	public void init() throws ServletException {
		try
		{
			Configuration configuration = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
			//Create the connection
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(configuration.getString("jms.connection"));
			connection = connectionFactory.createConnection();
			connection.start();
			
			//Create the session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(configuration.getStringArray("jms.queues")[0]);
			producer = session.createProducer(destination);
			
			replyToDestination = session.createQueue(configuration.getString("jms.replytoqueue"));
		}
		catch(Exception ex)
		{
			throw new ServletException(ex);
		}

	}	
	
	public void stop()
	{
		try
		{
			producer.close();
		}
		catch(Throwable ignore)
		{			
		}
		
		try
		{
			connection.close();
		}
		catch(Throwable ignore)
		{			
		}
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();
		writer.write("<html><body>");
		writer.write("<h1>Message Generator</h1>");
		writer.write("<p>This will submit messages to the jobqueue.  The jobType will be 'test'.</p>");

		String numberOfMessages = req.getParameter("num");
		String message = req.getParameter("message");
		boolean isTrue = false;
		if (req.getParameter("istrue") != null && req.getParameter("istrue").equals("true"))
		{
			isTrue = true;
		}
		if (numberOfMessages != null && message != null)
		{
			int num = Integer.parseInt(numberOfMessages);
			for(int i = 0; i < num; i++)
			{
				try
				{
					MapMessage mapMessage = session.createMapMessage();
					mapMessage.setJMSReplyTo(replyToDestination);
					mapMessage.setStringProperty("jobType", "test");
					mapMessage.setJMSCorrelationID(Integer.toString(i));
					mapMessage.setString("message", message);
					mapMessage.setBoolean("istrue", isTrue);
					mapMessage.setLong("key", 1L);
					producer.send(mapMessage);
				}
				catch(Exception ex)
				{
					throw new ServletException(ex);
				}
				
			}
			writer.write("<p><b>Submitted " + numberOfMessages + " messages.</b></p>");
		}
		writer.write("<form action='" + req.getRequestURI() + "' method='get'>");
		writer.write("Message:  <input type='text' name='message' value='foo' /><br/>");
		writer.write("<input type='checkbox' name='istrue' value='true' checked /> Is true<br/>");
		writer.write("Number of messages:  <select name='num'>");
		writer.write("<option selected>1</option>");
		for(int i=2; i <= 10; i++)
		{
			writer.write("<option>" + i + "</option>");
		}
		writer.write("<br/><input type='submit' value='Generate' />");
		writer.write("</body></html>");
	}
}
