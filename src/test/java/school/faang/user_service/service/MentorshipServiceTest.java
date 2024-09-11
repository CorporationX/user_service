package school.faang.user_service.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    private static final long ID_1 = 1;
    private static final long ID_2 = 2;
    private static final long ID_3 = 3;
    private static final User USER_1 = new User();
    private static final User USER_2 = new User();
    private static final User USER_3 = new User();

    @Mock
    private static MentorshipRepository mentorshipRepository;
    @InjectMocks
    private static MentorshipService mentorshipService;

    @Spy
    private UserMapper userMapper;

    @DisplayName("Each user is a mentor and mentee of each other")
    @BeforeAll
    public static void setUp() {
        USER_1.setId(ID_1);
        USER_2.setId(ID_2);
        USER_3.setId(ID_3);
    }

    @Captor
    private ArgumentCaptor<List<User>> usersCaptor;

    @Test
    public void testSuccessGetMentees() {
        when(mentorshipRepository.findMenteesByMentorId(ID_1)).thenReturn(List.of(USER_2, USER_3));
        when(mentorshipRepository.findMenteesByMentorId(ID_2)).thenReturn(List.of(USER_1, USER_3));
        when(mentorshipRepository.findMenteesByMentorId(ID_3)).thenReturn(List.of(USER_1, USER_2));

        mentorshipService.getMentees(ID_1);
        verify(userMapper, times(1)).toDTOs(usersCaptor.capture());
        List<User> first_result = usersCaptor.getValue();

        mentorshipService.getMentees(ID_2);
        verify(userMapper, times(2)).toDTOs(usersCaptor.capture());
        List<User> second_result = usersCaptor.getValue();

        mentorshipService.getMentees(ID_3);
        verify(userMapper, times(3)).toDTOs(usersCaptor.capture());
        List<User> third_result = usersCaptor.getValue();

        verify(mentorshipRepository, times(1)).findMenteesByMentorId(ID_1);
        verify(mentorshipRepository, times(1)).findMenteesByMentorId(ID_2);
        verify(mentorshipRepository, times(1)).findMenteesByMentorId(ID_3);

        assertArrayEquals(List.of(USER_2, USER_3).toArray(), first_result.toArray());
        assertArrayEquals(List.of(USER_1, USER_3).toArray(), second_result.toArray());
        assertArrayEquals(List.of(USER_1, USER_2).toArray(), third_result.toArray());

    }


    @Test
    public void testSuccessGetMentors() {
        when(mentorshipRepository.findMentorsByMenteeId(ID_1)).thenReturn(List.of(USER_2, USER_3));
        when(mentorshipRepository.findMentorsByMenteeId(ID_2)).thenReturn(List.of(USER_1, USER_3));
        when(mentorshipRepository.findMentorsByMenteeId(ID_3)).thenReturn(List.of(USER_1, USER_2));

        mentorshipService.getMentors(ID_1);
        verify(userMapper, times(1)).toDTOs(usersCaptor.capture());
        List<User> first_result = usersCaptor.getValue();

        mentorshipService.getMentors(ID_2);
        verify(userMapper, times(2)).toDTOs(usersCaptor.capture());
        List<User> second_result = usersCaptor.getValue();

        mentorshipService.getMentors(ID_3);
        verify(userMapper, times(3)).toDTOs(usersCaptor.capture());
        List<User> third_result = usersCaptor.getValue();

        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(ID_1);
        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(ID_2);
        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(ID_3);

        assertArrayEquals(List.of(USER_2, USER_3).toArray(), first_result.toArray());
        assertArrayEquals(List.of(USER_1, USER_3).toArray(), second_result.toArray());
        assertArrayEquals(List.of(USER_1, USER_2).toArray(), third_result.toArray());

    }

    @DisplayName("Return emptyList if id not exist or incorrect ")
    @Test
    public void testGetMentorsWithIncorrectMenteeId() {
        long zeroMenteeId = 0L;
        long negativeMenteeId = -1L;

        when(mentorshipRepository.findMentorsByMenteeId(zeroMenteeId))
                .thenReturn(Collections.emptyList());
        when(mentorshipRepository.findMentorsByMenteeId(negativeMenteeId))
                .thenReturn(Collections.emptyList());
        when(userMapper.toDTOs(anyList()))
                .thenReturn(Collections.emptyList());

        List<UserDto> resultWithZeroID = mentorshipService.getMentors(zeroMenteeId);
        List<UserDto> resultWithNegativeID = mentorshipService.getMentors(negativeMenteeId);

        assertNotNull(resultWithZeroID);
        assertNotNull(resultWithNegativeID);

        assertTrue(resultWithZeroID.isEmpty(), "Результат должен быть пустым, если menteeId равен 0");
        assertTrue(resultWithNegativeID.isEmpty(), "Результат должен быть пустым, если menteeId отрицательный");

        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(zeroMenteeId);
        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(negativeMenteeId);
        verify(userMapper, times(2)).toDTOs(anyList());
    }

}

