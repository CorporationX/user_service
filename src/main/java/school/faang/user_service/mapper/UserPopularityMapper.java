package school.faang.user_service.mapper;

import org.mapstruct.Mapping;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.entity.leaderboard.UserImpact;

import java.util.List;

public interface UserPopularityMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserPopularityResponseDto toUserPopularityResponseDto(UserImpact userPopularity);

    List<UserPopularityResponseDto> toUserPopularityResponseDtoList(List<UserImpact> users);
}
