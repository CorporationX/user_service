package school.faang.user_service.service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class UserAboutFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return Objects.nonNull(userFilterDto.getAboutPattern());
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto filterDto) {
        Pattern pattern = Pattern.compile(filterDto.getAboutPattern(), Pattern.CASE_INSENSITIVE);

        return userStream.filter(user -> pattern.matcher(user.getAboutMe()).find());
    }
}
