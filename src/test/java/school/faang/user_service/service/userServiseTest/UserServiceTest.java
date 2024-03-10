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
    private UserService userService;

    @Test
    public void testDeleteMentee() {
        User mentor = User.builder()
                .id(1L)
                .mentees(Collections.singletonList(new User()))
                .build();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(mentor));
        userService.deleteMentee(2L, 1L);
    }

    @Test
    public void testDeleteMentor() {
        User mentee = User.builder()
                .id(1L)
                .mentors(Collections.singletonList(new User()))
                .build();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(mentee));
        userService.deleteMentor(3L, 1L);
    }
}