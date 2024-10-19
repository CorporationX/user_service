package school.faang.user_service.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.exception.annotation.InvalidReturnTypeException;

import java.util.List;

@Aspect
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@Component
public class AspectEventPublisher {
    private final List<EventPublisher> publishers;

    @AfterReturning(pointcut = "@annotation(publishEvent)", returning = "returnValue")
    public void execute(PublishEvent publishEvent, Object returnValue) {
        EventPublisher publisher = definePublisher(publishEvent.returnType());
        publisher.publish(returnValue);
    }

    private EventPublisher definePublisher(Class<?> returnType) {
        return publishers.stream()
                .filter(publisher -> publisher.getInstance().equals(returnType))
                .findFirst()
                .orElseThrow(() -> new InvalidReturnTypeException("No implementation of the publisher for class: " +
                        returnType.getName()));
    }
}
