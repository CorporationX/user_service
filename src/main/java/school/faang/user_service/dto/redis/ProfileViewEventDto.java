package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileViewEventDto {
    private Long viewerId;
    private Long profileOwnerId;
}
