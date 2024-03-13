package school.faang.user_service.service.controller.mentorshipTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.userMapper.UserMapper;
import school.faang.user_service.userService.UserService;




import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MentorshipService mentorshipService;
    @InjectMocks
    private UserService userService;

//    @Test
//    public void testGetMentees_Found() {
//        // ...
//        List<User> mentees = Arrays.asList(new User(1L ), new User(1L);
//        User user = new User(4L).withMentees(mentees);
//        when(userService.findUserById(anyLong())).thenReturn(user);
//        // Настройте заглушку UserMapper
//        List<UserDto> expectedDtos = Arrays.asList(new UserDto(1L), new UserDto(2L));
//        when(userMapper.toUserDto(any(User.class))).thenReturn(expectedDtos.get(0), expectedDtos.get(1));
//
//        // ...
//        List<UserDto> menteeDtos = mentorshipService.getMentees(1L);
//        // Проверьте результат
//        assertEquals(expectedDtos, menteeDtos);
//    }

//    @Test
//    public void testGetMentees_Empty() {
//        // Создайте тестового пользователя без подопечных
//        User user = new User("User");
//        when(userService.findUserById(anyLong())).thenReturn(user);
//
//        // Вызовите метод
//        List<UserDto> menteeDtos = userService.getMentees(1L);
//
//        // Проверьте результат
//        assertTrue(menteeDtos.isEmpty());
//    }
//
//    // Аналогичные тесты для методов getMentors, deleteMentee, deleteMentor
//    // ...
@Test
public void testDeleteMentee() {
    User mentor = User.builder()
            .id(2L)
            .build();
    User mentee = User.builder()
            .id(1L)
            .build();

    mentorshipService.deleteMentee(1,2);
    assertTrue(mentor.getMentees().isEmpty());
}
@Test
public void testDeleteMentor() {
    User mentor = User.builder()
            .id(2L)
            .build();
    User mentee = User.builder()
            .id(1L)
            .build();

    mentorshipService.deleteMentor(1,2);
    assertTrue(mentee.getMentors().isEmpty());
}
}
