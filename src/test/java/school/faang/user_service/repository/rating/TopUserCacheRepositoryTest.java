package school.faang.user_service.repository.rating;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import school.faang.user_service.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TopUserCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, UserDto> redisTemplate;

    @Mock
    private ZSetOperations<String, UserDto> zSetOperations;

    @InjectMocks
    private TopUserCacheRepository topUserCacheRepository;

    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    public void testSave_UserAddedSuccessfully() {
        // Arrange
        UserDto userDto = new UserDto(); // Assume UserDto has a default constructor
        Double score = 10.0;
        when(zSetOperations.add("topusers", userDto, score)).thenReturn(true);

        // Act
        boolean result = topUserCacheRepository.save(userDto, score);

        // Assert
        assertTrue(result);
        verify(zSetOperations).add("topusers", userDto, score);
    }

    @Test
    public void testSave_UserNotAdded() {
        // Arrange
        UserDto userDto = new UserDto();
        Double score = 10.0;
        when(zSetOperations.add("topusers", userDto, score)).thenReturn(false);

        // Act
        boolean result = topUserCacheRepository.save(userDto, score);

        // Assert
        assertFalse(result);
        verify(zSetOperations).add("topusers", userDto, score);
    }

    @Test
    public void testSaveAll() {
        // Arrange
        when(zSetOperations.add(anyString(), any(UserDto.class), anyDouble())).thenReturn(true);
        UserDto user1 = UserDto.builder().id(1L).build();
        UserDto user2 = UserDto.builder().id(2L).build();
        Map<UserDto, Double> usersRatingScores = new HashMap<>();
        usersRatingScores.put(user1, 10.0);
        usersRatingScores.put(user2, 20.0);

        // Act
        topUserCacheRepository.saveAll(usersRatingScores);

        // Assert
        verify(zSetOperations, times(1)).add("topusers", user1, 10.0);
        verify(zSetOperations, times(1)).add("topusers", user2, 20.0);
    }

    @Test
    public void testGetTopUsers() {
        // Arrange
        UserDto user1 = new UserDto();
        UserDto user2 = new UserDto();
        ZSetOperations.TypedTuple<UserDto> tuple1 = mock(ZSetOperations.TypedTuple.class);
        ZSetOperations.TypedTuple<UserDto> tuple2 = mock(ZSetOperations.TypedTuple.class);

        when(tuple1.getValue()).thenReturn(user1);
        when(tuple2.getValue()).thenReturn(user2);

        Set<ZSetOperations.TypedTuple<UserDto>> tuples = Set.of(tuple1, tuple2);
        when(zSetOperations.reverseRangeWithScores("topusers", 0, 1)).thenReturn(tuples);

        // Act
        List<UserDto> result = topUserCacheRepository.getTopUsers(2);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        verify(zSetOperations).reverseRangeWithScores("topusers", 0, 1);
    }
}