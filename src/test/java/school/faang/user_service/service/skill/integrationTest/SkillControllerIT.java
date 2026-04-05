package school.faang.user_service.service.skill.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.annotation.IT;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IT
public class SkillControllerIT {
/*
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private SkillOfferRepository skillOfferRepository;
    @Autowired
    private RecommendationRepository recommendationRepository;

    private long savedSkillId;
    private long savedReceiverId;

    @Container
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void setPostgresSqlContainer(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0")).withKraft();


    @DynamicPropertySource
    static void setKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.producer.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @BeforeEach
    public void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE skill_offer RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE recommendation RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE skill RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE country RESTART IDENTITY CASCADE");
    }

    @Test
    public void createSkill_whenValidData_thenReturnsOk() throws Exception {
        mockMvc.perform(post("/skill/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SkillDto.builder().title("neTest").build())))
                .andExpect(status().isOk());
    }

    @Test
    public void createSkill_whenTitleTooShort_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/skill/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SkillDto.builder().title("123").build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createSkill_whenTitleIsEmpty_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/skill/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SkillDto.builder().title("").build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserSkills_whenUserExists_thenReturnsOk() throws Exception {
        long userId = creatUserList(1).get(0).getId();

        mockMvc.perform(get("/skill/{userId}/get-user-skill", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void getOfferedSkills_whenUserExists_thenReturnsOk() throws Exception {
        long userId = creatUserList(1).get(0).getId();

        mockMvc.perform(get("/skill/{userId}/get-offered-skills", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void acquireSkillFromOffers_whenValidData_thenReturnsOk() throws Exception {
        prepareTestData();

        mockMvc.perform(put("/skill/{skillId}/{userId}/acquire-skill-from-offers", savedSkillId, savedReceiverId))
                .andExpect(status().isOk());
    }

    private void prepareTestData() {
        Skill skill = Skill.builder().title("testSkill").build();
        savedSkillId = skillRepository.save(skill).getId();
        List<User> users = creatUserList(4);
        savedReceiverId = users.get(1).getId();

        List<Long> recommendations = new ArrayList<>();
        for (int i = 1; i < users.size(); i++) {
            recommendations.add(recommendationRepository.create(users.get(i).getId(), savedReceiverId, "rec" + i));
        }

        for (Long recommendationId : recommendations) {
            skillOfferRepository.create(savedSkillId, recommendationId);
        }
    }

    private @NotNull List<User> creatUserList(int numberUsers) {
        Country russia = Country.builder().title("Russia").build();
        Country country = countryRepository.save(russia);

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= numberUsers; i++) {
            users.add(User.builder()
                    .username("User" + i)
                    .email("user" + i + "@example.com")
                    .phone("7000000000" + i)
                    .password("password")
                    .active(true)
                    .country(country)
                    .build());
        }
        return users = userRepository.saveAll(users);
    }

 */
}