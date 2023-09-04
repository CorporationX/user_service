package school.faang.user_service.dto.fundraising;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FundRaisedEvent {
    private long donorId;
    private long projectId;
    private int amount;
    private LocalDateTime timestamp;
}
