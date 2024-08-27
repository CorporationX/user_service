package school.faang.user_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        var params = new StringBuilder();
        for (Object param : obj) {
            params.append(param);
            params.append("\n");
        }
        log.error("Exception occurred at {}.{} with message: {} and params: {}", method.getDeclaringClass().getName(),
                method.getName(), throwable.getMessage(), params);
        throw new AsyncTaskFailedException(throwable.getMessage());
    }
}
