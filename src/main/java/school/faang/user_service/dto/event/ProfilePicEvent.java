package school.faang.user_service.dto.event;

import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProfilePicEvent extends MessageEvent {
    private long userId;
    private LocalDateTime loadedAt;
}
