package school.faang.user_service.scheduler;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration") // Marker to exclude this test from unit tests
@TestPropertySource(locations = "classpath:application-test.yaml")
@Testcontainers
@SpringBootTest
public class EventSchedulerTest {

    @Container
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:13.3")
            .withDatabaseName("user_service_test")
            .withUsername("sa")
            .withPassword("sa");

    @Container
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(
            DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    private static final int SECONDS_TO_WAIT = 10;
    @DynamicPropertySource
    private static void setDatasourceProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        properties.add("spring.datasource.username", postgreSQLContainer::getUsername);
        properties.add("spring.datasource.password", postgreSQLContainer::getPassword);
        properties.add("event.removal.cron", () -> String.format("*/%d * * * * *", SECONDS_TO_WAIT));

        properties.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        properties.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @Autowired
    private EventScheduler scheduler;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testClearEvents() {

        Country country = Country.builder()
                .title("Test country")
                .build();
        country = countryRepository.save(country);

        User user = User.builder()
                .username("test")
                .email("test@email.com")
                .password("test")
                .active(true)
                .country(Country.builder().id(country.getId()).build())
                .build();
        user = userRepository.save(user);

        Event event1 = Event.builder()
                .title("Test event1")
                .description("Test description1")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().minusDays(1).plusHours(1))
                .type(EventType.MEETING)
                .status(EventStatus.PLANNED)
                .location("Test location1")
                .maxAttendees(10)
                .owner(User.builder().id(user.getId()).build())
                .build();
        Event event2 = Event.builder()
                .title("Test event2")
                .description("Test description2")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().minusDays(1).plusHours(1))
                .type(EventType.MEETING)
                .status(EventStatus.PLANNED)
                .location("Test location2")
                .maxAttendees(10)
                .owner(User.builder().id(user.getId()).build())
                .build();

        List<Event> events = List.of(event1, event2);
        eventRepository.saveAll(events);

        await()
                .atMost(SECONDS_TO_WAIT + 5, TimeUnit.SECONDS)
                .untilAsserted(() -> events.forEach(e -> assertTrue(eventRepository.findById(e.getId()).isEmpty())));
    }
}
