package school.faang.user_service.dto.subscription;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequestDto {
    @NotNull
    @Positive
    private long followerId;

    @NotNull
    @Positive
    private long followeeId;
}
