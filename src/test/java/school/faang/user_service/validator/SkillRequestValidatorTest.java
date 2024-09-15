package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.skill.SkillRequestNotValidException;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.SkillRequestValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillRequestValidatorTest {

    @InjectMocks
    private SkillRequestValidator skillRequestValidator;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    private List<SkillRequest> skills;
    private static final long SKILL_REQUEST_ID_ONE = 1L;

    @BeforeEach
    void setUp() {
        skills = List.of(SkillRequest.builder()
                .id(SKILL_REQUEST_ID_ONE)
                .build());
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("When Optional is present then no exception thrown")
        public void whenSkillRequestExistsThenDoNotThrowException() {
            when(skillRequestRepository.findById(skills.get(0).getId()))
                    .thenReturn(Optional.of(SkillRequest.builder().build()));

            assertDoesNotThrow(() -> skillRequestValidator.validateSkillsExist(skills));
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("When Optional is null then exception thrown")
        public void whenSkillRequestDoesNotExistsThenThrowException() {
            when(skillRequestRepository.findById(skills.get(0).getId())).thenReturn(Optional.empty());

            assertThrows(SkillRequestNotValidException.class, () -> skillRequestValidator.validateSkillsExist(skills));
        }
    }
}
