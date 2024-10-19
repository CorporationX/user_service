package school.faang.user_service.service.user.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.exception.annotation.InvalidReturnTypeException;
import school.faang.user_service.service.user.view.publisher.AspectRedisProfileViewEventPublisher;

import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.users.UserTestUtil.buildUser;
import static school.faang.user_service.util.users.UserTestUtil.buildUsers;

@ExtendWith(MockitoExtension.class)
class AspectRedisProfileViewEventPublisherTest {
    private static final long RECEIVER_ID = 1L;
    private static final String RECEIVER_NAME = "Name";
    private static final long ACTOR_ID = 2L;
    private static final int NUMBER_OF_ACTORS = 3;

    @Mock
    private RedisTemplate<String, ProfileViewEventDto> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private AspectRedisProfileViewEventPublisher redisProfileViewEventPublisher;

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given not User and List<User> return value and do nothing")
    void testAddToPublishNotInstanceDoNothing() {
        assertThatThrownBy(() -> redisProfileViewEventPublisher.addToPublish(new Object()))
                .isInstanceOf(InvalidReturnTypeException.class);
        Queue<ProfileViewEventDto> analyticEvents =
                (Queue<ProfileViewEventDto>) ReflectionTestUtils.getField(redisProfileViewEventPublisher, "events");
        assertThat(analyticEvents).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given instanceof User returnValue and add tu queue")
    void testAddToPublishAddOneEventToQueue() {
        when(userContext.getUserId()).thenReturn(RECEIVER_ID);
        when(userContext.getUserName()).thenReturn(RECEIVER_NAME);

        redisProfileViewEventPublisher.addToPublish(buildUser(ACTOR_ID));
        Queue<ProfileViewEventDto> analyticEvents =
                (Queue<ProfileViewEventDto>) ReflectionTestUtils.getField(redisProfileViewEventPublisher, "events");

        assertThat(analyticEvents).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given List<Object> not instanceof List<User> and do nothing")
    void testAddToPublishListNotInstance() {
        assertThatThrownBy(() -> redisProfileViewEventPublisher.addToPublish(List.of(new Object())))
                .isInstanceOf(InvalidReturnTypeException.class);
        Queue<ProfileViewEventDto> analyticEvents =
                (Queue<ProfileViewEventDto>) ReflectionTestUtils.getField(redisProfileViewEventPublisher, "events");

        assertThat(analyticEvents).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given instanceof List<User> returnValue and add to queue")
    void testAddToPublishAddListOfEventToQueue() {
        when(userContext.getUserId()).thenReturn(RECEIVER_ID);
        when(userContext.getUserName()).thenReturn(RECEIVER_NAME);

        redisProfileViewEventPublisher.addToPublish(buildUsers(NUMBER_OF_ACTORS));
        Queue<ProfileViewEventDto> analyticEvents =
                (Queue<ProfileViewEventDto>) ReflectionTestUtils.getField(redisProfileViewEventPublisher, "events");

        assertThat(analyticEvents).isNotEmpty();
    }
}