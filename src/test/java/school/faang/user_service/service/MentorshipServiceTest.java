package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.IncorrectIdException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;

    private User nonExistentUser;
    private User correctUser;
    private User testUser;
    private UserDto nonExistentUserDto;
    private UserDto testUserDto;
    private List<UserDto> expectedDtos;
    private final long CORRECT_USER_ID = 1L;
    private final long INCORRECT_USER_ID = 0L;
    private final long NON_EXISTENT_USER_ID = 7777777L;

    @BeforeEach
    void initData() {
        testUser = User.builder()
                .id(2L)
                .username("Andrew")
                .city("Milan")
                .email("an@mail.ru")
                .build();
        nonExistentUser = User.builder()
                .id(NON_EXISTENT_USER_ID)
                .username("Nikita")
                .city("Moscow")
                .email("nick@mail.ru")
                .mentors(new ArrayList<>())
                .mentees(new ArrayList<>())
                .build();
        correctUser = User.builder()
                .id(CORRECT_USER_ID)
                .username("Max")
                .city("Rome")
                .email("max@google.com")
                .mentors(new ArrayList<>(List.of(nonExistentUser, testUser)))
                .mentees(new ArrayList<>(List.of(nonExistentUser, testUser)))
                .build();

        nonExistentUserDto = UserDto.builder()
                .id(NON_EXISTENT_USER_ID)
                .username("Nikita")
                .city("Moscow")
                .email("nick@mail.ru")
                .build();
        testUserDto = UserDto.builder()
                .id(2L)
                .username("Andrew")
                .city("Milan")
                .email("an@mail.ru")
                .build();
        expectedDtos = List.of(nonExistentUserDto, testUserDto);
    }

    @Test
    void testGetMenteesInputIncorrectUserId() {
        assertThrows(IncorrectIdException.class, () -> mentorshipService.getMentees(INCORRECT_USER_ID));
    }

    @Test
    void testGetMentorsInputIncorrectUserId() {
        assertThrows(IncorrectIdException.class, () -> mentorshipService.getMentors(INCORRECT_USER_ID));
    }

    @Test
    void testDeleteMenteeInputIncorrectUserId() {
        assertThrows(IncorrectIdException.class, () -> mentorshipService.deleteMentee(INCORRECT_USER_ID, INCORRECT_USER_ID));
    }

    @Test
    void testDeleteMentorInputIncorrectUserId() {
        assertThrows(IncorrectIdException.class, () -> mentorshipService.deleteMentor(INCORRECT_USER_ID, INCORRECT_USER_ID));
    }

    @Test
    void testGetMenteesNonExistentUserId() {
        when(mentorshipRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.ofNullable(nonExistentUser));

        List<UserDto> actualList = mentorshipService.getMentees(NON_EXISTENT_USER_ID);
        List<UserDto> expectedList = new ArrayList<>();

        verify(mentorshipRepository, times(1)).findById(NON_EXISTENT_USER_ID);
        assertEquals(expectedList, actualList);
    }

    @Test
    void testGetMentorsNonExistentUserId() {
        when(mentorshipRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.of(nonExistentUser));

        List<UserDto> actualList = mentorshipService.getMentors(NON_EXISTENT_USER_ID);
        List<UserDto> expectedList = new ArrayList<>();

        verify(mentorshipRepository, times(1)).findById(NON_EXISTENT_USER_ID);
        assertEquals(expectedList, actualList);
    }

    @Test
    void testGetMenteesCorrectUserId() {
        when(mentorshipRepository.findById(CORRECT_USER_ID)).thenReturn(Optional.ofNullable(correctUser));

        List<UserDto> actualList = mentorshipService.getMentees(CORRECT_USER_ID);

        verify(mentorshipRepository, times(1)).findById(CORRECT_USER_ID);
        assertEquals(expectedDtos, actualList);
    }

    @Test
    void testGetMentorsCorrectUserId() {
       when(mentorshipRepository.findById(CORRECT_USER_ID)).thenReturn(Optional.ofNullable(correctUser));

       List<UserDto> actualList = mentorshipService.getMentors(CORRECT_USER_ID);
       assertEquals(expectedDtos, actualList);
    }

    @Test
    void testDeleteMenteeCorrectUserId() {
        when(mentorshipRepository.findById(CORRECT_USER_ID)).thenReturn(Optional.of(correctUser));
        when(mentorshipRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.of(nonExistentUser));

        mentorshipService.deleteMentee(NON_EXISTENT_USER_ID, CORRECT_USER_ID);

        List<User> actualMentorList = correctUser.getMentees();
        List<User> expectedMentorList = List.of(testUser);
        assertEquals(expectedMentorList, actualMentorList);
    }

    @Test
    void testDeleteMentorCorrectUserId() {
        when(mentorshipRepository.findById(CORRECT_USER_ID)).thenReturn(Optional.ofNullable(correctUser));
        when(mentorshipRepository.findById(NON_EXISTENT_USER_ID)).thenReturn(Optional.ofNullable(nonExistentUser));

        mentorshipService.deleteMentor(CORRECT_USER_ID, NON_EXISTENT_USER_ID);

        List<User> actualMenteeList = correctUser.getMentors();
        List<User> expectedMenteeList = List.of(testUser);
        assertEquals(expectedMenteeList, actualMenteeList);
    }

    @Test
    void testToDto() {
        UserDto actualUserDto = userMapper.toDto(nonExistentUser);
        assertEquals(nonExistentUserDto, actualUserDto);
    }
}
