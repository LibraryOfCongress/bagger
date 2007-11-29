package gov.loc.repository.transfer.components.annotations;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JobType {
	String name();
}
