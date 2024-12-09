package school.faang.user_service.dto.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDto {

    @NotNull(message = "The follower id mustn't be null")
    Long followerId;

    @NotNull(message = "The followee id mustn't be null")
    Long followeeId;
}
