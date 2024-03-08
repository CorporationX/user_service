package school.faang.user_service.UserMapper;

import org.mapstruct.Mapper;
import school.faang.user_service.UserDto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);
}
