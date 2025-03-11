package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.FollowerResponse;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    FollowerResponse userToUserDto(User user);
}
