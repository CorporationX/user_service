package school.faang.user_service.filters;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDto userToDto(User user);
    User dtoToUser(UserDto userDto);
}
