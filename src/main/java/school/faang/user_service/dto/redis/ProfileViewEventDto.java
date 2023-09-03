package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileViewEventDto {
    private Long viewerId;
    private Long profileOwnerId;
}
