package school.faang.user_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class Utils implements Serializable {

    public static ObjectMapper createJsonMapper() {
        return new ObjectMapper().
                configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).
                registerModule(new JavaTimeModule()).
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static String objectToJsonString(Object object) {
        try {
            return createJsonMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
