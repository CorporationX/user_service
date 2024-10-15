package school.faang.user_service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.enums.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalInvitationDto {
    @NotNull(groups = {AfterCreate.class})
    private Long id;
    @NotNull(groups = {AfterCreate.class, BeforeCreate.class})
    private Long inviterId;
    @NotNull(groups = {AfterCreate.class, BeforeCreate.class})
    private Long invitedUserId;
    @NotNull(groups = {AfterCreate.class, BeforeCreate.class})
    private Long goalId;
    @NotNull(groups = {AfterCreate.class})
    private RequestStatus status;

    public interface BeforeCreate {
    }

    public interface AfterCreate {
    }
}