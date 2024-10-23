package school.faang.user_service.annotation.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.aspect.redis.ProfileViewEventPublisherToRedis;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.users.UserTestUtil.buildUser;
import static school.faang.user_service.util.users.UserTestUtil.buildUsers;

@ExtendWith(MockitoExtension.class)
class ProfileViewEventPublisherToRedisTest {
    private static final long RECEIVER_ID = 1L;
    private static final String RECEIVER_NAME = "Name";
    private static final long ACTOR_ID = 2L;
    private static final int NUMBER_OF_ACTORS = 3;
    private static final User RECEIVER = buildUser(RECEIVER_ID, RECEIVER_NAME);

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileViewEventPublisherToRedis profileViewEventPublisherToRedis;

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given not User and List<User> return value and do nothing")
    void testAddToPublishNotInstanceDoNothing() {
        profileViewEventPublisherToRedis.publish(new Object());
        Queue<ProfileViewEventDto> analyticEvents =
                (Queue<ProfileViewEventDto>) ReflectionTestUtils.getField(profileViewEventPublisherToRedis, "events");

        assertThat(analyticEvents).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given instanceof User returnValue and add tu queue")
    void testAddToPublishAddOneEventToQueue() {
        when(userContext.getUserId()).thenReturn(RECEIVER_ID);
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(RECEIVER));

        profileViewEventPublisherToRedis.publish(buildUser(ACTOR_ID));
        Queue<ProfileViewEventDto> analyticEvents =
                (Queue<ProfileViewEventDto>) ReflectionTestUtils.getField(profileViewEventPublisherToRedis, "events");

        assertThat(analyticEvents).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given instanceof List<User> returnValue and add to queue")
    void testAddToPublishAddListOfEventToQueue() {
        when(userContext.getUserId()).thenReturn(RECEIVER_ID);
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(RECEIVER));

        profileViewEventPublisherToRedis.publish(buildUsers(NUMBER_OF_ACTORS));
        Queue<ProfileViewEventDto> analyticEvents =
                (Queue<ProfileViewEventDto>) ReflectionTestUtils.getField(profileViewEventPublisherToRedis, "events");

        assertThat(analyticEvents).isNotEmpty();
    }
}