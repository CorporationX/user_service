package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController controller;

    @Mock
    private UserService service;

    private Long userId = 1L;
    private List<Long> idsList = new ArrayList<>();

    @Test
    public void getUserTest() {
        controller.getUser(userId);
        Mockito.verify(service, Mockito.atLeastOnce()).getUser(userId);
    }

    @Test
    public void getUsersByIdsTest() {
        controller.getUsersByIds(idsList);
        Mockito.verify(service, Mockito.atLeastOnce()).getUsersByIds(idsList);
    }
}
