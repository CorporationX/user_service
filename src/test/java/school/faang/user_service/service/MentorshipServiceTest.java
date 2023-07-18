package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentee.UserDto;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;


    @Test
    public void getMentees_When() {
        when(mentorshipRepository.existsById(anyLong())).thenReturn(false);

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> mentorshipService.getMentees(anyLong()));

        assertEquals("User with id not found", runtimeException.getMessage());
    }

    @Test
    public void getMentees_CorrectAnswer() {
        long mentorId = 2;

        when(mentorshipRepository.existsById(anyLong())).thenReturn(true);
        when(mentorshipService.getMentees(mentorId)).thenReturn(List.of(new UserDto(), new UserDto()));

        List<UserDto> userDto = mentorshipService.getMentees(mentorId);

        assertEquals(2, userDto.size());
    }
}