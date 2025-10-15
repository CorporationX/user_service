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

    @Test
    void testAddMentorship() {
        when(userContext.getUserId()).thenReturn(5L);

        mentorshipController.addMentorship(5L, 4L);

        verify(mentorshipService, times(1)).addMentorship(5L, 4L);
    }

    @Test
    void testAddMentorship_shouldThrow_whenIdenticalIds() {
        when(userContext.getUserId()).thenReturn(5L);

        assertThrows(DataValidationException.class, () ->
                mentorshipController.addMentorship(5L, 5L)
        );

        verify(mentorshipService, never()).addMentorship(anyLong(), anyLong());
    }

    @Test
    void testAddMentorship_shouldThrow_whenUserNotInRelation() {
        when(userContext.getUserId()).thenReturn(5L);

        assertThrows(ForbiddenException.class, () ->
                mentorshipController.addMentorship(4L, 3L)
        );

        verify(mentorshipService, never()).addMentorship(anyLong(), anyLong());
    }

    @Test
    void testDeleteMentorship() {
        when(userContext.getUserId()).thenReturn(5L);

        mentorshipController.deleteMentorship(5L, 4L);

        verify(mentorshipService, times(1)).deleteMentorship(5L, 4L);
    }

    @Test
    void testDeleteMentorship_shouldThrow_whenIdenticalIds() {
        when(userContext.getUserId()).thenReturn(5L);

        assertThrows(DataValidationException.class, () ->
                mentorshipController.deleteMentorship(5L, 5L)
        );

        verify(mentorshipService, never()).deleteMentorship(anyLong(), anyLong());
    }

    @Test
    void testDeleteMentorship_shouldThrow_whenUserNotInRelation() {
        when(userContext.getUserId()).thenReturn(5L);

        assertThrows(ForbiddenException.class, () ->
                mentorshipController.deleteMentorship(4L, 3L)
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