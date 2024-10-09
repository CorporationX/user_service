package school.faang.user_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ResourceUtils;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static school.faang.user_service.factory.CountryFactory.buildDefaultCountry;
import static school.faang.user_service.factory.GoalFactory.buildDefaultCreateGoalDto;
import static school.faang.user_service.factory.UserFactory.buildDefaultUser;

@AutoConfigureMockMvc
@ActiveProfiles("integration-tests")
@SpringBootTest
public abstract class CommonIntegrationTest {
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String truncateTablesSql;

    @PostConstruct
    public void init() throws IOException {
        File sqlFile = ResourceUtils.getFile("classpath:truncate-tables.sql");
        truncateTablesSql = new String(Files.readAllBytes(Paths.get(sqlFile.toURI())));
    }

    @AfterEach
    public void afterEach() {
        jdbcTemplate.execute(truncateTablesSql);
    }

    protected Country saveDefaultCountry() {
        Country country = buildDefaultCountry();
        return countryRepository.save(country);
    }

    protected User saveDefaultUser() {
        Country country = saveDefaultCountry();
        User user = buildDefaultUser(country);
        return userRepository.save(user);
    }

    protected GoalResponseDto createDefaultGoal() throws Exception {
        /* Create User */
        User savedUser = saveDefaultUser();

        /* Create Goal before updating */
        CreateGoalDto createGoalDto = buildDefaultCreateGoalDto(savedUser.getId());
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
