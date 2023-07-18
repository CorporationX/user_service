package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentor.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;


    @Test
    public void getMentees_When() {
        RuntimeException runtimeException = assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentees(null));
        assertEquals("User with id " + null + " not found", runtimeException.getMessage());
    }

    @Test
    public void getMentees_CorrectAnswer() {
        long mentorId = 2;
        User user = new User();
        user.setId(mentorId);
        Optional<User> userOptional = Optional.of(user);
        when(mentorshipRepository.findById(anyLong())).thenReturn(userOptional);
        when(mentorshipService.getMentees(mentorId)).thenReturn(List.of(new UserDto(), new UserDto()));

        List<UserDto> userDto = mentorshipService.getMentees(mentorId);

        assertEquals(2, userDto.size());
    }

    @Test
    public void getMentors_When() {
        RuntimeException runtimeException = assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentors(null));
        assertEquals("User with id " + null + " not found", runtimeException.getMessage());
    }

    @Test
    public void getMentors_CorrectAnswer() {
        long menteeId = 2;
        User user = new User();
        user.setId(menteeId);
        Optional<User> userOptional = Optional.of(user);
        when(mentorshipRepository.findById(anyLong())).thenReturn(userOptional);
        when(mentorshipService.getMentors(menteeId)).thenReturn(List.of(new UserDto(), new UserDto()));

        List<UserDto> userDto = mentorshipService.getMentors(menteeId);

        assertEquals(2, userDto.size());
    }
}