package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class CityPatternSubscriptionFilter implements UserSubscriptionFilter {
    @Override
    public boolean isApplication(UserSubscriptionFilterDto userSubscriptionFilterDto) {
        return userSubscriptionFilterDto.getCityPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserSubscriptionFilterDto userSubscriptionFilterDto) {
        String cityPattern = userSubscriptionFilterDto.getCityPattern();
        Pattern pattern = Pattern.compile(cityPattern);
        return userStream.filter(user -> pattern.matcher(user.getCity()).find());
    }
}
