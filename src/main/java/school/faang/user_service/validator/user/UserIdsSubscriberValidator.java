package school.faang.user_service.validator.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserIdsSubscriberValidator {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public void validateId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException(String.format("User with id '%s' does not exist", id));
        }
    }

    public List<Long> parseUserIds(Object message) {
        try {
            if (message instanceof List<?>) {
                return (List<Long>) message;
            } else if (message instanceof String) {
                return objectMapper.readValue((String) message, new TypeReference<List<Long>>() {});
            } else {
                log.warn("Received unexpected message type: {}", message.getClass().getName());
                return null;
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse message: {}", message, e);
        } catch (ClassCastException e) {
            log.error("Failed to cast message to List<Long>: {}", message, e);
        }
        return null;
    }
}
