package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record GoalInvitationDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,                  // чтобы при сериализации игнорировать это поле, но выводить при десериализации

        @Min(value = 1, message = "Id can't be lower than 1")
        @NotNull(message = "Id can't be null")
        Long inviterId,

        @Min(value = 1, message = "Id can't be lower than 1")
        @NotNull(message = "Id can't be null")
        Long invitedUserId,

        @Min(value = 1, message = "Id can't be lower than 1")
        @NotNull(message = "Id can't be null")
        Long goalId,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        RequestStatus status      // чтобы при сериализации игнорировать это поле, но выводить при десериализации
) {
}
