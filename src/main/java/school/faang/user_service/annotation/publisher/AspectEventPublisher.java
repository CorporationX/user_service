package school.faang.user_service.annotation.publisher;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@Component
public class AspectEventPublisher {
    private final List<EventPublisher> publishers;

    @AfterReturning(pointcut = "@annotation(publishEvent)", returning = "returnValue")
    public void method(PublishEvent publishEvent, Object returnValue) {
        EventPublisher publisher = definePublisher(publishEvent.returnType());
        publisher.publish(returnValue);
    }

    public EventPublisher definePublisher(Class<?> returnType) {
        return publishers.stream()
                .filter(publisher -> publisher.getInstance().equals(returnType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Нет реализации паблишера для класса: " + returnType.getName()));
    }
}
