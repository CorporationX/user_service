package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.mentorship.UserMapper;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @Mock
    MentorshipService mentorshipService;
    @InjectMocks
    MentorshipController controller;
    @Spy
    UserMapper userMapper;

    @Test
    void testGetMentees_success() {
        User user = new User();
        user.setId(1L);
        User mentee1 = new User();
        mentee1.setId(2L);
        mentee1.setUsername("mentee1");

        User mentee2 = new User();
        mentee2.setId(3L);
        mentee2.setUsername("mentee2");

        List<User> mentees = List.of(mentee1, mentee2);
        List<UserDto> menteesDto = userMapper.toDtoList(mentees);

        when(mentorshipService.getMentees(anyLong())).thenReturn(menteesDto);
        assertDoesNotThrow(() -> controller.getMentees(1L));
    }

    @Test
    void testGetMentees_withNoMentees() {
        User user = new User();
        user.setId(1L);
        List<UserDto> menteesDto = new ArrayList<>();

        when(mentorshipService.getMentees(anyLong())).thenReturn(menteesDto);
        assertDoesNotThrow(() -> controller.getMentees(1L));
    }

    @Test
    void testGetMentees_userNotFound() {
        when(mentorshipService.getMentees(anyLong())).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        Exception exception = assertThrows(NotFoundException.class, () -> controller.getMentees(1L));
        assertEquals(exception.getMessage(), ErrorMessage.USER_NOT_FOUND.getMessage());
    }

    @Test
    void testGetMentors_success() {
        User mentor1 = new User();
        mentor1.setId(4L);
        mentor1.setUsername("mentor1");

        User mentor2 = new User();
        mentor2.setId(5L);
        mentor2.setUsername("mentor2");

        List<User> mentors = List.of(mentor1, mentor2);
        List<UserDto> mentorsDto = userMapper.toDtoList(mentors);

        when(mentorshipService.getMentors(anyLong())).thenReturn(mentorsDto);
        assertDoesNotThrow(() -> controller.getMentors(1L));
    }

    @Test
    void testGetMentors_withNoMentors() {
        User user = new User();
        user.setId(1L);
        List<UserDto> mentorsDto = new ArrayList<>();

        when(mentorshipService.getMentors(anyLong())).thenReturn(mentorsDto);
        assertDoesNotThrow(() -> controller.getMentors(1L));
    }

    @Test
    void testGetMentors_userNotFound() {
        when(mentorshipService.getMentors(anyLong())).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        Exception exception = assertThrows(NotFoundException.class, () -> controller.getMentors(1L));
        assertEquals(exception.getMessage(), ErrorMessage.USER_NOT_FOUND.getMessage());
    }

    @Test
    void testDeleteMentee_success() {
        assertDoesNotThrow(() -> controller.deleteMentee(1,2));
        verify(mentorshipService, times(1)).deleteMentee(1,2);
    }

    @Test
    void testDeleteMentee_whenMentorNotFound() {
        doThrow(new NotFoundException(ErrorMessage.MENTOR_NOT_FOUND)).when(mentorshipService).deleteMentee(1,2);

        Exception exception = assertThrows(NotFoundException.class, () -> controller.deleteMentee(1,2));
        assertEquals(exception.getMessage(), ErrorMessage.MENTOR_NOT_FOUND.getMessage());
    }

    @Test
    void testDeleteMentee_withMenteeNotFound() {
        doThrow(new NotFoundException(ErrorMessage.MENTEE_NOT_FOUND)).when(mentorshipService).deleteMentee(1,2);

        Exception exception = assertThrows(NotFoundException.class, () -> controller.deleteMentee(1,2));
        assertEquals(exception.getMessage(), ErrorMessage.MENTEE_NOT_FOUND.getMessage());
    }

    @Test
    void testDeleteMentor_success() {
        assertDoesNotThrow(() -> controller.deleteMentor(1,2));
        verify(mentorshipService, times(1)).deleteMentor(1,2);
    }

    @Test
    void testDeleteMentor_whenMenteeNotFound() {
        doThrow(new NotFoundException(ErrorMessage.MENTEE_NOT_FOUND)).when(mentorshipService).deleteMentor(1,2);

        Exception exception = assertThrows(NotFoundException.class, () -> controller.deleteMentor(1,2));
        assertEquals(exception.getMessage(), ErrorMessage.MENTEE_NOT_FOUND.getMessage());
    }

    @Test
    void testDeleteMentor_whenMentorNotFound() {
        doThrow(new NotFoundException(ErrorMessage.MENTOR_NOT_FOUND)).when(mentorshipService).deleteMentor(1,2);

        Exception exception = assertThrows(NotFoundException.class, () -> controller.deleteMentor(1,2));
        assertEquals(exception.getMessage(), ErrorMessage.MENTOR_NOT_FOUND.getMessage());
    }
}
