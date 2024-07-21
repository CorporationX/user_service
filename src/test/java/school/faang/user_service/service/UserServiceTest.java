package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(Extension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void getUserTest_whenException(){
        assertThrows(RuntimeException.class , service.getUser());
    }
}

