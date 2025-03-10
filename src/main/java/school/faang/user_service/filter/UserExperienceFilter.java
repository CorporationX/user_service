package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        if (userFilterDto.experienceMin() < 0) {
            throw new IllegalArgumentException("Minimal experience must be positive.");
        }
        if (userFilterDto.experienceMax() < 0) {
            throw new IllegalArgumentException("Maximal experience must be positive.");
        }
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(user -> user.getExperience() >= userFilterDto.experienceMin()
                && user.getExperience() <= userFilterDto.experienceMax());
    }
}
