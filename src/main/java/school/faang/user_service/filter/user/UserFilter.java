package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.DtoFilter;


public interface UserFilter extends DtoFilter<UserFilterDto, User> {}
