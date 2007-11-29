package gov.loc.repository.workflow.processdefinitions.prototype.prototype1;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.jmock.Expectations;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;

import gov.loc.repository.transfer.components.packaging.Unpackager;
import gov.loc.repository.transfer.components.remote.GenericHttpClient;
import gov.loc.repository.workflow.processdefinitions.AbstractProcessDefinitionTest;
import gov.loc.repository.workflow.processdefinitions.ProcessDefinitionHelper;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;
import gov.loc.repository.workflow.continuations.impl.SimpleContinuationControllerImpl;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

@RunWith(JMock.class)
public class Prototype1Test extends AbstractProcessDefinitionTest
{	
	static Mockery context = new JUnit4Mockery();
	private static final String PROCESS_DEFINITION_RESOURCE = "gov/loc/repository/workflow/processdefinitions/prototype/prototype1/processdefinition.xml";
	private static final String PROCESS_DEFINITION_RESOURCE_B = "gov/loc/repository/workflow/processdefinitions/prototype/prototype1b/processdefinition.xml";
	private static final String IDENTITIES =
		"<identity>" +
		"<user name='ray' />" +
		"<user name='myron' />" +		
		"<group name='qr' />" +
		"<membership user='ray' group='qr' />" +
		"<membership user='myron' group='qr' />" +
		"</identity>";
	
	@Override
	public void createFixtures() throws Exception {
		//Load identities to be used by the process definition
		loadIdentities(IDENTITIES);
	}
		
	@Test
	public void defaultFlow() throws Exception
	{

		//A helper for manipulating and deploying process definitions for testing
		ProcessDefinitionHelper helper = new ProcessDefinitionHelper();
		//A process definition that will be invoked by the primary process definition to demonstrate composability of process definitions
		//The subprocess has to be deployed first
		//This subprocess is intended to represent backup
		helper.setProcessDefinitionResource(PROCESS_DEFINITION_RESOURCE_B);
		helper.deploy();
		
		helper.clear();
		
		//The primary process definition
		helper.setProcessDefinitionResource(PROCESS_DEFINITION_RESOURCE);
		//Register mocks to be used by actionhandlers
		helper.registerFactoryMethod("//action[@class='gov.loc.repository.workflow.actionhandlers.UnzipActionHandler']", "Unpackager", "gov.loc.repository.workflow.processdefinitions.prototype.prototype1.Prototype1Test.createMockUnpackager");
		helper.registerFactoryMethod("//action[@class='gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandler']", "GenericHttpClient", "gov.loc.repository.workflow.processdefinitions.prototype.prototype1.Prototype1Test.createMockSyncClient");
		helper.registerFactoryMethod("//action[@class='gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandler']", "GenericHttpClient", "gov.loc.repository.workflow.processdefinitions.prototype.prototype1.Prototype1Test.createMockAsyncClient");
		String processDefinitionName = helper.deploy();
				
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		long tokenInstanceId;
		long processInstanceId;
		try
		{
			//Normally the creation of the process instance would be performed by a monitor or scheduler that detected the zip
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			tokenInstanceId = processInstance.getRootToken().getId();
			processInstanceId = processInstance.getId();
			//Makes sure everything is set-up OK
			assertEquals(processDefinitionName, processInstance.getProcessDefinition().getName());
			
			processInstance.getContextInstance().setVariable("sourceFile", "c:/test/here.zip");
			processInstance.signal();

			//Unzip is marked async, so should be waiting for Job Executor
			assertEquals("unzip", processInstance.getRootToken().getNode().getName());
			
		}
		finally
		{
			jbpmContext.close();
		}

		//Start the Job Executor and wait
		jbpmConfiguration.startJobExecutor();
		while(jbpmConfiguration.getJobExecutor().isStarted() == false)
		{
			Thread.sleep(500);
		}
		//Wait for the Job Executor
		Thread.sleep(1000);
		jbpmConfiguration.getJobExecutor().stopAndJoin();

		List<String> actorList = new ArrayList<String>();
		actorList.add("qr");
		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			//Will pass right through validate, since isn't wait state
			//If you look at the process definition, note that there is a VariableAdapterActionHandler
			//on the transition between unzip and validate.  This is because validate expects the variable packageDirectory.
			//The VariableAdapterActionHandler copies the destinationDirectory variable to the packageDirectory variable.
			//jBPM should have a more elegant way of doing this, but it doesn't.
			//Validate is an example of a synchronous request to a remote service (in this case, via http).
			//Should be at verify node, which is a wait node
			//Verify is an example of an asynchronous request to a remote service (in this case, via http).
			//The request was made on node-enter and now waiting for the callback
			assertEquals("verify", processInstance.getRootToken().getNode().getName());
			
			//Before continuing, prove that qr has no tasks
			assertEquals(0, jbpmContext.getGroupTaskList(actorList).size());
			
		}
		finally
		{
			jbpmContext.close();
		}
		
		//Will invoke the callback.  This would probably be done by an http callback to a servlet.
		SimpleContinuationController controller = new SimpleContinuationControllerImpl();
		controller.invoke(tokenInstanceId, true, null);

		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			
			//Should now be waiting at quality review for someone in qr group to pull
			assertEquals("quality review", processInstance.getRootToken().getNode().getName());
			List taskInstanceList = jbpmContext.getGroupTaskList(actorList);
			//There is now a task waiting for the qr group
			assertEquals(1, taskInstanceList.size());
			TaskInstance taskInstance = (TaskInstance)taskInstanceList.get(0);
			assertEquals("quality review task", taskInstance.getName());
			
			//Ray will now pull this into his personal task list
			//This would probably be done by some sort of a web interface
			assertEquals(0, jbpmContext.getTaskList("ray").size());
			taskInstance.setActorId("ray");
			assertEquals(1, jbpmContext.getTaskList("ray").size());
						
			//Before continuing, prove that no sub-tokens
			assertFalse(processInstance.getRootToken().hasActiveChildren());
			
			//Ray will now pass it
			taskInstance.end("continue");
			
			//Will fork.  Now the root token has two sub-tokens.
			//Backup invokes a sub-process.  However, since the sub-process doesn't have a wait state, no good way to test.
			//Ingest waits for signal.  There is no real reason for it to wait, other than to show testing of sub-tokens.
			assertTrue(processInstance.getRootToken().hasActiveChildren());
			List tokenList = processInstance.getRootToken().getChildrenAtNode(processInstance.getProcessDefinition().getNode("ingest")); 
			assertEquals(1, tokenList.size());
			((Token)tokenList.get(0)).signal("continue");
			//Can now join and continue to end
			assertEquals("end1", processInstance.getRootToken().getNode().getName());
			
		}
		finally
		{
			jbpmContext.close();
		}
		
		
	}
	
	public static Unpackager createMockUnpackager() throws Exception
	{
		//Setup mock
		final Unpackager unpackager = context.mock(Unpackager.class);
		context.checking(new Expectations() {{
			one(unpackager).unpackage(with(any(File.class)), with(any(File.class)));
		}});
		return unpackager;
	}
	
	@SuppressWarnings("unchecked")
	public static GenericHttpClient createMockSyncClient() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			one(client).execute(with(equalTo("http://foo.com/bar.html")), with(any(Map.class)));
			will(returnValue(true));
		}});
		return client;
	}

	@SuppressWarnings("unchecked")
	public static GenericHttpClient createMockAsyncClient() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			one(client).execute(with(equalTo("http://localhost/request.html")), with(any(Map.class)));
			will(returnValue(true));
		}});
		return client;
	}
	
}
