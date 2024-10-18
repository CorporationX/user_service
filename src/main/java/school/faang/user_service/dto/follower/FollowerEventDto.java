package school.faang.user_service.dto.follower;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowerEventDto {
    private String username;
    private Long followerId;
    private Long followeeId;
}
