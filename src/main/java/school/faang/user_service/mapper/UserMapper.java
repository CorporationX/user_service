package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserDto userDto);

    @Mapping(target = "followedUserIds", source = "followers", qualifiedByName = "usersToIds")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    @Named("usersToIds")
    default List<Long> mapUsersToIds(List<User> value) {
        if (value == null) {
            return null;
        }
        return value.stream().map(User::getId).toList();
    }
}