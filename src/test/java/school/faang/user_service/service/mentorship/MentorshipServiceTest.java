package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.UserDto;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipMapper mentorshipMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    void testGetMentees() {
        long mentorId = 1L;

        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);

        List<User> mockMentees = List.of(mockUser1, mockUser2);

        UserDto dto1 = new UserDto(1L, "Alex", "alex@alex.com", "1234567890", "About Alex");
        UserDto dto2 = new UserDto(2L, "Zorro", "zorro@example.com", "0987654321", "About Zorro");

        when(mentorshipRepository.findById(mentorId)).thenReturn(Optional.of(mockUser1));
        when(mockUser1.getMentees()).thenReturn(mockMentees);
        when(mentorshipMapper.toDto(mockUser1)).thenReturn(dto1);
        when(mentorshipMapper.toDto(mockUser2)).thenReturn(dto2);

        List<UserDto> result = mentorshipService.getMentees(mentorId);

        assertEquals(2, result.size());
        assertEquals(List.of(dto1, dto2), result);

        verify(mentorshipRepository, times(1)).findById(mentorId);
        verify(mentorshipMapper, times(1)).toDto(mockUser1);
        verify(mentorshipMapper, times(1)).toDto(mockUser2);
    }

    @Test
    void testGetMentors() {
        long menteeId = 2L;

        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        List<User> mockMentors = List.of(mockUser1, mockUser2);

        UserDto dto1 = new UserDto(1L, "Alex", "alex@alex.com", "1234567890", "About Alex");
        UserDto dto2 = new UserDto(2L, "Zorro", "zorro@example.com", "0987654321", "About Zorro");

        when(mentorshipRepository.findById(menteeId)).thenReturn(Optional.of(mockUser2));
        when(mockUser2.getMentors()).thenReturn(mockMentors);
        when(mentorshipMapper.toDto(mockUser1)).thenReturn(dto1);
        when(mentorshipMapper.toDto(mockUser2)).thenReturn(dto2);

        List<UserDto> result = mentorshipService.getMentors(menteeId);

        assertEquals(2, result.size());
        assertEquals(List.of(dto1, dto2), result);

        verify(mentorshipRepository, times(1)).findById(menteeId);
        verify(mentorshipMapper, times(1)).toDto(mockUser1);
        verify(mentorshipMapper, times(1)).toDto(mockUser2);
    }

    @Test
    void testDeleteMentee() {
        long menteeId = 2L;
        long mentorId = 1L;

        mentorshipService.deleteMentee(menteeId, mentorId);

        verify(mentorshipRepository, times(1)).deleteMentorship(menteeId, mentorId);
    }

    @Test
    void testDeleteMentor() {
        long menteeId = 2L;
        long mentorId = 1L;

        mentorshipService.deleteMentor(menteeId, mentorId);

        verify(mentorshipRepository, times(1)).deleteMentorship(menteeId, mentorId);
    }
}
