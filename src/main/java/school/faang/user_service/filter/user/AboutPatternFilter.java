package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;
@Component
public class AboutPatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getAboutPattern() != null && !filter.getAboutPattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(
                user -> user.getAboutMe() != null && user.getAboutMe().toLowerCase().contains(filterDto.getAboutPattern().toLowerCase().trim())
        );
    }
}
