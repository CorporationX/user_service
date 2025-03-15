package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.entity.leaderboard.UserImpact;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeaderboardMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserActivityResponseDto toUserActivityResponseDto(UserActivity userActivity);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserActivityRequestDto toUserActivityRequestDto(UserActivity userActivity);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserPopularityResponseDto toUserPopularityResponseDto(UserImpact UserImpact);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserPopularityRequestDto toUserPopularityRequestDto(UserImpact UserImpact);

    List<UserActivityResponseDto> toUserActivityResponseDtoList(List<UserActivity> users);

    List<UserPopularityResponseDto> toUserPopularityResponseDtoList(List<UserImpact> users);
}
