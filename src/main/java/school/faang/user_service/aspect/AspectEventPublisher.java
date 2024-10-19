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

    @AfterReturning(pointcut = "@annotation(publishEvent)", returning = "returnedValue")
    public void execute(PublishEvent publishEvent, Object returnedValue) {
        EventPublisher publisher = definePublisher(publishEvent.returnedType());
        publisher.publish(returnedValue);
    }

    private EventPublisher definePublisher(Class<?> returnedType) {
        return publishers.stream()
                .filter(publisher -> publisher.getInstance().equals(returnedType))
                .findFirst()
                .orElseThrow(() -> new InvalidReturnTypeException("No implementation of the publisher for class: " +
                        returnedType.getName()));
    }
}
