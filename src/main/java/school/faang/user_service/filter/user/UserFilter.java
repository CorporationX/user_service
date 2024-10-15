package school.faang.user_service.filter.user;

import school.faang.user_service.model.filter_dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.filter.DtoFilter;

public interface UserFilter extends DtoFilter<UserFilterDto, User> {
}
