package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    UserViewDto toUserViewDto(User user);

    List<UserViewDto> toUserViewDtos(List<User> users);
}
