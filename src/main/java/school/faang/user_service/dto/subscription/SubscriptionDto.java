package school.faang.user_service.dto.subscription;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class SubscriptionDto {

    private Long userId;
    private List<Long> followeeIds;

    public SubscriptionDto(Long userId, List<Long> followeeIds) {
        this.userId = userId;
        this.followeeIds = followeeIds;
    }
}
