package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class CountryPatternSubscriptionFilter implements UserSubscriptionFilter {
    @Override
    public boolean isApplication(UserSubscriptionFilterDto  userSubscriptionFilterDto) {
        return userSubscriptionFilterDto.getCountryPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserSubscriptionFilterDto userSubscriptionFilterDto) {
        String countryPattern = userSubscriptionFilterDto.getCountryPattern();
        Pattern pattern = Pattern.compile(countryPattern);
        return userStream.filter(user -> pattern.matcher(user.getCountry().getTitle()).find());
    }
}
