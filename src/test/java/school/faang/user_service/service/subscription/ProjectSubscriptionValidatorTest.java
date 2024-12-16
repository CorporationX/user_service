package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.service.ProjectSubscriptionValidator;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectSubscriptionValidatorTest {

    @Mock
    private ProjectSubscriptionRepository repository;

    @InjectMocks
    private ProjectSubscriptionValidator validator;

    @Test
    @DisplayName("Тест: возвращает true, если пользователь уже подписан на проект")
    void testIsAlreadySubscribedTrue() {
        when(repository.existsByFollowerIdAndProjectId(anyLong(), anyLong())).thenReturn(true);

        assertTrue(validator.isAlreadySubscribed(1L, 1L));
    }

    @Test
    @DisplayName("Тест: возвращает false, если пользователь не подписан на проект")
    void testIsAlreadySubscribedFalse() {
        when(repository.existsByFollowerIdAndProjectId(anyLong(), anyLong())).thenReturn(false);

        assertFalse(validator.isAlreadySubscribed(1L, 1L));
    }
}
