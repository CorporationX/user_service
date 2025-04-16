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
import school.faang.user_service.dto.leaderboard.UserActionDto;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.mapper.leaderboard.UserActivityMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation.COUNTRY_CANT_BE_NULL_OR_BLANK;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation.NUMBER_OF_TOP_USERS_MUST_BE_POSITIVE;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation.REQUESTED_INVALID_RANGE_OF_USERS;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation.USERNAME_CANT_BE_NULL_OR_BLANK;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation.USER_ACTION_DTO_CANT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation.USER_ACTIVITY_REQUEST_DTO_CANT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation.USER_ACTIVITY_REQUEST_DTO_ID_CANT_BE_NULL;
import static school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation.USER_ID_CANT_BE_NULL;


@ExtendWith(MockitoExtension.class)
public class UserActivityServiceTest {
    @InjectMocks
    private UserActivityService userActivityService;

    @Mock
    private UserActivityRepository userActivityRepository;

    @Mock
    private UserActivityRedisService userActivityRedisService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserActivityMapper userActivityMapper;

    private final UserActionDto userActionDto = UserActionDto.COMMENTED;
    private final Country country = Country.builder().title("country").build();
    private final User user = User.builder().id(1L).username("name").country(country).build();
    private final Long id = 1L;
    private final Long userId = 2L;
    private final String username = "name";
    private final UserActivityRequestDto activityDto = new UserActivityRequestDto(
            id, userId, username, country.getTitle(), userActionDto);
    private final UserActivity userActivity = new UserActivity(id, user, LocalDateTime.now(), 100);
    private final int maxCachedLeaderboardSize = 100;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userActivityService, "maxCachedLeaderboardSize", maxCachedLeaderboardSize);
    }

    @Test
    public void testRecordUserAction_nullUserActivityRequestDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.recordUserAction(null, userActionDto)
        );
        assertEquals(USER_ACTIVITY_REQUEST_DTO_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullIdInUserActivityRequestDto() {
        UserActivityRequestDto dto = new UserActivityRequestDto(null, userId, username,
                country.getTitle(), userActionDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.recordUserAction(dto, userActionDto)
        );
        assertEquals(USER_ACTIVITY_REQUEST_DTO_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullUserIdInUserActivityRequestDto() {
        UserActivityRequestDto dto = new UserActivityRequestDto(id, null, username,
                country.getTitle(), userActionDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.recordUserAction(dto, userActionDto)
        );
        assertEquals(USER_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullCountryInUserActivityRequestDto() {
        UserActivityRequestDto dto = new UserActivityRequestDto(id, userId, username,
                null, userActionDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.recordUserAction(dto, userActionDto)
        );
        assertEquals(COUNTRY_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullUsernameInUserActivityRequestDto() {
        UserActivityRequestDto dto = new UserActivityRequestDto(id, userId, null,
                country.getTitle(), userActionDto);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.recordUserAction(dto, userActionDto)
        );
        assertEquals(USERNAME_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_nullUserActionDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.recordUserAction(activityDto, null)
        );
        assertEquals(USER_ACTION_DTO_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testRecordUserAction_NewActivity() {
        when(userActivityRepository.findById(userActivity.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(activityDto.userId())).thenReturn(Optional.of(user));
        userActivityService.recordUserAction(activityDto, userActionDto);

        ArgumentCaptor<UserActivity> captor = ArgumentCaptor.forClass(UserActivity.class);
        verify(userActivityRepository, times(1)).save(captor.capture());
        UserActivity savedActivity = captor.getValue();

        assertEquals(userActionDto.getRating(), savedActivity.getRating());
        assertEquals(user, savedActivity.getUser());
        verify(userActivityRedisService, times(1)).recordUserAction(savedActivity, activityDto);
    }

    @Test
    public void testRecordUserAction_ExistingActivity() {
        long ratingBefore = userActivity.getRating();
        when(userActivityRepository.findById(userActivity.getId())).thenReturn(Optional.of(userActivity));

        userActivityService.recordUserAction(activityDto, userActionDto);

        assertEquals(ratingBefore + userActionDto.getRating(), userActivity.getRating());
        verify(userActivityRedisService, times(1)).recordUserAction(userActivity, activityDto);
    }

    @Test
    public void testGetTopActiveUsers_negativeTopN() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.getTopActiveUsers(-1)
        );
        assertEquals(NUMBER_OF_TOP_USERS_MUST_BE_POSITIVE, exception.getMessage());
    }

    @Test
    public void testGetTopActiveUsers_returnFromRedis() {
        userActivityService.getTopActiveUsers(maxCachedLeaderboardSize - 1);
        verify(userActivityRedisService, times(1))
                .getTopActiveUsers(maxCachedLeaderboardSize - 1);
        verify(userActivityRepository, times(0)).getTopActive(any());
    }

    @Test
    public void testGetTopActiveUsers_returnFromDB() {
        userActivityService.getTopActiveUsers(maxCachedLeaderboardSize + 1);
        verify(userActivityRedisService, times(0)).
                getTopActiveUsers(anyInt());
        verify(userActivityRepository, times(1))
                .getTopActive(PageRequest.of(0, maxCachedLeaderboardSize + 1));
    }

    @Test
    public void testGetTopActiveUsers_invalidRange() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.getTopActiveUsers(-1, 1)
        );
        assertEquals(String.format(REQUESTED_INVALID_RANGE_OF_USERS, -1, 1), exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> userActivityService.getTopActiveUsers(10, 1)
        );
        assertEquals(String.format(REQUESTED_INVALID_RANGE_OF_USERS, 10, 1), exception.getMessage());
    }

    @Test
    public void testGetTopActiveUsers_rangeFromRedis() {
        userActivityService.getTopActiveUsers(1, maxCachedLeaderboardSize - 1);
        verify(userActivityRedisService, times(1))
                .getTopActiveUsers(1, maxCachedLeaderboardSize - 1);
        verify(userActivityRepository, times(0)).getTopActive(any());
    }

    @Test
    public void testGetTopActiveUsers_rangeFromDB() {
        userActivityService.getTopActiveUsers(1, maxCachedLeaderboardSize + 1);
        verify(userActivityRedisService, times(0))
                .getTopActiveUsers(1, maxCachedLeaderboardSize + 1);
        verify(userActivityRepository, times(1))
                .getTopActive(PageRequest.of(0, maxCachedLeaderboardSize + 1));
    }
}
