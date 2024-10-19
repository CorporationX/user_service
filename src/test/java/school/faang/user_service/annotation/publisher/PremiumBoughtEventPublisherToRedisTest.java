package school.faang.user_service.annotation.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.aspect.redis.PremiumBoughtEventPublisherToRedis;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.premium.PremiumFabric.getPremium;
import static school.faang.user_service.util.users.UserTestUtil.getUser;

@ExtendWith(MockitoExtension.class)
class PremiumBoughtEventPublisherToRedisTest {
    private static final long USER_ID = 1L;
    private static final long PREMIUM_ID = 1L;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final LocalDateTime END_DATE = START_DATE.plusDays(31);

    @InjectMocks
    PremiumBoughtEventPublisherToRedis premiumBoughtEventPublisherToRedis;

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given not User and List<User> return value and do nothing")
    void testAddToPublishNotInstanceDoNothing() {
        premiumBoughtEventPublisherToRedis.publish(new Object());
        Queue<PremiumBoughtEventDto> analyticEvents =
                (Queue<PremiumBoughtEventDto>) ReflectionTestUtils.getField(premiumBoughtEventPublisherToRedis, "events");

        assertThat(analyticEvents).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given instanceof Premium returnValue and add tu queue")
    void testAddToPublishAddOneEventToQueue() {
        User user = getUser(USER_ID);
        Premium premium = getPremium(PREMIUM_ID, user, START_DATE, END_DATE);

        premiumBoughtEventPublisherToRedis.publish(premium);
        Queue<PremiumBoughtEventDto> analyticEvents =
                (Queue<PremiumBoughtEventDto>) ReflectionTestUtils.getField(premiumBoughtEventPublisherToRedis, "events");

        assertThat(analyticEvents).isNotEmpty();
    }
}