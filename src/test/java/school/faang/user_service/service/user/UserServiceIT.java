package school.faang.user_service.service.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"/UserService/drop.sql", "/UserService/user_initial.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/UserService/drop.sql", executionPhase = AFTER_TEST_METHOD)
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
public class UserServiceIT {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3");

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.5"));

    @Container
    private static final RedisContainer REDIS_CONTAINER = new RedisContainer(
            DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @Test
    void testGetUser_Success() throws Exception {
        UserDto expectedUserDto = UserDto.builder()
                .id(1L)
                .username("JohnDoe")
                .country("Russia")
                .experience(3)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/users/{userId}", 1)
                                .header("x-user-id", 2)
                ).andExpect(status().isOk())
                .andReturn();

        UserDto actualUserDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(mvcResult);
        assertEquals(expectedUserDto.getId(), actualUserDto.getId());
        assertEquals(expectedUserDto.getUsername(), actualUserDto.getUsername());
        assertEquals(expectedUserDto.getExperience(), actualUserDto.getExperience());
        assertEquals(expectedUserDto.getCountry(), actualUserDto.getCountry());
    }

    @Test
    void testGetUser_UserNotFound() throws Exception {
        Long nonExistentUserId = 3L;
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/users/{userId}", nonExistentUserId)
                ).andExpect(status().is4xxClientError())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        String error = JsonPath.read(responseBody, "$.error");
        String errorMessage = JsonPath.read(responseBody, "$.message");

        assertEquals("Bad Request", error);
        assertEquals("User with this id not found: 3", errorMessage);
    }

    @Test
    void testGetUsers_Success() throws Exception {
        List<UserDto> userDtoList = List.of(
                UserDto.builder()
                        .id(1L)
                        .build(),
                UserDto.builder()
                        .id(2L)
                        .build()
        );

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/list")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(userDtoList))
                ).andExpect(status().isOk())
                .andReturn();

        List<UserDto> actualList = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<UserDto>>() {});

        assertEquals(userDtoList.size(), actualList.size());
        assertNotNull(actualList.get(0));
        assertNotNull(actualList.get(1));
        assertEquals(userDtoList.get(0).getId(), actualList.get(0).getId());
        assertEquals(userDtoList.get(1).getId(), actualList.get(1).getId());
    }

    @Test
    void testGetUsers_UserNotFound() throws Exception{
        Long nonExistentUserId = 3L;
        List<UserDto> userDtoList = List.of(
                UserDto.builder()
                        .id(nonExistentUserId)
                        .build()
        );

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/list")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(userDtoList))
                ).andExpect(status().is4xxClientError())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        String error = JsonPath.read(responseBody, "$.error");
        String errorMessage = JsonPath.read(responseBody, "$.message");

        assertEquals("Bad Request", error);
        assertEquals("Users with this ids not found", errorMessage);
    }
}

