package school.faang.user_service.service.mentorship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipServiceTest {
    private static final long MENTOR_ID = 1L;
    private static final long MENTEE_ID = 2L;
    private static final long INCORRECT_USER_ID = 3L;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;

    @BeforeEach
    void setUp() {
        when(mentorshipRepository.findById(INCORRECT_USER_ID))
                .thenReturn(Optional.empty());

        User mentor = User.builder()
                .id(MENTOR_ID)
                .mentees(new ArrayList<>(Collections.singletonList(User.builder().id(MENTEE_ID).build())))
                .build();
        when(mentorshipRepository.findById(MENTOR_ID))
                .thenReturn(Optional.of(mentor));

        User mentee = User.builder()
                .id(MENTEE_ID)
                .mentors(new ArrayList<>(Collections.singletonList(User.builder().id(MENTOR_ID).build())))
                .build();
        when(mentorshipRepository.findById(MENTEE_ID))
                .thenReturn(Optional.of(mentee));
    }

    @Test
    void getMentees_shouldMatchMenteesSize() {
        List<UserDto> mentees = mentorshipService.getMentees(MENTOR_ID);
        assertEquals(1, mentees.size());
    }

    @Test
    void getMentees_shouldInvokeFindByIdMethod() {
        mentorshipService.getMentees(MENTOR_ID);
        verify(mentorshipRepository).findById(MENTOR_ID);
    }

    @Test
    void getMentees_shouldThrowException() {
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentees(INCORRECT_USER_ID),
                "Invalid user id");
    }

    @Test
    void getMentors_shouldMatchMenteesSize() {
        List<UserDto> mentees = mentorshipService.getMentors(MENTEE_ID);
        assertEquals(1, mentees.size());
    }

    @Test
    void getMentors_shouldInvokeFindByIdMethod() {
        mentorshipService.getMentors(MENTEE_ID);
        verify(mentorshipRepository).findById(MENTEE_ID);
    }

    @Test
    void getMentors_shouldThrowException() {
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.getMentors(INCORRECT_USER_ID),
                "Invalid user id");
    }

    @Test
    void deleteMentee_shouldInvokeFindByIdMethod() {
        mentorshipService.deleteMentee(MENTOR_ID, MENTEE_ID);
        verify(mentorshipRepository).findById(MENTOR_ID);
    }

    @Test
    void deleteMentee_shouldDeleteMenteeFromMentorMenteesList() {
        User mentor = mentorshipRepository.findById(MENTOR_ID).orElseThrow();
        assertEquals(1, mentor.getMentees().size());
        mentorshipService.deleteMentee(MENTOR_ID, MENTEE_ID);
        assertEquals(0, mentor.getMentees().size());
    }

    @Test
    void deleteMentee_shouldThrowUserNotFoundException() {
        assertThrows(EntityNotFoundException.class,
                () -> mentorshipService.deleteMentee(INCORRECT_USER_ID, MENTEE_ID),
                "Invalid user id");
    }
}