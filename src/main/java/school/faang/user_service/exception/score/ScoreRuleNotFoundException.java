package school.faang.user_service.exception.score;

import jakarta.persistence.EntityNotFoundException;

public class ScoreRuleNotFoundException extends EntityNotFoundException {
    public ScoreRuleNotFoundException(String type) {
        super(String.format("Score rule with type %s not found", type));
    }
}
