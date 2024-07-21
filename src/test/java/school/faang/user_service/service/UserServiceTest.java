package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
        @Mock
        private UserRepository repository;

        @Mock
        private UserMapper mapper;

        @InjectMocks
        private UserService service;

        @Test
        void getUserTest_whenException(){
            Long userId = null;
                assertThrows(RuntimeException.class ,() -> service.getUser(userId));
        }

        @Test
        void getUserTest(){
                Long userId = 1l;

        }
}
