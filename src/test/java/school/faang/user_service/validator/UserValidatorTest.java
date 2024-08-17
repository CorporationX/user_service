package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    @DisplayName("testing checkAllFollowersExist method with non appropriate value")
    public void testCheckAllFollowersExist() {
        List<Long> userIds = List.of(1L);
        when(userRepository.findAllById(userIds)).thenReturn(List.of());
        assertFalse(() -> userValidator.checkAllFollowersExist(userIds));
    }
}