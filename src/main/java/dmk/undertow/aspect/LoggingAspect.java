package dmk.undertow.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

	private Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	@Around("execution(* dmk.undertow.api..*.*(..))")
	public Object logging(ProceedingJoinPoint jp) throws Throwable {

		MethodSignature signature = (MethodSignature) jp.getSignature();
		Method method = signature.getMethod();

		logger.info("[ASPECT] " + method.getName());

		return jp.proceed();
	}
}
