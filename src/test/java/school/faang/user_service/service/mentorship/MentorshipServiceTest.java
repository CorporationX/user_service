package school.faang.user_service.service.mentorship;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

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
    public void testGetMenteesNotFoundUser() {
        Mockito.when(mentorshipRepository.findById(Mockito.anyLong())).thenReturn(null);
        Assert.assertThrows(RuntimeException.class, () -> mentorshipService.getMentees(Mockito.anyLong()));
    }

    @Test
    public void testGetMenteesIsGetting() {
        long mentorId = 3;
        Mockito.when(mentorshipRepository.findById(Mockito.anyLong())).thenReturn(new User());
        mentorshipService.getMentees(mentorId);
        Mockito.verify(userMapper, Mockito.times(1)).toDto(new User());
    }
}
