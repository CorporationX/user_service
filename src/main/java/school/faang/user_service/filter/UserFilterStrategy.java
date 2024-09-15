package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserFilterStrategy {
    boolean applyFilter(UserFilterDto filter);

    List<User> filter(List<User> users, UserFilterDto filter);
}