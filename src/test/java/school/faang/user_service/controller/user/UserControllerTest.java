package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private MockMvc mockMvc;
    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUserByIdExistsTest() throws Exception {
        ResultActions result = mockMvc.perform(
                        get("/users/1"))
                .andExpect(status().isOk());

        System.out.println(result.andReturn().getResponse().getContentAsString());

        ObjectMapper mapper = new ObjectMapper();
        UserDto user = mapper.readValue(result.andReturn().getResponse().getContentAsString(), UserDto.class);
        assertEquals(1, user.getId());
    }

    @Test
    void getUserByIdNotExistsTest() throws Exception {
        mockMvc.perform(
                        get("/users/32"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersByIdsTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<Long> ids = List.of(1L, 2L, 3L, 4L);
        String json = mapper.writeValueAsString(ids);

        ResultActions result = mockMvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(json)
        ).andExpect(status().isOk());


        List<UserDto> users = mapper.readValue(result.andReturn().getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));

        assertEquals(4, users.size());
    }

    @Test
    public void deactivateUser() throws Exception {
        long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/deactivate", userId)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }
}