package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.kafka.UserDtoNotification;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDtoNotification toDtoNotification(User user);

    UserViewDto toUserViewDto(User user);

    List<UserViewDto> toUserViewDtos(List<User> users);
}
