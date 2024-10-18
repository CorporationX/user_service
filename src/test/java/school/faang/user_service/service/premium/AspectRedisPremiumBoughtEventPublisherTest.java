package school.faang.user_service.service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.annotation.InvalidReturnTypeException;

import java.time.LocalDateTime;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.util.premium.PremiumFabric.getPremium;
import static school.faang.user_service.util.users.UserTestUtil.getUser;

@ExtendWith(MockitoExtension.class)
class AspectRedisPremiumBoughtEventPublisherTest {
    private static final long USER_ID = 1L;
    private static final long PREMIUM_ID = 1L;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final LocalDateTime END_DATE = START_DATE.plusDays(31);

    @Mock
    private RedisTemplate<String, PremiumBoughtEventDto> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private AspectRedisPremiumBoughtEventPublisher redisPremiumBoughtEventPublisher;

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given not User and List<User> return value and do nothing")
    void testAddToPublishNotInstanceDoNothing() {
        assertThatThrownBy(() -> redisPremiumBoughtEventPublisher.addToPublish(new Object()))
                .isInstanceOf(InvalidReturnTypeException.class);
        Queue<PremiumBoughtEventDto> analyticEvents =
                (Queue<PremiumBoughtEventDto>) ReflectionTestUtils.getField(redisPremiumBoughtEventPublisher, "events");
        assertThat(analyticEvents).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given instanceof Premium returnValue and add tu queue")
    void testAddToPublishAddOneEventToQueue() {
        User user = getUser(USER_ID);
        Premium premium = getPremium(PREMIUM_ID, user, START_DATE, END_DATE);

        redisPremiumBoughtEventPublisher.addToPublish(premium);
        Queue<PremiumBoughtEventDto> analyticEvents =
                (Queue<PremiumBoughtEventDto>) ReflectionTestUtils.getField(redisPremiumBoughtEventPublisher, "events");
        assertThat(analyticEvents).isNotEmpty();
    }
}