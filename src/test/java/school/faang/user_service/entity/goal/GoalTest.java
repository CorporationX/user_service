package school.faang.user_service.entity.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalTest {
    @Mock
    private User mockUser;

    @Mock
    private Skill mockSkill;

    private Goal goal;

    @BeforeEach
    void setUp() {

        goal = new Goal();
        goal.setUsers(new ArrayList<>());
        goal.setSkillsToAchieve(new ArrayList<>());
    }

    @Test
    void testIsEmptyExecutingUsers_EmptyList() {
        assertTrue(goal.isEmptyExecutingUsers(), "The list of users should be empty");
    }

    @Test
    void testIsEmptyExecutingUsers_NonEmptyList() {
        goal.getUsers().add(mockUser);

        assertFalse(goal.isEmptyExecutingUsers(), "The list of users should not be empty");
    }

    @Test
    void testRemoveExecutingUser_UserExists() {
        goal.getUsers().add(mockUser);

        assertTrue(goal.getUsers().contains(mockUser), "The user should be in the list");

        goal.removeExecutingUser(mockUser);

        assertFalse(goal.getUsers().contains(mockUser), "The user should be removed from the list");
    }

    @Test
    void testRemoveExecutingUser_UserDoesNotExist() {
        goal.removeExecutingUser(mockUser);

        assertTrue(goal.getUsers().isEmpty(), "The list should remain empty");
    }
}