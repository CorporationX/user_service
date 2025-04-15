package school.faang.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
    private static final String S3_DOCKER_IMAGE = "minio/minio:latest";
    private static final int S3_EXPOSED_PORT = 9000;
    private static final String S3_USER = "user";
    private static final String S3_PASSWORD = "password";

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Container
    private static final GenericContainer<?> S3_CONTAINER =
            new GenericContainer<>(DockerImageName.parse(S3_DOCKER_IMAGE))
                    .withExposedPorts(S3_EXPOSED_PORT)
                    .withEnv("MINIO_ROOT_USER", S3_USER)
                    .withEnv("MINIO_ROOT_PASSWORD", S3_PASSWORD)
                    .withCommand("server", "/data")
                    .withCreateContainerCmdModifier(
                            cmd ->
                                    cmd.withHostConfig(
                                            new HostConfig()
                                                    .withPortBindings(
                                                            new PortBinding(
                                                                    Ports.Binding.bindPort(
                                                                            S3_EXPOSED_PORT),
                                                                    new ExposedPort(
                                                                            S3_EXPOSED_PORT)))));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        registry.add("S3_PORT", S3_CONTAINER::getFirstMappedPort);
        registry.add("S3_ACCESS_KEY", () -> S3_USER);
        registry.add("S3_SECRET_KEY", () -> S3_PASSWORD);
    }
}
