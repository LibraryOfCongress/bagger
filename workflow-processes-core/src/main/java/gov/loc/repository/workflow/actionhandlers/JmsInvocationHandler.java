package gov.loc.repository.workflow.actionhandlers;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class JmsInvocationHandler implements InvocationHandler {

	public Object invoke(Object object, Method method, Object[] args)
			throws Throwable {
		System.out.println("Called " + method.getName());
		return null;
	}

}
