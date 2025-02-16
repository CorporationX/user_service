package school.faang.user_service.service.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.publisher.UserProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    UserProfileViewEventPublisher userProfileViewEventPublisher;
    @Mock
    UserRepository userRepository;
    @Mock
    UserContext userContext;
    @Spy
    UserMapperImpl userMapper;
    @InjectMocks
    private UsersServiceImpl usersService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Test get user")
    void testGetUser() {
        Long visitedUserId = 1L;
        Long visitorUserId = 2L;

        User user = User.builder().id(1L).username("testUser").build();
        Mockito.when(userRepository.findById(visitedUserId)).thenReturn(Optional.ofNullable(user));
        Mockito.when(userContext.getUserId()).thenReturn(visitorUserId);

        usersService.getUser(visitedUserId);

        Mockito.verify(userProfileViewEventPublisher, Mockito.times(1))
                .publish(Mockito.any());
    }
}