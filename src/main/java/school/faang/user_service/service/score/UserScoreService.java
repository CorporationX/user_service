package school.faang.user_service.service.score;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.score.UserScore;
import school.faang.user_service.repository.score.UserScoreRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserScoreService {

    private final UserScoreRepository userScoreRepository;

    @Transactional(readOnly = true)
    public List<UserScore> getUserScores() {
        return userScoreRepository.findAll();
    }

    @Transactional
    public int incrementScore(long userId, int scoreDelta) {
        Optional<UserScore> optional = userScoreRepository.findForUpdate(userId);

        if (optional.isPresent()) {
            UserScore userScore = optional.get();
            userScore.setScore(userScore.getScore() + scoreDelta);
            userScoreRepository.save(userScore);
            return userScore.getScore();
        } else {
            UserScore newScore = new UserScore();
            newScore.setUserId(userId);
            newScore.setScore(scoreDelta);
            userScoreRepository.save(newScore);
            return scoreDelta;
        }
    }
}
