package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserFilterDto userFilterDto;

    @InjectMocks
    private UserController userController;

    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        userCreateDto = UserCreateDto.builder()
                .username("Alex")
                .email("mail@mail.com")
                .phone("123456789")
                .password("qwerty")
                .countryId(4L)
                .build();
    }

    @Test
    void testGetPremiumUsers() {
        userController.getPremiumUsers(userFilterDto);

        verify(userService, times(1)).getPremiumUsers(userFilterDto);
    }

    @Test
    void testGetUserById() {
        userController.getUserById(anyLong());

        verify(userService, times(1)).getUserById(anyLong());
    }
    @Test
    void testCallCreateUserServiceWhenCreateUserIsInvoked() {
        userController.createUser(userCreateDto);

        verify(userService, times(1)).createUser(userCreateDto);
    }
}