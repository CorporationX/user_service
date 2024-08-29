package school.faang.user_service.event.profilepic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePicEvent {
    private final UUID eventId = UUID.randomUUID();
    private Long userId;
    private String avatarUrl;
}
