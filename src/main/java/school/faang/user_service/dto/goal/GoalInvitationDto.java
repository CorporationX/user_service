package school.faang.user_service.dto.goal;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalInvitationDto {
    private long id;
    private UserDto inviter;
    private UserDto invited;
    private GoalDto goal;
    private RequestStatus status;
}
