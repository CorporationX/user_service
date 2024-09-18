package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long SKILL_ID = 1L;
    private static final String SKILL_TITLE = "Test Skill";

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    private Skill skill;

    @BeforeEach
    public void setUp() {
        skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle(SKILL_TITLE);
    }

    @Nested
    @DisplayName("Skill Existence Tests")
    class SkillExistenceTests {

        @Test
        @DisplayName("Returns true when all skills exist")
        void whenAllSkillsExistThenReturnTrue() {
            when(skillRepository.existsById(anyLong())).thenReturn(true);

            boolean result = skillService.existsById(List.of(SKILL_ID));

            verify(skillRepository).existsById(anyLong());
            assertTrue(result, "All skills should exist by id");
        }

        @Test
        @DisplayName("Returns false when not all skills exist")
        void whenNotAllSkillsExistThenReturnFalse() {
            when(skillRepository.existsById(anyLong())).thenReturn(false);

            boolean result = skillService.existsById(List.of(SKILL_ID));

            verify(skillRepository).existsById(anyLong());
            assertFalse(result, "Not all skills exist by id");
        }
    }

    @Nested
    @DisplayName("Skill Creation Tests")
    class SkillCreationTests {

        @Test
        @DisplayName("Assigns skills to user when creating skills")
        void whenCreatingSkillsThenAssignThemToUser() {
            when(skillRepository.existsById(SKILL_ID)).thenReturn(true);
            when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.ofNullable(skill));

            skillService.addSkillsToUserId(List.of(SKILL_ID), USER_ID);

            verify(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);
        }
    }

    @Nested
    @DisplayName("Adding Skills to Users Tests")
    class AddSkillsToUsersTests {

        @Test
        @DisplayName("Adds skills to users when goal ID is provided")
        void whenGoalIdProvidedThenAddSkillsToUsers() {
            when(skillRepository.existsById(SKILL_ID)).thenReturn(true);
            when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.ofNullable(skill));

            skillService.addSkillsToUserId(List.of(SKILL_ID), USER_ID);

            verify(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);
        }
    }
}
