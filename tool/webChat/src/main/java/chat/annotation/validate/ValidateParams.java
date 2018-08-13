package chat.annotation.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateParams {

	String[] isNotNull() default "";

	String[] isNull() default "";

	String[] isNotEmpty() default "";

	String[] isEmpty() default "";

	String[] length() default "0";

	String[] min() default "0";

	String[] max() default "0";

	boolean isValidate() default false;
}
