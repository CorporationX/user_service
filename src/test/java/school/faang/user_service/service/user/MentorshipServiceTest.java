package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User otherMentor;
    private List<User> mentees;

    @BeforeEach
    public void setUp() {
        mentor = new User();
        mentor.setId(1L);

        otherMentor = new User();
        otherMentor.setId(2L);

        List<Goal> goals = new ArrayList<>(List.of(
                Goal.builder()
                        .mentor(mentor)
                        .build(),
                Goal.builder()
                        .mentor(otherMentor)
                        .build()
        ));

        mentor.setMentees(new ArrayList<>(List.of(
                User.builder()
                        .id(2L)
                        .mentors(new ArrayList<>(List.of(mentor)))
                        .goals(goals)
                        .build(),
                User.builder()
                        .id(3L)
                        .mentors(new ArrayList<>(List.of(mentor, otherMentor)))
                        .goals(new ArrayList<>(List.of(goals.get(1))))
                        .build()
        )));
    }

    @Test
    public void deleteMentorFromMenteesSuccess() {
        List<User> mentees = mentor.getMentees();

        mentorshipService.deleteMentorFromMentees(mentor.getId(), mentees);

        verify(mentorshipRepository).saveAll(mentor.getMentees());

        User mentee1 = mentees.get(0);
        User mentee2 = mentees.get(1);

        assertEquals(mentee1.getGoals().get(0).getMentor(), mentee1);
        assertEquals(mentee1.getGoals().get(1).getMentor(), otherMentor);
        assertEquals(mentee1.getMentors().size(), 0);

        assertEquals(mentee2.getGoals().get(0).getMentor(), otherMentor);
        assertEquals(mentee2.getMentors().size(), 1);
    }
}