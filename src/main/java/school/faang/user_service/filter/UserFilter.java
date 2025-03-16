package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterRequestDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFilterRequestDto userFilterRequestDto);

    Stream<User> apply(Stream<User> users, UserFilterRequestDto userFilterRequestDto);
}
