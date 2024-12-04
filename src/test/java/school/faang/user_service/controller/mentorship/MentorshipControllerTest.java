package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.dto.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    @Test
    void testGetMentees() {
        long mentorId = 1L;
        List<UserDto> mockMentees = List.of(
                new UserDto(1L, "Alex", "alex@alex.com", "1234567890", "About Alex"),
                new UserDto(2L, "Zorro", "zorro@example.com", "0987654321", "About Zorro")
        );

        when(mentorshipService.getMentees(mentorId)).thenReturn(mockMentees);

        List<UserDto> result = mentorshipController.getMentees(mentorId);

        assertEquals(mockMentees.size(), result.size());
        assertEquals(mockMentees, result);

        verify(mentorshipService, times(1)).getMentees(mentorId);
    }

    @Test
    void testGetMentors() {
        long menteeId = 2L;

        List<UserDto> mockMentors = List.of(
                new UserDto(1L, "Alex", "alex@alex.com", "1234567890", "About Alex"),
                new UserDto(2L, "Zorro", "zorro@example.com", "0987654321", "About Zorro")
        );

        when(mentorshipService.getMentors(menteeId)).thenReturn(mockMentors);

        List<UserDto> result = mentorshipController.getMentors(menteeId);

        assertEquals(mockMentors.size(), result.size());
        assertEquals(mockMentors, result);

        verify(mentorshipService, times(1)).getMentors(menteeId);
    }

    @Test
    void testDeleteMentees() {
        long mentorId = 1L;
        long menteeId = 2L;

        mentorshipController.deleteMentees(mentorId, menteeId);

        verify(mentorshipService, times(1)).deleteMentee(mentorId, menteeId);
    }

    @Test
    void testDeleteMentors() {
        long mentorId = 1L;
        long menteeId = 2L;

        mentorshipController.deleteMentors(mentorId, menteeId);

        verify(mentorshipService, times(1)).deleteMentor(mentorId, menteeId);
    }
}