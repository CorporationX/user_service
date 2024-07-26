package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.userDto.UserDto;
import school.faang.user_service.entity.User;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    List<UserDto> toDtoList(List<User> users);

    @Mapping(source = "followers", target = "followersId", qualifiedByName = "mapFollowers")
    UserDto toDto(User user);


//    @Mapping(source = "posts", target = "postsId", qualifiedByName = "mapPost")
//    @Mapping(source = "visibilityUsers", target = "visibilityUsersId", qualifiedByName = "mapVisibilityUsers")
//    AlbumDto toDto(Album album);

    @Named("mapFollowers")
    default List<Long> mapFollowers(List<User> followers) {
        return followers.stream().map(User::getId).toList();
    }
}