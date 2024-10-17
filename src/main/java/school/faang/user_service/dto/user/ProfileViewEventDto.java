package school.faang.user_service.dto.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@RequiredArgsConstructor
public class ProfileViewEventDto {
    private final long receiverId;
    private final long actorId;
    private LocalDateTime receivedAt = LocalDateTime.now();
}