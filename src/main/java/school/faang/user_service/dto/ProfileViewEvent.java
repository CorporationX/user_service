package school.faang.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProfileViewEvent {
    private Long viewerId;
    private Long authorId;
}
