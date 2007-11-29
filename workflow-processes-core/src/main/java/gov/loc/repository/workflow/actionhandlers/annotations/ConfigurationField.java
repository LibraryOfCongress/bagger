package gov.loc.repository.workflow.actionhandlers.annotations;

import java.lang.annotation.*;


@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigurationField {
	boolean isRequired() default true;
}
