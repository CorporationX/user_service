package school.faang.user_service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.integration.factory.GoalFactory;
import school.faang.user_service.integration.factory.UserFactory;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.io.UnsupportedEncodingException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static school.faang.user_service.integration.factory.CountryFactory.buildDefaultCountry;

@Sql(scripts = "/truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest
public class CommonIntegrationTest {
    protected static final String POSTGRES_CONTAINER_NAME = "postgres:13.3";
    protected static final String REDIS_CONTAINER_NAME = "redis/redis-stack:latest";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected CountryRepository countryRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected GoalRepository goalRepository;

    protected Country saveDefaultCountry() {
        Country country = buildDefaultCountry();
        return countryRepository.save(country);
    }

    protected User saveDefaultUser() {
        Country country = saveDefaultCountry();
        User user = UserFactory.buildDefaultUser(country);
        return userRepository.save(user);
    }

    protected GoalResponseDto createDefaultGoal() throws Exception {
        /* Create User */
        User savedUser = saveDefaultUser();

        /* Create Goal before updating */
        CreateGoalDto createGoalDto = GoalFactory.buildDefaultCreateGoalDto(savedUser.getId());
        MvcResult result = mockMvc.perform(post("/goals")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createGoalDto)))
                .andExpect(status().isCreated())
                .andReturn();
        return parseResponseResult(result, GoalResponseDto.class);
    }

    protected <R> R parseResponseResult(MvcResult responseResult, Class<R> dto) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = responseResult.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, dto);
    }
}
