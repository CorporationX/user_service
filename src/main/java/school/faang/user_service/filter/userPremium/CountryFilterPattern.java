package school.faang.user_service.filter.userPremium;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userPremium.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class CountryFilterPattern implements UserPremiumFilter {
    @Override
    public boolean isApplication(UserFilterDto userFilterDto) {
        return userFilterDto.getCountryFilter() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(user -> user.getCountry().getTitle().equals(userFilterDto.getCountryFilter()));
    }
}
