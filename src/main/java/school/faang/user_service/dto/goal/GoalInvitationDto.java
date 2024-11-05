package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalInvitationDto {

    @NotNull(groups = {After.class})
    private Long id;

    @NotNull(groups = {Before.class})
    private Long inviterId;

    @NotNull(groups = {Before.class})
    private Long invitedUserId;

    @NotNull(groups = {Before.class})
    private Long goalId;

    @NotNull(groups = {After.class})
    private RequestStatus status;

    public interface After {}

    public interface Before {}
}
