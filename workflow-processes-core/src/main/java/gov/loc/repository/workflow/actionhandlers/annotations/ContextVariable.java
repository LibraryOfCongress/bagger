package gov.loc.repository.workflow.actionhandlers.annotations;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ContextVariable {
	String name() default "";
	String configurationFieldName() default "";
	boolean isRequired() default true;
}
