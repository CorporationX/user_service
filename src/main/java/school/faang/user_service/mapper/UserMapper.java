package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "users", target = "user", qualifiedByName = "map")
    List<UserDto> toDto(List<User> user);

    User toEntity(UserDto userDto);

    default List<Long> map(List<User> users) {
        return users.stream().map(User::getId).toList();
    }
}