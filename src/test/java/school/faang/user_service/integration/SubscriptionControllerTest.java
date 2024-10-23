package school.faang.user_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.entity.User;
import school.faang.user_service.redis.event.FollowerEvent;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.subscription.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionControllerTest extends TestContainersConfig {
    private static final String REDIS_TOPIC = "follower_event_topic";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisMessageListenerContainer messageListenerContainer;

    @Autowired
    private ObjectMapper objectMapper;

    private User follower;
    private User followee;
    private FollowerEvent followerEvent;
    private CountDownLatch latch;

    @BeforeEach
    public void setup() {
        makeSubscribeListener();

        List<User> users = userRepository.findAll();
        follower = users.get(0);
        followee = users.get(1);
        userRepository.deleteAll();
        follower = userRepository.save(follower);
        followee = userRepository.save(followee);
    }

    @Test
    public void followUserTest() throws Exception {
        mvc.perform(post("/subscriptions/{followerId}/follow/{followeeId}", follower.getId(), followee.getId()))
                .andExpect(status().is2xxSuccessful());

        assertEquals(subscriptionService.getFollowersCount(followee.getId()), 1);

        boolean messageReceived = latch.await(2, TimeUnit.SECONDS);

        assertThat(messageReceived).isTrue();
        assertNotNull(followerEvent);
        assertEquals(followerEvent.getFollowerId(), follower.getId());
        assertEquals(followerEvent.getFollowedId(), followee.getId());
        assertEquals(followerEvent.getFollowerName(), follower.getUsername());
    }

    @Test
    public void followUserIdenticalUsersTest() throws Exception {
        mvc.perform(post("/subscriptions/{followerId}/follow/{followeeId}", follower.getId(), follower.getId()))
                .andExpect(status().isBadRequest());

        assertEquals(subscriptionService.getFollowersCount(follower.getId()), 0);
        boolean messageReceived = latch.await(2, TimeUnit.SECONDS);
        assertFalse(messageReceived);
        assertNull(followerEvent);
    }

    @Test
    public void followUserExistsFollowerTest() throws Exception {
        followee.setFollowers(List.of(follower));
        userRepository.save(followee);

        mvc.perform(post("/subscriptions/{followerId}/follow/{followeeId}", follower.getId(), followee.getId()))
                .andExpect(status().isBadRequest());

        assertEquals(subscriptionService.getFollowersCount(followee.getId()), followee.getFollowers().size());
        boolean messageReceived = latch.await(2, TimeUnit.SECONDS);
        assertFalse(messageReceived);
        assertNull(followerEvent);
    }

    private void makeSubscribeListener() {
        latch = new CountDownLatch(1);

        messageListenerContainer.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    followerEvent = objectMapper.readValue(message.getBody(), FollowerEvent.class);
                    latch.countDown();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new ChannelTopic(REDIS_TOPIC));
    }
}
