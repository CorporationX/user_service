package school.faang.user_service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonObjectMapper {
    private final ObjectMapper objectMapper;

    public byte[] toJson(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (Exception e) {
            log.error("Error while converting object to string", e);
        }
        return null;
    }
}