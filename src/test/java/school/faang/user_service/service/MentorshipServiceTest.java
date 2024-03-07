package school.faang.user_service.service;


import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;


    @Test
    public void getMenteesTest() {

        User user = new User();
        List<User> mentees = List.of(
                new User(),
                new User(),
                new User()
        );

        user.setMentees(mentees);
        user.setId(18998);
        long id = 898;


        when(userRepository.findById(id))
                .thenReturn(Optional.ofNullable(user));
        mentorshipService.getMentees(id);
        Mockito.verify(userRepository, times(1)).findById(Mockito.anyLong());

        Mockito.verify(userMapper, times(3)).toDto(Mockito.any());


    }

    @Test
    public void NoSuchUser() {
        Assert.assertThrows(IllegalArgumentException.class, () -> mentorshipService.getMentees(0));
    }


}