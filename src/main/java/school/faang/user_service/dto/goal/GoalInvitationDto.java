package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoalInvitationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    @NotNull(message = "Id of the User inviting to a Goal cannot be null")
    private Long inviterId;
    @NotNull(message = "Id of the User invited to a Goal cannot be null")
    private Long invitedId;
    @NotNull(message = "Id of the Goal cannot be null")
    private Long goalId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private RequestStatus status;
}
