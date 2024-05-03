package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {

    Stream<User> applyFilter(Stream<User> users, UserFilterDto userFilterDto);
}
