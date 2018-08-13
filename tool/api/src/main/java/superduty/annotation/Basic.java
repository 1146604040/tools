package superduty.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * 自定义校验注解
 * 
 * @author 王露
 *
 */
public @interface Basic {
	/**
	 * 最小值
	 * 
	 * @return
	 */
	int minInt() default Integer.MIN_VALUE;

	/**
	 * 最大值
	 * 
	 * @return
	 */
	int maxInt() default Integer.MAX_VALUE;

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
