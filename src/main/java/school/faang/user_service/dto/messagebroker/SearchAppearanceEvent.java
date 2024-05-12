package school.faang.user_service.dto.messagebroker;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class SearchAppearanceEvent {
    private Long userId;
    private Long searchUserId;
    private LocalDateTime dateAndTimeViewing;
}
