package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userdto);

    default List<UserDto> toDto(List<User> users) {
        return users.stream()
                .map(user -> new UserDto())
                .collect(Collectors.toList());
    }

    default List<User> toEntity(List<UserDto> usersdto) {
        return usersdto.stream()
                .map(userdto -> new User())
                .collect(Collectors.toList());
    }
}

