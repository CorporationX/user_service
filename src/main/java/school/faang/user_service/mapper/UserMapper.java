package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "followers", target = "followerIds", qualifiedByName = "toFollowerIds")
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    @Named(value = "toFollowerIds")
    default List<Long> toFollowerIds(List<User> followers) {
        if (followers == null) {
            return new ArrayList<>();
        }

        List<Long> followerIds = new ArrayList<>();
        for (User follower : followers) {
            followerIds.add(follower.getId());
        }

        return followerIds;
    }
}
