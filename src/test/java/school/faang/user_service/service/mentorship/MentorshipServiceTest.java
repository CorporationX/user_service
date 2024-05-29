package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;

    @BeforeEach
    void beforeEach() {
        mentor = getMentorUser();
        mentee = getMenteeUser();
    }

    @Test
    void testGetMentorsException() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentors(mentor.getId()));
    }

    @Test
    void testGetMentorsList() {
        when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        mentorshipService.getMentors(mentor.getId());

        verify(mentorshipRepository).findById(mentor.getId());
        verify(userMapper).toDtoList(mentorshipRepository.findById(mentor.getId()).get().getMentees());
    }

    @Test
    void testGetMenteesException() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> mentorshipService.getMentees(mentee.getId()));
    }

    @Test
    void testGetMenteesList() {
        when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));
        mentorshipService.getMentees(mentee.getId());

        verify(mentorshipRepository).findById(mentee.getId());
        verify(userMapper).toDtoList(mentorshipRepository.findById(mentee.getId()).get().getMentees());
    }


    private static User getMenteeUser() {
        return User.builder()
                .id(11L)
                .username("Mentee")
                .build();
    }

    private static User getMentorUser() {
        return User.builder()
                .id(12L)
                .username("Mentor")
                .build();
    }
}
