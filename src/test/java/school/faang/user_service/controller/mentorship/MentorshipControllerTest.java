package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.MentorshipController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.MentorshipValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipValidator mentorshipValidator;
    @InjectMocks
    private MentorshipController mentorshipController;
    private final long USER_ID_WITH_MENTORSHIP = 1;
    private final long USER_ID_WITH_NO_MENTORSHIP = 2;
    private final long mentorId = 1L;
    private final long menteeId = 1L;
    private final List<UserDto> mentorshipDtos = new ArrayList<>(List.of(new UserDto(), new UserDto()));

    @BeforeEach
    public void init() {
    }

    @Test
    public void testGetMentors_userHasMentors_returnsUserDtos() {
        List<UserDto> mentors = serviceGetMentors(true);
        assertTrue(mentors.size() > 0);
    }

    @Test
    public void testGetMentees_userHasMentees_returnsUserDtos() {
        List<UserDto> mentees = serviceGetMentees(true);
        assertTrue(mentees.size() > 0);
    }

    @Test
    public void testGetMentors_userHasNotMentorship_returnsEmptyList() {
        List<UserDto> mentors = serviceGetMentors(false);
        assertEquals(0, mentors.size());
    }

    @Test
    public void testGetMentees_userHasNotMentorship_returnsEmptyList() {
        List<UserDto> mentees = serviceGetMentees(false);
        assertEquals(0, mentees.size());
    }

    @Test
    public void testDeleteMentor_userHasMentorship_deleteMentor() {
        mentorshipController.deleteMentor(menteeId, mentorId);
        verify(mentorshipService, Mockito.times(1)).deleteMentor(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_userHasMentorship_deleteMentee() {
        mentorshipController.deleteMentee(mentorId, menteeId);
        verify(mentorshipService, Mockito.times(1)).deleteMentee(mentorId, menteeId);
    }

    private List<UserDto> serviceGetMentors(boolean userHas) {
        long id;
        List<UserDto> expected;
        if (userHas) {
            id = USER_ID_WITH_MENTORSHIP;
            expected = List.copyOf(mentorshipDtos);
        } else {
            id = USER_ID_WITH_NO_MENTORSHIP;
            expected = Collections.emptyList();
        }
        when(mentorshipService.getMentors(id)).thenReturn(expected);

        return mentorshipController.getMentors(id);
    }

    private List<UserDto> serviceGetMentees(boolean userHas) {
        long id;
        List<UserDto> expected;
        if (userHas) {
            id = USER_ID_WITH_MENTORSHIP;
            expected = List.copyOf(mentorshipDtos);
        } else {
            id = USER_ID_WITH_NO_MENTORSHIP;
            expected = Collections.emptyList();
        }
        when(mentorshipService.getMentees(id)).thenReturn(expected);

        return mentorshipController.getMentees(id);
    }
}
