package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.listener.RedisBanMessageListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisBanMessageListener banMessageListener;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.ban-user-channel.name}")
    private String banUserTopic;

    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String followerEvent;

    @Value("${spring.data.redis.channels.event-start-channel.name}")
    private String eventStartTopic;

    @Value("${spring.data.redis.channels.mentorship-accepted-event-channel.name}")
    private String mentorshipAcceptedEventTopic;

    @Value("${spring.data.redis.channels.recommendation-request-channel.name}")
    private String recommendationReqTopic;

    @Value("${spring.data.redis.channels.recommendation-received-channel.name}")
    private String recommendationReceived;
    @Value("${spring.data.redis.channels.nice-guy-achievement-channel.name}")
    private String niceGuyAchievementReceived;

    @Value("${spring.data.redis.channels.follow-project-channel.name}")
    private String followProjectTopic;

    @Value("${spring.data.redis.channels.premium-bought-channel.name}")
    private String premiumBoughtTopic;

    @Value("${spring.data.redis.channels.event-starter}")
    private String eventStarter;

    @Value("${spring.data.redis.channels.goal-completed-event-channel.name}")
    private String goalCompletedTopic;

    @Value("${spring.data.redis.channels.skill-acquired-channel.name}")
    private String skillAcquired;

    @Value("${spring.data.redis.channels.profile}")
    private String profileView;

    @Value("${spring.data.redis.channels.skill-offered-channel.name}")
    private String skillOffered;

    @Value("${spring.data.redis.channels.mentorship-request-channel.name}")
    private String mentorshipRequestTopic;

    @Value("${spring.data.redis.channels.mentorship-start-channel.name}")
    private String mentorshipStart;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(banUserMessageListenerAdapter(), banUserChannelTopic());

        return container;
    }

    @Bean
    public MessageListenerAdapter banUserMessageListenerAdapter() {
        return new MessageListenerAdapter(banMessageListener);
    }

    @Bean
    public ChannelTopic banUserChannelTopic() {
        return new ChannelTopic(banUserTopic);
    }
    @Bean
    public ChannelTopic niceGuyAchievementTopic() { return new ChannelTopic(niceGuyAchievementReceived);}

    @Bean
    public ChannelTopic followerTopic() {
        return new ChannelTopic(followerEvent);
    }

    @Bean
    public ChannelTopic eventStartTopic() {
        return new ChannelTopic(eventStartTopic);
    }

    @Bean
    public ChannelTopic recommendationReceivedTopic() {
        return new ChannelTopic(recommendationReceived);
    }

    @Bean
    public ChannelTopic followProjectTopic() {
        return new ChannelTopic(followProjectTopic);
    }

    @Bean
    public ChannelTopic profileViewTopic() {
        return new ChannelTopic(profileView);
    }

    @Bean
    public ChannelTopic goalCompletedTopic() {
        return new ChannelTopic(goalCompletedTopic);
    }

    @Bean
    public ChannelTopic skillAcquiredTopic() {
        return new ChannelTopic(skillAcquired);
    }

    @Bean
    public ChannelTopic recommendationRequestTopic() {
        return new ChannelTopic(recommendationReqTopic);
    }

    @Bean
    public ChannelTopic mentorshipAcceptedEventTopic() {
        return new ChannelTopic(mentorshipAcceptedEventTopic);
    }

    @Bean
    public ChannelTopic premiumBoughtTopic() {
        return new ChannelTopic(premiumBoughtTopic);
    }

    @Bean
    public ChannelTopic eventStarter() {
        return new ChannelTopic(eventStarter);
    }

    @Bean
    public ChannelTopic skillOfferedTopic() {
        return new ChannelTopic(skillOffered);
    }

    @Bean
    public ChannelTopic mentorshipRequestTopic() {
        return new ChannelTopic(mentorshipRequestTopic);
    }

    @Bean
    public ChannelTopic mentorshipStartTopic() {
        return new ChannelTopic(mentorshipStart);
    }
}
