package school.faang.user_service.service.controller;

import integration.kafka.FakePremiumListener;
import integration.kafka.TestKafkaPublisher;
import integration.kafka.TestKafkaTopicsConfig;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.UserServiceApplication;
import school.faang.user_service.controller.PremiumController;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.enums.premium.PremiumType;
import school.faang.user_service.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(
        classes = {
                KafkaAutoConfiguration.class,
                UserServiceApplication.class,
                KafkaListenerEndpointRegistry.class
        }
)
@Import({
        TestKafkaTopicsConfig.class,
        FakePremiumListener.class,
        TestKafkaPublisher.class
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@Slf4j
public class PremiumControllerIT {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withExposedPorts(9093);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PremiumController premiumController;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private RestTemplate restTemplate;

    @DynamicPropertySource
    static void overrideKafkaProps(DynamicPropertyRegistry registry) {
        registry.add("TEST_KAFKA_BOOTSTRAP_SERVERS", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private FakePremiumListener fakePremiumListener;

    @Autowired
    private TestKafkaPublisher testKafkaPublisher;

    @BeforeEach
    void awaitListenerContainer() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
            ContainerTestUtils.waitForAssignment(container, kafka.getBootstrapServers().split(",").length);
        });

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                kafkaAdminClient().listTopics().names().get()
                        .containsAll(List.of(
                                "premium-payment-request-topic",
                                "premium-payment-response-topic"
                        ))
        );

        kafkaListenerEndpointRegistry.getListenerContainers()
                .forEach(container -> await()
                        .until(container::isRunning));

    }

    private AdminClient kafkaAdminClient() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        return AdminClient.create(config);
    }

    @SneakyThrows
    @Test
    public void testBuyPremium_success() {
        log.info("Kafka bootstrap server: " + kafka.getBootstrapServers());
        PremiumRequestDto premiumRequest = new PremiumRequestDto(PremiumType.ONE_MONTH,
                1L, CurrencyDto.USD, true);

        mockMvc.perform(post("/api/v1/premium")
                        .contentType("application/json")
                        .content(jsonUtils.serialize(premiumRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.premiumType", is("ONE_MONTH")))
                .andExpect(jsonPath("$.paymentStatus", is("SUCCESS")))
                .andExpect(jsonPath("$.startDate").isNotEmpty())
                .andExpect(jsonPath("$.endDate").isNotEmpty());
    }
}
