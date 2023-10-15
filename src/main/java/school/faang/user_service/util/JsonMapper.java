package school.faang.user_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonMapper {

    private final ObjectMapper objectMapper;

    public <T> Optional<String> toJson(T event) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Error when converting event to json string with " + event + "." + "\nJsonProcessingException: ", e);
        }
        return Optional.ofNullable(json);
    }
}