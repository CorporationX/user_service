package school.faang.user_service.service.mentorship;

import ch.qos.logback.core.testUtil.MockInitialContext;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.UserNotFound;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees_IncorrectInputs_ShouldThrowException() {
        Mockito.when(mentorshipRepository.findById(Mockito.anyLong())).thenReturn(null);
        Assert.assertThrows(RuntimeException.class, () -> mentorshipService.getMentees(Mockito.anyLong()));
    }

    @Test
    public void testGetMentees_CorrectInputs_ShouldReturnEmptyList() {
        User user = User.builder()
                .mentees(Collections.emptyList())
                .build();

        Mockito.when(mentorshipRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        List<UserDTO> userDTOS = mentorshipService.getMentees(Mockito.anyLong());

        Assertions.assertEquals(0, userDTOS.size());
    }

    @Test
    public void testGetMentees_NotFoundUser_ShouldThrowException() {
        Mockito.when(mentorshipRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(UserNotFound.class, () -> mentorshipService.getMentees(Mockito.anyLong()));
    }
}
