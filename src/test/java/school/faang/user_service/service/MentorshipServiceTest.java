package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.mentorship.UserMapperImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private MentorshipService mentorshipService;

    private User nonExistentUser;
    private User correctUser;
    private UserDto nonExistentUserDto;
    private UserDto correctUserDto;
    private List<UserDto> expectedDtos;
    private final long CORRECT_USER_ID = 1L;
    private final long INCORRECT_USER_ID = 0L;
    private final long NON_EXISTENT_USER_ID = 7777777L;

    @BeforeEach
    void initData() {
        nonExistentUser = new User();
        nonExistentUser.setId(NON_EXISTENT_USER_ID);
        nonExistentUser.setUsername("Nikita");
        nonExistentUser.setCity("Moscow");
        nonExistentUser.setEmail("nick@mail.ru");
        nonExistentUser.setMentors(new ArrayList<>());
        nonExistentUser.setMentees(new ArrayList<>());
        correctUser = new User();
        correctUser.setId(CORRECT_USER_ID);
        correctUser.setUsername("Max");
        correctUser.setCity("Rome");
        correctUser.setEmail("max@google.com");
        correctUser.setMentees(new ArrayList<>(List.of(nonExistentUser, correctUser)));
        correctUser.setMentors(new ArrayList<>(List.of(nonExistentUser, correctUser)));

        nonExistentUserDto = new UserDto();
        nonExistentUserDto.setId(NON_EXISTENT_USER_ID);
        nonExistentUserDto.setUsername("Nikita");
        nonExistentUserDto.setCity("Moscow");
        nonExistentUserDto.setEmail("nick@mail.ru");
        correctUserDto = new UserDto();
        correctUserDto.setId(CORRECT_USER_ID);
        correctUserDto.setUsername("Max");
        correctUserDto.setCity("Rome");
        correctUserDto.setEmail("max@google.com");

        expectedDtos = List.of(nonExistentUserDto, correctUserDto);
    }

    @Test
    void testGetMenteesInputIncorrectUserId() {
        assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentees(INCORRECT_USER_ID));
    }

    @Test
    void testGetMenteesNonExistentUserId() {
        when(mentorshipRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.ofNullable(nonExistentUser));

        List<UserDto> actualList = mentorshipService.getMentees(NON_EXISTENT_USER_ID);
        List<UserDto> expectedList = new ArrayList<>();

        verify(mentorshipRepository, times(1)).findById(NON_EXISTENT_USER_ID);
        assertArrayEquals(expectedList.toArray(), actualList.toArray());
    }

    @Test
    void testGetMenteesInputCorrectUserId() {
        when(mentorshipRepository.findById(CORRECT_USER_ID)).thenReturn(Optional.ofNullable(correctUser));

        List<UserDto> actualList = mentorshipService.getMentees(CORRECT_USER_ID);

        verify(mentorshipRepository, times(1)).findById(CORRECT_USER_ID);
        assertArrayEquals(expectedDtos.toArray(), actualList.toArray());
    }

    @Test
    void testToDto() {
        UserDto actualUserDto = userMapper.userToUserDto(nonExistentUser);

        assertEquals(nonExistentUserDto, actualUserDto);
    }
}
