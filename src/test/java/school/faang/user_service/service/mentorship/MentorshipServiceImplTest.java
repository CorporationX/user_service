package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceImplTest {

    @Mock  // Фальшивый репозиторий (не ходит в БД)
    private MentorshipRepository mentorshipRepository;

    @Spy  // Реальный маппер (проверяем его логику)
    private UserMapperImpl userMapper;

    @InjectMocks  // Главный герой теста (тестируем его)
    private MentorshipServiceImpl mentorshipService;

    @Test
    void testAddMentorship_WhenValidIds_ThenSuccess() {}

    @Test
    void testAddMentorship_WhenAlreadyExists_ThenThrowsException() {}

    @Test
    void testGetMentees_WhenUserHasNoMentees_ThenReturnsEmptyList() {
        // ==================== ARRANGE (Подготовка тестовых данных) ====================

        long userId = 1L;  // Входной параметр

        User user = new User();  // Создаём пользователя (у него нет менти)
        user.setId(userId);

        // Настраиваем мок: "Когда вызовут getByIdOrThrow(1L), верни этого user"
        when(mentorshipRepository.getByIdOrThrow(userId))
                .thenReturn(user);

        // ==================== ACT (Вызываем тестируемый метод) ====================

        List<UserDto> result = mentorshipService.getMentees(userId);
        // Mockito перехватит вызов репозитория и вернёт наш user
        // Метод увидит, что у user нет менти → вернёт пустой список

        // ==================== ASSERT (Проверяем результат) ====================

        assertTrue(result.isEmpty());  // Ожидаем пустой список
        // Если result НЕ пуст → тест упадёт с ошибкой

        // Дополнительная проверка: репозиторий вызвали ровно 1 раз
        verify(mentorshipRepository, times(1)).getByIdOrThrow(userId);
    }

    @Test
    void testGetMentees_WhenUserHasMentees_ThenReturnUserDtos() {
        long userId = 1L;
        long menteeId = 2L;

        User user = new User();
        user.setId(userId);

        User mentee = new User();
        mentee.setId(menteeId);

        user.getMentees().add(mentee);

        when(mentorshipRepository.getByIdOrThrow(userId)).thenReturn(user);

        List<UserDto> result = mentorshipService.getMentees(userId);

        assertEquals(1, result.size());
        assertEquals(menteeId, result.get(0).id());

        verify(mentorshipRepository, times(1)).getByIdOrThrow(userId);
    }
}


