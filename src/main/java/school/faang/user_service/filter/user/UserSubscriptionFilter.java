package school.faang.user_service.filter.user;

import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserSubscriptionFilter {
    boolean isApplication(UserSubscriptionFilterDto filter);

    Stream<User> apply(Stream<User> userStream, UserSubscriptionFilterDto userSubscriptionFilterDto);
}
