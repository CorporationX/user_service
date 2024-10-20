package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.mentorship.MentorshipDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.impl.mentorship.MentorshipServiceImpl;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MentorshipMapper mentorshipMapper;

    @Mock
    MentorshipValidator validator;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    User user;
    User menteeOrMentor;
    MentorshipDto mentorshipDto;
    private int id;
    private List<User> mentees;

    @BeforeEach
    void setUp() {
        menteeOrMentor = User.builder()
                .id(1L)
                .username("JaneSmith")
                .email("jane@smith.com")
                .phone("0987654321")
                .aboutMe("aboutMe")
                .build();

        user = User.builder()
                .id(2L)
                .username("John")
                .email("john@example.com")
                .phone("123456789")
                .aboutMe("About John")
                .goals(List
                        .of(Goal
                                .builder()
                                .mentor(User
                                        .builder()
                                        .id(2L)
                                        .build())
                                .build()))
                .mentees(new ArrayList<>(List.of(menteeOrMentor)))
                .mentors(new ArrayList<>(List.of(menteeOrMentor)))
                .build();

        mentorshipDto = new MentorshipDto(
                1L,
                "John",
                "john@example.com",
                "123456789",
                "About John Doe"
        );

        id = 1;
        mentees = List.of(user);
    }

    @Test
    void testGetMentees() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doReturn(mentorshipDto).when(mentorshipMapper).toDto(any(User.class));

        var result = mentorshipService.getMentees(anyLong());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(mentorshipRepository, times(1)).findById(anyLong());
        verify(mentorshipMapper, times(1)).toDto(captor.capture());
        assertThat(menteeOrMentor).isEqualTo(captor.getValue());
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(mentorshipDto);
        verifyNoMoreInteractions(mentorshipRepository, mentorshipMapper);
    }

    @Test
    void testGetMentors() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doReturn(mentorshipDto).when(mentorshipMapper).toDto(any(User.class));

        var result = mentorshipService.getMentors(anyLong());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(mentorshipRepository, times(1)).findById(anyLong());
        verify(mentorshipMapper, times(1)).toDto(captor.capture());
        assertThat(menteeOrMentor).isEqualTo(captor.getValue());
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(mentorshipDto);
        verifyNoMoreInteractions(mentorshipRepository, mentorshipMapper);
    }

    @Test
    void deleteMentee() {
        mentorshipService.deleteMentee(1, 2);
        verify(mentorshipRepository, times(1)).deleteMentorship(anyLong(), anyLong());
        verifyNoMoreInteractions(mentorshipRepository);
    }

    @Test
    void deleteMentor() {
        mentorshipService.deleteMentor(1, 2);
        verify(mentorshipRepository, times(1)).deleteMentorship(anyLong(), anyLong());
        verifyNoMoreInteractions(mentorshipRepository);
    }

    @Test
    void testDeleteMentorFromMentees() {
        mentorshipService.deleteMentorFromMentees(id, mentees);

        verify(validator).validateMenteesList(mentees);
        verify(mentorshipRepository).saveAll(mentees);
    }
}