package school.faang.user_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class Mapper {
    private final ObjectMapper objectMapper;

    public <T> Optional<String> toJson(T event) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("An error with mapping to json with " + event + ". " + e.getMessage());
        }
        return Optional.ofNullable(result);
    }
}
