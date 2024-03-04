package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.user.UserController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.SearchAppearanceEventPublisher;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserContext userContext;
    @Mock
    private SearchAppearanceEventPublisher eventPublisher;
    @InjectMocks
    private UserController userController;


    @Test
    public void successDeactivationUserById() {
        long userId = 1L;
        userController.deactivateUserById(userId);
        Mockito.verify(userService).deactivationUserById(userId);
    }

    @Test
    public void testSearchUsersWhenSuccessfulThenReturnListOfUserDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        List<UserDto> userDtoList = List.of(userDto);

        when(userService.getAllUser()).thenReturn(userDtoList);
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(eventPublisher).publish(anyString());

        List<UserDto> actual = userController.searchUsers();

        assertEquals(userDtoList, actual);

        verify(userService, times(1)).getAllUser();
        verify(userContext, times(1)).getUserId();
        verify(eventPublisher, times(1)).publish(anyString());
    }

}
