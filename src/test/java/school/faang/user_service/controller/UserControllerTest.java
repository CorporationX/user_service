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
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private UserController userController;


    @Test
    public void successDeactivationUserById() {
        long userId = 1L;
        userController.deactivateUserById(userId);
        Mockito.verify(userService).deactivationUserById(userId);
    }

    @Test
    public void testSearchUsersWhenValidFilter() {
        UserFilterDto validFilter = new UserFilterDto();
        validFilter.setNamePattern("John");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("JohnDoe");

        UserDto invalidUserDto = new UserDto();
        invalidUserDto.setId(2L);
        invalidUserDto.setUsername("Kate");

        List<UserDto> expectedUserDtoList = List.of(userDto, invalidUserDto);

        long actorId = 1L;

        when(userContext.getUserId()).thenReturn(actorId);
        when(userService.getUsers(any(UserFilterDto.class), eq(actorId))).thenReturn(expectedUserDtoList);

        List<UserDto> result = userController.searchUsers(validFilter);

        assertEquals(expectedUserDtoList.size(), result.size());
    }
}
