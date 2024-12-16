package school.faang.user_service.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class SearchAppearanceEvent {

    private final Long userId;
    private final Long searchingUserId;
    private final LocalDateTime viewedAt;

}