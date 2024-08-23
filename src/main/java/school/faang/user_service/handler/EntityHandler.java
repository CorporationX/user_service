package school.faang.user_service.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Component
@Slf4j
public class EntityHandler {

    public <T> T getOrThrowException(Class<T> entityClass, Object identifier, Supplier<Optional<T>> finder) {
        return finder.get().orElseThrow(() -> {
            String errMessage = String.format("Could not find %s with ID: %s", entityClass.getName(), identifier);
            EntityNotFoundException exception = new EntityNotFoundException(errMessage);
            log.error(errMessage, exception);
            return exception;
        });
    }
}
