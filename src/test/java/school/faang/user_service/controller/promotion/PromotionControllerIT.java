package school.faang.user_service.controller.promotion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.configuration.TestContainersConfig;
import school.faang.user_service.dto.error.UserServiceErrorResponseDto;
import school.faang.user_service.dto.promotion.PromotionEventCreateRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.dto.promotion.PromotionUserCreateRequestDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.event.EventRedisModel;
import school.faang.user_service.model.promotion.PromotionRedisModel;
import school.faang.user_service.model.user.UserRedisModel;
import school.faang.user_service.repository.event.EventRedisRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.repository.user.UserRedisRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.promotion.PromotionTariffService;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        "/sql/insert_promotion.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "/sql/delete_promotion.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
public class PromotionControllerIT extends TestContainersConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionRedisRepository promotionRedisRepository;
    @Autowired
    private UserRedisRepository userRedisRepository;
    @Autowired
    private EventRedisRepository eventRedisRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PromotionTariffService promotionTariffService;

    @AfterEach
    public void cleanRedis() {
        promotionRedisRepository.deleteAll();
        eventRedisRepository.deleteAll();
    }

    @Test
    @DisplayName("Успешное создание промоушена для события с записью в БД и Redis")
    public void createEventPromotion_successfully() throws Exception {
        long eventId = 202L;
        long userId = 9L;
        long tariffId = 101L;

        PromotionEventCreateRequestDto requestDto = PromotionEventCreateRequestDto.builder()
                .eventId(eventId)
                .tariffId(tariffId)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/promotions/event")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isCreated())
                .andReturn();

        PromotionResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                PromotionResponseDto.class
        );

        assertThat(responseDto)
                .extracting(
                        PromotionResponseDto::getEventId,
                        PromotionResponseDto::getTariffId,
                        PromotionResponseDto::getType,
                        PromotionResponseDto::getStatus
                )
                .containsExactly(
                        eventId,
                        tariffId,
                        PromotionType.EVENT,
                        PromotionStatus.ACTIVE
                );
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getUserId()).isNull();


        checkPromotionEventDbAndRedis(responseDto.getId(), eventId, tariffId);
    }

    @Test
    @DisplayName("Создание промоушена для события, которого нет в БД")
    public void createEventPromotion_eventNotFound() throws Exception {
        long eventId = 2000L;
        long userId = 9L;
        long tariffId = 101L;

        long beforeCount = promotionRepository.count();

        PromotionEventCreateRequestDto requestDto = PromotionEventCreateRequestDto.builder()
                .eventId(eventId)
                .tariffId(tariffId)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/promotions/event")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isNotFound())
                .andReturn();

        UserServiceErrorResponseDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserServiceErrorResponseDto.class);

        assertThat(responseDto.getCodeResponse()).isEqualTo(HttpStatus.NOT_FOUND.value());

        long afterCount = promotionRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);

        Iterable<PromotionRedisModel> redisPromotions = promotionRedisRepository.findAll();
        assertThat(redisPromotions).isEmpty();
    }

    @Test
    @DisplayName("Создание промоушена для события с тарифом, которого нет в БД")
    public void createEventPromotion_tariffNotFound() throws Exception {
        long eventId = 202L;
        long userId = 9L;
        long tariffId = 1001L;

        long beforeCount = promotionRepository.count();

        PromotionEventCreateRequestDto requestDto = PromotionEventCreateRequestDto.builder()
                .eventId(eventId)
                .tariffId(tariffId)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/promotions/event")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isNotFound())
                .andReturn();

        UserServiceErrorResponseDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserServiceErrorResponseDto.class);

        assertThat(responseDto.getCodeResponse()).isEqualTo(HttpStatus.NOT_FOUND.value());

        long afterCount = promotionRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);

        Iterable<PromotionRedisModel> redisPromotions = promotionRedisRepository.findAll();
        assertThat(redisPromotions).isEmpty();
    }

    @Test
    @DisplayName("Создание промоушена для события, у которого уже есть активных промоушен")
    public void createEventPromotion_existsActivePromotion() throws Exception {
        long eventId = 201L;
        long userId = 9L;
        long tariffId = 101L;

        long beforeCount = promotionRepository.count();

        PromotionEventCreateRequestDto requestDto = PromotionEventCreateRequestDto.builder()
                .eventId(eventId)
                .tariffId(tariffId)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/promotions/event")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isConflict())
                .andReturn();

        UserServiceErrorResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserServiceErrorResponseDto.class
        );

        assertThat(responseDto.getCodeResponse()).isEqualTo(HttpStatus.CONFLICT.value());

        long afterCount = promotionRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);

        Iterable<PromotionRedisModel> redisPromotions = promotionRedisRepository.findAll();
        assertThat(redisPromotions).isEmpty();
    }

    @Test
    @DisplayName("Успешное создание промоушена для пользователя с записью в БД и Redis")
    public void createUserPromotion_successfully() throws Exception {
        long userId = 2L;
        long tariffId = 101L;

        PromotionUserCreateRequestDto requestDto = PromotionUserCreateRequestDto.builder()
                .userId(userId)
                .tariffId(tariffId)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/promotions/user")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isCreated())
                .andReturn();

        PromotionResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                PromotionResponseDto.class
        );

        assertThat(responseDto)
                .extracting(
                        PromotionResponseDto::getUserId,
                        PromotionResponseDto::getTariffId,
                        PromotionResponseDto::getType,
                        PromotionResponseDto::getStatus
                )
                .containsExactly(
                        userId,
                        tariffId,
                        PromotionType.USER,
                        PromotionStatus.ACTIVE
                );
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getEventId()).isNull();


        checkUserInDbAndRedis(responseDto.getId(), userId, tariffId);
    }

    @Test
    @DisplayName("Создание промоушена для пользователя, которого нет в БД")
    public void createUserPromotion_userNotFound() throws Exception {
        long userId = 2000L;
        long tariffId = 101L;

        long beforeCount = promotionRepository.count();

        PromotionUserCreateRequestDto requestDto = PromotionUserCreateRequestDto.builder()
                .userId(userId)
                .tariffId(tariffId)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/promotions/user")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isNotFound())
                .andReturn();

        UserServiceErrorResponseDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserServiceErrorResponseDto.class);

        assertThat(responseDto.getCodeResponse()).isEqualTo(HttpStatus.NOT_FOUND.value());

        long afterCount = promotionRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);

        Iterable<PromotionRedisModel> redisPromotions = promotionRedisRepository.findAll();
        assertThat(redisPromotions).isEmpty();
    }

    @Test
    @DisplayName("Создание промоушена для пользователя с тарифом, которого нет в БД")
    public void createUserPromotion_tariffNotFound() throws Exception {
        long userId = 2L;
        long tariffId = 1010L;

        long beforeCount = promotionRepository.count();

        PromotionUserCreateRequestDto requestDto = PromotionUserCreateRequestDto.builder()
                .userId(userId)
                .tariffId(tariffId)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/promotions/user")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isNotFound())
                .andReturn();

        UserServiceErrorResponseDto responseDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserServiceErrorResponseDto.class);

        assertThat(responseDto.getCodeResponse()).isEqualTo(HttpStatus.NOT_FOUND.value());

        long afterCount = promotionRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);

        Iterable<PromotionRedisModel> redisPromotions = promotionRedisRepository.findAll();
        assertThat(redisPromotions).isEmpty();
    }

    @Test
    @DisplayName("Создание промоушена для пользователя, у которого уже есть активных промоушен")
    public void createUserPromotion_existsActivePromotion() throws Exception {
        long userId = 9L;
        long tariffId = 101L;

        long beforeCount = promotionRepository.count();

        PromotionUserCreateRequestDto requestDto = PromotionUserCreateRequestDto.builder()
                .userId(userId)
                .tariffId(tariffId)
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/promotions/user")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isConflict())
                .andReturn();

        UserServiceErrorResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserServiceErrorResponseDto.class
        );

        assertThat(responseDto.getCodeResponse()).isEqualTo(HttpStatus.CONFLICT.value());

        long afterCount = promotionRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);

        Iterable<PromotionRedisModel> redisPromotions = promotionRedisRepository.findAll();
        assertThat(redisPromotions).isEmpty();
    }

    private void checkPromotionEventDbAndRedis(long promotionId, long eventId, long tariffId) {
        Promotion promotionDB = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new AssertionError("Promotion not found in DB"));

        assertThat(promotionDB.getEvent().getId()).isEqualTo(eventId);
        assertThat(promotionDB.getUser()).isNull();
        assertThat(promotionDB.getTariff().getId()).isEqualTo(tariffId);
        assertThat(promotionDB.getStatus()).isEqualTo(PromotionStatus.ACTIVE);
        assertThat(promotionDB.getType()).isEqualTo(PromotionType.EVENT);

        String promotionKey = RedisKeyUtil.getSmallKeyById(promotionId);
        String eventKey = RedisKeyUtil.getSmallKeyById(eventId);

        PromotionRedisModel promotionRedis = promotionRedisRepository.findById(promotionKey)
                .orElseThrow(() -> new AssertionError("Promotion not found in Redis"));
        assertThat(promotionRedis.getEventId()).isEqualTo(eventId);
        assertThat(promotionRedis.getUserId()).isNull();
        assertThat(promotionRedis.getTariffId()).isEqualTo(tariffId);

        EventRedisModel eventRedis = eventRedisRepository.findById(eventKey)
                .orElseThrow(() -> new AssertionError("Event not found in Redis"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AssertionError("Event not found in DB"));
        assertThat(eventRedis)
                .extracting(EventRedisModel::getTitle,
                        EventRedisModel::getDescription,
                        EventRedisModel::getStartDate,
                        EventRedisModel::getEndDate,
                        EventRedisModel::getLocation,
                        EventRedisModel::getMaxAttendees,
                        EventRedisModel::getType,
                        EventRedisModel::getStatus
                )
                .containsExactly(
                        event.getTitle(),
                        event.getDescription(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getLocation(),
                        event.getMaxAttendees(),
                        event.getType(),
                        event.getStatus()
                );
    }

    private void checkUserInDbAndRedis(long promotionId, long userId, long tariffId) {
        Promotion promotionDB = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new AssertionError("Promotion not found in DB"));

        assertThat(promotionDB.getUser().getId()).isEqualTo(userId);
        assertThat(promotionDB.getEvent()).isNull();
        assertThat(promotionDB.getTariff().getId()).isEqualTo(tariffId);
        assertThat(promotionDB.getStatus()).isEqualTo(PromotionStatus.ACTIVE);
        assertThat(promotionDB.getType()).isEqualTo(PromotionType.USER);

        String promotionKey = RedisKeyUtil.getSmallKeyById(promotionId);
        String userKey = RedisKeyUtil.getSmallKeyById(userId);

        PromotionRedisModel promotionRedis = promotionRedisRepository.findById(promotionKey)
                .orElseThrow(() -> new AssertionError("Promotion not found in Redis"));
        assertThat(promotionRedis.getUserId()).isEqualTo(userId);
        assertThat(promotionRedis.getEventId()).isNull();
        assertThat(promotionRedis.getTariffId()).isEqualTo(tariffId);

        UserRedisModel userRedis = userRedisRepository.findById(userKey)
                .orElseThrow(() -> new AssertionError("User not found in Redis"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AssertionError("User not found in DB"));
        assertThat(userRedis)
                .extracting(UserRedisModel::getUsername,
                        UserRedisModel::getEmail,
                        UserRedisModel::getPhone,
                        UserRedisModel::getCity,
                        UserRedisModel::getAboutMe,
                        UserRedisModel::getExperience
                )
                .containsExactly(
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getCity(),
                        user.getAboutMe(),
                        user.getExperience()
                );
    }
}
