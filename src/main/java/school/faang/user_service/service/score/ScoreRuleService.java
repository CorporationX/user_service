package school.faang.user_service.service.score;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.entity.score.ScoreRule;
import school.faang.user_service.exception.score.ScoreRuleNotFoundException;
import school.faang.user_service.repository.score.ScoreRuleRepository;

@Service
@RequiredArgsConstructor
public class ScoreRuleService {

    private final ScoreRuleRepository scoreRuleRepository;

    public int getScoreByTypeOrThrow(ScoreActionType type) {
        ScoreRule scoreRule = scoreRuleRepository.findByType(type)
                .orElseThrow(() -> new ScoreRuleNotFoundException(type.name()));
        return scoreRule.getScore();
    }

    public int getScoreByRoleOrThrow(ScoreActionType type, String role) {
        ScoreRule scoreRule = scoreRuleRepository.findByTypeAndRole_Name(type, role)
                .orElseThrow(() -> new ScoreRuleNotFoundException(type.name()));
        return scoreRule.getScore();
    }
}
