package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.EntityFilter;


public interface UserFilter extends EntityFilter<UserFilterDto, User> {}
