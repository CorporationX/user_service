package school.faang.user_service.dto.subscription;

import lombok.Data;

@Data
public class SubscriptionRequestDto {
    private long followerId;
    private long followeeId;
}
