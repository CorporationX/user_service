package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.mentorship.UserMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private  UserMapper userMapper;
    @Mock
    private MentorshipService mentorshipService;

    @Test
    void testInputIncorrectUserId() {
        when(mentorshipService.getMentees(0L)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentees(0L));
    }

    @Test
    void testNonExistentUserId() {
        when(mentorshipService.getMentees(7777777L)).thenReturn(new ArrayList<>());

        List<UserDto> actualList = mentorshipService.getMentees(7777777L);
        List<UserDto> expectedList = new ArrayList<>();

        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }

    @Test
    void testInputCorrectUserId() {
        mentorshipRepository.findById(1L);
        verify(mentorshipRepository, times(1)).findById(1L);
    }

    @Test
    void testService() {
        UserDto userDto1 = new UserDto();
        userDto1.setId(1l);
        userDto1.setUsername("Nikita");
        userDto1.setCity("Moscow");
        userDto1.setEmail("nick@mail.ru");
        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setUsername("Max");
        userDto2.setCity("Rome");
        userDto2.setEmail("max@google.com");

        List<UserDto> actualDtos = List.of(userDto1, userDto2);

        when(mentorshipService.getMentees(1L)).thenReturn(actualDtos);

        List<UserDto> mentees = mentorshipService.getMentees(1L);
        assertArrayEquals(mentees.toArray(), actualDtos.toArray());
    }

    @Test
    void testToDto() {
        User user1 = new User();
        user1.setId(1l);
        user1.setUsername("Nikita");
        user1.setCity("Moscow");
        user1.setEmail("nick@mail.ru");

        UserDto expectedDto = new UserDto();
        expectedDto.setId(1l);
        expectedDto.setUsername("Nikita");
        expectedDto.setCity("Moscow");
        expectedDto.setEmail("nick@mail.ru");

        when(userMapper.userToUserDto(user1)).thenReturn(expectedDto);
        UserDto actualDto = userMapper.userToUserDto(user1);

        assertEquals(expectedDto, actualDto);
    }
}
