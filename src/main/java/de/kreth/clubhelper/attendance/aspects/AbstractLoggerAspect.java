package de.kreth.clubhelper.attendance.aspects;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractLoggerAspect {

    private final Map<Class<?>, Logger> loggerCache = new HashMap<>();

    public enum LogLevel {
	TRACE, DEBUG, INFO, WARN, ERROR;

	boolean enabled(Logger logger) {
	    switch (this) {
	    case WARN:
		return logger.isWarnEnabled();
	    case INFO:
		return logger.isInfoEnabled();
	    case DEBUG:
		return logger.isDebugEnabled();
	    case TRACE:
		return logger.isTraceEnabled();

	    default:
		return true;
	    }
	}
    }

    protected void log(LogLevel level, JoinPoint joinPoint) {
	log(level, joinPoint, null);
    }

    protected synchronized void log(LogLevel level, JoinPoint joinPoint, Object relatedData) {

	Logger logger = getLoggerFor(joinPoint);
	if (level.enabled(logger)) {

	    StringBuilder msg = generateLogMessage(joinPoint);
	    Exception exception = null;

	    if (relatedData != null) {
		if (relatedData instanceof Exception) {
		    exception = (Exception) relatedData;
		} else {
		    msg.append(" ==> ").append(relatedData);
		}
	    }

	    log(level, logger, msg, exception);
	}
    }

    private synchronized void log(LogLevel level, Logger logger, CharSequence msg, Exception exception) {
	String logText = msg.toString();
	switch (level) {
	case ERROR:
	    if (exception == null) {
		logger.error(logText);
	    } else {
		logger.error(logText, exception);
	    }
	    break;
	case WARN:
	    if (exception == null) {
		logger.warn(logText);
	    } else {
		logger.warn(logText, exception);
	    }
	    break;
	case INFO:
	    if (exception == null) {
		logger.info(logText);
	    } else {
		logger.info(logText, exception);
	    }
	    break;
	case DEBUG:
	    if (exception == null) {
		logger.debug(logText);
	    } else {
		logger.debug(logText, exception);
	    }
	    break;
	default:
	    if (exception == null) {
		logger.trace(logText);
	    } else {
		logger.trace(logText, exception);
	    }
	}
    }

    protected Logger getLoggerFor(JoinPoint joinPoint) {
	Class<?> logeventClass = joinPoint.getTarget().getClass();
	Logger logger;
	if (loggerCache.containsKey(logeventClass)) {
	    logger = loggerCache.get(logeventClass);
	} else {
	    logger = LoggerFactory.getLogger(logeventClass);
	    loggerCache.put(logeventClass, logger);
	}
	return logger;
    }

    protected synchronized StringBuilder generateLogMessage(JoinPoint joinPoint) {
	StringBuilder logMessage = new StringBuilder();
	logMessage.append(joinPoint.getTarget().getClass().getName());
	logMessage.append(".");
	logMessage.append(joinPoint.getSignature().getName());
	logMessage.append("(");
	// append args
	Object[] args = joinPoint.getArgs();
	for (int i = 0; i < args.length; i++) {
	    logMessage.append(args[i]).append(",");
	}
	if (args.length > 0) {
	    logMessage.deleteCharAt(logMessage.length() - 1);
	}

	logMessage.append(")");
	return logMessage;
    }

}