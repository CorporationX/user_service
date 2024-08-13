package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class NamePatternSubscriptionFilter implements UserSubscriptionFilter {

    @Override
    public boolean isApplication(UserSubscriptionFilterDto userSubscriptionFilterDto) {
        return userSubscriptionFilterDto.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserSubscriptionFilterDto userSubscriptionFilterDto) {
        String namePattern = userSubscriptionFilterDto.getNamePattern();
        Pattern pattern = Pattern.compile(namePattern);
        return userStream.filter(user -> pattern.matcher(user.getUsername()).find());
    }
}
