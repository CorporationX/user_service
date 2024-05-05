package school.faang.user_service.service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @Test
    public void testGetMenteesWithNullParameter() {
        var exception = assertThrows(NullPointerException.class,
                () -> mentorshipController.getMentees(null));
        assertEquals("userId is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void testGetMenteesWithGettingMentees() {
        List<UserDto> users = List.of(UserDto.builder().id(2L).build(), UserDto.builder().id(3L).build());
        when(mentorshipService.getMentees(1L)).thenReturn(users);

        List<UserDto> result = mentorshipController.getMentees(1L);
        assertEquals(result.get(0).getId(), users.get(0).getId());
        assertEquals(result.get(1).getId(), users.get(1).getId());
    }

    @Test
    public void testGetMentorsWithNullParameter() {
        var exception = assertThrows(NullPointerException.class,
                () -> mentorshipController.getMentors(null));
        assertEquals("userId is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void testGetMentorsWithGettingMentors() {
        List<UserDto> users = List.of(UserDto.builder().id(2L).build(), UserDto.builder().id(3L).build());
        when(mentorshipService.getMentors(1L)).thenReturn(users);

        List<UserDto> result = mentorshipController.getMentors(1L);
        assertEquals(result.get(0).getId(), users.get(0).getId());
        assertEquals(result.get(1).getId(), users.get(1).getId());
    }

    @Test
    public void testDeleteMenteeWithNullMenteeParameter() {
        var exception = assertThrows(NullPointerException.class,
                () -> mentorshipController.deleteMentee(null, 1L));
        assertEquals("menteeId is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void testDeleteMenteeWithDeletionMentee() {
        mentorshipController.deleteMentee(1L, 2L);
        verify(mentorshipService, times(1)).deleteMentee(1L, 2L);
    }

    @Test
    public void testDeleteMenteeWithNullMentorParameter() {
        var exception = assertThrows(NullPointerException.class,
                () -> mentorshipController.deleteMentee(1L, null));
        assertEquals("mentorId is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void testDeleteMentorWithNullMenteeParameter() {
        var exception = assertThrows(NullPointerException.class,
                () -> mentorshipController.deleteMentor(null, 1L));
        assertEquals("menteeId is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void testDeleteMentorWithNullMentorParameter() {
        var exception = assertThrows(NullPointerException.class,
                () -> mentorshipController.deleteMentor(1L, null));
        assertEquals("mentorId is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void testDeleteMentorWithDeletionMentor() {
        mentorshipController.deleteMentor(1L, 2L);
        verify(mentorshipService, times(1)).deleteMentor(1L, 2L);
    }
}
