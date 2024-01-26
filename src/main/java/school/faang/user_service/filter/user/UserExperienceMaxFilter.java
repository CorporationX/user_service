package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import java.util.stream.Stream;
@Component
public class UserExperienceMaxFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getExperienceMax() != null;
    }

    @Override
    public Stream<UserDto> apply(Stream<UserDto> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(userDto -> userDto.getExperience().intValue() <= userFilterDto.getExperienceMax().intValue());
    }

}
