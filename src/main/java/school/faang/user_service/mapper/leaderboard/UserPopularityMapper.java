package school.faang.user_service.mapper.leaderboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.entity.leaderboard.UserPopularity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserPopularityMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserPopularityResponseDto toUserPopularityResponseDto(UserPopularity UserImpact);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.country.title", target = "country")
    UserPopularityRequestDto toUserPopularityRequestDto(UserPopularity UserImpact);

    List<UserPopularityResponseDto> toUserPopularityResponseDtoList(List<UserPopularity> users);
}
