package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.mentor.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    List<UserDto> toUserListDto (List<User> userList);
    UserDto toUserDto (User user);

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    default List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).toList();
    };

    default List<User> toEntityList(List<UserDto> usersDto){
        return usersDto.stream().map(this::toEntity).toList();
    };
}
