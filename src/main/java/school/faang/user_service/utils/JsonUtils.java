package school.faang.user_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {
    private final ObjectMapper objectMapper;

    public <T> T deserialize(String jsonResponse, Class<T> classType) {
        try {
            return objectMapper.readValue(jsonResponse, classType);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing " + classType.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }

    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error serializing", e);
            throw new RuntimeException(e);
        }
    }
}
