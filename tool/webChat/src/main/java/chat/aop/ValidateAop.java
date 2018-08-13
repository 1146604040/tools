package chat.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import chat.annotation.BasicValidate;
import chat.annotation.validate.ValidateParams;

@Component
@Aspect 
public class ValidateAop {

	@Pointcut("@annotation(chat.annotation.validate.ValidateParams)")
	public void point() {
	}

	@Before("point()")
	public void before(JoinPoint point) {

		Class<?>[] argTypes = new Class[point.getArgs().length];
		for (int i = 0; i < argTypes.length; i++) {
			argTypes[i] = point.getArgs().getClass();
		}
		try {
			Method method = point.getTarget().getClass().getMethod(point.getSignature().getName(), argTypes);
			ValidateParams annotataion = method.getAnnotation(ValidateParams.class);

		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

	}

	public void validateParams(ValidateParams v, Object[] args) {
		
	}

}
