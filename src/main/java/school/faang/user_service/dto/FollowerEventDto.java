package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowerEventDto {
    private long followeeId;
    private long followerId;
    private long projectId;
    private LocalDateTime receivedAt;
}
