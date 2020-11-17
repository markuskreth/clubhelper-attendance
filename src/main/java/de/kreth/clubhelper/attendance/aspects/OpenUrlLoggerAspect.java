package de.kreth.clubhelper.attendance.aspects;

import static de.kreth.clubhelper.attendance.aspects.AbstractLoggerAspect.LogLevel.INFO;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class OpenUrlLoggerAspect extends AbstractLoggerAspect {

    @Pointcut("execution (public * com.vaadin.flow.component.page.Page.open(..))")
    private void invocation() {
    }

    @Before("invocation()")
    public void logPageOpen(JoinPoint joinPoint) {
	log(INFO, joinPoint);
    }

}
