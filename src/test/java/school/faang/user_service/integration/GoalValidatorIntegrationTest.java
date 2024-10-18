package school.faang.user_service.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class GoalValidatorIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalValidator goalValidator;

    @Container
    static  PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testDB")
            .withUsername("test")
            .withPassword("test");

    @Test
    @DisplayName("Проверка на количество максимальный активных целей у " +
            "пользователя не должно быть более 3 целей включительно." +
            "Версия PostgreSQL стоит postgres:latest")
    public void givenUserWithActiveGoals_whenValidateCreationGoal_thenThrowDataValidationException() {
        Goal goal1 = new Goal();
        goal1.setTitle("test1");
        goal1.setDescription("test1");
        goal1.setStatus(GoalStatus.ACTIVE);
        User user = userRepository.findById(1L).get();
        goal1.setUsers(List.of(user));

        Goal goal2 = new Goal();
        goal2.setTitle("test2");
        goal2.setDescription("test2");
        goal2.setUsers(List.of(user));
        goal2.setStatus(GoalStatus.ACTIVE);

        Goal goal3 = new Goal();
        goal3.setTitle("test2");
        goal3.setDescription("test2");
        goal3.setUsers(List.of(user));
        goal3.setStatus(GoalStatus.ACTIVE);

        goalRepository.save(goal1);
        goalRepository.save(goal2);
        goalRepository.save(goal3);

        assertThrows(DataValidationException.class, () -> goalValidator.validateCreationGoal(user.getId()));
    }
}
