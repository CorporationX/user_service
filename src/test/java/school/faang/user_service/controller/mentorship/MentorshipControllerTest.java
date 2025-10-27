package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {
    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private MentorshipController mentorshipController;

    UserDto initUserDto() {
        return new UserDto(8L,
                "Dima",
                "moneyWaster@mail.ru",
                "972-98-90-34",
                "Люблю футбол, шахматы и математику"
        );
    }

    private final long userContextId = 5L;

    @Test
    void testAddMentorship() {
        long mentorId = 5L;
        long menteeId = 4L;

        when(userContext.getUserId()).thenReturn(userContextId);

        mentorshipController.addMentorship(mentorId, menteeId);

        verify(mentorshipService, times(1)).addMentorship(mentorId, menteeId);
    }

    @Test
    void testAddMentorship_shouldThrow_whenIdenticalIds() {
        long mentorId = 5L;
        long menteeId = 5L;

        when(userContext.getUserId()).thenReturn(userContextId);

        assertThrows(DataValidationException.class, () ->
                mentorshipController.addMentorship(mentorId, menteeId)
        );

        verify(mentorshipService, never()).addMentorship(anyLong(), anyLong());
    }

    @Test
    void testAddMentorship_shouldThrow_whenUserNotInRelation() {
        long mentorId = 4L;
        long menteeId = 3L;

        when(userContext.getUserId()).thenReturn(userContextId);

        assertThrows(ForbiddenException.class, () ->
                mentorshipController.addMentorship(mentorId, menteeId)
        );

        verify(mentorshipService, never()).addMentorship(anyLong(), anyLong());
    }

    @Test
    void testDeleteMentorship() {
        long menteeId = 5L;
        long mentorId = 4L;

        when(userContext.getUserId()).thenReturn(userContextId);

        mentorshipController.deleteMentorship(menteeId, mentorId);

        verify(mentorshipService, times(1)).deleteMentorship(menteeId, mentorId);
    }

    @Test
    void testDeleteMentorship_shouldThrow_whenIdenticalIds() {
        long mentorAndMenteeIds = 5L;

        when(userContext.getUserId()).thenReturn(userContextId);

        assertThrows(DataValidationException.class, () ->
                mentorshipController.deleteMentorship(mentorAndMenteeIds, mentorAndMenteeIds)
        );

        verify(mentorshipService, never()).deleteMentorship(anyLong(), anyLong());
    }

    @Test
    void testDeleteMentorship_shouldThrow_whenUserNotInRelation() {
        long menteeId = 4L;
        long mentorId = 3L;

        when(userContext.getUserId()).thenReturn(userContextId);

        assertThrows(ForbiddenException.class, () ->
                mentorshipController.deleteMentorship(menteeId, mentorId)
        );

        verify(mentorshipService, never()).deleteMentorship(anyLong(), anyLong());
    }

    @Test
    void testGetMentees() {
        long userId = 7L;
        UserDto userDto = initUserDto();
        List<UserDto> userDtos = List.of(userDto);

        when(mentorshipService.getMentees(userId)).thenReturn(userDtos);
        List<UserDto> result = mentorshipController.getMentees(userId);

        assertNotNull(result);
        assertEquals(userDtos, result);
        verify(mentorshipService, times(1)).getMentees(userId);
    }

    @Test
    void testGetMentors() {
        long userId = 7L;
        UserDto userDto = initUserDto();
        List<UserDto> userDtos = List.of(userDto);

        when(mentorshipService.getMentors(userId)).thenReturn(userDtos);
        List<UserDto> result = mentorshipController.getMentors(userId);

        assertNotNull(result);
        assertEquals(userDtos, result);
        verify(mentorshipService, times(1)).getMentors(userId);
    }
}