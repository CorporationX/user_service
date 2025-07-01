package school.faang.user_service.service.score;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.score.UserScore;
import school.faang.user_service.repository.score.UserScoreRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserScoreServiceTest {

    @Mock
    private UserScoreRepository userScoreRepository;

    @InjectMocks
    private UserScoreService userScoreService;

    private long userId;
    private UserScore userScore;
    private int scoreDelta;

    private static final int INITIAL_USER_SCORE = 100;

    @BeforeEach
    public void setUp() {
        userId = 1L;

        userScore = new UserScore();
        userScore.setUserId(userId);
        userScore.setScore(INITIAL_USER_SCORE);

        scoreDelta = 10;
    }

    @Test
    public void testUpdateScoreWithExistingUser_returnCorrect() {
        int expected = userScore.getScore() + 2 * scoreDelta;
        Mockito.when(userScoreRepository.findForUpdate(userId)).thenReturn(Optional.ofNullable(userScore));

        userScoreService.incrementScore(userId, scoreDelta);
        int result = userScoreService.incrementScore(userId, scoreDelta);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testUpdateScoreWithNotExistingUser_returnCorrect() {
        int expected = userScore.getScore() + scoreDelta;
        Mockito.when(userScoreRepository.findForUpdate(userId)).thenReturn(Optional.ofNullable(userScore));

        int result = userScoreService.incrementScore(userId, scoreDelta);

        Assertions.assertEquals(expected, result);
    }
}


