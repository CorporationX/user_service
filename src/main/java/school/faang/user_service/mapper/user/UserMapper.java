package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "followees", target = "followees", qualifiedByName = "getFolloweeId")
    UserDto toDto(User entity);

    @Mapping(source = "followees", target = "followees", qualifiedByName = "getFolloweeId")
    List<UserDto> toDtos(List<User> entities);

    @Named("getFolloweeId")
    default List<Long> getFolloweeIds(List<User> followees) {
        return followees.stream()
                .map(User::getId).toList();
    }

}
