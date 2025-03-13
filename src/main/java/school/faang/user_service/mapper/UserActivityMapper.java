package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.leaderboard.UserActivityDto;
import school.faang.user_service.entity.leaderboard.UserActivity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserActivityMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserActivityDto toUserActivityDto(UserActivity userActivity);

    List<UserActivityDto> toUserActivityDtoList(List<UserActivity> users);
}
