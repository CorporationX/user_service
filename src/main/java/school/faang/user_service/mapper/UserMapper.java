package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}
