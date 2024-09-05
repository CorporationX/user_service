package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @Spy
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    User user;
    MentorshipDto mentorshipDto;

    @BeforeEach
    void setUp() {
        var mentorOrMentee = User.builder()
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
                .aboutMe("123456789")
                .mentees(new ArrayList<>(List.of(mentorOrMentee)))
                .mentors(new ArrayList<>(List.of(mentorOrMentee)))
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
        when(mentorshipMapper.toDto(any(User.class))).thenReturn(mentorshipDto);

        mentorshipService.getMentees(anyLong());
        verify(mentorshipRepository, times(1)).findById(anyLong());
        verify(mentorshipMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void testGetMentors() {
        when(mentorshipRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mentorshipMapper.toDto(any(User.class))).thenReturn(mentorshipDto);

        mentorshipService.getMentors(anyLong());
        verify(mentorshipRepository, times(1)).findById(anyLong());
        verify(mentorshipMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void deleteMentee() {
        mentorshipService.deleteMentee(1, 2);
        verify(mentorshipRepository, times(1)).deleteMentorship(anyLong(), anyLong());
    }

    @Test
    void deleteMentor() {
        mentorshipService.deleteMentor(1, 2);
        verify(mentorshipRepository, times(1)).deleteMentorship(anyLong(), anyLong());
    }
}