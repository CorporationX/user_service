package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    User user;
    List<UserDto> usersDto = new ArrayList<>();
    long userId;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1L);
        usersDto = List.of(
                new UserDto(2L),
                new UserDto(3L));
        userId = user.getId();
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    public void testValidateIdCorrect(long id) {
        assertThrows(DataValidationException.class,
                () -> mentorshipController.getMentees(id),
                "Incorrect id");
    }

    @Test
    public void testGetMentees() {
        when(mentorshipService.getMentees(userId))
                .thenReturn(usersDto);
        List<UserDto> resultList = mentorshipController.getMentees(userId);

        assertEquals(usersDto, resultList);
    }

    @Test
    public void testGetMentors() {
        when(mentorshipService.getMentors(userId))
                .thenReturn(usersDto);
        List<UserDto> resultList = mentorshipController.getMentors(userId);

        assertEquals(usersDto, resultList);
    }

    @Test
    public void testDeleteMentee() {
        long secondUserId = usersDto.get(0).getId();
        doNothing().when(mentorshipService).deleteMentee(userId, secondUserId);
        mentorshipController.deleteMentee(userId, secondUserId);

        verify(mentorshipService, times(1))
                .deleteMentee(userId, secondUserId);
    }

    @Test
    public void testDeleteMentor() {
        long secondUserId = usersDto.get(0).getId();
        doNothing().when(mentorshipService).deleteMentor(userId, secondUserId);
        mentorshipController.deleteMentor(userId, secondUserId);

        verify(mentorshipService, times(1))
                .deleteMentor(userId, secondUserId);
    }
}
