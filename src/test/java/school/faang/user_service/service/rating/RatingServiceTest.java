package school.faang.user_service.service.rating;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.repository.rating.TopUserRepository;
import school.faang.user_service.repository.rating.UserRatingRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.rating.user_rating.UserRank;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    UserRatingRepository userRatingRepository = mock(UserRatingRepository.class);
    RatingTypeService ratingTypeService = mock(RatingTypeService.class);
    UserService userService = mock(UserService.class);
    TopUserRepository topUserRepository = mock(TopUserRepository.class);
    UserRank userRank = mock(UserRank.class);
    List<UserRank> userRanks = List.of(userRank);
    RatingService ratingService = new RatingService(userRatingRepository, ratingTypeService, userService,
            topUserRepository, userRanks);

    @Test
    void addRating() {
        UserRating sourceRating = UserRating.builder()
                .user(User.builder()
                        .id(1L)
                        .build())
                .build();
        UserRating targetRating = UserRating.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .build())
                .build();
        when(userRatingRepository.save(sourceRating)).thenReturn(targetRating);
        UserRating actual = ratingService.addRating(sourceRating);

        assertEquals(targetRating, actual);
        verify(userRatingRepository, times(1)).save(sourceRating);
    }

    @Test
    void deleteRating() {
        UserRating userRating = UserRating.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .build())
                .build();
        doNothing().when(userRatingRepository).delete(userRating);
        assertDoesNotThrow(() -> ratingService.deleteRating(userRating));
        verify(userRatingRepository, times(1)).delete(userRating);
    }

    @Test
    void addScore() {
        String ratingTypeName = "Skill rating";
        Long userId = 1L;
        UserRatingType userRatingType = UserRatingType.builder()
                .id(1L)
                .name("Skill rating")
                .cost(5)
                .build();
        UserRating userRating = UserRating.builder()
                .id(1L)
                .user(User.builder()
                        .id(userId)
                        .build())
                .score(10)
                .type(userRatingType)
                .build();

        when(userRatingRepository.findByUserIdAndTypeNameIs(userId, ratingTypeName)).thenReturn(userRating);
        when(ratingTypeService.findByName(ratingTypeName)).thenReturn(userRatingType);
        when(topUserRepository.getTopUserScore(userId)).thenReturn(20.0);
        when(topUserRepository.save(1L, 25.0)).thenReturn(true);
        when(userRatingRepository.save(userRating)).thenReturn(userRating);

        assertDoesNotThrow(() -> ratingService.addScore(userId, ratingTypeName));
        assertEquals(15, userRating.getScore());
        verify(userRatingRepository, times(1)).findByUserIdAndTypeNameIs(userId, ratingTypeName);
        verify(ratingTypeService, times(1)).findByName(ratingTypeName);
        verify(topUserRepository, times(1)).getTopUserScore(userId);
        verify(topUserRepository, times(1)).save(1L, 25.0);
        verify(userRatingRepository, times(1)).save(userRating);
    }

    @Test
    void addScoreRatingNotFound() {
        String ratingTypeName = "Skill rating";
        Long userId = 1L;
        UserRatingType userRatingType = UserRatingType.builder()
                .id(1L)
                .name("Skill rating")
                .cost(5)
                .build();

        UserRating resultUserRating = UserRating.builder()
                .id(1L)
                .user(User.builder()
                        .id(userId)
                        .build())
                .score(5)
                .type(userRatingType)
                .build();

        when(userRatingRepository.findByUserIdAndTypeNameIs(userId, ratingTypeName)).thenReturn(null);
        when(ratingTypeService.findByName(ratingTypeName)).thenReturn(userRatingType);
        when(topUserRepository.getTopUserScore(userId)).thenReturn(20.0);
        when(topUserRepository.save(1L, 25.0)).thenReturn(true);
        when(userRatingRepository.save(any())).thenReturn(resultUserRating);

        assertDoesNotThrow(() -> ratingService.addScore(userId, ratingTypeName));
        assertEquals(5, resultUserRating.getScore());
        verify(userRatingRepository, times(1)).findByUserIdAndTypeNameIs(userId, ratingTypeName);
        verify(ratingTypeService, times(1)).findByName(ratingTypeName);
        verify(topUserRepository, times(1)).getTopUserScore(userId);
        verify(topUserRepository, times(1)).save(1L, 25.0);
        verify(userRatingRepository, times(1)).save(any());
    }

    @Test
    void minusScore() {
        String ratingTypeName = "Skill rating";
        Long userId = 1L;
        UserRatingType userRatingType = UserRatingType.builder()
                .id(1L)
                .name("Skill rating")
                .cost(5)
                .build();
        UserRating userRating = UserRating.builder()
                .id(1L)
                .user(User.builder()
                        .id(userId)
                        .build())
                .score(10)
                .type(userRatingType)
                .build();

        when(userRatingRepository.findByUserIdAndTypeNameIs(userId, ratingTypeName)).thenReturn(userRating);
        when(ratingTypeService.findByName(ratingTypeName)).thenReturn(userRatingType);
        when(topUserRepository.getTopUserScore(userId)).thenReturn(20.0);
        when(topUserRepository.save(1L, 15.0)).thenReturn(true);
        when(userRatingRepository.save(userRating)).thenReturn(userRating);

        assertDoesNotThrow(() -> ratingService.minusScore(userId, ratingTypeName));
        assertEquals(5, userRating.getScore());
        verify(userRatingRepository, times(1)).findByUserIdAndTypeNameIs(userId, ratingTypeName);
        verify(ratingTypeService, times(1)).findByName(ratingTypeName);
        verify(topUserRepository, times(1)).getTopUserScore(userId);
        verify(topUserRepository, times(1)).save(1L, 15.0);
        verify(userRatingRepository, times(1)).save(userRating);
    }

    @Test
    void minusScoreRatingNotFound() {
        String ratingTypeName = "Skill rating";
        Long userId = 1L;
        UserRatingType userRatingType = UserRatingType.builder()
                .id(1L)
                .name("Skill rating")
                .cost(5)
                .build();

        UserRating resultUserRating = UserRating.builder()
                .id(1L)
                .user(User.builder()
                        .id(userId)
                        .build())
                .score(0)
                .type(userRatingType)
                .build();

        when(userRatingRepository.findByUserIdAndTypeNameIs(userId, ratingTypeName)).thenReturn(null);
        when(ratingTypeService.findByName(ratingTypeName)).thenReturn(userRatingType);
        when(topUserRepository.getTopUserScore(userId)).thenReturn(20.0);
        when(topUserRepository.save(1L, 15.0)).thenReturn(true);
        when(userRatingRepository.save(any())).thenReturn(resultUserRating);

        assertDoesNotThrow(() -> ratingService.minusScore(userId, ratingTypeName));
        assertEquals(0, resultUserRating.getScore());
        verify(userRatingRepository, times(1)).findByUserIdAndTypeNameIs(userId, ratingTypeName);
        verify(ratingTypeService, times(1)).findByName(ratingTypeName);
        verify(topUserRepository, times(1)).getTopUserScore(userId);
        verify(topUserRepository, times(1)).save(1L, 15.0);
        verify(userRatingRepository, times(1)).save(any());
    }

    @Test
    void getTopUsers() {
        Map<Long, Double> topUsers = Map.of(
                1L, 10.0,
                2L, 19.0,
                3L, 7.0
        );
        when(topUserRepository.getTopUsersWithScores()).thenReturn(topUsers);
        List<Long> excepted = List.of(2L, 1L, 3L);
        List<Long> result = ratingService.getTopUsers();
        assertEquals(excepted, result);
        verify(topUserRepository, times(1)).getTopUsersWithScores();
    }

    @Test
    void getTopUsersRepositoryIsEmpty() {
        Map<Long, Double> topUsers = Map.of(
                1L, 10.0,
                2L, 19.0,
                3L, 7.0
        );
        when(topUserRepository.getTopUsersWithScores()).thenAnswer(
                new Answer() {
                    private int count = 0;

                    public Map<Long, Double> answer(InvocationOnMock invocation) {
                        if (count <= 0) {
                            count++;
                            return Map.of();
                        }
                        return topUsers;
                    }
                }
        );
        List<Long> excepted = List.of(2L, 1L, 3L);
        List<Long> result = ratingService.getTopUsers();
        assertEquals(excepted, result);
        verify(topUserRepository, times(2)).getTopUsersWithScores();
    }

    @Test
    void getTopUsersRepositoryIsNull() {
        Map<Long, Double> topUsers = Map.of(
                1L, 10.0,
                2L, 19.0,
                3L, 7.0
        );
        when(topUserRepository.getTopUsersWithScores()).thenAnswer(
                new Answer() {
                    private int count = 0;

                    public Map<Long, Double> answer(InvocationOnMock invocation) {
                        if (count <= 0) {
                            count++;
                            return null;
                        }
                        return topUsers;
                    }
                }
        );
        List<Long> excepted = List.of(2L, 1L, 3L);
        List<Long> result = ratingService.getTopUsers();
        assertEquals(excepted, result);
        verify(topUserRepository, times(2)).getTopUsersWithScores();
    }

}