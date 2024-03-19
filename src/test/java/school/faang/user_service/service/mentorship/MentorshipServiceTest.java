package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.mentorship.MentorshipValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MentorshipValidator mentorshipValidator;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    MentorshipService mentorshipService;

    private User mentor;
    private User mentee;
    private Goal goal;
    private Long userId;
    private Long user1 = 3L;
    private Long user2 = 2L;
    private Long menteeId = 1L;
    private Long mentorId = 2L;
    private List<User> userList = new ArrayList<>();
    private List<UserDto> userListDto = new ArrayList<>();

    private User user;
    private User user4;

    @BeforeEach
    void setUp() {
        goal = Goal.builder()
                .id(3L)
                .build();
        mentor = User.builder()
                .id(1L)
                .build();
        mentee = User.builder()
                .id(2L)
                .build();
        goal.setMentor(mentor);
        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));
        mentee.setGoals(new ArrayList<>(List.of(goal)));
        userId = 1L;
        user4 = User.builder()
                .id(2L)
                .build();
        user = User.builder()
                .id(1L)
                .build();
        userList.add(User.builder().id(user1).build());
        userList.add(User.builder().id(user2).build());

        UserDto userDto1 = UserDto.builder().id(user1).build();
        UserDto userDto2 = UserDto.builder().id(user2).build();
        userListDto.add(userDto1);
        userListDto.add(userDto2);
    }

    @Test
    void deleteMentorForAllHisMentees_MentorDeletedMenteesUpdated_ThenMenteesSavedToDb() {
        mentorshipService.deleteMentorForAllHisMentees(mentor.getId(), List.of(mentee));

        assertAll(
                () -> verify(mentorshipRepository, times(1)).saveAll(List.of(mentee)),
                () -> assertEquals(Collections.emptyList(), mentee.getMentors()),
                () -> assertEquals(mentee.getGoals().get(0).getMentor(), mentee)
        );
    }

    @Test
    void testGetMentees() {
        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        user.setMentees(Collections.emptyList());

        assertEquals(Collections.emptyList(), mentorshipService.getMentees(userId));
        Mockito.verify(userService).getUserById(userId);
    }

    @Test
    void testGetMentors() {
        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        user.setMentors(Collections.emptyList());

        assertEquals(Collections.emptyList(), mentorshipService.getMentors(userId));
        Mockito.verify(userService).getUserById(userId);
    }

    @Test
    void shouldReturnMentorsAsUserDtoList() {
        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        user.setMentors(userList);
        Mockito.when(userMapper.toDto(userList)).thenReturn(userListDto);

        assertEquals(userListDto, mentorshipService.getMentors(userId));
        Mockito.verify(userService).getUserById(userId);
        Mockito.verify(userMapper).toDto(userList);
    }

    @Test
    void testDeleteMentee() {
        Mockito.when(userService.getUserById(menteeId)).thenReturn(mentee);
        Mockito.when(userService.getUserById(mentorId)).thenReturn(mentor);

        mentorshipService.deleteMentee(menteeId,mentorId);

        verify(userService, times(2)).getUserById(anyLong());
        verify(mentorshipValidator, times(1)).validateMentorMenteeIds(menteeId,mentorId);
        verify(userRepository, times(1)).save(mentor);
        assertEquals(Collections.emptyList(),mentor.getMentees());
    }

    @Test
    void testDeleteMentor() {
        Mockito.when(userService.getUserById(menteeId)).thenReturn(mentee);
        Mockito.when(userService.getUserById(mentorId)).thenReturn(mentor);

        mentorshipService.deleteMentor(menteeId,mentorId);

        verify(userService, times(2)).getUserById(anyLong());
        verify(mentorshipValidator, times(1)).validateMentorMenteeIds(menteeId,mentorId);
        verify(userRepository, times(1)).save(mentee);
        assertEquals(Collections.emptyList(),mentee.getMentors());
    }
}
