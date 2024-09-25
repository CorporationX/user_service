package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalInvitationDto {
    private Long id;

    private Long inviterId;

    @NotNull
    private Long invitedUserId;

    @NotNull
    private Long goalId;

    private RequestStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public GoalInvitationDto(Long invitedUserId, Long goalId) {
        this.invitedUserId = invitedUserId;
        this.goalId = goalId;
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
