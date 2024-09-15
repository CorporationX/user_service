package school.faang.user_service.service.mentorshipService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    User user;
    User mentee;
    User mentee2;

    Goal goal;

    @BeforeEach
    void setUp() {
        user = spy(new User());
        user.setId(1L);

        User mentor2 = new User();
        mentor2.setId(4L);

        mentee = spy(new User());
        mentee.setId(2L);
        mentee.setMentors(new ArrayList<>(List.of(user)));


        mentee2 = spy(new User());
        mentee2.setId(3L);


        goal = spy(new Goal());
        goal.setId(1L);
        goal.setUsers(List.of(mentee, mentee2));

        mentee.setGoals(List.of(goal));
        mentee2.setGoals(List.of(goal));

        user.setMentees(List.of(mentee));

        reset(user);
        reset(mentee);
    }

    @Test
    @DisplayName("Deactivate mentorship")
    void testDeactivateMentorship() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        mentorshipService.deactivateMentorship(user.getId());

        verify(goal).setMentor(mentee);
        verify(mentee).setMentors(List.of());

    }

    @Test
    @DisplayName("User not found")
    void testUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> mentorshipService.deactivateMentorship(user.getId()));
    }

}