package school.faang.user_service.filter.subfilter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface SubscriberFilter {
    boolean isApplicable(UserFilterDto filter);

    Stream<User> apply(Stream<User> users, UserFilterDto filterDto);
}
