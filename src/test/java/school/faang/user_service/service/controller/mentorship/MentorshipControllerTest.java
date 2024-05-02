package school.faang.user_service.service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @Test
    public void testGetMenteesWithNullParameter(){
        assertThrows(NullPointerException.class, ()->mentorshipController.getMentees(null));
    }

    @Test
    public void testGetMenteesWithGettingMentees(){
        User secondUser = User.builder().id(2L).username("second").build();
        User thirdUser = User.builder().id(3L).username("third").build();
        User firstUser = User.builder().id(1L).username("first").mentees(List.of(secondUser, thirdUser)).build();

        when(mentorshipService.getMentees(1L)).thenReturn(List.of(
                UserDto.builder().id(2L).username("second").build(),
                UserDto.builder().id(3L).username("third").build()
        ));
        List<UserDto> result = mentorshipService.getMentees(1L);

        assertEquals(result.get(0).getId(), secondUser.getId());
        assertEquals(result.get(0).getUsername(), secondUser.getUsername());
        assertEquals(result.get(1).getId(), thirdUser.getId());
        assertEquals(result.get(1).getUsername(), thirdUser.getUsername());
    }

    @Test
    public void testGetMentorsWithNullParameter(){
        assertThrows(NullPointerException.class, ()->mentorshipController.getMentors(null));
    }

    @Test
    public void testGetMentorsWithGettingMentors(){
        User secondUser = User.builder().id(2L).username("second").build();
        User thirdUser = User.builder().id(3L).username("third").build();
        User firstUser = User.builder().id(1L).username("first").mentors(List.of(secondUser, thirdUser)).build();

        when(mentorshipService.getMentors(1L)).thenReturn(List.of(
                UserDto.builder().id(2L).username("second").build(),
                UserDto.builder().id(3L).username("third").build()
        ));
        List<UserDto> result = mentorshipService.getMentors(1L);

        assertEquals(result.get(0).getId(), secondUser.getId());
        assertEquals(result.get(0).getUsername(), secondUser.getUsername());
        assertEquals(result.get(1).getId(), thirdUser.getId());
        assertEquals(result.get(1).getUsername(), thirdUser.getUsername());
    }
}
