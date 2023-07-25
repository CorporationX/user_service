package school.faang.user_service.filters.filtersForUserFilterDto;

import school.faang.user_service.entity.User;
import school.faang.user_service.filters.UserFilterDto;

import java.util.stream.Stream;

public interface DtoUserFilter {

    boolean isApplicable(UserFilterDto userFilterDto);

    Stream<User> apply(Stream<User> users, UserFilterDto filterDto);
}
