package school.faang.user_service.integration.app;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.controller.mentorship_request.MentorshipRequestController;
import school.faang.user_service.integration.CommonIntegrationTest;
import school.faang.user_service.service.event.EventServiceImpl;
import school.faang.user_service.service.goal.GoalServiceImpl;
import school.faang.user_service.service.mentorship_request.MentorshipRequestService;
import school.faang.user_service.service.recomendation.RecommendationRequestService;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class ApplicationContextTest extends CommonIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(POSTGRES_CONTAINER_NAME);

    @Container
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(DockerImageName.parse(REDIS_CONTAINER_NAME));

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", POSTGRESQL_CONTAINER::getDriverClassName);
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getFirstMappedPort().toString());
    }

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    public void testEventServiceBeanExists() {
        EventServiceImpl bean = context.getBean(EventServiceImpl.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testGoalServiceBeanExists() {
        GoalServiceImpl bean = context.getBean(GoalServiceImpl.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testMentorshipRequestServiceBeanExists() {
        MentorshipRequestService bean = context.getBean(MentorshipRequestService.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testMentorshipRequestControllerBeanExists() {
        MentorshipRequestController bean = context.getBean(MentorshipRequestController.class);
        assertThat(bean).isNotNull();
    }

    @Test
    public void testRecommendationRequestServiceBeanExists() {
        RecommendationRequestService bean = context.getBean(RecommendationRequestService.class);
        assertThat(bean).isNotNull();
    }
}
