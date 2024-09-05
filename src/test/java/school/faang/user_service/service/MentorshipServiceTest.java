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
        var mentorOrMentee = new User();
        mentorOrMentee.setId(2L);
        mentorOrMentee.setUsername("JaneSmith");
        mentorOrMentee.setEmail("janes@smith.com");
        mentorOrMentee.setPhone("0987654321");
        mentorOrMentee.setAboutMe("About me");

        user = new User();
        user.setId(1L);
        user.setUsername("John");
        user.setEmail("john@example.com");
        user.setPhone("123456789");
        user.setAboutMe("About John Doe");
        user.setMentees(new ArrayList<>(List.of(mentorOrMentee)));
        user.setMentors(new ArrayList<>(List.of(mentorOrMentee)));

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
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mentorshipMapper.toDto(any(User.class))).thenReturn(mentorshipDto);

        mentorshipService.getMentees(1L);
        verify(mentorshipRepository, times(1)).findById(1L);
        verify(mentorshipMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void testGetMentors() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mentorshipMapper.toDto(any(User.class))).thenReturn(mentorshipDto);

        mentorshipService.getMentors(1L);
        verify(mentorshipRepository, times(1)).findById(1L);
        verify(mentorshipMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void deleteMentee() {
        mentorshipService.deleteMentee(1, 2);
        verify(mentorshipRepository, times(1)).deleteMentorship(1L, 2);
    }

    @Test
    void deleteMentor() {
        mentorshipService.deleteMentor(1, 2);
        verify(mentorshipRepository, times(1)).deleteMentorship(1L, 2);
    }
}