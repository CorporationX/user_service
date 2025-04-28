package school.faang.user_service.service.leaderboard;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.leaderboard.UserPopularity;
import school.faang.user_service.mapper.leaderboard.UserPopularityMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserPopularityMapperTest {
    private final UserPopularityMapper mapper = Mappers.getMapper(UserPopularityMapper.class);
    private final Country country = Country.builder().title("country").build();
    private final User user = User.builder().id(1L).username("name").country(country).build();
    private final UserPopularity userPopularity = new UserPopularity(1L, user, LocalDateTime.now(), 100);

    @Test
    public void testToUserPopularityResponseDto() {
        UserPopularityResponseDto dto = mapper.toUserPopularityResponseDto(userPopularity);

        compareUserPopularityAndUserPopularityResponseDto(userPopularity, dto);
    }

    @Test
    public void testToUserPopularityRequestDto() {
        UserPopularityRequestDto dto = mapper.toUserPopularityRequestDto(userPopularity);

        assertEquals(userPopularity.getId(), dto.id());
        assertEquals(userPopularity.getUser().getId(), dto.userId());
        assertEquals(userPopularity.getUser().getUsername(), dto.username());
        assertEquals(userPopularity.getUser().getCountry().getTitle(), dto.country());
    }

    @Test
    public void testToUserPopularityResponseDtoList() {
        User user2 = User.builder().id(2L).username("name2").country(country).build();
        UserPopularity userPopularity2 = new UserPopularity(2L, user2, LocalDateTime.now(), 120);

        List<UserPopularityResponseDto> dtoList = mapper.toUserPopularityResponseDtoList(
                List.of(userPopularity, userPopularity2));

        assertEquals(2, dtoList.size());
        compareUserPopularityAndUserPopularityResponseDto(userPopularity, dtoList.get(0));
        compareUserPopularityAndUserPopularityResponseDto(userPopularity2, dtoList.get(1));
    }

    private void compareUserPopularityAndUserPopularityResponseDto(
            UserPopularity userPopularity, UserPopularityResponseDto dto) {

        assertEquals(userPopularity.getId(), dto.id());
        assertEquals(userPopularity.getUser().getId(), dto.userId());
        assertEquals(userPopularity.getUser().getUsername(), dto.username());
        assertEquals(userPopularity.getUser().getCountry().getTitle(), dto.country());
        assertEquals(userPopularity.getImpact(), dto.impact());
    }
}
