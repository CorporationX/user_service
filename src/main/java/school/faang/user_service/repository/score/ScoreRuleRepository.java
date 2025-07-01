package school.faang.user_service.repository.score;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.entity.score.ScoreRule;

import java.util.Optional;

@Repository
public interface ScoreRuleRepository extends JpaRepository<ScoreRule, Long> {

    Optional<ScoreRule> findByType(ScoreActionType type);

    Optional<ScoreRule> findByTypeAndRole_Name(ScoreActionType type, String role);
}
