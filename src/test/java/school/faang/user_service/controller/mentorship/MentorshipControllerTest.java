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
    public void testGetMenteesIsValidAnswer() {
        long mentorId = 7;
        Mockito.when(mentorshipService.getMentees(mentorId)).thenReturn(List.of(new UserDTO(), new UserDTO()));
        List<UserDTO> userDTOS = mentorshipController.getMentees(mentorId);
        Assertions.assertEquals(2, userDTOS.size());
    }
}
