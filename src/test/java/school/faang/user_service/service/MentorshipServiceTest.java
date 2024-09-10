package school.faang.user_service.service;

import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    private static final long FIRST_ID = 1;
    private static final long SECOND_ID = 2;
    private static final long THIRD_ID = 3;
    final static User FIRST_USER = new User();
    final static User SECOND_USER = new User();
    final static User THIRD_USER = new User();

    @Mock
    private static MentorshipRepository mentorshipRepository;
    @InjectMocks
    private static MentorshipService mentorshipService;


    @Spy
    private UserMapper userMapper;

    @DisplayName("Each user is a mentor and mentee of each other")
    @BeforeAll
    public static void setUp() {
        FIRST_USER.setId(FIRST_ID);
        SECOND_USER.setId(SECOND_ID);
        THIRD_USER.setId(THIRD_ID);
    }

    @Captor
    private ArgumentCaptor<List<User>> usersCaptor;

    @Test
    public void testSuccessGetMentees() {
        when(mentorshipRepository.findMenteesByMentorId(FIRST_ID)).thenReturn(List.of(SECOND_USER, THIRD_USER));
        when(mentorshipRepository.findMenteesByMentorId(SECOND_ID)).thenReturn(List.of(FIRST_USER, THIRD_USER));
        when(mentorshipRepository.findMenteesByMentorId(THIRD_ID)).thenReturn(List.of(FIRST_USER, SECOND_USER));


        mentorshipService.getMentees(FIRST_ID);
        verify(userMapper, times(1)).toDTOs(usersCaptor.capture());
        List<User> FIRST_RESULT = usersCaptor.getValue();

        mentorshipService.getMentees(SECOND_ID);
        verify(userMapper, times(2)).toDTOs(usersCaptor.capture());
        List<User> SECOND_RESULT = usersCaptor.getValue();

        mentorshipService.getMentees(THIRD_ID);
        verify(userMapper, times(3)).toDTOs(usersCaptor.capture());
        List<User> THIRD_RESULT = usersCaptor.getValue();

        verify(mentorshipRepository, times(1)).findMenteesByMentorId(FIRST_ID);
        verify(mentorshipRepository, times(1)).findMenteesByMentorId(SECOND_ID);
        verify(mentorshipRepository, times(1)).findMenteesByMentorId(THIRD_ID);


        assertArrayEquals(List.of(SECOND_USER,THIRD_USER).toArray(), FIRST_RESULT.toArray());
        assertArrayEquals(List.of(FIRST_USER,THIRD_USER).toArray(), SECOND_RESULT.toArray());
        assertArrayEquals(List.of(FIRST_USER,SECOND_USER).toArray(), THIRD_RESULT.toArray());


    }


    @Test
    public void testSuccessGetMentors() {
        when(mentorshipRepository.findMentorsByMenteeId(FIRST_ID)).thenReturn(List.of(SECOND_USER, THIRD_USER));
        when(mentorshipRepository.findMentorsByMenteeId(SECOND_ID)).thenReturn(List.of(FIRST_USER, THIRD_USER));
        when(mentorshipRepository.findMentorsByMenteeId(THIRD_ID)).thenReturn(List.of(FIRST_USER, SECOND_USER));

        mentorshipService.getMentors(FIRST_ID);
        verify(userMapper, times(1)).toDTOs(usersCaptor.capture());
        List<User> FIRST_RESULT = usersCaptor.getValue();

        mentorshipService.getMentors(SECOND_ID);
        verify(userMapper, times(2)).toDTOs(usersCaptor.capture());
        List<User> SECOND_RESULT = usersCaptor.getValue();

        mentorshipService.getMentors(THIRD_ID);
        verify(userMapper, times(3)).toDTOs(usersCaptor.capture());
        List<User> THIRD_RESULT = usersCaptor.getValue();

        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(FIRST_ID);
        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(SECOND_ID);
        verify(mentorshipRepository, times(1)).findMentorsByMenteeId(THIRD_ID);


        assertArrayEquals(List.of(SECOND_USER,THIRD_USER).toArray(), FIRST_RESULT.toArray());
        assertArrayEquals(List.of(FIRST_USER,THIRD_USER).toArray(), SECOND_RESULT.toArray());
        assertArrayEquals(List.of(FIRST_USER,SECOND_USER).toArray(), THIRD_RESULT.toArray());


    }


}

