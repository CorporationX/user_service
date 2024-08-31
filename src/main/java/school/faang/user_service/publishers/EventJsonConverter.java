package school.faang.user_service.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventJsonConverter<T> {
    public final ObjectMapper objectMapper;

    public String toJson(T event) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (IOException e) {
            log.warn("Failure occurred withing converting {} to String", event, e);
        }
        return json;
    }
}
