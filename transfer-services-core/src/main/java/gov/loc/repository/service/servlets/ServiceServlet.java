package gov.loc.repository.service.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import gov.loc.repository.service.Service;
import gov.loc.repository.service.Service.Status;
import gov.loc.repository.service.impl.ActiveMQJmsMessenger;
import gov.loc.repository.service.impl.FileSystemMementoStore;
import gov.loc.repository.service.impl.ServiceImpl;
import gov.loc.repository.service.impl.SpringTaskFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Service service;
	
	@Override
	public void init() throws ServletException {
		try
		{
			service = new ServiceImpl();
			//Create a Messenger		
			service.setMessenger(new ActiveMQJmsMessenger());
			
			//Create a MementoStore
			service.setMementoStore(new FileSystemMementoStore());
			
			//Create a Bean Factory
			BeanFactory beanFactory = new ClassPathXmlApplicationContext("service-beans-*.xml");
			
			//And use to configure TaskFactory
			SpringTaskFactory taskFactory = new SpringTaskFactory();
			taskFactory.setBeanFactory(beanFactory);
			service.setTaskFactory(taskFactory);
	
			service.start();
		}
		catch(Exception ex)
		{
			throw new ServletException(ex);
		}
	}
	
	@Override
	public void destroy() {
		if (service !=  null)
		{
			service.stop();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try
		{
			if (req.getParameter("start") != null && ! (service.getStatus() == Status.RUNNING))
			{
				service.start();
			}
			else if (req.getParameter("stop") != null && service.getStatus() == Status.RUNNING)
			{
				service.stop();
			}
			
			resp.setContentType("text/html");
			PrintWriter writer = resp.getWriter();
			writer.write("<html><body>");
			writer.write("<h1>Service monitor</h1>");
			writer.write("<p>Service status: " + service.getStatus() + "</p>");
			writer.write("<p>This service will process:<br/>");
			for(String jobType : service.getJobTypeList())
			{
				writer.write(jobType + "<br/>");
			}
			writer.write("</p>");
			writer.write("<p>This service will monitor:<br/>");
			for(String queue : service.getQueueList())
			{
				writer.write(queue + "<br/>");
			}
			writer.write("</p>");			
			writer.write("<p>Threads:<br/>");
			for(int i=0; i < service.getThreadCount(); i++)
			{
				writer.write("Thread " + i + " is ");
				if (service.isThreadActive(i))
				{
					writer.write("running " + service.getMementoStore().get(i).getJobType() + ".");
				}
				else
				{
					writer.write("inactive.");
				}
				writer.write("<br/>");
			}
			writer.write("</p>");
			if (service.getStatus() == Status.RUNNING)
			{
				writer.write("<p><a href='" + req.getRequestURI() + "?stop=true'>Stop the service.</a></p>");
			}
			else
			{
				writer.write("<p><a href='" + req.getRequestURI() + "?start=true'>Start the service.</a></p>");
			}
			writer.write("</body></html>");
		}
		catch(Exception ex)
		{
			throw new ServletException(ex);
		}
	}
	
}
