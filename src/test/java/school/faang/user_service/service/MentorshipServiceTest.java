package school.faang.user_service.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    private static final long ID_1 = 1;
    private static final long ID_2 = 2;
    private static final long ID_3 = 3;
    private static final User USER_1 = new User();
    private static final User USER_2 = new User();
    private static final User USER_3 = new User();


    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    @Spy
    private UserMapper userMapper;

    @DisplayName("Each user is a mentor and mentee of each other")
    @BeforeEach
    public void setUp() {
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

        when(mentorshipRepository.existsById(ID_1)).thenReturn(true);
        when(mentorshipRepository.existsById(ID_2)).thenReturn(true);
        when(mentorshipRepository.existsById(ID_3)).thenReturn(true);

        mentorshipService.getMentees(ID_1);
        verify(userMapper, times(1)).toUsersDtos(usersCaptor.capture());
        List<User> first_result = usersCaptor.getValue();

        mentorshipService.getMentees(ID_2);
        verify(userMapper, times(2)).toUsersDtos(usersCaptor.capture());
        List<User> second_result = usersCaptor.getValue();

        mentorshipService.getMentees(ID_3);
        verify(userMapper, times(3)).toUsersDtos(usersCaptor.capture());
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

        when(mentorshipRepository.existsById(ID_1)).thenReturn(true);
        when(mentorshipRepository.existsById(ID_2)).thenReturn(true);
        when(mentorshipRepository.existsById(ID_3)).thenReturn(true);

        mentorshipService.getMentors(ID_1);
        verify(userMapper, times(1)).toUsersDtos(usersCaptor.capture());
        List<User> first_result = usersCaptor.getValue();

        mentorshipService.getMentors(ID_2);
        verify(userMapper, times(2)).toUsersDtos(usersCaptor.capture());
        List<User> second_result = usersCaptor.getValue();

        mentorshipService.getMentors(ID_3);
        verify(userMapper, times(3)).toUsersDtos(usersCaptor.capture());
        List<User> third_result = usersCaptor.getValue();

        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(ID_1);
        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(ID_2);
        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(ID_3);

        assertArrayEquals(List.of(USER_2, USER_3).toArray(), first_result.toArray());
        assertArrayEquals(List.of(USER_1, USER_3).toArray(), second_result.toArray());
        assertArrayEquals(List.of(USER_1, USER_2).toArray(), third_result.toArray());

    }

    @Test
    public void testGetMenteesWithIncorrectMentorId() {
        long zeroMentorId = 0L;
        long negativeMentorId = -1L;
        when(mentorshipRepository.existsById(zeroMentorId)).thenReturn(false);
        when(mentorshipRepository.existsById(negativeMentorId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentees(zeroMentorId));
        assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentees(negativeMentorId));

        verify(mentorshipRepository, times(1)).existsById(zeroMentorId);
        verify(mentorshipRepository, times(1)).existsById(negativeMentorId);
    }


    @Test
    public void testGetMentorsWithIncorrectMenteeId() {
        long zeroMenteeId = 0L;
        long negativeMenteeId = -1L;
        when(mentorshipRepository.existsById(zeroMenteeId)).thenReturn(false);
        when(mentorshipRepository.existsById(negativeMenteeId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentors(zeroMenteeId));
        assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentors(negativeMenteeId));

        verify(mentorshipRepository, times(1)).existsById(zeroMenteeId);
        verify(mentorshipRepository, times(1)).existsById(negativeMenteeId);
    }



    @DisplayName("Delete mentor of mentee")
    @Test
    public void deleteMentorOfMentee_ShouldCallRepository() {
        long mentorId = 1L;
        long menteeId = 2L;
        when(mentorshipRepository.existsById(mentorId)).thenReturn(true);
        when(mentorshipRepository.existsById(menteeId)).thenReturn(true);
        mentorshipService.deleteMentorOfMentee(mentorId, menteeId);

        Mockito.verify(mentorshipRepository).deleteMentorOfMentee(mentorId, menteeId);
    }

    @DisplayName("Delete mentee of mentor when only one exists")
    @Test
    public void deleteMenteeOfMentor_ShouldCallRepository() {
        long mentorId = 1L;
        long menteeId = 2L;

        when(mentorshipRepository.existsById(mentorId)).thenReturn(true);
        when(mentorshipRepository.existsById(menteeId)).thenReturn(true);

        mentorshipService.deleteMenteeOfMentor(mentorId, menteeId);

        verify(mentorshipRepository).deleteMenteeOfMentor(mentorId, menteeId);
    }

    @DisplayName("Throw exception if mentor or mentee does not exist when deleting mentee of mentor")
    @Test
    public void deleteMenteeOfMentor_ShouldThrowException() {
        long mentorId = 1L;
        long menteeId = 2L;

        when(mentorshipRepository.existsById(mentorId)).thenReturn(true);
        when(mentorshipRepository.existsById(menteeId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                mentorshipService.deleteMenteeOfMentor(mentorId, menteeId)
        );
    }

}

