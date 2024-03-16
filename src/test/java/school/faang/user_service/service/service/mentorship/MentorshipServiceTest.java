package school.faang.user_service.service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.entity.User;
import school.faang.user_service.userDto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {


    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;


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
    void init() {
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
        Mockito.when(userMapper.toUserDtoList(userList)).thenReturn(userListDto);

        assertEquals(userListDto, mentorshipService.getMentors(userId));
        Mockito.verify(userService).getUserById(userId);
        Mockito.verify(userMapper).toUserDtoList(userList);
    }


    @Test
    void testDeleteMentee() {
        Mockito.when(userService.getUserById(menteeId)).thenReturn(user);
        Mockito.when(userService.getUserById(mentorId)).thenReturn(user);
        assertThrows(IllegalArgumentException.class, () -> mentorshipService.deleteMentee(menteeId, mentorId));
        Mockito.verify(userService, times(2)).getUserById(anyLong());
    }

    @Test
    void testDeleteMentor() {
        Mockito.when(userService.getUserById(menteeId)).thenReturn(user);
        Mockito.when(userService.getUserById(mentorId)).thenReturn(user);
        assertThrows(IllegalArgumentException.class, () -> mentorshipService.deleteMentor(menteeId, mentorId));
        Mockito.verify(userService, times(2)).getUserById(anyLong());
    }

}
