package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.messaging.RedisUserUpdateSubscriber;
import school.faang.user_service.messaging.ban.UserBanEventListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.channels.user_update_channel.name}")
    private String userUpdateChannel;
    @Value("${spring.data.redis.channels.mentorship_event_topic.name}")
    private String mentorshipEventTopic;
    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerChannel;
    @Value("${spring.data.redis.channels.skill-event.skill-offered-channel}")
    private String skillOfferedChannel;
    @Value("${spring.data.redis.channels.mentorship_offered_event.name}")
    private String mentorshipOfferedEvent;
    @Value("${spring.data.redis.channels.fund-raised-event.name}")
    private String fundRaisedChannel;
    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBanEvent;
    @Value("${spring.data.redis.channels.recommendation_received_channel.name}")
    private String recommendationReceivedChannel;

    private final UserBanEventListener userBanEventListener;
    private final RedisUserUpdateSubscriber redisUserUpdateSubscriber;


    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        System.out.println(port);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    MessageListenerAdapter messageListener(RedisUserUpdateSubscriber redisUserUpdateSubscriber) {
        return new MessageListenerAdapter(redisUserUpdateSubscriber);
    }

    @Bean
    ChannelTopic userUpdateChannel() {
        return new ChannelTopic(userUpdateChannel);
    }

    @Bean
    ChannelTopic followerTopic() {
        return new ChannelTopic(followerChannel);
    }

    @Bean
    ChannelTopic mentorshipEventTopic() {
        return new ChannelTopic(mentorshipEventTopic);
    }

    @Bean
    ChannelTopic skillOfferedTopic() {
        return new ChannelTopic(skillOfferedChannel);
    }

    @Bean
    ChannelTopic mentorshipOfferedEvent() {
        return new ChannelTopic(mentorshipOfferedEvent);
    }

    @Bean
    ChannelTopic fundRaisedChannel() {
        return new ChannelTopic(fundRaisedChannel);
    }

    @Bean
    ChannelTopic userBanTopic() {
        return new ChannelTopic(userBanEvent);
    }

    @Bean
    ChannelTopic recommendationReceivedChannel() {
        return new ChannelTopic(recommendationReceivedChannel);
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(new MessageListenerAdapter(redisUserUpdateSubscriber), userUpdateChannel());
        container.addMessageListener(new MessageListenerAdapter(userBanEventListener), userBanTopic());
        return container;
    }
}
