package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "Person.firstName", source = "User.username")
    @Mapping(target = "Person.lastName", source = "User.username")
    User toUser(Person person);
}
