package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectFollowerEventDto {
    private long subscriberId;
    private long ownerId;
    private long projectId;
}
