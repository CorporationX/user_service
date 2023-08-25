package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonObjectMapper {

    private final ObjectMapper objectMapper;

    public <T> T readValue(byte[] src, Class<T> valueType) {
        try {
            return objectMapper.readValue(src, valueType);
        } catch (IOException e) {
            log.error("IOException", e);
        }
        return null;
    }
}
