package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    User user;
    User menteeOrMentor;
    MentorshipDto mentorshipDto;

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
}