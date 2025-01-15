package school.faang.user_service.filter.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.service.UserService;

import java.util.stream.Stream;

@Component
public class InviterNamePatternFilter implements Filter<GoalInvitationDto, InvitationFilterDto> {

    private final UserService userService;

    @Autowired
    public InviterNamePatternFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getInviterNamePattern() != null;
    }

    @Override
    public void apply(Stream<GoalInvitationDto> goalInvitationDto, InvitationFilterDto filter) {
        goalInvitationDto.filter(invitation -> invitation.getInviterId().equals(
                userService.allUsersStream()
                        .filter(user -> user.getUsername().equals(filter.getInviterNamePattern()))
                        .map(User::getId)));
    }
}
