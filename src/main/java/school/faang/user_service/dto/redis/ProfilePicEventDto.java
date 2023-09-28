package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfilePicEventDto {
    private long id;
    private String link;
}
