package school.faang.user_service.service.leaderboard;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.mapper.leaderboard.UserActivityMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserActivityMapperTest {
    private final UserActivityMapper mapper = Mappers.getMapper(UserActivityMapper.class);
    private final Country country = Country.builder().title("country").build();
    private final User user = User.builder().id(1L).username("name").country(country).build();
    private final UserActivity userActivity = new UserActivity(1L, user, LocalDateTime.now(), 100);

    @Test
    public void testToUserActivityResponseDto() {
        UserActivityResponseDto dto = mapper.toUserActivityResponseDto(userActivity);

        compareUserActivityAndUserActivityResponseDto(userActivity, dto);
    }

    @Test
    public void testToUserActivityRequestDto() {
        UserActivityRequestDto dto = mapper.toUserActivityRequestDto(userActivity);

        assertEquals(userActivity.getId(), dto.id());
        assertEquals(userActivity.getUser().getId(), dto.userId());
        assertEquals(userActivity.getUser().getUsername(), dto.username());
        assertEquals(userActivity.getUser().getCountry().getTitle(), dto.country());
    }

    @Test
    public void testToUserActivityResponseDtoList() {
        User user2 = User.builder().id(2L).username("name2").country(country).build();
        UserActivity userActivity2 = new UserActivity(2L, user2, LocalDateTime.now(), 120);

        List<UserActivityResponseDto> dtoList = mapper.toUserActivityResponseDtoList(
                List.of(userActivity, userActivity2));

        assertEquals(2, dtoList.size());
        compareUserActivityAndUserActivityResponseDto(userActivity, dtoList.get(0));
        compareUserActivityAndUserActivityResponseDto(userActivity2, dtoList.get(1));
    }

    private void compareUserActivityAndUserActivityResponseDto(
            UserActivity userActivity, UserActivityResponseDto dto) {

        assertEquals(userActivity.getId(), dto.id());
        assertEquals(userActivity.getUser().getId(), dto.userId());
        assertEquals(userActivity.getUser().getUsername(), dto.username());
        assertEquals(userActivity.getUser().getCountry().getTitle(), dto.country());
        assertEquals(userActivity.getRating(), dto.rating());
    }
}
