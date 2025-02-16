package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MentorshipRequestMapper.class)
public interface UserMapper {
    User toUserEntity(UserDto userDto);

    @Mapping(source = "user.id", target = "userId")
    UserDto toUserDto(User user);

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserResponseDto toUserResponseDto(User user);

}
