package school.faang.user_service.service.userServiseTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.userService.UserService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    private UserService userService;

    @Test
    public void findUserByIdTest() {
        User testUser = User.builder()
                .id(1L)
                .build();
        userRepository.save(testUser);
        User foundUser = userService.findUserById(testUser.getId());
        assertEquals(testUser, foundUser);
        assertThrows(IllegalArgumentException.class, () -> userService.findUserById(0L));
    }
    @Test
    public void testFindMenteeById_Found() {
        User testMentee = User.builder()
                .id(2L)
                .build();
        when(userRepository.findById(testMentee.getId())).thenReturn(Optional.of(testMentee));
        User foundMentee = userService.findMenteeById(testMentee.getId());
        assertEquals(testMentee, foundMentee);
    }

    @Test
    public void testFindMenteeById_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> userService.findMenteeById(0L));
    }

    @Test
    public void testFindMenteeById_NotFound() {
        Long menteeId = 123L;
        when(userRepository.findById(menteeId)).thenReturn(Optional.empty());
        User foundMentee = userService.findMenteeById(menteeId);
        assertNull(foundMentee);
    }

}