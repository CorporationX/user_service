package school.faang.user_service.dto.redis;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTrafficDto {
    private Long viewerId;
    private Long userProfileId;
    private LocalDateTime viewDate;
}
