package school.faang.user_service.service.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Disabled
@SpringBootTest
class SubscriptionServiceImplTest {

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    private static final long FOLLOWEE_ID = 1L;

    private static final long ALEX_ID = 1L;
    private static final String ALEX_NAME = "Alex";
    private static final String ALEX_PHONE = "1234567890";
    private static final String ALEX_EMAIL = "alex@gmail.com";
    private static final int ALEX_EXPERIENCE = 2;

    private static final long SAM_ID = 2L;
    private static final String SAM_NAME = "Sam";
    private static final String SAM_PHONE = "1234567890";
    private static final String SAM_EMAIL = "sam@mail.ru";
    private static final int SAM_EXPERIENCE = 5;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(ALEX_ID)
                .username(ALEX_NAME)
                .phone(ALEX_PHONE)
                .email(ALEX_EMAIL)
                .experience(ALEX_EXPERIENCE)
                .contactPreference(null)
                .build();

        user2 = User.builder()
                .id(SAM_ID)
                .username(SAM_NAME)
                .phone(SAM_PHONE)
                .email(SAM_EMAIL)
                .experience(SAM_EXPERIENCE)
                .contactPreference(null)
                .build();

        when(subscriptionRepository.findByFolloweeId(FOLLOWEE_ID))
                .thenAnswer(inv -> Stream.of(user1, user2));
    }

    @Test
    @DisplayName("Фильтрация по имени: только Alex")
    void testGetFollowers_filtersByName() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("alex");

        List<UserDto> result = subscriptionService.getFollowers(FOLLOWEE_ID, filterDto);

        assertEquals(1, result.size());
        assertEquals(ALEX_NAME, result.get(0).getUsername());
        assertEquals(new UserDto(ALEX_ID, ALEX_NAME, ALEX_EMAIL, null), result.get(0));
    }

    @Test
    @DisplayName("Фильтрация по телефону: оба проходят")
    void testGetFollowers_filtersByPhone_bothMatch() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPhonePattern(ALEX_PHONE);

        List<UserDto> result = subscriptionService.getFollowers(FOLLOWEE_ID, filterDto);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Фильтрация по телефону: никто не проходит")
    void testGetFollowers_filtersByPhone_noneMatch() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPhonePattern("234567890");

        List<UserDto> result = subscriptionService.getFollowers(FOLLOWEE_ID, filterDto);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Фильтрация по телефону и опыту: только Sam")
    void testGetFollowers_filtersByPhoneAndExperience() {
        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setPhonePattern(SAM_PHONE);
        filterDto.setExperienceMin(ALEX_EXPERIENCE + 1);
        filterDto.setExperienceMax(SAM_EXPERIENCE);

        List<UserDto> result = subscriptionService.getFollowers(FOLLOWEE_ID, filterDto);

        assertEquals(1, result.size());
        assertEquals(SAM_NAME, result.get(0).getUsername());
        assertEquals(new UserDto(SAM_ID, SAM_NAME, SAM_EMAIL, null), result.get(0));
    }
}