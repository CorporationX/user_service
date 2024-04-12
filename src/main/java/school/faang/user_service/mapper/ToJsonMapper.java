package school.faang.user_service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ToJsonMapper {
    private final ObjectMapper objectMapper;
    public <T> String toJson(T object){
        String json;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("Unsuccessful mapping to JSON", e);
            throw new RuntimeException(e);
        }
        return json;
    }
}
