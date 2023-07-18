package school.faang.user_service.controller.mentorship;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.exception.mentorship.InvalidRequestMentorId;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {
    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    @Test
    public void testGetMenteesBadId() {
        Assert.assertThrows(RuntimeException.class, () -> mentorshipController.getMentees(-5));
    }

    @Test
    public void testGetMenteesIsGetting() {
        long mentorId = 5;
        mentorshipController.getMentees(mentorId);
        Mockito.verify(mentorshipService, Mockito.times(1)).getMentees(mentorId);
    }

    @Test
    public void testGetMentors_ShouldThrowException() {
        Assert.assertThrows(InvalidRequestMentorId.class, () -> mentorshipController.getMentors(-5));
    }

    @Test
    public void testGetMentors_IsInvokingGetMentors() {
        long menteeId = 5;
        mentorshipController.getMentors(menteeId);
        Mockito.verify(mentorshipService, Mockito.times(1)).getMentors(menteeId);
    }

    @Test
    public void testDeleteMentee_ShouldThrowException() {
        Assert.assertThrows(InvalidRequestMentorId.class, () -> mentorshipController.deleteMentee(-2, 5));
    }

    @Test
    public void testDeleteMentee_IsInvokingDeleteMentee() {
        mentorshipController.deleteMentee(5, 2);
        Mockito.verify(mentorshipService, Mockito.times(1)).deleteMentee(5, 2);
    }
}
