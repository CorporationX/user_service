package school.faang.user_service.service.score;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.entity.Role;
import school.faang.user_service.entity.score.ScoreRule;
import school.faang.user_service.exception.score.ScoreRuleNotFoundException;
import school.faang.user_service.model.user.RoleThesaurus;
import school.faang.user_service.repository.score.ScoreRuleRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ScoreRuleServiceTest {

    @Mock
    private ScoreRuleRepository scoreRuleRepository;

    @InjectMocks
    private ScoreRuleService scoreRuleService;

    @Test
    void getScoreByTypeOrThrow_shouldReturnScore() {
        ScoreActionType type = ScoreActionType.COMPLETE_EVENT;
        ScoreRule rule = new ScoreRule();
        rule.setType(type.name());
        rule.setScore(50);

        Mockito.when(scoreRuleRepository.findByType(type)).thenReturn(Optional.of(rule));

        int score = scoreRuleService.getScoreByTypeOrThrow(type);

        assertThat(score).isEqualTo(50);
    }

    @Test
    void getScoreByTypeOrThrow_shouldThrowIfNotFound() {
        ScoreActionType type = ScoreActionType.COMPLETE_EVENT;

        Mockito.when(scoreRuleRepository.findByType(type))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> scoreRuleService.getScoreByTypeOrThrow(type))
                .isInstanceOf(ScoreRuleNotFoundException.class)
                .hasMessageContaining(type.name());
    }

    @Test
    void getScoreByRoleOrThrow_shouldReturnScore() {
        ScoreActionType type = ScoreActionType.COMPLETE_EVENT;
        String roleName = "OWNER";

        Role role = new Role();
        role.setName(RoleThesaurus.OWNER);

        ScoreRule rule = new ScoreRule();
        rule.setType(type.name());
        rule.setRole(role);
        rule.setScore(100);

        Mockito.when(scoreRuleRepository.findByTypeAndRole_Name(type, roleName))
                .thenReturn(Optional.of(rule));

        int score = scoreRuleService.getScoreByRoleOrThrow(type, roleName);

        assertThat(score).isEqualTo(100);
    }

    @Test
    void getScoreByRoleOrThrow_shouldThrowIfNotFound() {
        ScoreActionType type = ScoreActionType.COMPLETE_EVENT;
        String roleName = "ATTENDEE";

        Mockito.when(scoreRuleRepository.findByTypeAndRole_Name(type, roleName))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> scoreRuleService.getScoreByRoleOrThrow(type, roleName))
                .isInstanceOf(ScoreRuleNotFoundException.class)
                .hasMessageContaining(type.name());
    }
}
