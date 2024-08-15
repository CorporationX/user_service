package school.faang.user_service.filter.userPremium;

import school.faang.user_service.dto.userPremium.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserPremiumFilter {
    boolean isApplication(UserFilterDto userFilterDto);

    Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto);
}
