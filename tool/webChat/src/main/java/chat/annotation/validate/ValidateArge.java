package chat.annotation.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateArge {

	/**
	 * 最小值
	 * 
	 * @return
	 */
	int min() default Integer.MIN_VALUE;

	/**
	 * 最大值
	 * 
	 * @return
	 */
	int max() default Integer.MAX_VALUE;

	/**
	 * 最大长度
	 * 
	 * @return
	 */
	int length() default 0;

	/**
	 * 正则表达式
	 * 
	 * @return
	 */
	String match() default ".*";

	/**
	 * 是否不为空
	 * 
	 * @return
	 */
	boolean isNotNull() default true;

	/**
	 * 是否为""或者null
	 * 
	 * @return
	 */
	boolean isEmpty() default true;
}
