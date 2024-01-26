package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import java.util.stream.Stream;
@Component
public class UserSkillFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getSkills() != null && !userFilterDto.getSkills().isEmpty();
    }

    @Override
    public Stream<UserDto> apply(Stream<UserDto> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(userDto -> userDto.getSkills().containsAll(userFilterDto.getSkills()));
    }
}
