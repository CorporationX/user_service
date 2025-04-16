package school.faang.user_service.service.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.leaderboard.UserImpactDto;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.leaderboard.UserPopularity;
import school.faang.user_service.mapper.leaderboard.UserPopularityMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserPopularityRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation.COUNTRY_CANT_BE_NULL_OR_BLANK;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation.NUMBER_OF_TOP_USERS_MUST_BE_POSITIVE;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation.REQUESTED_INVALID_RANGE_OF_USERS;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation.USERNAME_CANT_BE_NULL_OR_BLANK;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation.USER_ID_CANT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation.USER_IMPACT_DTO_CANT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation.USER_POPULARITY_REQUEST_DTO_CANT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation.USER_POPULARITY_REQUEST_DTO_ID_CANT_BE_NULL;

@ExtendWith(MockitoExtension.class)
public class UserPopularityServiceTest {
    @InjectMocks
    private UserPopularityService userPopularityService;

    @Mock
    private UserPopularityRepository userPopularityRepository;

    @Mock
    private UserPopularityRedisService userPopularityRedisService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPopularityMapper userPopularityMapper;

    private final UserImpactDto userImpactDto = UserImpactDto.FOLLOWED;
    private final Country country = Country.builder().title("country").build();
    private final User user = User.builder().id(1L).username("name").country(country).build();
    private final Long id = 1L;
    private final Long userId = 2L;
    private final String username = "name";
    private final UserPopularityRequestDto popularityDto = new UserPopularityRequestDto(
            id, userId, username, country.getTitle(), userImpactDto);
    private final UserPopularity userPopularity = new UserPopularity(id, user, LocalDateTime.now(), 100);
    private final int maxCachedLeaderboardSize = 100;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userPopularityService, "maxCachedLeaderboardSize", maxCachedLeaderboardSize);
    }

    @Test
    public void testRecordUserAction_nullUserActivityRequestDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.recordUserImpact(null, userImpactDto)
        );
        assertEquals(USER_POPULARITY_REQUEST_DTO_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullIdInUserActivityRequestDto() {
        UserPopularityRequestDto dto = new UserPopularityRequestDto(null, userId, username,
                country.getTitle(), userImpactDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.recordUserImpact(dto, userImpactDto)
        );
        assertEquals(USER_POPULARITY_REQUEST_DTO_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullUserIdInUserActivityRequestDto() {
        UserPopularityRequestDto dto = new UserPopularityRequestDto(id, null, username,
                country.getTitle(), userImpactDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.recordUserImpact(dto, userImpactDto)
        );
        assertEquals(USER_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullCountryInUserActivityRequestDto() {
        UserPopularityRequestDto dto = new UserPopularityRequestDto(id, userId, username,
                null, userImpactDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.recordUserImpact(dto, userImpactDto)
        );
        assertEquals(COUNTRY_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullUsernameInUserActivityRequestDto() {
        UserPopularityRequestDto dto = new UserPopularityRequestDto(id, userId, null,
                country.getTitle(), userImpactDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.recordUserImpact(dto, userImpactDto)
        );
        assertEquals(USERNAME_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullUserActionDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.recordUserImpact(popularityDto, null)
        );
        assertEquals(USER_IMPACT_DTO_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_NewActivity() {
        when(userPopularityRepository.findById(userPopularity.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(popularityDto.userId())).thenReturn(Optional.of(user));
        userPopularityService.recordUserImpact(popularityDto, userImpactDto);

        ArgumentCaptor<UserPopularity> captor = ArgumentCaptor.forClass(UserPopularity.class);
        verify(userPopularityRepository, times(1)).save(captor.capture());
        UserPopularity savedPopularity = captor.getValue();

        assertEquals(userImpactDto.getImpactScore(), savedPopularity.getImpact());
        assertEquals(user, savedPopularity.getUser());
        verify(userPopularityRedisService, times(1)).recordUserImpact(savedPopularity, popularityDto);
    }

    @Test
    public void testRecordUserAction_ExistingActivity() {
        long ratingBefore = userPopularity.getImpact();
        when(userPopularityRepository.findById(userPopularity.getId())).thenReturn(Optional.of(userPopularity));

        userPopularityService.recordUserImpact(popularityDto, userImpactDto);

        assertEquals(ratingBefore + userImpactDto.getImpactScore(), userPopularity.getImpact());
        verify(userPopularityRedisService, times(1)).recordUserImpact(userPopularity, popularityDto);
    }

    @Test
    public void testGetTopActiveUsers_negativeTopN() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.getTopPopularUsers(-1)
        );
        assertEquals(NUMBER_OF_TOP_USERS_MUST_BE_POSITIVE, exception.getMessage());
    }

    @Test
    public void testGetTopActiveUsers_returnFromRedis() {
        userPopularityService.getTopPopularUsers(maxCachedLeaderboardSize - 1);
        verify(userPopularityRedisService, times(1))
                .getTopPopularUsers(maxCachedLeaderboardSize - 1);
        verify(userPopularityRepository, times(0)).getTopPopular(any());
    }

    @Test
    public void testGetTopActiveUsers_returnFromDB() {
        userPopularityService.getTopPopularUsers(maxCachedLeaderboardSize + 1);
        verify(userPopularityRedisService, times(0)).
                getTopPopularUsers(anyInt());
        verify(userPopularityRepository, times(1))
                .getTopPopular(PageRequest.of(0, maxCachedLeaderboardSize + 1));
    }

    @Test
    public void testGetTopActiveUsers_invalidRange() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.getTopPopularUsers(-1, 1)
        );
        assertEquals(String.format(REQUESTED_INVALID_RANGE_OF_USERS, -1, 1), exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> userPopularityService.getTopPopularUsers(10, 1)
        );
        assertEquals(String.format(REQUESTED_INVALID_RANGE_OF_USERS, 10, 1), exception.getMessage());
    }

    @Test
    public void testGetTopActiveUsers_rangeFromRedis() {
        userPopularityService.getTopPopularUsers(1, maxCachedLeaderboardSize - 1);
        verify(userPopularityRedisService, times(1))
                .getTopPopularUsers(1, maxCachedLeaderboardSize - 1);
        verify(userPopularityRepository, times(0)).getTopPopular(any());
    }

    @Test
    public void testGetTopActiveUsers_rangeFromDB() {
        userPopularityService.getTopPopularUsers(1, maxCachedLeaderboardSize + 1);
        verify(userPopularityRedisService, times(0))
                .getTopPopularUsers(1, maxCachedLeaderboardSize + 1);
        verify(userPopularityRepository, times(1))
                .getTopPopular(PageRequest.of(0, maxCachedLeaderboardSize + 1));
    }
}
