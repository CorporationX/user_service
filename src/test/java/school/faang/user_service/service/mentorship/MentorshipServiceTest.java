package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    MentorshipRepository mentorshipRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    MentorshipService mentorshipService;

    private User mentor;
    private User mentee;
    private Goal goal;

    @BeforeEach
    void setUp() {
        goal = Goal.builder()
                .id(3L)
                .build();
        mentor = User.builder()
                .id(1L)
                .build();
        mentee = User.builder()
                .id(2L)
                .build();
        goal.setMentor(mentor);
        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));
        mentee.setGoals(new ArrayList<>(List.of(goal)));
    }

    @Test
    void deleteMentorForAllHisMentees_MentorDeletedMenteesUpdated_ThenMenteesSavedToDb() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mentee));

        mentorshipService.deleteMentorForAllHisMentees(mentor.getId(), List.of(mentee));

        assertAll(
                () -> verify(mentorshipRepository, times(1)).saveAll(List.of(mentee)),
                () -> assertEquals(Collections.emptyList(), mentee.getMentors()),
                () -> assertEquals(mentee.getGoals().get(0).getMentor(), mentee)
        );
    }
}
