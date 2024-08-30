package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "preference", source = "user.contactPreference.preference")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
