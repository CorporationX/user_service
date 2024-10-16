package school.faang.user_service.filter;

import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

public interface UserFilterStrategy {
    boolean applyFilter(UserFilterDto filter);

    List<User> filter(List<User> users, UserFilterDto filter);
}