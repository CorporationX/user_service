package school.faang.user_service.service.rating;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.rating.TopUserCacheRepository;
import school.faang.user_service.service.rating.initializer.RatingInitializer;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private TopUserCacheRepository topUserCacheRepository;

    @Mock
    private UserRatingTypeService userRatingTypeService;

    @Mock
    private UserService userService;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private List<RatingInitializer> ratingInitializers;

    @InjectMocks
    private RatingService ratingService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ratingService, "topUsersLimit", 1);
    }

    @Test
    void testInitCache() {
        userDto = new UserDto();
        Map<UserDto, Double> usersRatingScores = Map.of(userDto, 10.0);

        // Arrange
        when(userService.findAllUsersWithRatingScores(anyInt())).thenReturn(usersRatingScores);
        when(topUserCacheRepository.getTopUsers(anyInt())).thenReturn(List.of(userDto));

        // Act
        ratingService.initCache();

        // Assert
        verify(userService, times(1)).resetAllRatingScores();
        verify(ratingInitializers, times(1)).forEach(any(Consumer.class));
        verify(topUserCacheRepository, times(1)).saveAll(usersRatingScores);
    }

    @Test
    void testAddScore() {
        // Arrange
        User user = User.builder().id(1L).ratingScore(10.0).build();
        UserRatingType userRatingType = new UserRatingType();
        userRatingType.setCost(5.0);
        Double expectedScore = user.getRatingScore() + userRatingType.getCost();

        when(userRatingTypeService.findByName(RatingType.GOAL_RATING)).thenReturn(userRatingType);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(userService.addUserRatingScore(user.getId(), userRatingType.getCost())).thenReturn(expectedScore);
        when(topUserCacheRepository.save(any(), anyDouble())).thenReturn(true);

        // Act
        ratingService.addScore(RatingType.GOAL_RATING, user.getId());

        // Assert
        verify(userService, times(1)).addUserRatingScore(user.getId(), userRatingType.getCost());
        verify(userMapper, times(1)).toDto(user);
        verify(topUserCacheRepository, times(1)).save(any(), eq(expectedScore));
    }

    @Test
    void testMinusScore() {
        // Arrange
        // Arrange
        User user = User.builder().id(1L).ratingScore(10.0).build();
        UserRatingType userRatingType = new UserRatingType();
        userRatingType.setCost(5.0);
        Double expectedScore = user.getRatingScore() - userRatingType.getCost();

        when(userRatingTypeService.findByName(RatingType.GOAL_RATING)).thenReturn(userRatingType);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(userService.addUserRatingScore(user.getId(), -userRatingType.getCost())).thenReturn(expectedScore);
        when(topUserCacheRepository.save(any(), anyDouble())).thenReturn(true);

        // Act
        ratingService.minusScore(RatingType.GOAL_RATING, user.getId());

        // Assert
        verify(userService, times(1)).addUserRatingScore(user.getId(), -userRatingType.getCost());
        verify(userMapper, times(1)).toDto(user);
        verify(topUserCacheRepository, times(1)).save(any(), eq(expectedScore));
    }

    @Test
    void testGetTopUsers() {
        // Arrange
        List<UserDto> topUsers = List.of(new UserDto());
        when(topUserCacheRepository.getTopUsers(anyInt())).thenReturn(topUsers);

        // Act
        List<UserDto> result = ratingService.getTopUsers(10);

        // Assert
        assertEquals(topUsers, result);
        verify(topUserCacheRepository, times(1)).getTopUsers(10);
    }
}