package gov.loc.repository.workflow.actionhandlers.annotations;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Transitions {
	String[] transitions();
}
