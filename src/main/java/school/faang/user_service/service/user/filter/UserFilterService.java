package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilterService {

    Stream<User> applyFilters(Stream<User> users, UserFilterDto userFilterDto);
}
