package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {

    @NotNull(message = "Follower id can't be null")
    private long followerId;

    @NotNull(message = "Followee id can't be null")
    private long followeeId;
}
