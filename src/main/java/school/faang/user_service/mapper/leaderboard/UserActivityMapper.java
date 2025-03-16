package school.faang.user_service.mapper.leaderboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
import school.faang.user_service.entity.leaderboard.UserActivity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserActivityMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserActivityResponseDto toUserActivityResponseDto(UserActivity userActivity);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserActivityRequestDto toUserActivityRequestDto(UserActivity userActivity);

    List<UserActivityResponseDto> toUserActivityResponseDtoList(List<UserActivity> users);
}
