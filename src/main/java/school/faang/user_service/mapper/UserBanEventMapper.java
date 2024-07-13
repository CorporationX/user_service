package school.faang.user_service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.UserBanEvent;

@Component
@Data
public class UserBanEventMapper {

    private final ObjectMapper objectMapper;

    public UserBanEvent mapToUserBanEvent(String json) {
        try {
            return objectMapper.readValue(json, UserBanEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map JSON to UserBanEvent", e);
        }
    }
}
