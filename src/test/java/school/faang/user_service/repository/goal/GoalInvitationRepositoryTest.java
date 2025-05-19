package school.faang.user_service.repository.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GoalInvitationRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private GoalInvitationRepository goalInvitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    private Goal goal;

    @Test
    void testConnection() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @BeforeEach
    void setUp() {
        goal = Goal.builder()
                .title("title")
                .description("description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(GoalStatus.COMPLETED)
                .build();
    }

    /*Знаю, что тестировать репозитории без кастомных кверей отчасти бесполезное занятие.
    Это я скорее пробовал настройку Test Containers*/
    @Test
    void testSave() {
        Goal savedGoal = goalRepository.save(goal);
        User inviter = userRepository.findById(1L).get();
        User invited = userRepository.findById(2L).get();

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(savedGoal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        GoalInvitation savedGoalInvitation = goalInvitationRepository.save(goalInvitation);
        assertEquals(1L, savedGoalInvitation.getId());
    }
}