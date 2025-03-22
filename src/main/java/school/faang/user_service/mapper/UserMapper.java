package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.FollowerResponseDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    FollowerResponseDto userToUserDto(User user);
}
