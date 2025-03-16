package school.faang.user_service.service.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.leaderboard.UserActionDto;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.leaderboard.UserActivity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserActivityRedisServiceTest {
    @Mock
    private HashOperations<String, String, String> hashOps;

    @Mock
    private ZSetOperations<String, String> zSetOps;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private UserActivityRedisService userActivityRedisService;

    private final Country country = Country.builder().title("country").build();
    private final User user = User.builder().id(1L).username("name").country(country).build();
    private final Long id = 1L;
    private final Long userId = 2L;
    private final String username = "name";
    private final int maxCachedLeaderboardSize = 100;
    private final UserActivity userActivity = new UserActivity(id, user, LocalDateTime.now(), 100);
    private final UserActivityRequestDto activityDto = new UserActivityRequestDto(
            id, userId, username, country.getTitle(), UserActionDto.LIKED);

    private static final String LEADERBOARD_KEY = "activityLeaderboard";
    private static final String USER_HASH_PREFIX = "activityUser:";
    private static final String USERNAME_HASH_KEY = "activityUsername:";
    private static final String COUNTRY_HASH_KEY = "activityCountry:";
    private static final String RATING_HASH_KEY = "activityRating:";
    private static final String ID_HASH_KEY = "activityId:";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userActivityRedisService, "maxCachedLeaderboardSize", maxCachedLeaderboardSize);
    }

    @Test
    public void testRecordUserActionInRedis_noEviction() {
        when(zSetOps.size(LEADERBOARD_KEY)).thenReturn((long) maxCachedLeaderboardSize - 1);

        userActivityRedisService.recordUserAction(userActivity, activityDto);

        String userIdStr = String.valueOf(activityDto.userId());
        String userKey = USER_HASH_PREFIX + userIdStr;

        verify(hashOps, times(1))
                .put(userKey, ID_HASH_KEY, String.valueOf(activityDto.id()));
        verify(hashOps, times(1))
                .put(userKey, USERNAME_HASH_KEY, activityDto.username());
        verify(hashOps, times(1))
                .put(userKey, COUNTRY_HASH_KEY, activityDto.country());
        verify(hashOps, times(1))
                .put(userKey, RATING_HASH_KEY, String.valueOf(userActivity.getRating()));
        verify(zSetOps, times(1))
                .add(LEADERBOARD_KEY, userIdStr, userActivity.getRating());
    }

    @Test
    public void testRecordUserActionInRedis_withEviction() {
        when(zSetOps.size(LEADERBOARD_KEY)).thenReturn((long) maxCachedLeaderboardSize + 1);
        Set<String> removedUserSet = new HashSet<>();
        removedUserSet.add("1");
        when(zSetOps.range(LEADERBOARD_KEY, 0, 0)).thenReturn(removedUserSet);
        userActivityRedisService.recordUserAction(userActivity, activityDto);

        String userIdStr = String.valueOf(activityDto.userId());
        String userKey = USER_HASH_PREFIX + userIdStr;

        verify(hashOps, times(1))
                .put(userKey, ID_HASH_KEY, String.valueOf(activityDto.id()));
        verify(hashOps, times(1))
                .put(userKey, USERNAME_HASH_KEY, activityDto.username());
        verify(hashOps, times(1))
                .put(userKey, COUNTRY_HASH_KEY, activityDto.country());
        verify(hashOps, times(1))
                .put(userKey, RATING_HASH_KEY, String.valueOf(userActivity.getRating()));
        verify(hashOps, times(1))
                .put(userKey, RATING_HASH_KEY, String.valueOf(userActivity.getRating()));
        verify(zSetOps, times(1))
                .add(LEADERBOARD_KEY, userIdStr, userActivity.getRating());
        verify(zSetOps, times(1))
                .remove(LEADERBOARD_KEY, removedUserSet.iterator().next());
        verify(redisTemplate, times(1))
                .delete(USER_HASH_PREFIX + removedUserSet.iterator().next());
    }
}
