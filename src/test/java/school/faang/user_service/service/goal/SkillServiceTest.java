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
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long GOAL_ID = 1L;
    private static final Long SKILL_ID = 1L;
    private static final String SKILL_TITLE = "Test Skill";

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    private User user;
    private List<Skill> skills;
    private List<User> users;

    @BeforeEach
    public void setUp() {
        Skill skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle(SKILL_TITLE);

        user = new User();
        user.setId(USER_ID);
        user.setSkills(new ArrayList<>(List.of(skill)));

        skills = new ArrayList<>(List.of(skill));
        users = new ArrayList<>(List.of(user));
    }

    @Nested
    @DisplayName("Skill Existence Tests")
    class SkillExistenceTests {

        @Test
        @DisplayName("Returns true when all skills exist")
        void whenAllSkillsExistThenReturnTrue() {
            when(skillRepository.existsByTitle(SKILL_TITLE)).thenReturn(true);

            boolean result = skillService.existsByTitle(skills);

            verify(skillRepository).existsByTitle(SKILL_TITLE);
            assertTrue(result, "All skills should exist by title");
        }

        @Test
        @DisplayName("Returns false when not all skills exist")
        void whenNotAllSkillsExistThenReturnFalse() {
            when(skillRepository.existsByTitle(SKILL_TITLE)).thenReturn(false);

            boolean result = skillService.existsByTitle(skills);

            verify(skillRepository).existsByTitle(SKILL_TITLE);
            assertFalse(result, "Not all skills exist by title");
        }
    }

    @Nested
    @DisplayName("Skill Creation Tests")
    class SkillCreationTests {

        @Test
        @DisplayName("Assigns skills to user when creating skills")
        void whenCreatingSkillsThenAssignThemToUser() {
            skillService.create(skills, USER_ID);

            verify(skillRepository).assignSkillToUser(SKILL_ID, USER_ID);
        }
    }

    @Nested
    @DisplayName("Adding Skills to Users Tests")
    class AddSkillsToUsersTests {

        @Test
        @DisplayName("Adds skills to users when goal ID is provided")
        void whenGoalIdProvidedThenAddSkillsToUsers() {
            when(skillRepository.findSkillsByGoalId(GOAL_ID)).thenReturn(skills);

            skillService.addSkillToUsers(users, GOAL_ID);

            verify(skillRepository).findSkillsByGoalId(GOAL_ID);
            assertTrue(user.getSkills().containsAll(skills), "User should have the new skills added");
        }
    }
}
