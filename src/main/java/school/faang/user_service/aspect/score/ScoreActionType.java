package school.faang.user_service.aspect.score;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScoreActionType {
    COMPLETE_GOAL,
    COMPLETE_EVENT
}
