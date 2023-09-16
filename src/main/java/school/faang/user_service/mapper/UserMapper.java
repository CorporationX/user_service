package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    List<UserDto> toUserListDto(List<User> userList);

    @Mapping(target = "preference", source = "contactPreference.preference")
    UserDto toUserDto(User user);

    User toEntity(UserDto userDto);

    default List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(this::toUserDto).toList();
    };
}
