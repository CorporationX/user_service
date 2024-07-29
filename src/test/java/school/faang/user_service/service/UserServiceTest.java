package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("invoke findAll method and mapper method")
    void testToInvokeRepositoryAndMapperMethodsWhenGetUsersDtoByIds() {
        List<Long> ids = new ArrayList<>(List.of(1L, 2L));

        when(userRepository.findAllById(anyList())).thenReturn(new ArrayList<>());
        userService.getUsersDtoByIds(ids);

        verify(userRepository, times(1)).findAllById(anyList());
        verify(userMapper, times(1)).usersToUserDTOs(anyList());
    }
}
