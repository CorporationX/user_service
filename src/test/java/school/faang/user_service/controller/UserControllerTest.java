package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    private long USER_ID = 1L;

    @Test
    void testGetUsersByIdsWithEmptyList() {
        List<UserDto> actualList = userController.getUsersByIds(new ArrayList<>());
        assertEquals(new ArrayList<>(), actualList);
    }

    @Test
    void testGetUsersByIds() {
        userController.getUsersByIds(List.of(USER_ID));
        verify(userService).getUsersByIds(List.of(USER_ID));

    void testGetUser() {
        userController.getUser(USER_ID);
        verify(userService).getUser(USER_ID);
    }
}
